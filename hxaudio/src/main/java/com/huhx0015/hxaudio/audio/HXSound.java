package com.huhx0015.hxaudio.audio;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.huhx0015.hxaudio.builder.HXSoundBuilder;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [HXSound] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class is used to create multiple instances of HXSoundEngine, as well as to
 *  help minimize the SoundPool limitations present in Android 2.3.7 and less. All sound effect
 *  functionality is now handled by HXSound, where sound effect functionality is re-directed towards
 *  the HXSoundEngine instances.
 *  -----------------------------------------------------------------------------------------------
 */
public class HXSound {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // INSTANCE VARIABLES:
    private static HXSound hxSound; // HXSound instance variable.

    // AUDIO VARIABLES:
    private boolean isEnabled; // Used for determining if the sound system is enabled or not.
    private int currentEngine; // Used for determining the active HXSoundEngine instance.
    private int numberOfEngines; // Used for determining the number of HXSoundEngine instances.
    private HXSoundStatus soundStatus = HXSoundStatus.NOT_READY; // Used to determine the current status of the sound system.
    private LinkedList<HXSoundEngine> hxSoundEngines; // LinkedList object which contains the HXSoundEngine instances.

    // CONSTANT VARIABLES:
    private static final int NUMBER_OF_ENGINES_GB = 2; // Number of sound engines for GINGERBREAD.
    private static final int NUMBER_OF_ENGINES_HC = 1; // Number of sound engines for HONEYCOMB+.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXSound.class.getSimpleName();

    /** ENUM ___________________________________________________________________________________ **/

    private enum HXSoundStatus {
        NOT_READY,
        READY,
        RELEASED
    }

    /** INSTANCE METHOD ________________________________________________________________________ **/

    // instance(): Returns the hxSound instance.
    public static HXSound instance() {
        if (hxSound == null) {
            hxSound = new HXSound();
        }
        return hxSound;
    }

    /** BUILDER METHOD _________________________________________________________________________ **/

    // sound(): The main builder method used for constructing a HXSoundBuilder object for use with
    // the HXSound class.
    public static HXSoundBuilder sound() {
        instance();
        return new HXSoundBuilder();
    }

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // initializeSoundEngines(): Initializes the HXSoundEngine instances.
    private void initializeSoundEngines() {

        this.currentEngine = 0; // Sets the current engine instance to 0.

        // ANDROID 3.0+: Only one HXSoundEngine instance is built, as Android 3.0 and higher are
        // not subject to the same SoundPool audio bugs present on Android 2.3.7 and earlier.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            this.numberOfEngines = NUMBER_OF_ENGINES_HC;
            Log.d(LOG_TAG, "BUILD: Android API 11 or greater detected. Only one HXSoundEngine instances will be built.");
        } else {
            this.numberOfEngines = NUMBER_OF_ENGINES_GB;
            Log.d(LOG_TAG, "BUILD: Android API 10 or less detected. Two HXSoundEngine instances will be built.");
        }

        // LinkedList object which contains the HXSoundEngine instances.
        if (hxSoundEngines == null) {
            hxSoundEngines = new LinkedList<>();
        }

        Log.d(LOG_TAG, "BUILD: Building " + numberOfEngines + " HXSoundEngine instances...");

        // Initializes and adds HXSoundEngine instances to the LinkedList.
        int i = 0;
        for (int x : new int[numberOfEngines]) {
            HXSoundEngine soundEngine = new HXSoundEngine(i);
            hxSoundEngines.add(soundEngine);
            i++;
        }

