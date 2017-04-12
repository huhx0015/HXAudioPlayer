package com.huhx0015.hxgselib.builder;

import android.content.Context;
import android.util.Log;

import com.huhx0015.hxgselib.audio.HXMusic;
import com.huhx0015.hxgselib.model.HXMusicItem;

/**
 * Created by Michael Yoon Huh on 4/12/2017.
 */

public class HXMusicBuilder {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private HXMusicItem musicItem;

    private static final String LOG_TAG = HXMusicBuilder.class.getSimpleName();

    /** CONSTRUCTOR METHOD _____________________________________________________________________ **/

    public HXMusicBuilder() {
        if (musicItem == null) {
            this.musicItem = new HXMusicItem();
        }
    }

    /** BUILDER METHODS ________________________________________________________________________ **/

    public HXMusicBuilder load(int resource) {
        this.musicItem.setMusicResource(resource);
        return this;
    }

    public HXMusicBuilder title(String title) {
        this.musicItem.setMusicTitle(title);
        return this;
    }

    // at(): Sets the starting position of the song.
    public HXMusicBuilder at(int position) {
        this.musicItem.setMusicPosition(position);
        return this;
    }

    public HXMusicBuilder looped(boolean looped) {
        this.musicItem.setLooped(looped);
        return this;
    }

    public void play(Context context) {
        if (context == null || context.getApplicationContext() == null) {
            Log.e(LOG_TAG, "ERROR: play(): Context cannot be null.");
        } else {
            HXMusic.instance().playMusic(musicItem, context);
        }
    }
}
