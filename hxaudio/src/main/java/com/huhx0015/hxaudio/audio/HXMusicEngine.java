package com.huhx0015.hxaudio.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import com.huhx0015.hxaudio.interfaces.HXMusicEngineListener;
import com.huhx0015.hxaudio.model.HXMusicItem;

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
    boolean initMusicEngine(HXMusicItem music, final int position, final boolean isGapless,
                            final boolean isLooped, final Context context) {
        this.context = context;
        this.musicItem = music;
        this.musicPosition = position;

        // Stops any music currently playing in the background.
        if (currentPlayer != null && currentPlayer.isPlaying()) {
            Log.d(LOG_TAG, "PREPARING: initMusicEngine(): Song currently playing in the background. Stopping playback before switching to a new song.");
            currentPlayer.stop();
        }

        release(); // Releases MediaPool resources.
        currentPlayer = prepareMediaPlayer(context);

        if (currentPlayer != null) {

            // Sets up the prepared listener for the MediaPlayer object. Music playback begins
            // immediately once the MediaPlayer object is ready.
            currentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer currentPlayer) {

                    if (musicPosition != 0) {
                        currentPlayer.seekTo(musicPosition);
                        Log.d(LOG_TAG, "PREPARING: onPrepared(): MediaPlayer position set to: " + position);
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

                        Log.d(LOG_TAG, "PREPARING: Gapless mode prepared.");
                    } else {
                        currentPlayer.setLooping(isLooped); // Sets the looping attribute.
                        Log.d(LOG_TAG, "PREPARING: onPrepared(): MediaPlayer looping status: " + isLooped);
                    }

                    currentPlayer.start(); // Begins playing the music.

                    // Invokes the associated listener call.
                    if (musicEngineListener != null) {
                        musicEngineListener.onMusicEnginePrepared();
                    }

                    Log.d(LOG_TAG, "MUSIC: onPrepared(): Music playback has begun.");
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

                        Log.d(LOG_TAG, "MUSIC: onCompletion(): Music playback has completed.");
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
            Log.e(LOG_TAG, "ERROR: initMusicEngine(): An error occurred while preparing the MediaPlayer object.");
            return false;
        }
    }

    // prepareMediaPlayer(): Prepares a MediaPlayer object with the resource or path defined by the
    // HXMusicItem.
    private MediaPlayer prepareMediaPlayer(Context context) {

        // Sets up the MediaPlayer object for the music to be played.
        MediaPlayer player = new MediaPlayer(); // Initializes the MediaPlayer.
        player.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.
        Log.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer stream type set to STREAM_MUSIC.");

        // Prepares the specified music URL for playback.
        if (musicItem.getMusicUrl() != null) {
            try {
                player.setDataSource(context, Uri.parse(musicItem.getMusicUrl()));
                player.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
                Log.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer URL was set, preparing MediaPlayer...");
            } catch (Exception e) {
                Log.e(LOG_TAG, "ERROR: prepareMediaPlayer(): An error occurred while loading the music from the specified URL: " + e.getLocalizedMessage());
            }
        }

        // Prepares the specified music resource for playback.
        else if (musicItem.getMusicResource() != 0) {
            try {
                AssetFileDescriptor asset = context.getResources().openRawResourceFd(musicItem.getMusicResource());
                player.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
                player.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
                Log.d(LOG_TAG, "PREPARING: prepareMediaPlayer(): MediaPlayer resource was set, preparing MediaPlayer...");
            } catch (Exception e) {
                Log.e(LOG_TAG, "ERROR: prepareMediaPlayer(): An error occurred while loading the music resource: " + e.getLocalizedMessage());
            }
        }

        return player;
    }

    /** LISTENER METHODS ________________________________________________________________________**/

    // nextPlayerPreparedListener: Used to set the next OnPreparedListener for the
    // nextMediaPlayer object when gapless playback mode has been enabled.
    private MediaPlayer.OnPreparedListener nextPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mp) {
            currentPlayer.setNextMediaPlayer(nextPlayer);
            currentPlayer.setOnCompletionListener(nextPlayerCompletionListener);
        }
    };

    // nextPlayerCompletionListener: Used to set the next OnCompletionListener for the
    // nextMediaPlayer object when gapless playback mode has been enabled.
    private MediaPlayer.OnCompletionListener nextPlayerCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            currentPlayer = nextPlayer; // Sets the current MediaPlayer.
            nextPlayer = prepareMediaPlayer(context); // Prepares the next MediaPlayer.
            nextPlayer.setOnPreparedListener(nextPlayerPreparedListener);
            mp.release(); // Releases the previous MediaPlayer.
            Log.d(LOG_TAG, "MUSIC: onCompletion(): Preparing next MediaPlayer object for gapless playback.");
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

            Log.d(LOG_TAG, "MUSIC: initMusicEngine(): Music buffering at: " + percent);
        }
    };

    /** MUSIC METHODS __________________________________________________________________________ **/

    // isPlaying(): Determines if a music is currently playing in the background.
    boolean isPlaying() {
        return currentPlayer != null && currentPlayer.isPlaying();
    }

    // pauseMusic(): Pauses any music playing in the background.
    int pauseMusic() {

        // Checks to see if the MediaPlayer object has been initialized first before retrieving the
        // current music position and pausing the music.
        if (currentPlayer != null) {

            musicPosition = currentPlayer.getCurrentPosition(); // Retrieves the current music position.

            // Pauses the music only if there is a music is currently playing.
            if (currentPlayer != null && currentPlayer.isPlaying()) {
                currentPlayer.pause(); // Pauses the music.

                Log.d(LOG_TAG, "MUSIC: pauseMusic(): Music playback has been paused.");
                return musicPosition;
            }
        }

        Log.e(LOG_TAG, "ERROR: pauseMusic(): Music could not be paused.");
        return 0;
    }

    // release(): Used to release the resources being used by the MediaPlayer object.
    boolean release() {

        if (currentPlayer != null) {
            currentPlayer.reset();
            currentPlayer.release();
            currentPlayer = null;
            context = null;

            Log.d(LOG_TAG, "RELEASE: release(): MediaPlayer object has been released.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: release(): MediaPlayer object is null and cannot be released.");
            return false;
        }
    }

    // stopMusic(): Stops any music playing in the background.
    boolean stopMusic() {

        if (currentPlayer != null) {
            currentPlayer.stop(); // Stops any music currently playing in the background.

            Log.d(LOG_TAG, "MUSIC: stopMusic(): Music playback has been stopped.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: stopMusic(): Cannot stop music, as MediaPlayer object is already null.");
            return false;
        }
    }

    /** SET METHODS ____________________________________________________________________________ **/

    // setListener(): Sets the HXMusicEngineListener between this HXMusicEngine and HXMusic classes.
    void setListener(HXMusicEngineListener listener) {
        this.musicEngineListener = listener;
    }
}