package com.huhx0015.hxaudio.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import com.huhx0015.hxaudio.interfaces.HXMusicEngineListener;
import com.huhx0015.hxaudio.model.HXMusicItem;
import com.huhx0015.hxaudio.utils.HXLog;

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
    private int musicPosition; // Used for tracking the current music position.
    private Context context; // Context class used for initializing the MediaPlayer objects.
    private HXMusicItem musicItem; // References the current HXMusicItem that stores information about the current music.
    private MediaPlayer currentPlayer; // MediaPlayer object used for playing back the current music.
    private MediaPlayer nextPlayer; // A secondary MediaPlayer object used when gapless playback has been enabled.

    // LISTENER VARIABLES:
    private HXMusicEngineListener musicEngineListener; // Interface for listening for events from the MediaPlayer object.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXMusicEngine.class.getSimpleName(); // Used for logging output to logcat.

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // initMusicEngine(): Initializes the engine with the specified music parameters.
    synchronized boolean initMusicEngine(HXMusicItem music, final int position, final boolean isGapless,
                            final boolean isLooped, final Context context) {
        this.context = context;
        this.musicItem = music;
        this.musicPosition = position;

        // Stops any music currently playing in the background.
        if (currentPlayer != null) {
            try {
                if (currentPlayer.isPlaying()) {
                    HXLog.d(LOG_TAG, "PREPARING: initMusicEngine(): Song currently playing in the background. Stopping playback before switching to a new song.");
                    removeNextMediaPlayer(); // Prevents nextPlayer from starting after currentPlayer has completed playback.
                    currentPlayer.stop();
                }
                release(); // Releases MediaPool resources.
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: initMusicEngine(): An exception occurred while attempting to stop & release the existing MediaPlayer object. ");
            }
        }

        currentPlayer = prepareMediaPlayer(context);

        if (currentPlayer != null) {

            // Sets up the prepared listener for the MediaPlayer object. Music playback begins
            // immediately once the MediaPlayer object is ready.
            currentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer currentPlayer) {
                    try {

                        if (musicPosition != 0) {
                            currentPlayer.seekTo(musicPosition);
                            HXLog.d(LOG_TAG, "PREPARING: onPrepared(): MediaPlayer position set to: " + position);
                        }

                        // GAPLESS: If gapless mode is enabled, the secondary MediaPlayer will begin
                        // immediate playback after playback on the current MediaPlayer has completed.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isGapless &&
                                isLooped) {

                            currentPlayer.setLooping(false); // Disables looping attribute.

                            nextPlayer = prepareMediaPlayer(context);
                            nextPlayer.setOnPreparedListener(nextPlayerPreparedListener);
                            nextPlayer.setOnCompletionListener(nextPlayerCompletionListener);
                            nextPlayer.setOnBufferingUpdateListener(playerBufferingUpdateListener);

                            HXLog.d(LOG_TAG, "PREPARING: Gapless mode prepared.");
                        } else {
                            currentPlayer.setLooping(isLooped); // Sets the looping attribute.
                            HXLog.d(LOG_TAG, "PREPARING: onPrepared(): MediaPlayer looping status: " + isLooped);
                        }

                        currentPlayer.start(); // Begins playing the music.

                        // Invokes the associated listener call.
                        if (musicEngineListener != null) {
                            musicEngineListener.onMusicEnginePrepared();
                        }

                        HXLog.d(LOG_TAG, "MUSIC: onPrepared(): Music playback has begun.");
                    } catch (Exception e) {
                        HXLog.e(LOG_TAG, "ERROR: onPrepared(): " + e.getLocalizedMessage());
                    }
                }
            });

            // Sets up a completion listener for the MediaPlayer object.
            currentPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    // GAPLESS: Sets the current MediaPlayer object and prepares the next
                    // MediaPlayer to be played when the current MediaPlayer playback has completed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isGapless &&
                            isLooped) {
                        currentPlayer = nextPlayer; // Sets the current MediaPlayer.
                        nextPlayer = prepareMediaPlayer(context); // Prepares the next MediaPlayer.
                        nextPlayer.setOnPreparedListener(nextPlayerPreparedListener);
                        mp.release(); // Releases the previous MediaPlayer object.
                    } else {
                        musicPosition = 0;

                        // Invokes the associated listener call.
                        if (musicEngineListener != null) {
                            musicEngineListener.onMusicEngineCompletion();
                        }

                        HXLog.d(LOG_TAG, "MUSIC: onCompletion(): Music playback has completed.");
                    }
                }
            });

            // Sets up a buffering update listener for the MediaPlayer object. This listener will
            // be constantly invoked as the song is being buffered.
            if (musicItem.getMusicUrl() != null) {
                currentPlayer.setOnBufferingUpdateListener(playerBufferingUpdateListener);
            }

            return true;
        } else {
            HXLog.e(LOG_TAG, "ERROR: initMusicEngine(): An error occurred while preparing the MediaPlayer object.");
            return false;
        }
    }

    // prepareMediaPlayer(): Prepares a MediaPlayer object with the resource or path defined by the
    // HXMusicItem.
    private synchronized MediaPlayer prepareMediaPlayer(Context context) {

        // Sets up the MediaPlayer object for the music to be played.
        MediaPlayer player = new MediaPlayer(); // Initializes the MediaPlayer.
        player.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.
        HXLog.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer stream type set to STREAM_MUSIC.");

        // Prepares the specified music URL for playback.
        if (musicItem.getMusicUrl() != null) {
            try {
                player.setDataSource(context, Uri.parse(musicItem.getMusicUrl()));
                player.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
                isInitialized = true;
                HXLog.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer URL was set, preparing MediaPlayer...");
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: prepareMediaPlayer(): An error occurred while loading the music from the specified URL: " + e.getLocalizedMessage());
            }
        }

        // Prepares the specified music resource for playback.
        else if (musicItem.getMusicResource() != 0) {
            try {
                AssetFileDescriptor asset = context.getResources().openRawResourceFd(musicItem.getMusicResource());
                player.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
                player.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
                isInitialized = true;
                HXLog.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer resource was set, preparing MediaPlayer...");
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: prepareMediaPlayer(): An error occurred while loading the music resource: " + e.getLocalizedMessage());
            }
        }

        return player;
    }

    // removeNextMediaPlayer(): Prevents the next MediaPlayer from being played after currentPlayer
    // playback has been completed.
    private synchronized void removeNextMediaPlayer() {

        // Removes the link between currentPlayer and nextPlayer if nextPlayer has been prepared for
        // playback after currentPlayer completes playback.
        if (nextPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                currentPlayer.setNextMediaPlayer(null);
                nextPlayer = null;
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: pause(): " + e.getLocalizedMessage());
            }
        }
    }

    /** LISTENER METHODS ________________________________________________________________________**/

    // nextPlayerPreparedListener: Used to set the next OnPreparedListener for the
    // nextMediaPlayer object when gapless playback mode has been enabled.
    private MediaPlayer.OnPreparedListener nextPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mp) {
            try {
                if (currentPlayer != null && nextPlayer != null) {
                    currentPlayer.setNextMediaPlayer(nextPlayer);
                    currentPlayer.setOnCompletionListener(nextPlayerCompletionListener);
                }
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: onPrepared(): " + e.getLocalizedMessage());
            }
        }
    };

    // nextPlayerCompletionListener: Used to set the next OnCompletionListener for the
    // nextMediaPlayer object when gapless playback mode has been enabled.
    private MediaPlayer.OnCompletionListener nextPlayerCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (nextPlayer != null) {
                currentPlayer = nextPlayer; // Sets the current MediaPlayer.
                nextPlayer = prepareMediaPlayer(context); // Prepares the next MediaPlayer.
                nextPlayer.setOnPreparedListener(nextPlayerPreparedListener);
                mp.release(); // Releases the previous MediaPlayer.
                HXLog.d(LOG_TAG, "MUSIC: onCompletion(): Preparing next MediaPlayer object for gapless playback.");
            } else {
                HXLog.e(LOG_TAG, "ERROR: onCompletion(): Unable to set nextPlayer as currentPlayer as nextPlayer was null.");
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

    /** MUSIC METHODS __________________________________________________________________________ **/

    // isPlaying(): Determines if a music is currently playing in the background.
    boolean isPlaying() {
        try {
            return currentPlayer != null && isInitialized && currentPlayer.isPlaying();
        } catch (Exception e) {
            HXLog.e(LOG_TAG, "ERROR: isPlaying(): " + e.getLocalizedMessage());
            return false;
        }
    }

    // pause(): Pauses any music playing in the background.
    int pauseMusic() {

        // Checks to see if the MediaPlayer object has been initialized first before retrieving the
        // current music position and pausing the music.
        if (currentPlayer != null && isInitialized) {

            try {
                musicPosition = currentPlayer.getCurrentPosition(); // Retrieves the current music position.

                // Pauses the music only if there is a music is currently playing.
                if (currentPlayer != null && currentPlayer.isPlaying()) {

                    removeNextMediaPlayer(); // Prevents nextPlayer from starting after currentPlayer has completed playback.
                    currentPlayer.pause(); // Pauses the music.

                    // Invokes the associated listener call.
                    if (musicEngineListener != null) {
                        musicEngineListener.onMusicEnginePause();
                    }

                    HXLog.d(LOG_TAG, "MUSIC: pause(): Music playback has been paused.");
                    return musicPosition;
                }
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: pause(): An exception occurred while attempting to pause the existing MediaPlayer object.");
            }
        }

        HXLog.e(LOG_TAG, "ERROR: pause(): Music could not be paused.");
        return 0;
    }

    // release(): Used to release the resources being used by the MediaPlayer object.
    synchronized boolean release() {
        isInitialized = false;

        if (currentPlayer != null) {
            currentPlayer.reset();
            currentPlayer.release();
            currentPlayer = null;
            HXLog.d(LOG_TAG, "RELEASE: release(): MediaPlayer object has been released.");
            return true;
        } else {
            HXLog.e(LOG_TAG, "ERROR: release(): MediaPlayer object is null and cannot be released.");
            return false;
        }
    }

    // stop(): Stops any music playing in the background.
    boolean stopMusic() {

        if (currentPlayer != null) {
            try {
                if (currentPlayer.isPlaying()) {
                    removeNextMediaPlayer(); // Prevents nextPlayer from starting after currentPlayer has completed playback.
                    currentPlayer.stop(); // Stops any music currently playing in the background.
                }
                release(); // Releases MediaPool resources.

                // Invokes the associated listener call.
                if (musicEngineListener != null) {
                    musicEngineListener.onMusicEngineStop();
                }

                HXLog.d(LOG_TAG, "MUSIC: stop(): Music playback has been stopped.");
                return true;
            } catch (Exception e) {
                HXLog.e(LOG_TAG, "ERROR: stopMusic(): An exception occurred while attempting to stop & release the existing MediaPlayer object. ");
                return false;
            }
        } else {
            HXLog.e(LOG_TAG, "ERROR: stop(): Cannot stop music, as MediaPlayer object is already null.");
            return false;
        }
    }

    /** SET METHODS ____________________________________________________________________________ **/

    // setListener(): Sets the HXMusicEngineListener between this HXMusicEngine and HXMusic classes.
    void setListener(HXMusicEngineListener listener) {
        this.musicEngineListener = listener;
    }
}