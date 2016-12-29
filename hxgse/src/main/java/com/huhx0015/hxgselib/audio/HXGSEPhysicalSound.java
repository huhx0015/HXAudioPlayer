package com.huhx0015.hxgselib.audio;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEPhysicalSound] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains methods that affect the system sound state.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSEPhysicalSound {

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

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
