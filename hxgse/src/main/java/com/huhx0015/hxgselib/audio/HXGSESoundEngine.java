package com.huhx0015.hxgselib.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;
import com.huhx0015.hxgselib.model.HXGSESoundFX;
import com.huhx0015.hxgselib.resources.HXGSESoundList;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSESoundEngine] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXGSESoundEngine class is used for outputting sound effects for the application.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSESoundEngine {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES:
    private AudioManager soundManager; // AudioManager variable for sound effects.
    private LinkedList<HXGSESoundFX> soundList; // List of sound effects.
    private SoundPool hxgse_soundpool; // SoundPool variable for sound effects.
    private SparseIntArray soundEffectMap; // Hash map for sound effects.
    private boolean autoInitialize = true; // Used to control the auto initialization of the SoundPool object. This is only used on Android 2.3 (GINGERBREAD) devices.
    private boolean soundOn; // Used for determining if sound option is enabled or not.
    private int engineID; // Used to determine the ID value of this instance.
    private final int MAX_SIMULTANEOUS_SOUNDS = 8; // Can output eight sound effects simultaneously. Adjust this value accordingly.
    private final int MAX_SOUND_EVENTS = 8; // Maximum number of sound events before the SoundPool object is reset. Adjust this value based on sound sample sizes. Android 2.3 (GINGERBREAD) only.
    private int soundEventCount = 0; // Used to count the number of sound events that have occurred.

    // SYSTEM VARIABLES:
    private final int api_level = android.os.Build.VERSION.SDK_INT; // Used to determine the device's Android API version.
    private static final String TAG = HXGSESoundEngine.class.getSimpleName(); // Used for logging output to logcat.

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // initializeAudio(): Initializes the HXGSESoundEngine class variables.
    public void initializeAudio(Context context, int id) {

        Log.d(TAG, "INITIALIZING (" + id + "): Initializing HXGSE sound engine.");

        engineID = id; // Sets the ID value for this instance.
        soundEffectMap = new SparseIntArray(); // SparseIntArray of sound effects.
        soundOn = true; // Sets the sound flag to be enabled by default.
        setUpSoundPool(); // Initializes the SoundPool object.
        loadSoundEffects(context); // Loads the sound effects into the SoundPool object.

        Log.d(TAG, "INITIALIZING (" + id + "): HXGSE sound engine initialization complete.");
    }

    /** SETUP FUNCTIONALITY ____________________________________________________________________ **/

    // setUpSoundPool(): Initializes the SoundPool object. Depending on the Android version of the
    // device, the SoundPool object is created using the appropriate methods.
    private void setUpSoundPool() {

        // API 21+: Android 5.0 and above.
        if (api_level > 20) {
            Log.d(TAG, "INITIALIZING (" + engineID + "): Using Lollipop (API 21) SoundPool initialization.");
            hxgse_soundpool = constructSoundPool();
        }

        // API 9 - 20: Android 2.3 - 4.4
        else {
            Log.d(TAG, "INITIALIZING (" + engineID + "): Using GB/HC/ICS/JB/KK (API 9 - 20) SoundPool initialization.");
            hxgse_soundpool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
    }

    // constructSoundPool(): Builds the SoundPool object. This implementation is only used on
    // devices running Android 5.0 and later.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private SoundPool constructSoundPool() {

        // Initializes the AudioAttributes.Builder object.
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME) // Sets the audio type to USAGE_GAME.
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        // Initializes the SoundPool.Builder object.
        SoundPool soundBuilder = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(MAX_SIMULTANEOUS_SOUNDS) // Sets the maximum number of audio streams.
                .build();

        Log.d(TAG, "INITIALIZING (" + engineID + "): SoundPool construction complete.");

        return soundBuilder; // Returns the newly created SoundPool object.
    }

    // loadSoundEffects(): Loads sound effects into the soundEffectMap hash map.
    private void loadSoundEffects(Context context) {

        // Retrieves the list of sound effects.
        soundList = HXGSESoundList.hxgseSoundList();

        // Checks to see if the soundList size is invalid or not.
        if (soundList.size() > 1) {

            Log.d(TAG, "INITIALIZING (" + engineID + "): " + soundList.size() + " sound effects found.");

            // Populates the sound effect map from the soundList LinkedList array.
            for (int position = 0; position < soundList.size(); position++) {
                soundEffectMap.put(position + 1, hxgse_soundpool.load(context, soundList.get(position).getSoundRes(), 1));
                Log.d(TAG, "INITIALIZING (" + engineID + "): Loading sound effect " + soundList.get(position).getSoundName() + " into position " + (position + 1) + ".");
            }

            Log.d(TAG, "INITIALIZING (" + engineID + "): SoundEffectMap populated.");
        }

        // Outputs an error message.
        else {
            Log.d(TAG, "ERROR (" + engineID + "): Cannot initialize HXGSESound engine due to empty list of sound effects.");
        }
    }

    /** SOUND FUNCTIONALITY ____________________________________________________________________ **/

    // playSoundFX(): This is a function that plays the specified sound effect.
    // LOOPING:
    // -1: Loops the sound effect infinitely.
    // 0: Plays the sound effect once.
    // 1+: Loop the sound effect to the amount specified.
    public int playSoundFx(String sfx, final int loop, Context context) {

        boolean soundEffectFound = false; // Used to determine if the sound effect has been found and played.
        int soundID = 0; // The reference ID of the sound effect being played.

        // Processes and plays the sound effect only if soundOn variable is set to true.
        if (soundOn) {

            // Checks to see if the soundPool class has been instantiated first before playing a
            // sound effect. This is to prevent a rare null pointer exception bug.
            if (hxgse_soundpool == null) {
                Log.d(TAG, "WARNING (" + engineID + "): SoundPool object was null. Re-initializing SoundPool object.");
                soundEffectMap = new SparseIntArray(); // SparseIntArray of sound effects.
                setUpSoundPool();
                loadSoundEffects(context);
            }

            else {

                // ANDROID 2.3 (GINGERBREAD): The SoundPool object is re-initialized if the sound event
                // counter has reached the MAX_SOUND_EVENT limit. This is to handle the AudioTrack
                // 1 MB buffer limit issue.
                if ((api_level < 11) && (soundEventCount >= MAX_SOUND_EVENTS) && (autoInitialize)) {
                    Log.d(TAG, "WARNING (" + engineID + "): Sound event count (" + soundEventCount + ") has exceeded the maximum number of sound events. Re-initializing the engine.");
                    reinitializeSoundPool(context);
                }

                else {

                    final int NUM_SOUNDS = soundList.size(); // Retrieves the number of defined sound effects in the list.

                    // Checks to see if the sound effect list is valid or not.
                    if (NUM_SOUNDS < 1) {
                        Log.d(TAG, "ERROR (" + engineID + "): The sound effect list doesn't contain any valid sound objects. Has the sound effect list been populated?");
                        return 0;
                    }

                    // Loops through the sound list to find the specified sound effect in the pre-defined sound effect list.
                    for (int i = 1; i <= NUM_SOUNDS; i++) {

                        String retrievedSfx = soundList.get(i - 1).getSoundName(); // Retrieves the sound name string.

                        // Compares the specified song name against the current song name in the list.
                        if (retrievedSfx.equals(sfx)) {

                            soundEffectFound = true; // Indicates that the specified SFX was found in the sound effect list.

                            // Retrieves the current volume value.
                            if (soundManager == null) {
                                soundManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                            }
                            float volume = soundManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                            // Checks to see if the SoundPool object is null first. If not, the sound effect
                            // is played.
                            if (hxgse_soundpool != null) {
                                soundID = hxgse_soundpool.play(soundEffectMap.get(i), volume, volume, 1, loop, 1.0f); // Plays the sound effect.
                                soundEventCount++; // Increments the sound event counter.
                                Log.d(TAG, "SOUND (" + engineID + "): Playing " + retrievedSfx + " sound effect at soundEffectMap position " + i + ".");
                            } else {
                                Log.d(TAG, "ERROR (" + engineID + "): Cannot play sound effect due to SoundPool object being null.");
                            }
                        }
                    }

                    // If the sound effect was not found, an error message is outputted to logcat.
                    if (!soundEffectFound) {
                        Log.d(TAG, "ERROR (" + engineID + "): Specified sound effect was not found in the sound effect list.");
                    }
                }
            }
        }

        // Outputs an error message to logcat, indicating that the sound effect could not be played
        // and that the sound engine is disabled.
        else {
            Log.d(TAG, "ERROR (" + engineID + "): Sound effect could not be played due to the sound engine being disabled.");
        }

        return soundID;
    }

    // pauseSounds(): Pauses all sound effects playing in the background.
    public void pauseSounds() {

        // Checks to see if the hxgse_soundpool object has been initiated first before pausing sound
        // effect playback.
        if (hxgse_soundpool != null) {
            hxgse_soundpool.autoPause(); // Pauses all sound effect playback.
            Log.d(TAG, "SOUND (" + engineID + "): All sound playback has been paused.");
        }

        else {
            Log.d(TAG, "ERROR (" + engineID + "): Cannot pause sound playback due to SoundPool object being null.");
        }
    }

    // resumeSounds(): Resumes all sound effect playback in the background.
    public void resumeSounds() {

        // Checks to see if hxgse_soundpool has been initiated first before resuming sound effect playback.
        if (hxgse_soundpool != null) {
            hxgse_soundpool.autoResume(); // Resumes all sound effect playback.
            Log.d(TAG, "SOUND (" + engineID + "): Resuming sound effect playback.");
        }
    }

    // reinitializeSoundPool(): This method re-initializes the SoundPool object for devices running
    // on Android 2.3 (GINGERBREAD) and earlier. This is to help minimize the AudioTrack out of
    // memory error, which was limited to a small 1 MB size buffer.
    public void reinitializeSoundPool(Context context) {

        // GINGERBREAD: The SoundPool is released and re-initialized. This is done to minimize the
        // AudioTrack out of memory (-12) error.
        if (api_level < 11) {

            Log.d(TAG, "RE-INITIALIZING (" + engineID + "): The SoundPool object is being re-initialized.");

            releaseSound(); // Releases the SoundPool object.
            setUpSoundPool(); // Initializes the SoundPool object.
            loadSoundEffects(context); // Loads the sound effect hash map.

            soundEventCount = 0; // Resets the sound event counter.
        }
    }

    // releaseSound(): Used to free up memory resources when all audio effects are no longer needed.
    public void releaseSound() {

        // Releases SoundPool resources.
        if (hxgse_soundpool != null) {

            hxgse_soundpool.release();
            hxgse_soundpool = null;

            Log.d(TAG, "RELEASE (" + engineID + "): SoundPool object has been released.");
        }

        else {
            Log.d(TAG, "ERROR (" + engineID + "): SoundPool object is null and cannot be released.");
        }
    }
}