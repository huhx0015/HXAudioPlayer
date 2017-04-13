package com.huhx0015.hxgselib.builder;

import android.content.Context;
import android.util.Log;
import com.huhx0015.hxgselib.audio.HXSound;

/**
 * Created by Michael Yoon Huh on 4/12/2017.
 */

public class HXSoundBuilder {

    private int soundResource;
    private boolean isLooped;

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXSoundBuilder.class.getSimpleName();

    public HXSoundBuilder load(int resource) {
        this.soundResource = resource;
        return this;
    }

    public HXSoundBuilder looped(boolean isLooped) {
        this.isLooped = isLooped;
        return this;
    }

    public void play(Context context) {
        if (context == null || context.getApplicationContext() == null) {
            Log.e(LOG_TAG, "ERROR: play(): Context cannot be null.");
        } else {
            HXSound.instance().playSoundFx(soundResource, isLooped, context.getApplicationContext());
        }
    }
}
