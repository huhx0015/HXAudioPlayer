package com.huhx0015.hxgselib.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

/** -----------------------------------------------------------------------------------------------
 *  [HXAudioPlayerUtils] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains utility methods for the HXAudioPlayer library.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXAudioPlayerUtils {

    /** UTILITY METHODS ________________________________________________________________________ **/

    // disablePhysicalSounds(): Enables or disables the devices physical button's sound effects.
    // Note: Android 7.0 and above requires "android.permission.ACCESS_NOTIFICATION_POLICY".
    public static void disablePhysSounds(boolean mode, Context context) {

        // ANDROID 2.3 - ANDROID 6.0:
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

            AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            // ANDROID 6.0+:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (mode) {
                    mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
                } else {
                    mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
                }
            }

            // ANDROID 2.3 - ANDROID 5.1:
            else {
                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, mode);
            }
        }
    }
}
