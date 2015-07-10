package com.huhx0015.hxgselib.model;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSESong] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class stores the song attributes, such as it's name and it's associated
 *  resource file.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSESong {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private int musicRes; // Stores the raw ID reference for the song resource.
    private String songName; // Stores the String name of the song.

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // HXGSESong(): Constructor method that initializes the HXGSESong object.
    public HXGSESong(String name, int resource) {
        musicRes = resource;
        songName = name;
    }

    // HXGSESong(): Deconstructor for HXGSESong class.
    public HXGSESong() {
        musicRes = 0;
        songName = null;
    }

    /** GET FUNCTIONALITY ______________________________________________________________________ **/

    // setMusicRes(): Sets the reference ID for the song.
    public void setMusicRes(int resource) { musicRes = resource; }

    // setSongName(): Sets the name of the song.
    public void setSongName(String name) { songName = name; }

    /** SET FUNCTIONALITY ______________________________________________________________________ **/

    // getMusicRes(): Retrieves the song reference ID.
    public int getMusicRes() { return musicRes; }

    // getSongName(): Retrieves the name of the song.
    public String getSongName() { return songName; }
}