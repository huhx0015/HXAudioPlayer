package com.huhx0015.hxaudio.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import com.huhx0015.hxaudio.builder.HXMusicBuilder;
import com.huhx0015.hxaudio.model.HXMusicItem;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusic] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXMusic class is a singleton and wrapper class for the MediaPlayer object and
 *  serves as a way to simplify music access for applications. HXMusic is designed to play a single
 *  local audio stream at a time and is ideal for applications such as games.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXMusic {
    
    /** CLASS VARIABLES ________________________________________________________________________ **/

    // INSTANCE VARIABLES:
    private static HXMusic hxMusic; // Instance variable for HXMusic.

    // AUDIO VARIABLES:
    private boolean isLooping; // Used to determine if the current music has looping enabled or not.
    private int musicPosition; // Used for tracking the current music position.
    private HXMusicItem musicItem; // References the current HXMusicItem that stores information about the current music.
    private HXMusicStatus musicStatus = HXMusicStatus.READY; // Used to determine the current status of the music.
    private MediaPlayer mediaPlayer; // MediaPlayer object used for playing back the current music.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXMusic.class.getSimpleName(); // Used for logging output to logcat.

    /** ENUM ___________________________________________________________________________________ **/

    private enum HXMusicStatus {
        READY,
        PLAYING,
        PAUSED,
        STOPPED,
        DISABLED,
        RELEASED
    }

    /** INSTANCE METHOD ________________________________________________________________________ **/

    // instance(): Returns the hxMusic instance.
    public static HXMusic instance() {
        if (hxMusic == null) {
            hxMusic = new HXMusic();
        }
        return hxMusic;
    }

    /** BUILDER METHOD _________________________________________________________________________ **/

    // music(): The main builder method used for constructing a HXMusicBuilder object for use with
    // the HXMusic class.
    public static HXMusicBuilder music() {
        instance();
        return new HXMusicBuilder();
    }

    /** MUSIC ACTION METHODS ___________________________________________________________________ **/

    // playMusic(): Plays the specified music with the given position and isLooped parameters.
    public boolean playMusic(HXMusicItem music, int position, boolean isLooped, Context context) {

        if (checkMusicStatus(music)) {

            this.musicItem = music;
            this.musicPosition = position;
            this.isLooping = isLooped;

            // Stops any music currently playing in the background.
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.d(LOG_TAG, "PREPARING: playMusic(): Song currently playing in the background. Stopping playback before switching to a new song.");
                mediaPlayer.stop();
            }

            // Sets up the MediaPlayer object for the music to be played.
            release(); // Releases MediaPool resources.
            mediaPlayer = new MediaPlayer(); // Initializes the MediaPlayer.
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.

            Log.d(LOG_TAG, "PREPARING: playMusic(): MediaPlayer stream type set to STREAM_MUSIC.");

            // Sets up the MediaPlayer for the music.
            mediaPlayer = MediaPlayer.create(context, musicItem.getMusicResource());
            mediaPlayer.setLooping(isLooped);

            Log.d(LOG_TAG, "PREPARING: playMusic(): Loop condition has been set to " + isLooped + ".");

            if (musicPosition != 0) {
                mediaPlayer.seekTo(musicPosition);
            }

            // Sets up the prepared listener for the MediaPlayer object. Music playback begins
            // immediately once the MediaPlayer object is ready.
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.d(LOG_TAG, "MUSIC: playMusic(): Music playback has begun.");
                    mediaPlayer.start(); // Begins playing the music.
                    musicStatus = HXMusicStatus.PLAYING;
                }
            });

            // Sets up a completion listener for the MediaPlayer object. Music position resets back
            // to 0 when music is completed.
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(LOG_TAG, "MUSIC: playMusic(): Music playback has completed.");
                    musicPosition = 0;
                    musicStatus = HXMusicStatus.STOPPED;
                }
            });
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: playMusic(): Music could not be played.");
            return false;
        }
    }

    // pauseMusic(): Pauses any music playing in the background.
    public static boolean pauseMusic() {

        // Checks to see if the MediaPlayer object has been initialized first before retrieving the
        // current music position and pausing the music.
        if (hxMusic != null && hxMusic.mediaPlayer != null) {

            hxMusic.musicPosition = hxMusic.mediaPlayer.getCurrentPosition(); // Retrieves the current music position.

            // Pauses the music only if there is a music is currently playing.
            if (hxMusic.mediaPlayer != null && hxMusic.mediaPlayer.isPlaying()) {
                hxMusic.mediaPlayer.pause(); // Pauses the music.
            }

            hxMusic.musicStatus = HXMusicStatus.PAUSED;  // Indicates that the music is currently paused.
            Log.d(LOG_TAG, "MUSIC: pauseMusic(): Music playback has been paused.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: pauseMusic(): Music could not be paused.");
            return false;
        }
    }

    // resumeMusic(): Resumes playback of the current music.
    public static boolean resumeMusic(Context context) {

        if (context == null || context.getApplicationContext() == null) {
            Log.e(LOG_TAG, "ERROR: resumeMusic(): Context cannot be null.");
            return false;
        } else if (hxMusic == null) {
            Log.e(LOG_TAG, "ERROR: resumeMusic(): Music could not be resumed.");
            return false;
        } else {
            hxMusic.playMusic(hxMusic.musicItem, hxMusic.musicPosition, hxMusic.isLooping,
                    context.getApplicationContext());
            return true;
        }
    }

    //  stopMusic(): Stops any music playing in the background.
    public static boolean stopMusic() {

        if (hxMusic != null && hxMusic.mediaPlayer != null) {
            hxMusic.mediaPlayer.stop(); // Stops any music currently playing in the background.
            hxMusic.musicStatus = HXMusicStatus.STOPPED;
            Log.d(LOG_TAG, "MUSIC: stopMusic(): Music playback has been stopped.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: stopMusic(): Cannot stop music, as MediaPlayer object is already null.");
            return false;
        }
    }

    /** MUSIC HELPER METHODS ___________________________________________________________________ **/

    // checkMusicStatus(): Verifies if the HXMusicItem object is valid and is used to determine if
    // the specified music can be played or not.
    private boolean checkMusicStatus(HXMusicItem music) {

        if (musicStatus.equals(HXMusicStatus.DISABLED)) {
            Log.e(LOG_TAG, "ERROR: checkMusicState(): Music has been currently disabled.");
            return false;
        } else if (music == null || music.getMusicResource() == 0) {
            Log.e(LOG_TAG, "ERROR: checkMusicState(): Music item was null or an invalid audio resource was set.");
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
    public static void clear() {
        if (hxMusic != null) {
            hxMusic.release();
            hxMusic = null;
        }
    }

    // enableMusic(): Used for enabling and disabling music playback.
    public static void enableMusic(boolean isEnabled) {
        instance();

        if (isEnabled) {
            hxMusic.musicStatus = HXMusicStatus.READY;
        } else {
            hxMusic.musicStatus = HXMusicStatus.DISABLED;
        }
    }

    // isMusicPlaying(): Determines if a music is currently playing in the background.
    public static boolean isMusicPlaying() {
        return hxMusic != null && hxMusic.mediaPlayer != null && hxMusic.mediaPlayer.isPlaying();
    }

    // release(): Used to release the resources being used by the MediaPlayer object.
    private boolean release() {

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            musicStatus = HXMusicStatus.RELEASED;
            Log.d(LOG_TAG, "RELEASE: release(): MediaPlayer object has been released.");
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: release(): MediaPlayer object is null and cannot be released.");
            return false;
        }
    }

    /** GET METHODS ____________________________________________________________________________ **/

    // getMusicPosition(): Returns the current music position.
    public static int getSongPosition() {
        instance();
        return hxMusic.musicPosition;
    }

    // getMusicStatus(): Returns the current music status of this object.
    public static String getMusicStatus() {
        instance();
        return hxMusic.musicStatus.toString();
    }

    /** SET METHODS ____________________________________________________________________________ **/

    // setMusicPosition(): Sets the current music position.
    public static void setSongPosition(int songPosition) {
        instance();
        hxMusic.musicPosition = songPosition;
    }
}