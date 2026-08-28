package com.huhx0015.hxaudio.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.media.MediaPlayer;
import android.net.Uri;
import com.huhx0015.hxaudio.interfaces.HXMusicEngineListener;
import com.huhx0015.hxaudio.model.HXMusicItem;
import com.huhx0015.hxaudio.utils.HXLog;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusicEngine] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXMusicEngine class is a wrapper class for the MediaPlayer class and is used
 *  directly by the HXMusic class to start, pause, stop, and resume music playback. HXMusicEngine is
 *  designed to handle the full playback state of a single audio stream at a time and is ideal for
 *  applications such as games.
 *  -----------------------------------------------------------------------------------------------
 */

class HXMusicEngine {
    
    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    private boolean isInitialized; // Used to keep track of the initialization state of the current player.
    private boolean isGapless; // Used to determine if gapless playback mode has been enabled or not.
    private boolean isLooped; // Used to determine if the current music has looping enabled or not.
    private int musicPosition; // Used for tracking the current music position.
    private Context context; // Context class used for initializing the MediaPlayer objects.
    private HXMusicItem musicItem; // References the current HXMusicItem that stores information about the current music.
    private MediaPlayer currentPlayer; // MediaPlayer object used for playing back the current music.
    private MediaPlayer nextPlayer; // A secondary MediaPlayer object used when gapless playback has been enabled.
    private AudioManager audioManager; // Used for requesting and abandoning audio focus.
    private AudioFocusRequest audioFocusRequest; // Audio focus request for API 26+.
    private boolean hasAudioFocus; // Tracks whether this engine currently owns focus.
    private boolean shouldResumeAfterFocusGain; // Tracks whether playback should auto-resume after transient focus loss.
    private float playbackVolume = 1.0f; // Tracks active playback volume for ducking/focus restoration.

    // CONCURRENCY VARIABLES:
    // Guards every read and write of the MediaPlayer references and their associated playback
    // state. MediaPlayer callbacks, audio focus changes, and the HXMusic operation executor all
    // reach this class from different threads.
    private final Object playerLock = new Object();
    private ScheduledThreadPoolExecutor teardownExecutor; // Performs the deferred teardown of unlinked players.

    // LISTENER VARIABLES:
    private HXMusicEngineListener musicEngineListener; // Interface for listening for events from the MediaPlayer object.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXMusicEngine.class.getSimpleName(); // Used for logging output to logcat.

    private static final float FULL_VOLUME = 1.0f;
    private static final float DUCK_VOLUME = 0.2f;

    // Grace period granted to a MediaPlayer that has been unlinked from a gapless chain, before it
    // is reset and released. See scheduleArmedPlayerTeardown().
    private static final long ARMED_PLAYER_TEARDOWN_DELAY_MS = 750L;
    private static final long TEARDOWN_THREAD_KEEP_ALIVE_MS = 5000L;
    private static final String TEARDOWN_THREAD_NAME = "HXMusicEngine-teardown";

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // initMusicEngine(): Initializes the engine with the specified music parameters.
    boolean initMusicEngine(HXMusicItem music, int position, boolean isGapless, boolean isLooped,
                            Context context) {
        synchronized (playerLock) {
            isInitialized = false;
            this.context = context;
            this.musicItem = music;
            this.musicPosition = position;
            this.isGapless = isGapless;
            this.isLooped = isLooped;

            // Stops any music currently playing in the background. currentPlayer is stopped before
            // release() unlinks nextPlayer, as the native media framework will otherwise hand
            // playback off to nextPlayer while it is being torn down.
            if (currentPlayer != null || nextPlayer != null) {
                if (currentPlayer != null) {
                    try {
                        if (currentPlayer.isPlaying()) {
                            HXLog.d(LOG_TAG, "PREPARING: initMusicEngine(): Song currently playing in the background. Stopping playback before switching to a new song.");
                            currentPlayer.stop();
                        }
                    } catch (Exception e) {
                        HXLog.e(LOG_TAG, "ERROR: initMusicEngine(): An exception occurred while attempting to stop the existing MediaPlayer object.");
                    }
                }

                release(); // Releases MediaPool resources.
            }

            currentPlayer = prepareMediaPlayer(context);

            if (currentPlayer == null) {
                HXLog.e(LOG_TAG, "ERROR: initMusicEngine(): An error occurred while preparing the MediaPlayer object.");
                return false;
            }

            // Music playback begins immediately once the MediaPlayer object is ready.
            currentPlayer.setOnPreparedListener(playerPreparedListener);
            currentPlayer.setOnCompletionListener(playerCompletionListener);

            // Sets up a buffering update listener for the MediaPlayer object. This listener will
            // be constantly invoked as the song is being buffered.
            if (musicItem.getMusicUrl() != null) {
                currentPlayer.setOnBufferingUpdateListener(playerBufferingUpdateListener);
            }

            return true;
        }
    }