        soundStatus = HXSoundStatus.READY;
        Log.d(LOG_TAG, "BUILD: All HXSoundEngines are ready.");
    }

    // reinitialize(): This method re-initializes all SoundPool objects for devices running
    // on Android 2.3 (GINGERBREAD) and earlier. This is to help minimize the AudioTrack out of
    // memory error, which was limited to a small 1 MB size buffer.
    public static void reinitialize() {
        instance();

        // GINGERBREAD: The SoundPool is released and re-initialized. This is done to minimize the
        // AudioTrack out of memory (-12) error.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {

            Log.d(LOG_TAG, "RE-INITIALIZING: The HXSoundEngine instances are being re-initialized.");

            // Resumes sound effect playback in all HXSoundEngine instances.
            int i = 0;
            for (int x : new int[hxSound.numberOfEngines]) {
                hxSound.hxSoundEngines.get(i).reinitialize();
                Log.d(LOG_TAG, "RE-INITIALIZING: HXSoundEngine (" + i + ") is re-initialized.");
                i++;
            }
        }
    }

    /** SOUND ACTION METHODS ___________________________________________________________________ **/

    // playSoundFx(): Attempts to play the sound effect via the HXSoundEngine instance(s).
    public boolean playSoundFx(int resource, boolean isLooped, Context context) {

        if (resource == 0) {
            Log.e(LOG_TAG, "ERROR: prepareSoundFx(): Invalid sound resource was set.");
            return false;
        }

        if (soundStatus.equals(HXSoundStatus.NOT_READY)) {
            initializeSoundEngines();
        }

        if (isEnabled) {

            Log.d(LOG_TAG, "SOUND: Attempting to play sound effect on HXSoundEngine (" + currentEngine + ")...");
            hxSoundEngines.get(currentEngine).prepareSoundFx(resource, isLooped, context);

            // Sets the currentEngine value to point to the next HXSoundEngine instance.
            if (numberOfEngines > 1) {
                currentEngine++;

                // Resets the currentEngine value to alternate sound playback between the number of
                // engines available.
                if (currentEngine == numberOfEngines) { currentEngine = 0; }

                Log.d(LOG_TAG, "SOUND: HXSoundEngine (" + currentEngine + ") is now the active instance.");
            }
            return true;
        } else {
            Log.e(LOG_TAG, "ERROR: prepareSoundFx(): Sound is currently disabled.");
            return false;
        }
    }

    // pauseSounds(): Pauses all sound effect playback in all HXSoundEngine instances.
    public static void pauseSounds() {
        instance();

        // Pauses sound effect playback in all HXSoundEngine instances.
        if (hxSound.hxSoundEngines != null) {

            Log.d(LOG_TAG, "PAUSE: Pausing sound playback on all HXSoundEngine instances...");

            int i = 0;
            for (int x : new int[hxSound.numberOfEngines]) {
                hxSound.hxSoundEngines.get(i).pauseSounds();
                Log.d(LOG_TAG, "PAUSE: HXSoundEngine (" + i + ") is paused.");
                i++;
            }
        } else {
            Log.e(LOG_TAG, "ERROR: pauseSounds(): Could not pause sound effects.");
        }
    }

    // resumeSounds(): Resumes all sound effect playback in all HXSoundEngine instances.
    public static void resumeSounds() {
        instance();

        // Resumes sound effect playback in all HXSoundEngine instances.
        if (hxSound.hxSoundEngines != null) {

            Log.d(LOG_TAG, "RESUME: Resuming sound playback on all HXSoundEngine instances...");

            int i = 0;
            for (int x : new int[hxSound.numberOfEngines]) {
                hxSound.hxSoundEngines.get(i).resumeSounds();
                Log.d(LOG_TAG, "RESUME: HXSoundEngine (" + i + ") is resumed.");
                i++;
            }
        } else {
            Log.e(LOG_TAG, "ERROR: resumeSounds(): Could not resume sound effect playback.");
        }
    }

    /** SOUND HELPER METHODS ___________________________________________________________________ **/

    // clear(): Releases resources held by this singleton and other objects associated with this
    // object. This method should be called when the singleton object is no longer in use.
    public static void clear() {
        if (hxSound != null) {
            hxSound.release();
            hxSound = null;
        }
    }

    // enable(): Used to enable or disable the HXSound system.
    public static void enable(boolean isEnabled) {
        instance();
        hxSound.isEnabled = isEnabled;
    }

    // release(): Used to free up memory resources utilized by all HXSoundEngine instances.
    private void release() {

        Log.d(LOG_TAG, "RELEASE: Releasing all HXSoundEngine instances...");

        // Releases all HXSoundEngine instances.
        int i = 0;
        for (int x : new int[numberOfEngines]) {
            hxSoundEngines.get(i).release();
            Log.d(LOG_TAG, "RELEASE: HXSoundEngine (" + i + ") is released.");
            i++;
        }

        soundStatus = HXSoundStatus.RELEASED;
    }
}