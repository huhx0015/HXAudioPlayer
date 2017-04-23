package com.huhx0015.hxaudio.builder;

import android.content.Context;
import android.util.Log;
import com.huhx0015.hxaudio.audio.HXSound;

/** -----------------------------------------------------------------------------------------------
 *  [HXSoundBuilder] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXSoundBuilder class is a builder class for the HXSound object and is used to
 *  prepare a specified sound resource for sound playback in HXSound.
 *  -----------------------------------------------------------------------------------------------
 */
public class HXSoundBuilder {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ATTRIBUTE VARIABLES:
    private int soundResource;
    private boolean isLooped;

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXSoundBuilder.class.getSimpleName();

    /** BUILDER METHODS ________________________________________________________________________ **/

    // load(): Sets the resource ID for this sound effect.
    public HXSoundBuilder load(int resource) {
        this.soundResource = resource;
        return this;
    }

    // looped(): Specifies whether this sound effect should be looped or not.
    public HXSoundBuilder looped(boolean isLooped) {
        this.isLooped = isLooped;
        return this;
    }

    // play(): Calls the HXSound playSoundFx() method to attempt to play the specified sound effect.
    public void play(final Context context) {
        if (context == null || context.getApplicationContext() == null) {
            Log.e(LOG_TAG, "ERROR: play(): Context cannot be null.");
        } else {
            Thread playThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HXSound.instance().playSoundFx(soundResource, isLooped,
                            context.getApplicationContext());
                }
            });
            playThread.start();
        }
    }
}