package com.huhx0015.hxaudio.audio;

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
 *  DESCRIPTION: HXMusicEngine class is a singleton and wrapper class for the MediaPlayer object and
 *  serves as a way to simplify music access for applications. HXMusicEngine is designed to play a single
 *  audio stream at a time and is ideal for applications such as games.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXMusicEngine {
    
    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    private boolean isLooping; // Used to determine if the current music has looping enabled or not.
    private boolean isPrepared; // Used to determine if the MediaPlayer object has been prepared.
    private int engineID; // Used to determine the ID value of this instance.
    private int musicPosition; // Used for tracking the current music position.
    private HXMusicItem musicItem; // References the current HXMusicItem that stores information about the current music.
    private MediaPlayer mediaPlayer; // MediaPlayer object used for playing back the current music.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXMusicEngine.class.getSimpleName(); // Used for logging output to logcat.

    private HXMusicEngineListener musicEngineListener;

    public HXMusicEngine(int id) {
        this.engineID = id;
    }

    /** MUSIC ACTION METHODS ___________________________________________________________________ **/

    public void prepareMusic(Context context) {

        // Sets up the MediaPlayer object for the music to be played.
        release(); // Releases MediaPool resources.
        mediaPlayer = new MediaPlayer(); // Initializes the MediaPlayer.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.
        Log.d(LOG_TAG, "PREPARING: initMusic(): MediaPlayer stream type set to STREAM_MUSIC.");

        // Prepares the specified music URL for playback.
        if (musicItem.getMusicUrl() != null) {
            try {
                mediaPlayer.setDataSource(context, Uri.parse(musicItem.getMusicUrl()));
                Log.d(LOG_TAG, "PREPARING: initMusic(): MediaPlayer URL was set, preparing MediaPlayer...");
            } catch (Exception e) {
                Log.e(LOG_TAG, "ERROR: initMusic(): An error occurred while loading the music from the specified URL: " + e.getLocalizedMessage());
                //return false;
            }
        }

        // Prepares the specified music resource for playback.
        else if (musicItem.getMusicResource() != 0) {
            try {
                AssetFileDescriptor asset = context.getResources().openRawResourceFd(musicItem.getMusicResource());
                mediaPlayer.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
                Log.d(LOG_TAG, "PREPARING: initMusic(): MediaPlayer resource was set, preparing MediaPlayer...");
            } catch (Exception e) {
                Log.e(LOG_TAG, "ERROR: initMusic(): An error occurred while loading the music resource: " + e.getLocalizedMessage());
                //return false;
            }
        }

        mediaPlayer.prepareAsync(); // Prepares the MediaPlayer object asynchronously.
    }

    // initMusic(): Plays the specified music with the given position and isLooped parameters.
    public synchronized boolean initMusic(HXMusicItem music, final int position,
                                          final boolean isLooped, Context context) {

        if (checkStatus(music)) {

            this.musicItem = music;
            this.musicPosition = position;
            this.isLooping = isLooped;

            // Stops any music currently playing in the background.
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.d(LOG_TAG, "PREPARING: initMusic(): Song currently playing in the background. Stopping playback before switching to a new song.");
                mediaPlayer.stop();
            }

            prepareMusic(context);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPrepared = true;

                    if (musicEngineListener != null) {
                        musicEngineListener.onMusicEnginePrepared(mp, engineID);
                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (musicEngineListener != null) {
                        musicEngineListener.onMusicEngineCompletion(engineID);
                    }
                }
            });

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    if (musicEngineListener != null) {
                        musicEngineListener.onMusicEngineBufferingUpdate(percent, engineID);
                    }
                }
            });

