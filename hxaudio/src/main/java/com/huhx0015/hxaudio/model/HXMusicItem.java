package com.huhx0015.hxaudio.model;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusicItem] CLASS
 *  DEVELOPER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class stores the music attributes, such as resource ID, music title, music
 *  artist, and music date.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXMusicItem {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private int musicResource;

    private String musicUrl;
    private String musicArtist;
    private String musicDate;
    private String musicTitle;

    /** GET METHODS ____________________________________________________________________________ **/

    public int getMusicResource() {
        return musicResource;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public String getMusicDate() {
        return musicDate;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    /** SET METHODS ____________________________________________________________________________ **/

    public void setMusicResource(int musicResource) {
        this.musicResource = musicResource;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }

    public void setMusicDate(String musicDate) {
        this.musicDate = musicDate;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }
}