    // prepareMediaPlayer(): Prepares a MediaPlayer object with the resource or path defined by the
    // HXMusicItem. Callers must hold playerLock.
    private MediaPlayer prepareMediaPlayer(Context context) {
        boolean hasValidDataSource = false;

        // Sets up the MediaPlayer object for the music to be played.
        MediaPlayer player = new MediaPlayer(); // Initializes the MediaPlayer.
        AudioAttributes attributes = buildPlaybackAudioAttributes();
        player.setAudioAttributes(attributes);
        HXLog.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer audio attributes configured.");

        // Prepares the specified music URL for playback.
        if (musicItem.getMusicUrl() != null) {
            try {
                player.setDataSource(context, Uri.parse(musicItem.getMusicUrl()));
                player.setOnErrorListener(playerErrorListener);
                player.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
                hasValidDataSource = true;
                HXLog.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer URL was set, preparing MediaPlayer...");
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: prepareMediaPlayer(): An error occurred while loading the music from the specified URL: " + e.getLocalizedMessage());
            }
        }

        // Prepares the specified music resource for playback.
        else if (musicItem.getMusicResource() != 0) {
            try {
                try (AssetFileDescriptor asset = context.getResources().openRawResourceFd(musicItem.getMusicResource())) {
                    if (asset == null) {
                        HXLog.e(LOG_TAG, "ERROR: prepareMediaPlayer(): Failed to open AssetFileDescriptor for music resource.");
                        player.release();
                        return null;
                    }
                    player.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
                }
                player.setOnErrorListener(playerErrorListener);
                player.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
                hasValidDataSource = true;
                HXLog.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer resource was set, preparing MediaPlayer...");
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: prepareMediaPlayer(): An error occurred while loading the music resource: " + e.getLocalizedMessage());
            }
        }

        if (!hasValidDataSource) {
            player.release();
            return null;
        }

        return player;
    }

    private boolean requestAudioFocus(AudioAttributes attributes) {
        synchronized (playerLock) {
            Context appContext = context != null ? context.getApplicationContext() : null;
            if (appContext == null) {
                HXLog.e(LOG_TAG, "ERROR: requestAudioFocus(): Context was null.");
                return false;
            }

            if (audioManager == null) {
                audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
            }

            if (audioManager == null) {
                HXLog.e(LOG_TAG, "ERROR: requestAudioFocus(): AudioManager unavailable.");
                return false;
            }

            int result;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(attributes)
                        .setAcceptsDelayedFocusGain(false)
                        .setOnAudioFocusChangeListener(audioFocusChangeListener)
                        .build();
                result = audioManager.requestAudioFocus(audioFocusRequest);
            } else {
                result = audioManager.requestAudioFocus(audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }

            hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            shouldResumeAfterFocusGain = false;
            HXLog.d(LOG_TAG, "AUDIO_FOCUS: requestAudioFocus(): result=" + result + ", granted=" + hasAudioFocus);
            return hasAudioFocus;
        }
    }

    private AudioAttributes buildPlaybackAudioAttributes() {
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    }

    private void abandonAudioFocus() {
        synchronized (playerLock) {
            if (!hasAudioFocus && audioManager == null) {
                return;
            }

            if (audioManager != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
                        audioManager.abandonAudioFocusRequest(audioFocusRequest);
                    } else {
                        audioManager.abandonAudioFocus(audioFocusChangeListener);
                    }
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: abandonAudioFocus(): " + e.getLocalizedMessage());
                }
            }

