package com.huhx0015.huhxagse.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.huhx0015.huhxagse.R;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEPreferences] CLASS
 *  DESCRIPTION: HXGSEPreferences is a class that contains functionality that pertains to the use and
 *  manipulation of shared preferences data.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSEPreferences {

    /** SHARED PREFERENCES FUNCTIONALITY _______________________________________________________ **/

    // getPreferenceResource(): Selects the appropriate resource based on the shared preference type.
    private static int getPreferenceResource() {
        return R.xml.hxgse_preferences;  // Main preferences resource file.
    }

    // initializePreferences(): Initializes and returns the SharedPreferences object.
    public static SharedPreferences initializePreferences(Context context) {
        return context.getSharedPreferences("hxgse_preferences", Context.MODE_PRIVATE);
    }

    // setDefaultPreferences(): Sets the shared preference values to default values.
    public static void setDefaultPreferences(Boolean isReset, Context context) {

        int prefResource = getPreferenceResource(); // Determines the appropriate resource file to use.

        // Resets the preference values to default values.
        if (isReset) {
            SharedPreferences preferences = initializePreferences(context);
            preferences.edit().clear().apply();
        }

        // Sets the default values for the SharedPreferences object.
        PreferenceManager.setDefaultValues(context, "hxgse_preferences", Context.MODE_PRIVATE, prefResource, true);
    }

    /** GET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // getCurrentSong(): Retrieves the "hxgse_current_song" value from preferences.
    public static String getCurrentSong(SharedPreferences preferences) {
        return preferences.getString("hxgse_current_song", "STOPPED"); // Retrieves the current song name from preferences.
    }

    // getMusicOn(): Retrieves the "hxgse_music_on" value from preferences.
    public static Boolean getMusicOn(SharedPreferences preferences) {
        return preferences.getBoolean("hxgse_music_on", true); // Retrieves the music option setting.
    }

    // getSoundOn(): Retrieves the "hxgse_sound_on" value from preferences.
    public static Boolean getSoundOn(SharedPreferences preferences) {
        return preferences.getBoolean("hxgse_sound_on", true); // Retrieves the sound option setting.
    }

    /** SET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // setCurrentSong(): Sets the name of the currently playing song to preferences.
    public static void setCurrentSong(String songName, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putString("hxgse_current_song", songName); // Sets the name of the currently playing song to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setMusicOn(): Sets the "hxgse_music_on" value to preferences.
    public static void setMusicOn(Boolean isOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("hxgse_music_on", isOn); // Sets the music option value to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setSongPosition(): Sets the "hxgse_song_position" value to preferences.
    public static void setSongPosition(int position, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putInt("hxgse_song_position", position); // Sets the current song position.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setSoundOn(): Sets the "hxgse_sound_on" value to preferences.
    public static void setSoundOn(Boolean isOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("hxgse_sound_on", isOn); // Sets the sound option value to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }
}