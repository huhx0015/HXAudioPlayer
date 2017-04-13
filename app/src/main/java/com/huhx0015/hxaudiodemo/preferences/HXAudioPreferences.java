package com.huhx0015.hxaudiodemo.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/** -----------------------------------------------------------------------------------------------
 *  [HXAudioPreferences] CLASS
 *  DESCRIPTION: HXAudioPreferences is a class that contains functionality that pertains to the use
 *  and manipulation of shared preferences data.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXAudioPreferences {

    /** SHARED PREFERENCES FUNCTIONALITY _______________________________________________________ **/

    // initializePreferences(): Initializes and returns the SharedPreferences object.
    public static SharedPreferences initializePreferences(Context context) {
        return context.getSharedPreferences("hx_audio_preferences", Context.MODE_PRIVATE);
    }

    /** GET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // getMusicOn(): Retrieves the "hx_audio_music_on" value from preferences.
    public static boolean getMusicOn(SharedPreferences preferences) {
        return preferences.getBoolean("hx_audio_music_on", true); // Retrieves the music option setting.
    }

    // getSoundOn(): Retrieves the "hx_audio_sound_on" value from preferences.
    public static boolean getSoundOn(SharedPreferences preferences) {
        return preferences.getBoolean("hx_audio_sound_on", true); // Retrieves the sound option setting.
    }

    /** SET PREFERENCES FUNCTIONALITY __________________________________________________________ **/

    // setMusicOn(): Sets the "hx_audio_music_on" value to preferences.
    public static void setMusicOn(boolean isOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("hx_audio_music_on", isOn); // Sets the music option value to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }

    // setSoundOn(): Sets the "hx_audio_sound_on" value to preferences.
    public static void setSoundOn(boolean isOn, SharedPreferences preferences) {

        // Prepares the SharedPreferences object for editing.
        SharedPreferences.Editor prefEdit = preferences.edit();

        prefEdit.putBoolean("hx_audio_sound_on", isOn); // Sets the sound option value to preferences.
        prefEdit.apply(); // Applies the changes to SharedPreferences.
    }
}