package com.huhx0015.hxgselib.audio;

import android.content.Context;
import android.util.Log;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSESoundHandler] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class is used to create multiple instances of HXGSESoundEngine, as well as to
 *  help minimize the SoundPool limitations present in Android 2.3.7 and less. All sound effect
 *  functionality is now handled by HXGSESoundHandler, where sound effect functionality is
 *  re-directed towards the HXGSESoundEngine instances.
 *  -----------------------------------------------------------------------------------------------
 */
public class HXGSESoundHandler {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    public boolean soundOn; // Used for determining if sound option is enabled or not.
    private int currentEngine; // Used for determining the active HXGSESoundEngine instance.
    private int numberOfEngines; // Used for determining the number of HXGSESoundEngine instances.
    private LinkedList<HXGSESoundEngine> hxgse_sound_engines; // LinkedList object which contains the HXGSESoundEngine instances.

    // SYSTEM VARIABLES:
    private Context context; // Context for the instance in which this class is used.
    private final int api_level = android.os.Build.VERSION.SDK_INT; // Used to determine the device's Android API version.
    private static final String TAG = HXGSESoundHandler.class.getSimpleName(); // Used for logging output to logcat.

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // HXGSESoundHandler(): Constructor for HXGSESoundHandler class.
    private final static HXGSESoundHandler hgsxe_handler = new HXGSESoundHandler();

    // HXGSESoundHandler(): Deconstructor for HXGSESoundHandler class.
    private HXGSESoundHandler() {}

    // getInstance(): Returns the hgsxe_handler instance.
    public static HXGSESoundHandler getInstance() { return hgsxe_handler; }

    // initializeAudio(): Initializes the HXGSESoundHandler class variables.
    public void initializeAudio(Context con, int engineInstances) {

        context = con; // Context for the instance in which this class is used.
        currentEngine = 0; // Sets the current engine instance to 0.
        soundOn = true; // Sets the sound flag to be enabled by default.

        // ANDROID 3.0 - ANDROID 5.1: Only one HXGSESoundEngine instance is built, as Android 3.0
        // and higher are not subject to the same SoundPool audio bugs present on Android 2.3.7 and
        // earlier.
        if (api_level > 11) {
            engineInstances = 1;
            Log.d(TAG, "BUILD: Android API 12 or greater detected. Only one HXGSESoundEngine instance will be built.");
        }

        // Initially checks to see if a proper engineInstances value has been inputted.
        if (engineInstances <= 0) {
            engineInstances = 1;
            Log.d(TAG, "WARNING: An invalid engineInstances value (" + engineInstances + ") has been specified. Only one HXGSESoundEngine instance will be built.");
        }

        numberOfEngines = engineInstances; // Sets the number of HXGSESoundEngine instances.

        // LinkedList object which contains the HXGSESoundEngine instances.
        hxgse_sound_engines = new LinkedList<>();

        Log.d(TAG, "BUILD: Building " + engineInstances + " HXGSESoundEngine instances...");

        // Initializes and adds HXGSESoundEngine instances to the LinkedList.
        for (int i = 0; i < engineInstances; i++) {
            HXGSESoundEngine hxgse_sound_engine = new HXGSESoundEngine();
            hxgse_sound_engine.initializeAudio(con, i);
            hxgse_sound_engines.add(hxgse_sound_engine);
            Log.d(TAG, "BUILD: HXGSESoundEngine (" + i + ") is ready.");
        }

        Log.d(TAG, "BUILD: All HXGSESoundEngines are ready.");
    }

    /** SOUND FUNCTIONALITY ____________________________________________________________________ **/

    // playSoundFX(): This is a function that directs the playing of the specified sound effect to
    // the HXGSESoundEngines.
    public int playSoundFx(String sfx, final int loop) {

        int soundID = 0;

        // Processes and plays the sound effect only if soundOn variable is set to true.
        if (soundOn == true) {

            Log.d(TAG, "SOUND: Attempting to play Sound effect on HXGSESoundEngine (" + currentEngine + ")...");
            soundID = hxgse_sound_engines.get(currentEngine).playSoundFx(sfx, loop);

            // Sets the currentEngine value to point to the next HXGSESoundEngine instance.
            currentEngine++;

            // Resets the currentEngine value back to 0 if the
            if (currentEngine == numberOfEngines) { currentEngine = 0; }

            Log.d(TAG, "SOUND: HXGSESoundEngine (" + currentEngine + ") is now the active instance.");
        }

        // Outputs an error message to logcat, indicating that the sound effect could not be played
        // and that the sound handler is disabled.
        else {
            Log.d(TAG, "ERROR: Sound effect could not be played due to the sound handler being disabled.");
        }

        return soundID;
    }

    // pauseSounds(): Pauses all sound effect playback in all HXGSESoundEngine instances.
    public void pauseSounds() {

        Log.d(TAG, "PAUSE: Pausing sound playback on all HXGSESoundEngine instances...");

        // Pauses sound effect playback in all HXGSESoundEngine instances.
        for (int i = 0; i < numberOfEngines; i++) {
            hxgse_sound_engines.get(i).pauseSounds();
            Log.d(TAG, "PAUSE: HXGSESoundEngine (" + i + ") is paused.");
        }
    }

    // resumeSounds(): Resumes all sound effect playback in all HXGSESoundEngine instances.
    public void resumeSounds() {

        Log.d(TAG, "RESUME: Resuming sound playback on all HXGSESoundEngine instances...");

        // Resumes sound effect playback in all HXGSESoundEngine instances.
        for (int i = 0; i < numberOfEngines; i++) {
            hxgse_sound_engines.get(i).resumeSounds();
            Log.d(TAG, "PAUSE: HXGSESoundEngine (" + i + ") is resumed.");
        }
    }

    // reinitializeSoundPool(): This method re-initializes all SoundPool objects for devices running
    // on Android 2.3 (GINGERBREAD) and earlier. This is to help minimize the AudioTrack out of
    // memory error, which was limited to a small 1 MB size buffer.
    public void reinitializeSoundPool() {

        // GINGERBREAD: The SoundPool is released and re-initialized. This is done to minimize the
        // AudioTrack out of memory (-12) error.
        if (api_level < 11) {

            Log.d(TAG, "RE-INITIALIZING: The HXGSESoundEngine instances are being re-initialized.");

            // Resumes sound effect playback in all HXGSESoundEngine instances.
            for (int i = 0; i < numberOfEngines; i++) {
                hxgse_sound_engines.get(i).reinitializeSoundPool();
                Log.d(TAG, "RE-INITIALIZING: HXGSESoundEngine (" + i + ") is re-initialized.");
            }
        }
    }

    // releaseSound(): Used to free up memory resources utilized by all HXGSESoundEngine instances.
    public void releaseSound() {

        Log.d(TAG, "RELEASE: Releasing all HXGSESoundEngine instances...");

        // Releases all HXGSESoundEngine instances.
        for (int i = 0; i < numberOfEngines; i++) {
            hxgse_sound_engines.get(i).releaseSound();
            Log.d(TAG, "RELEASE: HXGSESoundEngine (" + i + ") is released.");
        }
    }
}
