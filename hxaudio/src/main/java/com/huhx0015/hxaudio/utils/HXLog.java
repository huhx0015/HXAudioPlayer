package com.huhx0015.hxaudio.utils;

import android.util.Log;
import com.huhx0015.hxaudio.BuildConfig;

/** -----------------------------------------------------------------------------------------------
 *  [HXLog] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class contains methods for outputting Log messages. By default, Log messages
 *  are only outputted when the build is in DEBUG mode unless set by setLogging().
 *  -----------------------------------------------------------------------------------------------
 */

public class HXLog {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // INSTANCE VARIABLES
    private static HXLog hxLog;

    // LOGGING VARIABLES
    private boolean isLogging;

    /** LOG METHODS ____________________________________________________________________________ **/

    // d(): Outputs a Log.d message if build is DEBUG mode.
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG || (hxLog != null && hxLog.isLogging)) {
            Log.d(tag, message);
        }
    }

    // e(): Outputs a Log.e message if build is DEBUG mode.
    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG || (hxLog != null && hxLog.isLogging)) {
            Log.e(tag, message);
        }
    }

    // w(): Outputs a Log.w message if build is DEBUG mode.
    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG || (hxLog != null && hxLog.isLogging)) {
            Log.w(tag, message);
        }
    }

    /** SET METHODS ____________________________________________________________________________ **/

    // setLogging(): Enables or disables logging.
    public static void setLogging(boolean isLogged) {
        if (hxLog == null) {
            hxLog = new HXLog();
        }
        hxLog.isLogging = isLogged;
    }
}