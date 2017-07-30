package com.huhx0015.hxaudio.utils;

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

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = HXAudioPlayerUtils.class.getSimpleName();

    /** UTILITY METHODS ________________________________________________________________________ **/

    // enableSystemSound(): Enables or disables the device's system sound effects. This is most
    // commonly used if physical button's sound effects need to be enabled/disabled.
    // NOTE 1: Android 7.0 and above requires "android.permission.ACCESS_NOTIFICATION_POLICY".
    // NOTE 2: This method has been marked as deprecated and will be removed in the next major
    // release of HXAudioPlayer.
    @Deprecated
    public static void enableSystemSound(boolean mode, Context context) {
        try {
            AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

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
        } catch (NullPointerException e) {
            HXLog.e(LOG_TAG, "ERROR: An null pointer exception occurred while attempting to access the AudioManager: " + e.getLocalizedMessage());
        } catch (SecurityException e) {
            HXLog.e(LOG_TAG, "ERROR: An security exception occurred while attempting to access the AudioManager: " + e.getLocalizedMessage());
        } catch (Exception e) {
            HXLog.e(LOG_TAG, "ERROR: An exception occurred while attempting to access the AudioManager: " + e.getLocalizedMessage());
        }
    }
}