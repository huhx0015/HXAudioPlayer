package com.huhx0015.hxgselib.audio;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEPhysicalSound] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains methods that affect the system sound state.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSEPhysicalSound {

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

    // disablePhysicalSounds(): Enables or disables the devices physical button's sound effects.
    public static void disablePhysSounds(boolean mode, Context context) {

        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // ANDROID 6.0+ (MARSHMALLOW):
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Checks to see if 'Do Not Disturb' mode is on. Fixes a crash issue seen on Android
            // 6.0+ devices.
            try {
                if (Settings.Global.getInt(context.getContentResolver(), "zen_mode") != 0) {
                    if (mode) {
                        mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
                    } else {
                        mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
                    }
                }
            }

            catch (Settings.SettingNotFoundException e) { e.printStackTrace(); }
        }

        // ANDROID 2.3 - ANDROID 5.1:
        else { mgr.setStreamMute(AudioManager.STREAM_SYSTEM, mode); }
    }
}