//            // Sets up the prepared listener for the MediaPlayer object. Music playback begins
//            // immediately once the MediaPlayer object is ready.
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    if (musicPosition != 0) {
//                        mediaPlayer.seekTo(musicPosition);
//                        Log.d(LOG_TAG, "PREPARING: initMusic(): MediaPlayer position set to: " + position);
//                    }
//
//                    mediaPlayer.setLooping(isLooped); // Sets the looping attribute.
//                    Log.d(LOG_TAG, "PREPARING: initMusic(): MediaPlayer looping status: " + isLooped);
//
//                    mediaPlayer.start(); // Begins playing the music.
//                    musicStatus = HXMusicStatus.PLAYING;
//
//                    // Invokes the associated listener call.
//                    if (musicListener != null) {
//                        musicListener.onMusicPrepared(musicItem);
//                    }
//
//                    Log.d(LOG_TAG, "MUSIC: initMusic(): Music playback has begun.");
//                }
//            });
//
//            // Sets up a completion listener for the MediaPlayer object. Music position resets back
//            // to 0 when music is completed.
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    musicPosition = 0;
//                    musicStatus = HXMusicStatus.STOPPED;
//
//                    // Invokes the associated listener call.
//                    if (musicListener != null) {
//                        musicListener.onMusicCompletion(musicItem);
//                    }
//
//                    Log.d(LOG_TAG, "MUSIC: initMusic(): Music playback has completed.");
//                }
//            });
//
//            // Sets up a buffering update listener for the MediaPlayer object. This listener will
//            // be constantly invoked as the song is being buffered.
//            if (musicItem.getMusicUrl() != null) {
//                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                    @Override
//                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//
//                        // Invokes the associated listener call.
//                        if (musicListener != null) {
//                            musicListener.onMusicBufferingUpdate(musicItem, percent);
//                        }
//
//                        Log.d(LOG_TAG, "MUSIC: initMusic(): Music buffering at: " + percent);
//                    }
//                });
//            }

            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: initMusic(): Music could not be played.");
            return false;
        }
    }

    public boolean playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            return true;
        } else {
            return false;
        }
    }

    // pauseMusic(): Pauses any music playing in the background.
    public int pauseMusic() {

        // Checks to see if the MediaPlayer object has been initialized first before retrieving the
        // current music position and pausing the music.
        if (mediaPlayer != null) {

            musicPosition = mediaPlayer.getCurrentPosition(); // Retrieves the current music position.

            // Pauses the music only if there is a music is currently playing.
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Pauses the music.

//                // Invokes the associated listener call.
//                if (hxMusicEngine.musicListener != null && hxMusicEngine.musicItem != null) {
//                    hxMusicEngine.musicListener.onMusicPause(hxMusicEngine.musicItem);
//                }

                Log.d(LOG_TAG, "MUSIC: pauseMusic(): Music playback has been paused.");
                return musicPosition;
            } else {
                Log.e(LOG_TAG, "ERROR: pauseMusic(): Music could not be paused.");
                return 0;
            }
        } else {
            Log.e(LOG_TAG, "ERROR: pauseMusic(): Music could not be paused.");
            return 0;
        }
    }

    // resumeMusic(): Resumes playback of the current music.
