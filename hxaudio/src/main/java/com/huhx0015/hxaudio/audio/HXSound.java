package com.huhx0015.hxaudio.audio;

import android.content.Context;
import com.huhx0015.hxaudio.builder.HXSoundBuilder;
import com.huhx0015.hxaudio.utils.HXLog;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** -----------------------------------------------------------------------------------------------
 *  [HXSound] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class manages HXSoundEngine instance(s) and routes all sound effect
 *  functionality to the active engine.
 *  -----------------------------------------------------------------------------------------------
 */
public class HXSound {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // INSTANCE VARIABLES:
    private static volatile HXSound hxSound; // HXSound instance variable.

    // AUDIO VARIABLES:
    private boolean isEnabled = true; // Used for determining if the sound system is enabled or not.
    private volatile int currentEngine; // Used for determining the active HXSoundEngine instance.
    private int numberOfEngines = NUMBER_OF_ENGINES; // Used for determining the number of HXSoundEngine instances.
    private List<HXSoundEngine> hxSoundEngines; // List which contains the HXSoundEngine instances.
    private ExecutorService operationExecutor; // Serializes sound operations off the caller thread.

    // CONSTANT VARIABLES:
    private static final int NUMBER_OF_ENGINES = 1; // API 21+ baseline supports a single engine.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXSound.class.getSimpleName();

    /** INSTANCE METHOD ________________________________________________________________________ **/

