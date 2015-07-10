package com.huhx0015.hxgselib.audio;

import android.content.Context;
import android.media.AudioManager;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEPhysicalSound] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains methods that affect the system sound state.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSEPhysicalSound {

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

    // disablePhysicalSounds(): Enables or disables the devices physical button's sound effects.
    public static void disablePhysSounds(Boolean mode, Context context) {
        AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, mode);
    }
}