//    public boolean resumeMusic(Context context) {
//
//        if (context == null || context.getApplicationContext() == null) {
//            Log.e(LOG_TAG, "ERROR: resumeMusic(): Context cannot be null.");
//            return false;
//        } else if (hxMusicEngine == null) {
//            Log.e(LOG_TAG, "ERROR: resumeMusic(): Music could not be resumed.");
//            return false;
//        } else if (hxMusicEngine.musicStatus.equals(HXMusicStatus.PAUSED)) {
//            hxMusicEngine.playMusic(hxMusicEngine.musicItem, hxMusicEngine.musicPosition, hxMusicEngine.isLooping,
//                    context.getApplicationContext());
//
//            // Invokes the associated listener call.
//            if (hxMusicEngine.musicListener != null && hxMusicEngine.musicItem != null) {
//                hxMusicEngine.musicListener.onMusicResume(hxMusicEngine.musicItem);
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    //  stopMusic(): Stops any music playing in the background.
    public boolean stopMusic() {

        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Stops any music currently playing in the background.

            Log.d(LOG_TAG, "MUSIC: stopMusic(): Music playback has been stopped.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: stopMusic(): Cannot stop music, as MediaPlayer object is already null.");
            return false;
        }
    }

    /** MUSIC HELPER METHODS ___________________________________________________________________ **/

    // checkStatus(): Verifies if the HXMusicItem object is valid and is used to determine if
    // the specified music can be played or not.
    private boolean checkStatus(HXMusicItem music) {

//        if (musicStatus.equals(HXMusicStatus.DISABLED)) {
//            Log.e(LOG_TAG, "ERROR: checkMusicState(): Music has been currently disabled.");
//            return false;
//        }

        if (music == null) {
            Log.e(LOG_TAG, "ERROR: checkMusicState(): Music item was null.");
            return false;
        } else if (music.getMusicResource() == 0 && music.getMusicUrl() == null) {
            Log.e(LOG_TAG, "ERROR: checkMusicState(): No music resource or url was specified.");
            return false;
        } else if (musicItem != null && (musicItem.getMusicResource() == music.getMusicResource())) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.e(LOG_TAG, "ERROR: checkMusicState(): Specified song is already playing!");
                return false;
            }
        }

        return true;
    }

    // clear(): Releases resources held by the MediaPlayer object and clears this object. This
    // method should be called when the singleton object is no longer in use.
//    public void clear() {
//        if (hxMusicEngine != null) {
//            hxMusicEngine.release();
//            hxMusicEngine = null;
//        }
//    }

    // enable(): Used for enabling and disabling music playback.
    public static void enable(boolean isEnabled) {

        if (isEnabled) {
            //hxMusicEngine.musicStatus = HXMusicStatus.READY;
        } else {
            //hxMusicEngine.musicStatus = HXMusicStatus.DISABLED;
        }
    }

    // isPlaying(): Determines if a music is currently playing in the background.
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    // release(): Used to release the resources being used by the MediaPlayer object.
    public boolean release() {
        isPrepared = false;

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            Log.d(LOG_TAG, "RELEASE: release(): MediaPlayer object has been released.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: release(): MediaPlayer object is null and cannot be released.");
            return false;
        }
    }

    /** GET METHODS ____________________________________________________________________________ **/

    // getPosition(): Returns the current music position.
//    public static int getPosition() {
//        if (hxMusicEngine != null) {
//            return hxMusicEngine.musicPosition;
//        } else {
//            return 0;
//        }
//    }

    // getStatus(): Returns the current music status of this object.
//    public static String getStatus() {
//        if (hxMusicEngine != null) {
//            return hxMusicEngine.musicStatus.toString();
//        } else {
//            return HXMusicStatus.NOT_READY.toString();
//        }
//    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean getPrepared() {
        return isPrepared;
    }

    /** SET METHODS ____________________________________________________________________________ **/



    // setPosition(): Sets the current music position.
    public void setPosition(int position) {
        this.musicPosition = position;
    }

    public void setListener(HXMusicEngineListener listener) {
        this.musicEngineListener = listener;
    }

//    public void setListeners(MediaPlayer.OnPreparedListener preparedListener,
//                             MediaPlayer.OnBufferingUpdateListener bufferingListener,
//                             MediaPlayer.OnCompletionListener completionListener) {
//        mediaPlayer.setOnPreparedListener(preparedListener);
//        mediaPlayer.setOnBufferingUpdateListener(bufferingListener);
//        mediaPlayer.setOnCompletionListener(completionListener);
//    }

    public void setNextMediaPlayer(MediaPlayer nextMediaPlayer) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (mediaPlayer != null && nextMediaPlayer != null) {
                mediaPlayer.setNextMediaPlayer(nextMediaPlayer);
            }
        } else {
            // TODO: Log output stating this is only available on API 16+.
        }
    }
}