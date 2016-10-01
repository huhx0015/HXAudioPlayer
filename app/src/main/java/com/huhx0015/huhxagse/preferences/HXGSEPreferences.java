package com.huhx0015.huhxagse.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEPreferences] CLASS
 *  DESCRIPTION: HXGSEPreferences is a class that contains functionality that pertains to the use and
 *  manipulation of shared preferences data.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSEPreferences {

    /** SHARED PREFERENCES FUNCTIONALITY _______________________________________________________ **/

    // initializePreferences(): Initializes and returns the SharedPreferences object.
    public static SharedPreferences initializePreferences(Context context) {
        return context.getSharedPreferences("hxgse_preferences", Context.MODE_PRIVATE);
    }

    /** GET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // getMusicOn(): Retrieves the "hxgse_music_on" value from preferences.
    public static boolean getMusicOn(SharedPreferences preferences) {
        return preferences.getBoolean("hxgse_music_on", true); // Retrieves the music option setting.
    }

    // getSoundOn(): Retrieves the "hxgse_sound_on" value from preferences.
    public static boolean getSoundOn(SharedPreferences preferences) {
        return preferences.getBoolean("hxgse_sound_on", true); // Retrieves the sound option setting.
    }

    /** SET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // setMusicOn(): Sets the "hxgse_music_on" value to preferences.
    public static void setMusicOn(boolean isOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("hxgse_music_on", isOn); // Sets the music option value to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setSoundOn(): Sets the "hxgse_sound_on" value to preferences.
    public static void setSoundOn(boolean isOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("hxgse_sound_on", isOn); // Sets the sound option value to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }
}