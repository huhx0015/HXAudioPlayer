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
    public static void disablePhysSounds(boolean mode, Context context) {

        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (mode) {
                mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
            } else {
                mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
            }

        } else {
            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, mode);
        }
    }
}
