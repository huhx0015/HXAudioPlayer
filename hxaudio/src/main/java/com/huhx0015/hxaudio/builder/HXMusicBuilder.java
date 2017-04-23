package com.huhx0015.hxaudio.builder;

import android.content.Context;
import android.util.Log;
import com.huhx0015.hxaudio.audio.HXMusic;
import com.huhx0015.hxaudio.model.HXMusicItem;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusicBuilder] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXMusicBuilder class is a builder class for the HXMusic object and is used to
 *  construct an HXMusicItem and associated attributes to be used with the HXMusic object to
 *  playback specified music.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXMusicBuilder {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ATTRIBUTE VARIABLES:
    private boolean isLooped;
    private int musicPosition;

    // MUSIC ITEM VARIABLE:
    private HXMusicItem musicItem;

    // LOGGING VARIABLES:
    private static final String LOG_TAG = HXMusicBuilder.class.getSimpleName();

    /** CONSTRUCTOR METHOD _____________________________________________________________________ **/

    // HXMusicBuilder(): Constructor method for this class.
    public HXMusicBuilder() {
        if (musicItem == null) {
            this.musicItem = new HXMusicItem();
        }
    }

    /** BUILDER METHODS ________________________________________________________________________ **/

    // load(): Sets the resource ID for this music.
    public HXMusicBuilder load(int resource) {
        this.musicItem.setMusicResource(resource);
        return this;
    }

    // load(): Sets the URL for this music.
    public HXMusicBuilder load(String url) {
        this.musicItem.setMusicUrl(url);
        return this;
    }

    // title(): Sets the title for this music.
    public HXMusicBuilder title(String title) {
        this.musicItem.setMusicTitle(title);
        return this;
    }

    // artist(): Sets the artist for this music.
    public HXMusicBuilder artist(String artist) {
        this.musicItem.setMusicArtist(artist);
        return this;
    }

    // date(): Sets the date for this music.
    public HXMusicBuilder date(String date) {
        this.musicItem.setMusicDate(date);
        return this;
    }

    // at(): Sets the starting position of the music.
    public HXMusicBuilder at(int position) {
        this.musicPosition = position;
        return this;
    }

    // looped(): Specifies whether this music should be looped or not.
    public HXMusicBuilder looped(boolean looped) {
        this.isLooped = looped;
        return this;
    }

    // play(): Calls the HXMusic playMusic() method to attempt to play the built music.
    public void play(final Context context) {
        if (context == null || context.getApplicationContext() == null) {
            Log.e(LOG_TAG, "ERROR: play(): Context cannot be null.");
        } else if ( (musicItem.getMusicResource() != 0) && (musicItem.getMusicUrl() != null)) {
            Log.e(LOG_TAG, "ERROR: play(): Cannot set both a music resource and url.");
        } else {
            Thread playThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HXMusic.instance().playMusic(musicItem, musicPosition, isLooped, context);
                }
            });
            playThread.start();
        }
    }
}