    // instance(): Returns the hxSound instance.
    public static HXSound instance() {
        if (hxSound == null) {
            synchronized (HXSound.class) {
                if (hxSound == null) {
                    hxSound = new HXSound();
                }
            }
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

    // initSoundEngines(): Initializes the HXSoundEngine instances.
    private void initSoundEngines() {

        this.currentEngine = 0; // Sets the current engine instance to 0.

        if (hxSoundEngines == null) {
            hxSoundEngines = new ArrayList<>(numberOfEngines);
        } else {
            hxSoundEngines.clear();
        }

        HXLog.d(LOG_TAG, "BUILD: Building " + numberOfEngines + " HXSoundEngine instances...");

        // Initializes and adds HXSoundEngine instances.
        for (int i = 0; i < numberOfEngines; i++) {
            hxSoundEngines.add(new HXSoundEngine(i));
        }

        HXLog.d(LOG_TAG, "BUILD: All HXSoundEngines are ready.");
    }

    // reinitialize(): Compatibility API that reinitializes SoundPool objects and reloads cached sounds.
    @Deprecated(since = "4.0", forRemoval = false)
    public static void reinitialize(final Context context) {
        HXLog.w(LOG_TAG, "RE-INITIALIZING: reinitialize() is retained for compatibility; use only for explicit SoundPool reset needs.");

        // Checks if the context is null.
        if (context == null || context.getApplicationContext() == null) {
            HXLog.e(LOG_TAG, "ERROR: reinitialize(): Context cannot be null.");
            return;
        }
        instance(); // Checks the instance to ensure that hxSound is not null.

        if (hxSound.hxSoundEngines == null) {
            hxSound.initSoundEngines();
        }

        HXLog.d(LOG_TAG, "RE-INITIALIZING: Re-initializing HXSoundEngine instances.");
        hxSound.submitOperation(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < hxSound.numberOfEngines; i++) {
                    hxSound.hxSoundEngines.get(i).reinitialize(context.getApplicationContext());
                    HXLog.d(LOG_TAG, "RE-INITIALIZING: HXSoundEngine (" + i + ") reinitialized.");
                }
            }
        });
    }

    /** SOUND ACTION METHODS ___________________________________________________________________ **/

    // initSound(): Prepares the sound engines to play the specified sound effect.
    public synchronized boolean initSound(int resource, boolean isLooped, Context context) {

        if (resource == 0) {
            HXLog.e(LOG_TAG, "ERROR: prepareSoundFx(): Invalid sound resource was set.");
            return false;
        }

        if (isEnabled) {

            if (hxSoundEngines == null) {
                initSoundEngines();
            }

            HXLog.d(LOG_TAG, "SOUND: Attempting to play sound effect on HXSoundEngine (" + currentEngine + ")...");
            hxSoundEngines.get(currentEngine).prepareSoundFx(resource, isLooped, context);

            // Sets the currentEngine value to point to the next HXSoundEngine instance.
            if (numberOfEngines > 1) {
                currentEngine++;

                // Resets the currentEngine value to alternate sound playback between the number of
                // engines available.
                if (currentEngine == numberOfEngines) { currentEngine = 0; }

                HXLog.d(LOG_TAG, "SOUND: HXSoundEngine (" + currentEngine + ") is now the active instance.");
            }
            return true;
        } else {
            HXLog.e(LOG_TAG, "ERROR: prepareSoundFx(): Sound is currently disabled.");
            return false;
        }
    }

    // initSoundAsync(): Schedules initSound() on the audio operations executor.
    public void initSoundAsync(final int resource, final boolean isLooped, final Context context) {
        submitOperation(new Runnable() {
            @Override
            public void run() {
                initSound(resource, isLooped, context);
            }
        });
    }

    // pause(): Pauses all sound effect playback in all HXSoundEngine instances.
    public static void pause() {

        // Pauses sound effect playback in all HXSoundEngine instances.
        if (hxSound != null && hxSound.hxSoundEngines != null) {

            HXLog.d(LOG_TAG, "PAUSE: Pausing sound playback on all HXSoundEngine instances...");

            int i = 0;
            for (HXSoundEngine engine : hxSound.hxSoundEngines) {
                engine.pauseSounds();
                HXLog.d(LOG_TAG, "PAUSE: HXSoundEngine (" + i + ") is paused.");
                i++;
            }
        } else {
            HXLog.e(LOG_TAG, "ERROR: pauseSounds(): Could not pause sound effects.");
        }
    }

    // resume(): Resumes all sound effect playback in all HXSoundEngine instances.
    public static void resume() {

        if (hxSound != null && hxSound.hxSoundEngines != null) {

            HXLog.d(LOG_TAG, "RESUME: Resuming sound playback on all HXSoundEngine instances...");

            int i = 0;
            for (HXSoundEngine engine : hxSound.hxSoundEngines) {
                engine.resumeSounds();
                HXLog.d(LOG_TAG, "RESUME: HXSoundEngine (" + i + ") is resumed.");
                i++;
            }
        } else {
            HXLog.e(LOG_TAG, "ERROR: resumeSounds(): Could not resume sound effect playback.");
        }
    }

    /** SOUND HELPER METHODS ___________________________________________________________________ **/

    // play(): Convenience helper for one-shot sound playback.
    public static void play(Context context, int resource) {
        HXSound.sound()
                .load(resource)
                .play(context);
    }

    // play(): Convenience helper for looped/non-looped sound playback.
    public static void play(Context context, int resource, boolean looped) {
        HXSound.sound()
                .load(resource)
                .looped(looped)
                .play(context);
    }

    // load(): Loads the referenced list of sound resources into the HXSoundEngine(s).
    public static void load(final List<Integer> soundResourceList, final Context context) {

        // Checks if the context is null.
        if (context == null || context.getApplicationContext() == null) {
            HXLog.e(LOG_TAG, "ERROR: load(): Context cannot be null.");
            return;
        }
        if (soundResourceList == null || soundResourceList.isEmpty()) {
            HXLog.e(LOG_TAG, "ERROR: load(): Sound resource list cannot be null or empty.");
            return;
        }

        // Initializes hxSound and hxSoundEngines, if not already initialized.
        instance();
        if (hxSound.hxSoundEngines == null) {
            hxSound.initSoundEngines();
        }

        hxSound.submitOperation(new Runnable() {
            @Override
            public void run() {
                // Loads the list of sound resources into the HXSoundEngine's SoundPool objects.
                for (int i = 0; i < hxSound.numberOfEngines; i++) {
                    hxSound.hxSoundEngines.get(i).loadSoundFxList(soundResourceList, context.getApplicationContext());
                    HXLog.d(LOG_TAG, "LOADING: Loading HXSoundEngine (" + i + ") with list of sound resources.");
                }
            }
        });
    }

    // clear(): Releases resources held by this singleton and other objects associated with this
    // object. This method should be called when the singleton object is no longer in use.
    public static void clear() {
        if (hxSound != null) {
            if (hxSound.hxSoundEngines != null) {
                hxSound.release();
            }
            hxSound.shutdownExecutor();
            hxSound = null;
        }
    }

    // enable(): Used to enable or disable the HXSound system.
    public static void enable(boolean isEnabled) {
        instance();
        hxSound.isEnabled = isEnabled;
    }

    // engines(): Compatibility API retained for older integrations; ignored on API 21+.
    @Deprecated(since = "4.0", forRemoval = false)
    public static void engines(int engines) {
        HXLog.w(LOG_TAG, "PREPARING: engines(" + engines + "): Ignored. HXSound uses a single engine on API 21+.");
    }

    // logging(): Enables logging for HXSound and HXSoundEngine events.
    public static void logging(boolean isEnabled) {
        HXLog.setLogging(isEnabled);
    }

    // release(): Used to free up memory resources utilized by all HXSoundEngine instances.
    private void release() {

        HXLog.d(LOG_TAG, "RELEASE: release(): Releasing all HXSoundEngine instances...");

        // Releases all HXSoundEngine instances.
        int i = 0;
        for (HXSoundEngine engine : hxSoundEngines) {
            engine.release();
            HXLog.d(LOG_TAG, "RELEASE: release(): HXSoundEngine (" + i + ") is released.");
            i++;
        }
        hxSoundEngines = null;
    }

    private synchronized void submitOperation(Runnable operation) {
        ensureExecutor();
        operationExecutor.execute(operation);
    }

    private synchronized void ensureExecutor() {
        if (operationExecutor == null || operationExecutor.isShutdown()) {
            operationExecutor = Executors.newSingleThreadExecutor();
        }
    }

    private synchronized void shutdownExecutor() {
        if (operationExecutor != null) {
            operationExecutor.shutdownNow();
            operationExecutor = null;
        }
    }
}