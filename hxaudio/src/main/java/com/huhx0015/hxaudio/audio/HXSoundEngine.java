package com.huhx0015.hxaudio.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import com.huhx0015.hxaudio.utils.HXLog;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** -----------------------------------------------------------------------------------------------
 *  [HXSoundEngine] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXSoundEngine class is a wrapper class for the SoundPool object, used for
 *  simplifying the outputting of sound effects for the application.
 *  -----------------------------------------------------------------------------------------------
 */

class HXSoundEngine {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    private volatile ConcurrentHashMap<Integer, Integer> soundEffectMap; // Used for storing the loaded sound effects.
    private volatile List<Integer> soundFxList; // Used for storing the referenced sound effects.
    private volatile SoundPool soundPool; // SoundPool variable for sound effects.
    private int engineID; // Used to determine the ID value of this instance.

    // CONSTANT VARIABLES:
    private static final int MAX_SIMULTANEOUS_SOUNDS = 8; // Can output eight sound effects simultaneously. Adjust this value accordingly.
    private static final int SOUND_PRIORITY_LEVEL = 1; // Used for setting the sound priority level.
    private static final float SOUND_VOLUME_LEVEL = 1.0f; // Used for setting the left and right volume levels.

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXSoundEngine.class.getSimpleName();

    /** CONSTRUCTOR METHOD _____________________________________________________________________ **/

    // HXSoundEngine(): Constructor method for this class.
    HXSoundEngine(int id) {
        this.engineID = id;
    }

    /** INITIALIZATION METHODS _________________________________________________________________ **/

    // initSoundPool(): Initializes the SoundPool object.
    private synchronized void initSoundPool() {
        HXLog.d(LOG_TAG, "INITIALIZING (" + engineID + "): initSoundPool(): Using API 21+ SoundPool initialization.");
        soundPool = buildSoundPool();
    }

