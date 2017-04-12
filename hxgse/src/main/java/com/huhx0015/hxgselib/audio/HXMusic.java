package com.huhx0015.hxgselib.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.huhx0015.hxgselib.builder.HXMusicBuilder;
import com.huhx0015.hxgselib.model.HXGSESong;
import com.huhx0015.hxgselib.model.HXMusicItem;
import com.huhx0015.hxgselib.resources.HXGSEMusicList;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [HXMusic] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXMusic class is used to support music playback for the application.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXMusic {
    
    /** CLASS VARIABLES ________________________________________________________________________ **/

    // INSTANCE VARIABLES:
    private static HXMusic hxMusic; // Instance variable for HXMusic.

    // AUDIO VARIABLES:
    private MediaPlayer mediaPlayer; // MediaPlayer variable for background song.
    private String currentSong; // Used for determining what song is playing in the background.
    private Boolean isInitialized; // Used for determining if the HXMusic component has been initialized.
    private boolean isPaused; // Used for determining if a song has been paused.
    private int songPosition; // Used for resuming playback on a song that was paused.
    private boolean musicOn; // Used for determining whether music is playing in the background.


    private HXMusicItem musicItem; // Current music item.

    // CONSTANTS VARIABLES:
    private static final String STOPPED = "STOPPED";
    private static final String PAUSED = "PAUSED";

    // SYSTEM VARIABLES:
    private static final String TAG = HXMusic.class.getSimpleName(); // Used for logging output to logcat.


    /** CONSTRUCTOR FUNCTIONALITY ______________________________________________________________ **/




    /** INSTANCE FUNCTIONALITY _________________________________________________________________ **/

    public static HXMusicBuilder music() {
        instance();
        return new HXMusicBuilder();
    }

    // instance(): Returns the hxMusic instance.
    public static HXMusic instance() {
        if (hxMusic == null) {
            hxMusic = new HXMusic();
        }
        return hxMusic;
    }

    /** NEW METHODS ____________________________________________________________________________ **/

    public void playMusic(HXMusicItem musicItem, Context context) {

        if (musicItem != null && musicItem.getMusicResource() != 0) {

            // Stops any songs currently playing in the background.
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.d(TAG, "PREPARING: Song currently playing in the background. Stopping playback before switching to a new song.");
                mediaPlayer.stop();
            }

            // Sets up the MediaPlayer object for the song to be played.
            releaseMedia(); // Releases MediaPool resources.
            mediaPlayer = new MediaPlayer(); // Initializes the MediaPlayer.
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.

            Log.d(TAG, "PREPARING: MediaPlayer stream type set to STREAM_MUSIC.");

            // Sets up the MediaPlayer for the song.
            mediaPlayer = MediaPlayer.create(context, musicItem.getMusicResource());
            mediaPlayer.setLooping(musicItem.isLooped());

            Log.d(TAG, "PREPARING: Loop condition has been set to " + musicItem.isLooped() + ".");

            // If the song was previously paused, resume the song at it's previous location.
            if (isPaused) {

                Log.d(TAG, "PREPARING: Song was previously paused, resuming song playback.");

                mediaPlayer.seekTo(musicItem.getMusicPosition()); // Jumps to the position where the song left off.
                songPosition = 0; // Resets songPosition variable after song's position has been set.
                isPaused = false; // Indicates that the song is no longer paused.
            }

            // Sets up the listener for the MediaPlayer object. Song playback begins immediately
            // once the MediaPlayer object is ready.
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    Log.d(TAG, "MUSIC: Song playback has begun.");
                    mediaPlayer.start(); // Begins playing the song.
                }
            });
        } else {
            Log.e(TAG, "ERROR: playMusic(): musicItem was null or an invalid audio resource was set.");
        }
    }

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // initializeAudio(): Initializes the HXMusic class variables.
    public void initializeAudio() {

        Log.d(TAG, "INITIALIZING: Initializing HXGSE music engine.");

        mediaPlayer = new MediaPlayer(); // Instantiates the main MediaPlayer object.
        isInitialized = true; // Indicates that the engine has been initialized.
        isPaused = false; // Indicates that the song is not paused by default.
        musicOn = true; // Indicates that music playback is enabled by default.
        currentSong = STOPPED; // Sets the "STOPPED" condition for the song name string.
        songPosition = 0; // Sets the song position to the beginning of the song by default.

        Log.d(TAG, "INITIALIZING: HXGSE music engine initialization complete.");
    }

    // getInitStatus(): This function checks the current initialization status of the
    // HXMusic object. If it is null, the HXMusic parameters are reset. This is to
    // deal music a null pointer bug that can occur when the application is suspended for a long time
    // and the app activity is destroyed and re-created.
    public boolean getInitStatus() {

        // Checks to see if the HXMusic object has already been initialized. If it has not
        // been initialized, the HXMusic class is re-initialized.
        if (isInitialized == null) {
            initializeAudio(); // Initializes the HXMusic class variables.
            return false;
        }

        // Indicates that the HXMusic object has already been initialized.
        else { return true; }
    }

    /** MUSIC FUNCTIONALITY ____________________________________________________________________ **/

    // playSongName(): Plays the music file based on the specified String songName. The song is
    // changed only if the specified song does not match the current song that is playing.
    // Set loop variable to true to enable infinite song looping.
    // TRUE: Loops the song infinitely.
    // FALSE: Disables song looping.
    @Deprecated
    public void playSongName(String songName, boolean loop, Context context) {

        boolean musicFound = false; // Used for determining if a corresponding song for songName was found or not.
        int songID = 0; // Used for storing the reference ID to the raw music resource object.

        // If the music option has been enabled, a song is selected based on the passed in songName string.
        if (musicOn) {

            LinkedList<HXGSESong> songList = HXGSEMusicList.hxgseMusicList(); // Retrieves the list of songs.
            final int NUM_SONGS = songList.size(); // Retrieves the number of songs in the list.

            // Checks to see if the song list is valid or not.
            if (NUM_SONGS < 1) {
                Log.e(TAG, "ERROR: The songlist doesn't contain any valid song objects. Has the song list been populated?");
                return;
            }

            // Loops through the song list to find the specified song in the pre-defined music list.
            int i = 0;
            for (int x : new int[NUM_SONGS]) {

                String retrievedSong = songList.get(i).getSongName(); // Retrieves the song name string.

                // Compares the specified song name against the current song name in the list.
                if (retrievedSong.equals(songName)) {

                    // Checks to see if the specified song is already playing.
                    if (!currentSong.equals(songName)) {

                        songID = songList.get(i).getMusicRes(); // Sets the music resource file reference.

                        // Checks to see if the songID is a valid reference ID.
                        if (songID == 0) {

                            Log.e(TAG, "ERROR: Invalid song reference ID was found. ID was " + songID + ".");
                            return;
                        }

                        // Sets the currentSong to be the name of the specified song and sets the
                        // value of musicFound to be true.
                        else {

                            currentSong = songName;
                            musicFound = true;

                            Log.d(TAG, "PREPARING: Specified song " + songName + " was found.");
                        }
                    }

                    // Indicates that the specified song is already playing and the operation is
                    // cancelled.
                    else {
                        Log.e(TAG, "ERROR: Specified song " + songName + " is already playing!");
                        return;
                    }
                }
                i++;
            }

            // If a song match was found, play the music file from resources.
            if ( (musicFound) || (isPaused) ) {
                playSong(songID, loop, context); // Calls playSong to create a MediaPlayer object and play the song.
            }

            // Outputs a message to logcat indicating that the song could not be found.
            else {
                Log.e(TAG, "ERROR: Specified song " + songName + " was not found. Please specify a valid song name.");
            }
        }

        // Outputs a message to logcat indicating that the song cannot be played.
        else {
            Log.e(TAG, "ERROR: Song cannot be played. Music engine is currently disabled.");
        }
    }

    // isSongPlaying(): Determines if a song is currently playing in the background.
    public boolean isSongPlaying() {
        return mediaPlayer.isPlaying();
    }

    // pauseSong(): Pauses any songs playing in the background and returns it's position.
    public void pauseSong() {

        Log.d(TAG, "MUSIC: Music playback has been paused.");

        // Checks to see if mapSong has been initialized first before saving the song position and pausing the song.
        if (mediaPlayer != null) {

            songPosition = mediaPlayer.getCurrentPosition(); // Retrieves the current song position and saves it.

            // Pauses the song only if there is a song is currently playing.
            if (mediaPlayer.isPlaying()) { mediaPlayer.pause(); } // Pauses the song.

            isPaused = true; // Indicates that the song is currently paused.
            currentSong = PAUSED;
        }
    }


    //  playSong(): Sets up a MediaPlayer object and begins playing the song.
    @Deprecated
    private void playSong(final int songName, boolean loop, Context context) {

        // Stops any songs currently playing in the background.
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.d(TAG, "PREPARING: Song currently playing in the background. Stopping playback before switching to a new song.");
            mediaPlayer.stop();
        }

        // Sets up the MediaPlayer object for the song to be played.
        releaseMedia(); // Releases MediaPool resources.
        mediaPlayer = new MediaPlayer(); // Initializes the MediaPlayer.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // Sets the audio type for the MediaPlayer object.

        Log.d(TAG, "PREPARING: MediaPlayer stream type set to STREAM_MUSIC.");

        mediaPlayer = MediaPlayer.create(context, songName); // Sets up the MediaPlayer for the song.
        mediaPlayer.setLooping(loop); // Enables infinite looping of music.

        Log.d(TAG, "PREPARING: Loop condition has been set to " + loop + ".");

        // If the song was previously paused, resume the song at it's previous location.
        if (isPaused) {

            Log.d(TAG, "PREPARING: Song was previously paused, resuming song playback.");

            mediaPlayer.seekTo(songPosition); // Jumps to the position where the song left off.
            songPosition = 0; // Resets songPosition variable after song's position has been set.
            isPaused = false; // Indicates that the song is no longer paused.
        }

        // Sets up the listener for the MediaPlayer object. Song playback begins immediately
        // once the MediaPlayer object is ready.
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                Log.d(TAG, "MUSIC: Song playback has begun.");
                mediaPlayer.start(); // Begins playing the song.
            }
        });
    }

    // releaseMedia(): Used to release the resources being used by mediaPlayer objects.
    public void releaseMedia() {

        // Releases MediaPool resources.
        if (mediaPlayer != null) {

            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            Log.d(TAG, "RELEASE: MediaPlayer object has been released.");
        }

        else {
            Log.e(TAG, "ERROR: MediaPlayer object is null and cannot be released.");
        }
    }

    //  stopSong(): Stops any songs playing in the background.
    public void stopSong() {

        // Checks to see if mapSong has been initiated first before stopping song playback.
        if ( (mediaPlayer != null) && (musicOn) ) {
            mediaPlayer.stop(); // Stops any songs currently playing in the background.
            currentSong = STOPPED;
            Log.d(TAG, "MUSIC: Song playback has been stopped.");
        }

        else {
            Log.e(TAG, "ERROR: Cannot stop song, as MediaPlayer object is already null.");
        }
    }

    /** GET METHODS ____________________________________________________________________________ **/

    // getMusicPosition(): Returns the current song position.
    public int getSongPosition() {
        return songPosition;
    }

    // isMusicOn(): Returns the current value of musicOn.
    public boolean isMusicOn() {
        return musicOn;
    }

    /** SET METHODS ____________________________________________________________________________ **/

    // setMusicPosition(): Sets the current song position.
    public void setSongPosition(int songPosition) {
        this.songPosition = songPosition;
    }

    // setMusicOn(): Sets the musicOn value.
    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }
}