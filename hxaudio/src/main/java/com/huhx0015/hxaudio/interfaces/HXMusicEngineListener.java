package com.huhx0015.hxaudio.interfaces;

import android.media.MediaPlayer;

import com.huhx0015.hxaudio.model.HXMusicItem;

/**
 * Created by Michael Yoon Huh on 4/24/2017.
 */

public interface HXMusicEngineListener {

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // onMusicEnginePrepared(): Called when HXMusicEngine's MediaPlayer object has called
    // onPrepared().
    void onMusicEnginePrepared(MediaPlayer mp, int id);

    // onMusicEngineCompletion(): Called when HXMusicEngine's MediaPlayer object has called
    // onCompletion().
    void onMusicEngineCompletion(int id);

    // onMusicEngineBufferingUpdate(): Called when HXMusicEngine's MediaPlayer object has called
    // onBufferingUpdate().
    void onMusicEngineBufferingUpdate(int percent, int id);
}