            hasAudioFocus = false;
            shouldResumeAfterFocusGain = false;
            audioFocusRequest = null;
            setPlaybackVolume(FULL_VOLUME);
        }
    }

    // removeNextMediaPlayer(): Unlinks the queued MediaPlayer so that it is not auto-started once
    // currentPlayer has completed playback. Callers must hold playerLock and must have already
    // stopped or paused currentPlayer, as unlinking alone cannot undo a handoff that the native
    // media framework has already performed.
    private void removeNextMediaPlayer() {
        synchronized (playerLock) {
            if (nextPlayer == null) {
                return;
            }

            MediaPlayer unlinkedPlayer = nextPlayer;
            nextPlayer = null;

            if (currentPlayer != null) {
                try {
                    currentPlayer.setNextMediaPlayer(null);
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: removeNextMediaPlayer(): " + e.getLocalizedMessage());
                }
            }

            scheduleArmedPlayerTeardown(unlinkedPlayer);
        }
    }

    // scheduleArmedPlayerTeardown(): Silences a MediaPlayer that was armed through
    // setNextMediaPlayer() and defers its teardown. When the native media framework promotes an
    // armed player it calls start() on it from a detached thread that has no exception handler, so
    // resetting or releasing the player before that call lands raises an IllegalStateException that
    // terminates the process. Muting the player keeps an unavoidable auto-start inaudible, and the
    // delay lets the pending call complete against a still-valid object.
    private void scheduleArmedPlayerTeardown(final MediaPlayer player) {
        if (player == null) {
            return;
        }

        try {
            player.setVolume(0f, 0f);
        } catch (Exception e) {
            HXLog.e(LOG_TAG, "ERROR: scheduleArmedPlayerTeardown(): Unable to silence the queued MediaPlayer. " + e.getLocalizedMessage());
        }

        try {
            ensureTeardownExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    releasePlayer(player);
                    HXLog.d(LOG_TAG, "RELEASE: scheduleArmedPlayerTeardown(): Queued MediaPlayer has been released.");
                }
            }, ARMED_PLAYER_TEARDOWN_DELAY_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            HXLog.e(LOG_TAG, "ERROR: scheduleArmedPlayerTeardown(): Deferred teardown was unavailable, releasing immediately. " + e.getLocalizedMessage());
            releasePlayer(player);
        }
    }

    private ScheduledThreadPoolExecutor ensureTeardownExecutor() {
        synchronized (playerLock) {
            if (teardownExecutor == null || teardownExecutor.isShutdown()) {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable, TEARDOWN_THREAD_NAME);
                        thread.setDaemon(true);
                        return thread;
                    }
                });

                // Lets the worker expire while idle so a released engine does not keep a thread open.
                executor.setKeepAliveTime(TEARDOWN_THREAD_KEEP_ALIVE_MS, TimeUnit.MILLISECONDS);
                executor.allowCoreThreadTimeOut(true);
                teardownExecutor = executor;
            }

            return teardownExecutor;
        }
    }

    // promoteNextPlayer(): Promotes the queued MediaPlayer once gapless playback has handed off to
    // it, then queues its replacement. Callers must hold playerLock.
    private void promoteNextPlayer(MediaPlayer completedPlayer) {
        if (nextPlayer == null) {
            HXLog.e(LOG_TAG, "ERROR: promoteNextPlayer(): Unable to set nextPlayer as currentPlayer as nextPlayer was null.");
            return;
        }

        // Only the player that is currently driving playback can hand off to the queued player. A
        // completion reported by a superseded player would otherwise promote the queued player
        // while a different track is playing.
        if (completedPlayer != null && completedPlayer != currentPlayer) {
            HXLog.d(LOG_TAG, "MUSIC: promoteNextPlayer(): Ignoring completion from a MediaPlayer that is no longer current.");
            return;
        }

        currentPlayer = nextPlayer; // Sets the current MediaPlayer.
        nextPlayer = prepareMediaPlayer(context); // Prepares the next MediaPlayer.

        if (nextPlayer != null) {
            nextPlayer.setOnPreparedListener(nextPlayerPreparedListener);
            nextPlayer.setOnCompletionListener(playerCompletionListener);
            nextPlayer.setOnBufferingUpdateListener(playerBufferingUpdateListener);
        } else {
            try {
                currentPlayer.setLooping(true);
                HXLog.w(LOG_TAG, "MUSIC: promoteNextPlayer(): Next gapless player unavailable. Continuing with standard loop mode.");
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: promoteNextPlayer(): " + e.getLocalizedMessage());
            }
        }

        setPlaybackVolume(playbackVolume);

        // The completed player is no longer armed for auto-start, so it can be torn down at once.
        if (completedPlayer != null && completedPlayer != currentPlayer) {
            releasePlayer(completedPlayer);
        }

        HXLog.d(LOG_TAG, "MUSIC: promoteNextPlayer(): Preparing next MediaPlayer object for gapless playback.");
    }

    /** LISTENER METHODS ________________________________________________________________________**/

    // playerPreparedListener: Starts playback once the current MediaPlayer object is ready, and
    // queues the secondary MediaPlayer when gapless playback mode has been enabled.
    private MediaPlayer.OnPreparedListener playerPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            boolean focusDenied = false;
            boolean started = false;

            synchronized (playerLock) {

                // Discards callbacks belonging to a player that has since been replaced, so that a
                // superseded track cannot start playing.
                if (mp != currentPlayer) {
                    HXLog.d(LOG_TAG, "PREPARING: onPrepared(): Ignoring callback from a MediaPlayer that is no longer current.");
                    return;
                }

                try {
                    isInitialized = true;
                    if (musicPosition != 0) {
                        currentPlayer.seekTo(musicPosition);
                        HXLog.d(LOG_TAG, "PREPARING: onPrepared(): MediaPlayer position set to: " + musicPosition);
                    }

                    // GAPLESS: If gapless mode is enabled, the secondary MediaPlayer will begin
                    // immediate playback after playback on the current MediaPlayer has completed.
                    if (isGapless && isLooped) {

                        currentPlayer.setLooping(false); // Disables looping attribute.

                        nextPlayer = prepareMediaPlayer(context);
                        if (nextPlayer != null) {
                            nextPlayer.setOnPreparedListener(nextPlayerPreparedListener);
                            nextPlayer.setOnCompletionListener(playerCompletionListener);
                            nextPlayer.setOnBufferingUpdateListener(playerBufferingUpdateListener);
                        } else {
                            // Fall back to standard looping when the secondary player cannot be prepared.
                            currentPlayer.setLooping(true);
                            HXLog.w(LOG_TAG, "PREPARING: Gapless secondary player unavailable. Falling back to MediaPlayer loop mode.");
                        }

                        HXLog.d(LOG_TAG, "PREPARING: Gapless mode prepared.");
                    } else {
                        currentPlayer.setLooping(isLooped); // Sets the looping attribute.
                        HXLog.d(LOG_TAG, "PREPARING: onPrepared(): MediaPlayer looping status: " + isLooped);
                    }

                    if (!requestAudioFocus(buildPlaybackAudioAttributes())) {
                        focusDenied = true;
                    } else {
                        setPlaybackVolume(FULL_VOLUME);
                        currentPlayer.start(); // Begins playing the music.
                        started = true;
                    }
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: onPrepared(): " + e.getLocalizedMessage());
                }
            }

            // Invokes the associated listener calls outside of playerLock, as they reach into
            // application code that may call back into HXMusic.
            if (focusDenied) {
                HXLog.e(LOG_TAG, "ERROR: onPrepared(): Audio focus request failed. Playback start cancelled.");
                if (musicEngineListener != null) {
                    musicEngineListener.onMusicEngineError(AudioManager.AUDIOFOCUS_REQUEST_FAILED, 0);
                }
            } else if (started) {
                if (musicEngineListener != null) {
                    musicEngineListener.onMusicEnginePrepared();
                }
                HXLog.d(LOG_TAG, "MUSIC: onPrepared(): Music playback has begun.");
            }
        }
    };

    // playerCompletionListener: Handles playback completion for the current MediaPlayer object. In
    // gapless playback mode, the queued MediaPlayer is promoted and a replacement is prepared.
    private MediaPlayer.OnCompletionListener playerCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            boolean isCompleted = false;

            synchronized (playerLock) {
                if (isGapless && isLooped) {
                    promoteNextPlayer(mp);
                } else {
                    musicPosition = 0;
                    isCompleted = true;
                }
            }

            if (isCompleted) {
                abandonAudioFocus();

                // Invokes the associated listener call.
                if (musicEngineListener != null) {
                    musicEngineListener.onMusicEngineCompletion();
                }

                HXLog.d(LOG_TAG, "MUSIC: onCompletion(): Music playback has completed.");
            }
        }
    };

    // nextPlayerPreparedListener: Arms the gapless handoff once the secondary MediaPlayer object
    // has been prepared.
    private MediaPlayer.OnPreparedListener nextPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            synchronized (playerLock) {

                // Only arms the handoff when this callback belongs to the player that is still
                // queued. A superseded player is already scheduled for teardown and chaining it
                // would hand playback to a player that is about to be released.
                if (mp != nextPlayer || currentPlayer == null) {
                    HXLog.d(LOG_TAG, "PREPARING: onPrepared(): Ignoring callback from a MediaPlayer that is no longer queued.");
                    return;
                }

                try {
                    currentPlayer.setNextMediaPlayer(nextPlayer);
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: onPrepared(): " + e.getLocalizedMessage());
                }
            }
        }
    };

    // playerBufferingUpdateListener: Used to set the OnBufferingUpdateListener for the MediaPlayer
    // object.
    private MediaPlayer.OnBufferingUpdateListener playerBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

            // Invokes the associated listener call.
            if (musicEngineListener != null) {
                musicEngineListener.onMusicEngineBufferingUpdate(percent);
            }

            HXLog.d(LOG_TAG, "MUSIC: initMusicEngine(): Music buffering at: " + percent);
        }
    };

    private MediaPlayer.OnErrorListener playerErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            HXLog.e(LOG_TAG, "ERROR: playerErrorListener(): MediaPlayer error detected. what=" + what + ", extra=" + extra);
            abandonAudioFocus();
            if (musicEngineListener != null) {
                musicEngineListener.onMusicEngineError(what, extra);
            }
            return false;
        }
    };

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    synchronized (playerLock) {
                        setPlaybackVolume(FULL_VOLUME);
                        if (currentPlayer != null && shouldResumeAfterFocusGain && isInitialized
                                && !currentPlayer.isPlaying()) {
                            try {
                                currentPlayer.start();
                                shouldResumeAfterFocusGain = false;
                                HXLog.d(LOG_TAG, "AUDIO_FOCUS: onAudioFocusChange(): Focus regained, resumed playback.");
                            } catch (Exception e) {
                                HXLog.e(LOG_TAG, "ERROR: onAudioFocusChange(): Unable to resume after focus gain. " + e.getLocalizedMessage());
                            }
                        }
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    synchronized (playerLock) {
                        if (currentPlayer != null && isInitialized && currentPlayer.isPlaying()) {
                            try {
                                shouldResumeAfterFocusGain = true;
                                currentPlayer.pause();
                                HXLog.d(LOG_TAG, "AUDIO_FOCUS: onAudioFocusChange(): Transient loss, paused playback.");
                            } catch (Exception e) {
                                HXLog.e(LOG_TAG, "ERROR: onAudioFocusChange(): Unable to pause on transient loss. " + e.getLocalizedMessage());
                            }
                        }
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    synchronized (playerLock) {
                        setPlaybackVolume(DUCK_VOLUME);
                    }
                    HXLog.d(LOG_TAG, "AUDIO_FOCUS: onAudioFocusChange(): Ducking playback volume.");
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    synchronized (playerLock) {
                        shouldResumeAfterFocusGain = false;
                        if (currentPlayer != null && isInitialized && currentPlayer.isPlaying()) {
                            try {
                                currentPlayer.pause();
                            } catch (Exception e) {
                                HXLog.e(LOG_TAG, "ERROR: onAudioFocusChange(): Unable to pause on full loss. " + e.getLocalizedMessage());
                            }
                        }
                    }
                    abandonAudioFocus();
                    HXLog.d(LOG_TAG, "AUDIO_FOCUS: onAudioFocusChange(): Full focus loss, playback paused.");
                    break;

                default:
                    break;
            }
        }
    };

    /** MUSIC METHODS __________________________________________________________________________ **/

    // isPlaying(): Determines if a music is currently playing in the background.
    boolean isPlaying() {
        synchronized (playerLock) {
            try {
                return currentPlayer != null && isInitialized && currentPlayer.isPlaying();
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: isPlaying(): " + e.getLocalizedMessage());
                return false;
            }
        }
    }

    // pause(): Pauses any music playing in the background.
    int pauseMusic() {
        boolean isPaused = false;
        int position = 0;

        synchronized (playerLock) {

            // Checks to see if the MediaPlayer object has been initialized first before retrieving
            // the current music position and pausing the music.
            if (currentPlayer != null && isInitialized) {
                try {
                    musicPosition = currentPlayer.getCurrentPosition(); // Retrieves the current music position.
                    position = musicPosition;

                    // Pauses the music only if there is a music is currently playing. currentPlayer
                    // is paused before nextPlayer is unlinked, as the native media framework will
                    // otherwise hand playback off to nextPlayer while it is being torn down.
                    if (currentPlayer.isPlaying()) {
                        currentPlayer.pause(); // Pauses the music.
                        isPaused = true;
                    }

                    removeNextMediaPlayer(); // Prevents nextPlayer from starting after currentPlayer has completed playback.
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: pause(): An exception occurred while attempting to pause the existing MediaPlayer object.");
                }
            }
        }

        if (isPaused) {
            abandonAudioFocus();

            // Invokes the associated listener call.
            if (musicEngineListener != null) {
                musicEngineListener.onMusicEnginePause();
            }

            HXLog.d(LOG_TAG, "MUSIC: pause(): Music playback has been paused.");
            return position;
        }

        HXLog.e(LOG_TAG, "ERROR: pause(): Music could not be paused.");
        return 0;
    }

    // release(): Used to release the resources being used by the MediaPlayer object.
    boolean release() {
        MediaPlayer playerToRelease;
        MediaPlayer armedPlayer;

        synchronized (playerLock) {
            isInitialized = false;
            playerToRelease = currentPlayer;
            armedPlayer = nextPlayer;
            currentPlayer = null;
            nextPlayer = null;

            // Unlinks the gapless chain before either player is torn down. Playback is halted first,
            // as the native media framework will otherwise hand playback off to the queued player
            // while it is being torn down.
            if (playerToRelease != null && armedPlayer != null) {
                try {
                    if (playerToRelease.isPlaying()) {
                        playerToRelease.pause();
                    }
                    playerToRelease.setNextMediaPlayer(null);
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: release(): Unable to unlink the queued MediaPlayer. " + e.getLocalizedMessage());
                }
            }
        }

        abandonAudioFocus();

        boolean isReleased = false;

        if (playerToRelease != null) {
            releasePlayer(playerToRelease);
            isReleased = true;
        }

        // The queued player may already have been promoted by the native media framework, so its
        // teardown has to be deferred rather than performed inline.
        if (armedPlayer != null) {
            scheduleArmedPlayerTeardown(armedPlayer);
            isReleased = true;
        }

        if (isReleased) {
            HXLog.d(LOG_TAG, "RELEASE: release(): MediaPlayer object has been released.");
            return true;
        } else {
            HXLog.e(LOG_TAG, "ERROR: release(): MediaPlayer object is null and cannot be released.");
            return false;
        }
    }

    // stop(): Stops any music playing in the background.
    boolean stopMusic() {
        synchronized (playerLock) {
            if (currentPlayer == null) {
                HXLog.e(LOG_TAG, "ERROR: stop(): Cannot stop music, as MediaPlayer object is already null.");
                return false;
            }

            try {
                // currentPlayer is stopped before nextPlayer is unlinked, as the native media
                // framework will otherwise hand playback off to nextPlayer while it is being torn
                // down.
                if (currentPlayer.isPlaying()) {
                    currentPlayer.stop(); // Stops any music currently playing in the background.
                }

                removeNextMediaPlayer(); // Prevents nextPlayer from starting after currentPlayer has completed playback.
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: stopMusic(): An exception occurred while attempting to stop the existing MediaPlayer object. ");
                return false;
            }

            abandonAudioFocus();
            release(); // Releases MediaPool resources.
        }

        // Invokes the associated listener call.
        if (musicEngineListener != null) {
            musicEngineListener.onMusicEngineStop();
        }

        HXLog.d(LOG_TAG, "MUSIC: stop(): Music playback has been stopped.");
        return true;
    }

    private void releasePlayer(MediaPlayer player) {
        if (player == null) {
            return;
        }

        try {
            player.reset();
        } catch (Exception e) {
            HXLog.e(LOG_TAG, "ERROR: releasePlayer(): Error while resetting MediaPlayer. " + e.getLocalizedMessage());
        }

        try {
            player.release();
        } catch (Exception e) {
            HXLog.e(LOG_TAG, "ERROR: releasePlayer(): Error while releasing MediaPlayer. " + e.getLocalizedMessage());
        }
    }

    private void setPlaybackVolume(float volume) {
        synchronized (playerLock) {
            playbackVolume = volume;
            if (currentPlayer != null) {
                try {
                    currentPlayer.setVolume(volume, volume);
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: setPlaybackVolume(): " + e.getLocalizedMessage());
                }
            }
            if (nextPlayer != null) {
                try {
                    nextPlayer.setVolume(volume, volume);
                } catch (Exception e) {
                    HXLog.e(LOG_TAG, "ERROR: setPlaybackVolume(): " + e.getLocalizedMessage());
                }
            }
        }
    }

    /** SET METHODS ____________________________________________________________________________ **/

    // setListener(): Sets the HXMusicEngineListener between this HXMusicEngine and HXMusic classes.
    void setListener(HXMusicEngineListener listener) {
        this.musicEngineListener = listener;
    }
}
