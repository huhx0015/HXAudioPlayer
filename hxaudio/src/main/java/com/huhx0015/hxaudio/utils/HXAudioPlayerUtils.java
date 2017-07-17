package com.huhx0015.hxaudio.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

/** -----------------------------------------------------------------------------------------------
 *  [HXAudioPlayerUtils] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains utility methods for the HXAudioPlayer library.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXAudioPlayerUtils {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = HXAudioPlayerUtils.class.getSimpleName();

    /** UTILITY METHODS ________________________________________________________________________ **/

    // enableSystemSound(): Enables or disables the device's system sound effects. This is most
    // commonly used if physical button's sound effects need to be enabled/disabled.
    // NOTE: Android 7.0 and above requires "android.permission.ACCESS_NOTIFICATION_POLICY".
    public static void enableSystemSound(boolean mode, Context context) {

        // ANDROID 2.3 - ANDROID 6.0:
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (manager != null) {

                    // ANDROID 6.0+:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (mode) {
                            manager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
                        } else {
                            manager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
                        }
                    }

                    // ANDROID 2.3 - ANDROID 5.1:
                    else {
                        manager.setStreamMute(AudioManager.STREAM_SYSTEM, mode);
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "ERROR: An exception occurred while attempting to access the AudioManager: " + e.getLocalizedMessage());
            }
        }
    }
}