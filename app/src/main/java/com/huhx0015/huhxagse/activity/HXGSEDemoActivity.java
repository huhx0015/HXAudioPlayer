package com.huhx0015.huhxagse.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.huhx0015.huhxagse.R;
import com.huhx0015.huhxagse.preferences.HXGSEPreferences;
import com.huhx0015.hxgselib.audio.HXGSEDolbyEffects;
import com.huhx0015.hxgselib.audio.HXGSEMusicEngine;
import com.huhx0015.hxgselib.audio.HXGSEPhysicalSound;
import com.huhx0015.hxgselib.audio.HXGSESoundHandler;

public class HXGSEDemoActivity extends AppCompatActivity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES
    private String currentSong = "NONE"; // Sets the default song for the activity.
    private boolean musicOn = true; // Used to determine if music has been enabled or not.
    private boolean soundOn = true; // Used to determine if sound has been enabled or not.
    private boolean isPlaying = false; // Indicates that a song is currently playing in the background.

    // LAYOUT VARIABLES
    private int currentStar = 0; // Used to determine which star is currently toggled.

    // PREFERENCE VARIABLES
    private SharedPreferences HXGSE_prefs; // SharedPreferences object for storing app data.

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // AUDIO CLASS INITIALIZATION:
        HXGSEMusicEngine.getInstance().initializeAudio(getApplicationContext()); // Initializes the HXGSEMusic class object.
        HXGSESoundHandler.getInstance().initializeAudio(getApplicationContext(), 2); // Initializes the HXGSESound class object.
        HXGSEDolbyEffects.getInstance().initializeDolby(getApplicationContext()); // Initializes the HXGSEDolby class object.

        loadPreferences(); // Loads the settings values from the main SharedPreferences object.
        setUpLayout(); // Sets up layout for the activity.
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the activity is resumed from an onPause() state.
    @Override
    protected void onResume() {
        super.onResume();

        // Checks to see if songs were playing in the background previously; this call resumes
        // the audio playback.
        resumeAudioState();
        HXGSEPhysicalSound.disablePhysSounds(true, this); // Temporarily disables the physical button's sound effects.
    }

    // onPause(): This function is called whenever the current activity is suspended or another
    // activity is launched.
    @Override
    protected void onPause(){
        super.onPause();

        // Sets the isPlaying variable to determine if the song is currently playing.
        isPlaying = HXGSEMusicEngine.getInstance().isSongPlaying();

        HXGSEPhysicalSound.disablePhysSounds(false, this); // Re-enables the physical button's sound effects.
        HXGSEMusicEngine.getInstance().pauseSong(); // Pauses any song that is playing in the background.
    }

    // onStop(): This function runs when screen is no longer visible and the activity is in a
    // state prior to destruction.
    @Override
    protected void onStop() {

        super.onStop();

        // Refreshes the SoundPool object for Android 2.3 (GINGERBREAD) devices.
        HXGSESoundHandler.getInstance().reinitializeSoundPool();
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Releases all audio-related instances if the application is terminating.
        HXGSEMusicEngine.getInstance().releaseMedia();
        HXGSESoundHandler.getInstance().releaseSound();
        HXGSEDolbyEffects.getInstance().releaseDolbyEffects();
    }

    /** ACTIVITY EXTENSION FUNCTIONALITY _______________________________________________________ **/

    // onConfigurationChanged(): If the screen orientation changes, this function is called.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setUpLayout(); // The layout is re-created for the screen orientation change.
        toggleStar(currentStar); // Sets the toggled star for the song that was last selected.
    }

    /** PHYSICAL BUTTON FUNCTIONALITY __________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical BACK button key is pressed.
    @Override
    public void onBackPressed() {

        HXGSESoundHandler.getInstance().playSoundFx("SFX3", 0); // Plays the sound effect.
        finish(); // Finishes the activity.
    }

    // MENU KEY:
    // onKeyDown(): Defines the action to take when the MENU key button is pressed.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        // Plays a custom sound effect when the MENU key is pressed.
        if (keyCode == KeyEvent.KEYCODE_MENU ) {
            HXGSESoundHandler.getInstance().playSoundFx("SFX2", 0); // Plays the sound effect.
            return true; // Returns true to prevent further propagation of the key event.
        }

        // Allows the system to handle all other key events.
        return super.onKeyDown(keyCode, event);
    }

    /** LAYOUT FUNCTIONALITY ___________________________________________________________________ **/

    // setUpLayout(): Sets up the layout for the activity.
    public void setUpLayout() {

        // Sets the XML layout for the activity.
        setContentView(R.layout.hxgse_main_activity);

        setUpButtons(); // Sets up the button listeners for the activity.
    }

    // setUpButtons(): Sets up the button listeners for the activity.
    public void setUpButtons() {

        // References the Button and LinearLayout objects.
        final Button musicEnableButton = (Button) findViewById(R.id.music_enable_button);
        final Button soundEnableButton = (Button) findViewById(R.id.sound_enable_button);
        final ImageButton playButton = (ImageButton) findViewById(R.id.play_button);
        final ImageButton pauseButton = (ImageButton) findViewById(R.id.pause_button);
        final ImageButton stopButton = (ImageButton) findViewById(R.id.stop_button);
        final LinearLayout firstSongContainer = (LinearLayout) findViewById(R.id.first_song_container);
        final LinearLayout secondSongContainer = (LinearLayout) findViewById(R.id.second_song_container);
        final LinearLayout thirdSongContainer = (LinearLayout) findViewById(R.id.third_song_container);
        final LinearLayout firstFxContainer = (LinearLayout) findViewById(R.id.first_fx_container);
        final LinearLayout secondFxContainer = (LinearLayout) findViewById(R.id.second_fx_container);
        final LinearLayout thirdFxContainer = (LinearLayout) findViewById(R.id.third_fx_container);

        // Updates the text on the OPTIONS buttons, depending on their current state.
        if (musicOn) { musicEnableButton.setText("MUSIC ON"); }
        else { musicEnableButton.setText("MUSIC OFF"); }
        if (soundOn) { soundEnableButton.setText("SOUND ON"); }
        else { soundEnableButton.setText("SOUND OFF"); }

        // SONG LIST BUTTONS:
        // -----------------------------------------------------------------------------------------
        // Sets up the listener and the actions for the FIRST SONG container.
        firstSongContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Sets the name of the song and plays the song immediately if music is enabled.
                if (musicOn) {
                    currentSong = "SONG 1"; // Sets the song name.
                    HXGSEMusicEngine.getInstance().playSongName(currentSong, true);

                    toggleStar(1); // Toggles the star for the first song.
                }
            }
        });

        // Sets up the listener and the actions for the SECOND SONG container.
        secondSongContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Sets the name of the song and plays the song immediately if music is enabled.
                if (musicOn) {
                    currentSong = "SONG 2"; // Sets the song name.
                    HXGSEMusicEngine.getInstance().playSongName(currentSong, true);

                    toggleStar(2); // Toggles the star for the second song.
                }
            }
        });

        // Sets up the listener and the actions for the THIRD SONG container.
        thirdSongContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Sets the name of the song and plays the song immediately if music is enabled.
                if (musicOn) {
                    currentSong = "SONG 3"; // Sets the song name.
                    HXGSEMusicEngine.getInstance().playSongName(currentSong, true);

                    toggleStar(3); // Toggles the star for the third song.
                }
            }
        });

        // SOUND FX LIST BUTTONS:
        // -----------------------------------------------------------------------------------------

        // Sets up the listener and the actions for the FIRST FX container.
        firstFxContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Plays the sound effect.
                HXGSESoundHandler.getInstance().playSoundFx("SFX1", 0);
            }
        });

        // Sets up the listener and the actions for the SECOND FX container.
        secondFxContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Plays the sound effect.
                HXGSESoundHandler.getInstance().playSoundFx("SFX2", 0);
            }
        });

        // Sets up the listener and the actions for the THIRD FX container.
        thirdFxContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Plays the sound effect.
                HXGSESoundHandler.getInstance().playSoundFx("SFX3", 0);
            }
        });

        // MUSIC PLAYBACK BUTTONS:
        // -----------------------------------------------------------------------------------------

        // Sets up the listener and the actions for the PLAY button.
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // If no song has been selected, the first song is played by default.
                if (currentSong.equals("NONE")) {
                    firstSongContainer.performClick();
                }

                // Plays the last selected song.
                else {
                    HXGSEMusicEngine.getInstance().playSongName(currentSong, true);
                }
            }
        });

        // Sets up the listener and the actions for the PAUSE button.
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Pauses the song that is currently playing in the background.
                if (HXGSEMusicEngine.getInstance().isSongPlaying()) {
                    HXGSEMusicEngine.getInstance().pauseSong();
                }
            }
        });

        // Sets up the listener and the actions for the STOP button.
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Stops the song that is currently playing in the background.
                if (HXGSEMusicEngine.getInstance().isSongPlaying()) {
                    HXGSEMusicEngine.getInstance().stopSong();
                    currentSong = "NONE"; // Indicates no song has been selected.
                    toggleStar(0); // Updates the star song toggles.
                }
            }
        });

        // AUDIO OPTION BUTTONS:
        // -----------------------------------------------------------------------------------------

        // Sets up the listener and the actions for the MUSIC ON / MUSIC OFF button.
        musicEnableButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Disables music playback.
                if (musicOn) {

                    // Stops song playback if song is currently playing.
                    if (HXGSEMusicEngine.getInstance().isSongPlaying()) {
                        HXGSEMusicEngine.getInstance().stopSong();
                        currentSong  = "NONE"; // Indicates no song has been selected.
                        toggleStar(0); // Updates the song star toggles.
                    }

                    musicOn = false;
                    musicEnableButton.setText("MUSIC OFF");
                }

                // Enables music playback.
                else {
                    musicOn = true;
                    musicEnableButton.setText("MUSIC ON");
                }

                // Sets the updated music value in SharedPreferences.
                HXGSEPreferences.setMusicOn(musicOn, HXGSE_prefs);

                // Sets the musicOn value in the HXGSEMusic class.
                HXGSEMusicEngine.getInstance().musicOn = musicOn;
            }
        });

        // Sets up the listener and the actions for the SOUND ON / SOUND OFF button.
        soundEnableButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Disables sound playback.
                if (soundOn) {
                    soundOn = false;
                    soundEnableButton.setText("SOUND OFF");
                }

                // Enables sound playback.
                else {

                    // Refreshes the SoundPool object for Android 2.3 (GINGERBREAD) devices.
                    HXGSESoundHandler.getInstance().reinitializeSoundPool();

                    soundOn = true;
                    soundEnableButton.setText("SOUND ON");
                }

                // Sets the updated sound value in SharedPreferences.
                HXGSEPreferences.setSoundOn(soundOn, HXGSE_prefs);

                // Sets the soundOn value in the HXGSEMusic class.
                HXGSESoundHandler.getInstance().soundOn = soundOn;
            }
        });
    }

    // toggleStar(): Toggles the 'star' in the selected position.
    private void toggleStar(int starNo) {

        // References the star toggle buttons.
        ImageView firstSongStar = (ImageView) findViewById(R.id.first_song_toggle_button);
        ImageView secondSongStar = (ImageView) findViewById(R.id.second_song_toggle_button);
        ImageView thirdSongStar = (ImageView) findViewById(R.id.third_song_toggle_button);

        // References the Drawable resources.
        int starOn = android.R.drawable.star_big_on;
        int starOff = android.R.drawable.star_big_off;

        // 'Toggles' the star in the selected position and 'un-toggles' the other stars.
        switch (starNo) {

            // First Song 'STAR':
            case 1:
                firstSongStar.setImageResource(starOn);
                secondSongStar.setImageResource(starOff);
                thirdSongStar.setImageResource(starOff);
                currentStar = 1;
                break;

            // Second Song 'STAR':
            case 2:
                firstSongStar.setImageResource(starOff);
                secondSongStar.setImageResource(starOn);
                thirdSongStar.setImageResource(starOff);
                currentStar = 2;
                break;

            // Third Song 'STAR':
            case 3:
                firstSongStar.setImageResource(starOff);
                secondSongStar.setImageResource(starOff);
                thirdSongStar.setImageResource(starOn);
                currentStar = 3;
                break;

            // No song selected:
            default:
                firstSongStar.setImageResource(starOff);
                secondSongStar.setImageResource(starOff);
                thirdSongStar.setImageResource(starOff);
                currentStar = 0;
                break;
        }
    }

    /** AUDIO FUNCTIONALITY ____________________________________________________________________ **/

    // resumeAudioState(): If music was playing in the background prior to the activity from being
    // paused, the song is resumed.
    private void resumeAudioState() {

        // Checks the HXGSEMusicEngine initialization status to ensure that it is still initialized.
        // This is to prevent against a rare null object issue where the activity may be destroyed
        // in low memory situations.
        HXGSEMusicEngine.getInstance().getInitStatus(this);

        // Checks to see if the song was playing prior to the activity from being
        if (isPlaying) {
            HXGSEMusicEngine.getInstance().playSongName(currentSong, true);
        }
    }

    /** PREFERENCES FUNCTIONALITY ______________________________________________________________ **/

    // loadPreferences(): Loads the shared preference values.
    private void loadPreferences() {

        // PREFERENCES: Retrieves all values from the main SharedPreferences object.
        HXGSE_prefs = HXGSEPreferences.initializePreferences(this);
        musicOn = HXGSEPreferences.getMusicOn(HXGSE_prefs);
        soundOn = HXGSEPreferences.getSoundOn(HXGSE_prefs);

        // Assigns the retrieved preference values to the class objects.
        HXGSEMusicEngine.getInstance().musicOn = musicOn;
        HXGSESoundHandler.getInstance().soundOn = soundOn;
    }
}