    // buildSoundPool(): Builds the SoundPool object. This implementation is only used on devices
    // running Android 5.0 and later.
    private synchronized SoundPool buildSoundPool() {

        // Initializes the AudioAttributes.Builder object.
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME) // Sets the audio type to USAGE_GAME.
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_LOW_LATENCY)
                .build();

        // Initializes the SoundPool.Builder object.
        SoundPool soundBuilder = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(MAX_SIMULTANEOUS_SOUNDS) // Sets the maximum number of audio streams.
                .build();

        HXLog.d(LOG_TAG, "INITIALIZING (" + engineID + "): buildSoundPool(): SoundPool construction complete.");

        return soundBuilder; // Returns the newly created SoundPool object.
    }

    // reinitialize(): This method re-initializes the SoundPool object and reloads cached sounds.
    synchronized void reinitialize(Context context) {
        List<Integer> cachedSoundFx = soundFxList == null ? null : new ArrayList<>(soundFxList);

        HXLog.d(LOG_TAG, "RE-INITIALIZING (" + engineID + "): reinitialize(): Rebuilding SoundPool.");
        release(); // Releases the SoundPool object.
        initSoundPool(); // Initializes the SoundPool object.

        // Re-generates the soundEffectMap.
        if (cachedSoundFx != null && !cachedSoundFx.isEmpty()) {
            for (int i = 0; i < cachedSoundFx.size(); i++) {
                addSoundFx(cachedSoundFx.get(i), context);
            }
            HXLog.d(LOG_TAG, "RE-INITIALIZING (" + engineID + "): reinitialize(): Re-generated sound effect map.");
        }
    }

    /** SOUND METHODS __________________________________________________________________________ **/

    // prepareSoundFx(): Prepares the specified resource for sound playback.
    synchronized void prepareSoundFx(final int resource, final boolean isLoop, Context context) {

        // Initializes the SoundPool object.
        if (soundPool == null) {
            initSoundPool();
        }

        // Checks to see if the sound effect has been already added. If not it is added to the list
        // the sound effect is prepared in SoundPool.
        boolean isAdded = addSoundFx(resource, context);

        // If the soundPool object is not yet fully loaded, the listener will play the sound effect
        // after the soundPool object has fully loaded.
        if (isAdded) {
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0) {
                        HXLog.d(LOG_TAG, "READY (" + engineID + "): onLoadComplete(): The SoundPool object is ready.");
                        playSoundFx(sampleId, isLoop);
                    } else {
                        HXLog.e(LOG_TAG, "ERROR (" + engineID + "): onLoadComplete(): SoundPool load failed with status " + status + ".");
                    }
                }
            });
        } else {
            playSoundFx(soundEffectMap.get(resource), isLoop);
        }
    }

    // playSoundFx(): Plays the specified sound effect.
    private synchronized void playSoundFx(int id, boolean isLoop) {
        if (soundEffectMap != null && !soundEffectMap.isEmpty()) {
            soundPool.play(id, SOUND_VOLUME_LEVEL, SOUND_VOLUME_LEVEL, SOUND_PRIORITY_LEVEL,
                    isLoop ? -1 : 0, 1.0f);
        }
    }

    // pauseSounds(): Pauses all sound effects playing in the background.
    void pauseSounds() {

        // Checks to see if the soundPool object has been initiated first before pausing sound
        // effect playback.
        if (soundPool != null) {
            soundPool.autoPause(); // Pauses all sound effect playback.
            HXLog.d(LOG_TAG, "SOUND (" + engineID + "): pauseSounds(): All sound playback has been paused.");
        } else {
            HXLog.e(LOG_TAG, "ERROR (" + engineID + "): pauseSounds(): Cannot pause sound playback due to SoundPool object being null.");
        }
    }

    // resumeSounds(): Resumes all sound effect playback in the background.
    void resumeSounds() {

        // Checks to see if soundPool has been initiated first before resuming sound effect playback.
        if (soundPool != null) {
            soundPool.autoResume(); // Resumes all sound effect playback.
            HXLog.d(LOG_TAG, "SOUND (" + engineID + "): Resuming sound effect playback.");
        }
    }

    /** SOUND HELPER METHODS ___________________________________________________________________ **/

    // addSoundFx(): Adds the specified sound resource to the soundEffectMap, if it has not been
    // added.
    private synchronized boolean addSoundFx(int resource, Context context) {

        if (soundEffectMap == null) {
            soundEffectMap = new ConcurrentHashMap<>();
        }

        // Checks to see if the sound effect has already been added.
        Integer soundEffect = soundEffectMap.get(resource);
        if (soundEffect == null) {

            // Initializes the SoundPool object.
            if (soundPool == null) {
                initSoundPool();
            }

            soundEffectMap.put(resource, soundPool.load(context, resource, SOUND_PRIORITY_LEVEL));

            // Stores the reference for the added sound resource into soundFxList.
            if (soundFxList == null) {
                soundFxList = new ArrayList<>();
            }
            soundFxList.add(resource);

            HXLog.d(LOG_TAG, "PREPARING (" + engineID + "): addSoundFx(): New sound effect has been added.");
            return true;
        } else {
            HXLog.d(LOG_TAG, "PREPARING (" + engineID + "): addSoundFx(): Sound effect already added to soundEffectMap.");
            return false;
        }
    }

    // loadSoundFxList(): Loads the list of sound effects into the soundEffectMap.
    synchronized void loadSoundFxList(List<Integer> soundList, Context context) {

        // Removes any existing onLoadCompleteListeners for SoundPool.
        if (soundPool != null) {
            soundPool.setOnLoadCompleteListener(null);
        }

        // Loads each resource from the soundList into the soundEffectMap and soundFxList.
        for (int resource : soundList) {
            if (resource != 0) {
                addSoundFx(resource, context);
            }
        }
    }

    // release(): Used to free up memory resources when all audio effects are no longer needed.
    void release() {

        // Releases SoundPool resources.
        if (soundPool != null) {
            soundPool.setOnLoadCompleteListener(null);
            soundPool.release();
            soundPool = null;

            if (soundEffectMap != null) {
                soundEffectMap.clear();
            }
            if (soundFxList != null) {
                soundFxList.clear();
            }

            HXLog.d(LOG_TAG, "RELEASE (" + engineID + "): release(): SoundPool object has been released.");
        } else {
            HXLog.e(LOG_TAG, "ERROR (" + engineID + "): release(): SoundPool object is null and cannot be released.");
        }
    }
}