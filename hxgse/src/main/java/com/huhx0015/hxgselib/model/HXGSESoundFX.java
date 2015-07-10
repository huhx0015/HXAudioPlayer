package com.huhx0015.hxgselib.model;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSESoundFX] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class stores the sound effect attributes, such as it's name and it's
 *  associated resource file.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSESoundFX {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private int soundRes; // Stores the raw ID reference for the song resource.
    private String soundName; // Stores the String name of the song.

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // HXGSESoundFX(): Constructor method that initializes the HXGSESoundFX object.
    public HXGSESoundFX(String name, int resource) {
        soundName = name;
        soundRes = resource;
    }

    // HXGSESoundFX(): Deconstructor for HXGSESoundFX class.
    public HXGSESoundFX() {
        soundName = null;
        soundRes = 0;
    }

    /** GET FUNCTIONALITY ______________________________________________________________________ **/

    // setSongName(): Sets the name of the song.
    public void setSoundName(String name) { soundName = name; }

    // setSoundRes(): Sets the reference ID for the sound effect.
    public void setSoundRes(int resource) { soundRes = resource; }

    /** SET FUNCTIONALITY ______________________________________________________________________ **/

    // getSongName(): Retrieves the name of the song.
    public String getSoundName() { return soundName; }

    // getSoundRes(): Retrieves the sound effect reference ID.
    public int getSoundRes() { return soundRes; }
}