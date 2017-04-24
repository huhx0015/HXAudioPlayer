package com.huhx0015.hxaudio.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import com.huhx0015.hxaudio.builder.HXMusicBuilder;
import com.huhx0015.hxaudio.interfaces.HXMusicEngineListener;
import com.huhx0015.hxaudio.interfaces.HXMusicListener;
import com.huhx0015.hxaudio.model.HXMusicItem;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Michael Yoon Huh on 4/23/2017.
 */

public class HXMusic implements HXMusicEngineListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private Context context;

    // AUDIO VARIABLES:
    private boolean isGapless; // Used to determine if gapless mode has been enabled or not.
    private boolean isLooped; // Used to determine if the current music has looping enabled or not.
    private int currentEngine; // Used for determining the active HXMusicEngine instance.
    private int musicPosition; // Used for tracking the current music position.
    private int numberOfEngines; // Used for determining the number of HXMusicEngine instances.
    private HXMusicItem musicItem; // References the current HXMusicItem that stores information about the current music.

    //private HashMap<Integer, HXMusicEngine> hxMusicEngines;

    private HXMusicEngine hxPrimaryEngine;
    private HXMusicEngine hxSecondaryEngine;


    private HXMusicStatus musicStatus = HXMusicStatus.NOT_READY; // Used to determine the current status of the music.
    private LinkedList<HXMusicEngine> hxMusicEngines; // LinkedList object which contains the HXMusicEngine instances.


    // INSTANCE VARIABLES:
    private static HXMusic hxMusic; // Instance variable for HXMusic.


    // LISTENER VARIABLES:
    private HXMusicListener musicListener; // Interface for listening for events from the MediaPlayer object.


    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXMusic.class.getSimpleName(); // Used for logging output to logcat.

    // CONSTANT VARIABLES:
    private static final int NUMBER_OF_ENGINES_STANDARD = 1;
    private static final int NUMBER_OF_ENGINES_GAPLESS = 2;


    /** ENUM ___________________________________________________________________________________ **/

    private enum HXMusicStatus {
        NOT_READY,
        READY,
        PLAYING,
        PAUSED,
        STOPPED,
        DISABLED,
        RELEASED
    }

    public enum HXMusicEngineId {
        PRIMARY,
        SECONDARY
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
    // the HXMusicEngine class.
    public static HXMusicBuilder music() {
        instance();
        return new HXMusicBuilder();
    }

    /** MUSIC METHODS __________________________________________________________________________ **/

    // initMusic(): Prepares the MediaPlayer objects for music playback with the specified music
    // parameters.
    public synchronized void prepareMusic(HXMusicItem music, final int position,
                                             final boolean isLooped, boolean isGappless, Context context) {

        this.context = context;

        this.musicItem = music;
        this.musicPosition = position;
        this.isGapless = isGappless;
        this.isLooped = isLooped;

        // Initializes the HXMusicEngine instances. If previously initialized, the instances are
        // released.
        if (hxMusicEngines != null) {
            for (HXMusicEngine musicEngine : hxMusicEngines) {
                musicEngine.release();
            }
        }
        hxMusicEngines = new LinkedList<>();

        // Sets the number of HXMusicEngines to initialize, depending on if gapless mode has been
        // enabled or not. Gappless mode is only available on devices running on Android API 16+.
        numberOfEngines = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isGappless &&
                isLooped ? NUMBER_OF_ENGINES_GAPLESS : NUMBER_OF_ENGINES_STANDARD;

        // Initializes and adds HXMusicEngine instances to the LinkedList.
        int i = 0;
        for (int x : new int[numberOfEngines]) {
            HXMusicEngine musicEngine = new HXMusicEngine(i);
            musicEngine.initMusic(music, position, isLooped, context);
            musicEngine.setListener(this);
            hxMusicEngines.add(musicEngine);
            i++;
        }

        currentEngine = 0;
    }

    public static boolean pauseMusic() {

        if (hxMusic == null) {
            return false;
        } else if (hxMusic.hxMusicEngines != null) {
            hxMusic.musicPosition = hxMusic.hxMusicEngines.get(hxMusic.currentEngine).pauseMusic();
            hxMusic.musicStatus = HXMusicStatus.PAUSED;  // Indicates that the music is currently paused.

            // Invokes the associated listener call.
            if (hxMusic.musicListener != null && hxMusic.musicItem != null) {
                hxMusic.musicListener.onMusicPause(hxMusic.musicItem);
            }
            return true;
        } else {
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
        } else if (hxMusic.musicStatus.equals(HXMusicStatus.PAUSED) && hxMusic.hxMusicEngines != null) {
            boolean resumeMusicState = hxMusic.hxMusicEngines.get(hxMusic.currentEngine).initMusic(
                    hxMusic.musicItem, hxMusic.musicPosition, hxMusic.isLooped, context.getApplicationContext());

            if (resumeMusicState) {

                // Invokes the associated listener call.
                if (hxMusic.musicListener != null && hxMusic.musicItem != null) {
                    hxMusic.musicListener.onMusicResume(hxMusic.musicItem);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    //  stopMusic(): Stops any music playing in the background.
    public static boolean stopMusic() {

        if (hxMusic != null && hxMusic.hxMusicEngines != null) {
            boolean stopMusicState = hxMusic.hxMusicEngines.get(hxMusic.currentEngine).stopMusic();

            if (stopMusicState) {
                hxMusic.musicStatus = HXMusicStatus.STOPPED;

                // Invokes the associated listener call.
                if (hxMusic.musicListener != null && hxMusic.musicItem != null) {
                    hxMusic.musicListener.onMusicStop(hxMusic.musicItem);
                }

                Log.d(LOG_TAG, "MUSIC: stopMusic(): Music playback has been stopped.");
                return true;
            } else {
                return false;
            }
        } else {
            Log.e(LOG_TAG, "ERROR: stopMusic(): Cannot stop music, as MediaPlayer object is already null.");
            return false;
        }
    }

    // isPlaying(): Determines if a music is currently playing in the background.
    public static boolean isPlaying() {
        return hxMusic.hxMusicEngines != null && hxMusic.hxMusicEngines.get(hxMusic.currentEngine).isPlaying();
    }


    @Override
    public void onMusicEnginePrepared(MediaPlayer mp, int id) {
        musicStatus = HXMusicStatus.READY;

        if (musicPosition != 0) {
            mp.seekTo(musicPosition);
            Log.d(LOG_TAG, "PREPARED: MediaPlayer position set to: " + musicPosition);
        }

        // GAPLESS: If gapless mode is not set and looping mode is enabled, the song will be looped
        // using MediaPlayer's native setLooping() method (may cause skipping or gaps). If gapless
        // mode is enabled, the secondary MediaPlayer will begin immediate playback after playback
        // on the current MediaPlayer has completed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isGapless && isLooped) {
            mp.setLooping(false); // Disables looping attribute.

            // Sets the next MediaPlayer object to be played.
            int nextMediaPlayerId = id + 1 >= hxMusicEngines.size() ? 0 : id + 1;

            if (id == 0) {
                nextMediaPlayerId = 1;
            } else {
                nextMediaPlayerId = 0;
            }


            Log.d(LOG_TAG, "PREPARED: Gapless mode prepared for HXMusicEngine (" + id + "), with HXMusicEngine (" + nextMediaPlayerId + " set for next playback.");

            mp.setNextMediaPlayer(hxMusicEngines.get(nextMediaPlayerId).getMediaPlayer());

        } else {
            mp.setLooping(isLooped); // Sets the looping attribute.
        }

        Log.d(LOG_TAG, "PREPARED: MediaPlayer looping status: " + isLooped);

        // If all of the HXMusicEngines are prepared, music playback is started.
        boolean isReady = false;
        for (HXMusicEngine engine : hxMusicEngines) {
            isReady = engine.getPrepared();
        }

        if (isReady) {

            hxMusicEngines.get(0).getMediaPlayer().start();
            //mp.start(); // Begins playing the music.
            musicStatus = HXMusicStatus.PLAYING;

            // Invokes the associated listener call.
            if (musicListener != null) {
                musicListener.onMusicPrepared(musicItem);
            }

            Log.d(LOG_TAG, "PLAYING: Music playback has begun.");
        }
    }

    @Override
    public void onMusicEngineCompletion(final int id) {

        if (isGapless) {

            // Sets the next MediaPlayer object to be played.
            final int nextMediaPlayerId = id + 1 >= hxMusicEngines.size() ? 0 : id + 1;
            hxMusicEngines.get(id).prepareMusic(context);
            hxMusicEngines.get(id).setListener(this);
            hxMusicEngines.get(id).getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    int nextMediaPlayerId;
                    if (id == 0) {
                        nextMediaPlayerId = 1;
                    } else {
                        nextMediaPlayerId = 0;
                    }

                    mp.setNextMediaPlayer(null);
                    //mp.setNextMediaPlayer(hxMusicEngines.get(nextMediaPlayerId).getMediaPlayer());
                }
            });

            Log.d(LOG_TAG, "MUSIC: onMusicEngineCompletion(): Music playback has completed on HXMusicEngine ("
                    + id + "), starting playback on HXMusicEngine (" + nextMediaPlayerId + ").");

        } else {

            musicPosition = 0;
            musicStatus = HXMusicStatus.STOPPED;

            // Invokes the associated listener call.
            if (musicListener != null) {
                musicListener.onMusicCompletion(musicItem);
            }

            Log.d(LOG_TAG, "MUSIC: onMusicEngineCompletion(): Music playback has completed on HXMusicEngine (" + id + ").");
        }

    }

    @Override
    public void onMusicEngineBufferingUpdate(int id, int percent) {
        // Invokes the associated listener call.
        if (musicListener != null) {
            musicListener.onMusicBufferingUpdate(musicItem, percent);
        }

        Log.d(LOG_TAG, "MUSIC: initMusic(): Music buffering at: " + percent);
    }


    // setListener(): Sets the HXMusicListener interface for this class.
    public static void setListener(HXMusicListener listener) {
        instance();

        hxMusic.musicListener = listener;
    }
}
