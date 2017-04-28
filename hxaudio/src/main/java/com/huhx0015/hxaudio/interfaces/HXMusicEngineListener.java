package com.huhx0015.hxaudio.interfaces;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusicEngineListener] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXMusicEngineListener is an interface class used to listen for events from
 *  HXMusicEngine.
 *  -----------------------------------------------------------------------------------------------
 */

public interface HXMusicEngineListener {

    /** INTERFACE METHODS ______________________________________________________________________ **/

    // onMusicEnginePrepared(): Called when HXMusicEngine's MediaPlayer object has called
    // onPrepared().
    void onMusicEnginePrepared();

    // onMusicEngineCompletion(): Called when HXMusicEngine's MediaPlayer object has called
    // onCompletion().
    void onMusicEngineCompletion();

    // onMusicEngineBufferingUpdate(): Called when HXMusicEngine's MediaPlayer object has called
    // onBufferingUpdate().
    void onMusicEngineBufferingUpdate(int percent);

    // onMusicEnginePause(): Called when HXMusicEngine's pause() method has been called.
    void onMusicEnginePause();

    // onMusicStop(): Called when HXMusicEngine's stop() method has been called.
    void onMusicEngineStop();
}