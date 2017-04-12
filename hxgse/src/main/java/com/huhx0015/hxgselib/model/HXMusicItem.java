package com.huhx0015.hxgselib.model;

/**
 * Created by Michael Yoon Huh on 4/12/2017.
 */

/** -----------------------------------------------------------------------------------------------
 *  [HXGSESong] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class stores the song attributes, such as it's name and it's associated
 *  resource file.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXMusicItem {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private int musicPosition;
    private int musicResource;

    private boolean isLooped;

    private String musicArtist;
    private String musicDate;
    private String musicState;
    private String musicTitle;

    /** GET METHODS ____________________________________________________________________________ **/

    public int getMusicPosition() {
        return musicPosition;
    }

    public int getMusicResource() {
        return musicResource;
    }

    public boolean isLooped() {
        return isLooped;
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

    public String getMusicState() {
        return musicState;
    }

    /** SET METHODS ____________________________________________________________________________ **/

    public void setMusicPosition(int musicPosition) {
        this.musicPosition = musicPosition;
    }

    public void setMusicResource(int musicResource) {
        this.musicResource = musicResource;
    }

    public void setLooped(boolean looped) {
        isLooped = looped;
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

    public void setMusicState(String musicState) {
        this.musicState = musicState;
    }
}