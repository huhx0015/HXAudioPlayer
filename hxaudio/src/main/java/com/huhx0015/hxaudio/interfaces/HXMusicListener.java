package com.huhx0015.hxaudio.interfaces;

import com.huhx0015.hxaudio.model.HXMusicItem;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusicListener] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXMusicListener is an interface class used to listen for events from HXMusic and
 *  HXMusicEngine.
 *  -----------------------------------------------------------------------------------------------
 */

public interface HXMusicListener {

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // onMusicPrepared(): Called when HXMusicEngine's MediaPlayer object has called onPrepared().
    void onMusicPrepared(HXMusicItem music);

    // onMusicCompletion(): Called when HXMusicEngine's MediaPlayer object has called onCompletion().
    void onMusicCompletion(HXMusicItem music);

    // onMusicBufferingUpdate(): Called when HXMusicEngine's MediaPlayer object has called onBufferingUpdate().
    void onMusicBufferingUpdate(HXMusicItem music, int percent);

    // onMusicPause(): Called when HXMusic's pause() method has been called.
    void onMusicPause(HXMusicItem music);

    // onMusicStop(): Called when HXMusic's stop() method has been called.
    void onMusicStop(HXMusicItem music);
}
