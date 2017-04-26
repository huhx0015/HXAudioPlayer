package com.huhx0015.hxaudio.utils;

import android.util.Log;
import com.huhx0015.hxaudio.BuildConfig;

/** -----------------------------------------------------------------------------------------------
 *  [HXLog] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains methods for outputting Log messages when the build is in DEBUG
 *  mode.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXLog {

    /** LOG METHODS ____________________________________________________________________________ **/

    // d(): Outputs a Log.d message if build is DEBUG mode.
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    // e(): Outputs a Log.e message if build is DEBUG mode.
    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    // w(): Outputs a Log.w message if build is DEBUG mode.
    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }
}