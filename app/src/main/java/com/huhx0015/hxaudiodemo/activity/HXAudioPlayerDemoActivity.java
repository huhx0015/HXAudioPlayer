package com.huhx0015.hxaudiodemo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.huhx0015.hxaudiodemo.R;
import com.huhx0015.hxaudiodemo.preferences.HXAudioPreferences;
import com.huhx0015.hxaudio.audio.HXMusic;
import com.huhx0015.hxaudio.audio.HXSound;
import com.huhx0015.hxaudio.utils.HXAudioPlayerUtils;

public class HXAudioPlayerDemoActivity extends AppCompatActivity {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // AUDIO VARIABLES
    private String currentSong = "NONE"; // Sets the default song for the activity.
    private boolean musicOn = true; // Used to determine if music has been enabled or not.
    private boolean soundOn = true; // Used to determine if sound has been enabled or not.

    // PREFERENCE VARIABLES
    private SharedPreferences hxAudioPreferences; // SharedPreferences object for storing app data.

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _______________________________________________________ **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadPreferences(); // Loads the settings values from the main SharedPreferences object.
        initView(); // Sets up layout for the activity.
    }

    // onResume(): This function runs immediately after onCreate() finishes and is always re-run
    // whenever the activity is resumed from an onPause() state.
    @Override
    protected void onResume() {
        super.onResume();

        // Checks to see if songs were playing in the background previously; this call resumes
        // the audio playback.
        HXMusic.resumeMusic(this);
        HXAudioPlayerUtils.enableSystemSound(true, this); // Temporarily disables the physical button's sound effects.
    }

    // onPause(): This function is called whenever the current activity is suspended or another
    // activity is launched.
    @Override
    protected void onPause(){
        super.onPause();

        HXAudioPlayerUtils.enableSystemSound(false, this); // Re-enables the physical button's sound effects.
        HXMusic.pauseMusic(); // Pauses any song that is playing in the background.
    }

    // onStop(): This function runs when screen is no longer visible and the activity is in a
    // state prior to destruction.
    @Override
    protected void onStop() {

        super.onStop();

        // Refreshes the SoundPool object for Android 2.3 (GINGERBREAD) devices.
        HXSound.reinitialize();
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Releases all audio-related instances if the application is terminating.
        HXMusic.clear();
        HXSound.clear();
    }

    /** PHYSICAL BUTTON FUNCTIONALITY __________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical BACK button key is pressed.
    @Override
    public void onBackPressed() {

        // Plays the sound effect.
        HXSound.sound()
                .load(R.raw.sfx_3_digital_life_1)
                .play(HXAudioPlayerDemoActivity.this);

        finish(); // Finishes the activity.
    }

    // MENU KEY:
    // onKeyDown(): Defines the action to take when the MENU key button is pressed.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        // Plays a custom sound effect when the MENU key is pressed.
        if (keyCode == KeyEvent.KEYCODE_MENU ) {

            // Plays the sound effect.
            HXSound.sound()
                    .load(R.raw.sfx_2_machine)
                    .play(this);

            return true; // Returns true to prevent further propagation of the key event.
        }

        // Allows the system to handle all other key events.
        return super.onKeyDown(keyCode, event);
    }

    /** LAYOUT FUNCTIONALITY ___________________________________________________________________ **/

    // initView(): Sets up the layout for the activity.
    public void initView() {

        // Sets the XML layout for the activity.
        setContentView(R.layout.activity_hx_audio_player_demo);

        initButtons(); // Sets up the button listeners for the activity.
    }

    // initButtons(): Sets up the button listeners for the activity.
    public void initButtons() {

        // References the Button and LinearLayout objects.
        final Button musicEnableButton = (Button) findViewById(R.id.music_enable_button);
        final Button soundEnableButton = (Button) findViewById(R.id.sound_enable_button);
        final ImageButton playButton = (ImageButton) findViewById(R.id.play_button);
        final ImageButton pauseButton = (ImageButton) findViewById(R.id.pause_button);
        final ImageButton stopButton = (ImageButton) findViewById(R.id.stop_button);
        final LinearLayout firstSongContainer = (LinearLayout) findViewById(R.id.activity_hx_audio_first_song_container);
        final LinearLayout secondSongContainer = (LinearLayout) findViewById(R.id.activity_hx_audio_second_song_container);
        final LinearLayout thirdSongContainer = (LinearLayout) findViewById(R.id.activity_hx_audio_third_song_container);
        final LinearLayout firstFxContainer = (LinearLayout) findViewById(R.id.activity_hx_audio_first_sound_fx_container);
        final LinearLayout secondFxContainer = (LinearLayout) findViewById(R.id.activity_hx_audio_second_sound_fx_container);
        final LinearLayout thirdFxContainer = (LinearLayout) findViewById(R.id.activity_hx_audio_third_sound_fx_container);

        // Updates the text on the OPTIONS buttons, depending on their current state.
        if (musicOn) { musicEnableButton.setText(getString(R.string.music_on)); }
        else { musicEnableButton.setText(getString(R.string.music_off)); }
        if (soundOn) { soundEnableButton.setText(getString(R.string.sound_on)); }
        else { soundEnableButton.setText(getString(R.string.sound_off)); }

        // SONG LIST BUTTONS:
        // -----------------------------------------------------------------------------------------
        // Sets up the listener and the actions for the FIRST SONG container.
        firstSongContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Sets the name of the song and plays the song immediately if music is enabled.
                if (musicOn) {
                    currentSong = "SONG 1"; // Sets the song name.

                    HXMusic.music()
                            .load(R.raw.song_1_gamerstep_bass_triplets)
                            .title(currentSong)
                            .looped(true)
                            .play(HXAudioPlayerDemoActivity.this);

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

                    HXMusic.music()
                            .load(R.raw.song_2_ts_drums)
                            .title(currentSong)
                            .looped(true)
                            .play(HXAudioPlayerDemoActivity.this);

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

                    HXMusic.music()
                            .load(R.raw.song_3_ts_digi_lead_2)
                            .title(currentSong)
                            .looped(true)
                            .play(HXAudioPlayerDemoActivity.this);

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
                HXSound.sound()
                        .load(R.raw.sfx_1_sci_fi_5)
                        .play(HXAudioPlayerDemoActivity.this);
            }
        });

        // Sets up the listener and the actions for the SECOND FX container.
        secondFxContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Plays the sound effect.
                HXSound.sound()
                        .load(R.raw.sfx_2_machine)
                        .play(HXAudioPlayerDemoActivity.this);
            }
        });

        // Sets up the listener and the actions for the THIRD FX container.
        thirdFxContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Plays the sound effect.
                HXSound.sound()
                        .load(R.raw.sfx_3_digital_life_1)
                        .play(HXAudioPlayerDemoActivity.this);
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
                    HXMusic.resumeMusic(HXAudioPlayerDemoActivity.this);
                }
            }
        });

        // Sets up the listener and the actions for the PAUSE button.
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Pauses the song that is currently playing in the background.
                if (HXMusic.isPlaying()) {
                    HXMusic.pauseMusic();
                }
            }
        });

        // Sets up the listener and the actions for the STOP button.
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Stops the song that is currently playing in the background.
                if (HXMusic.isPlaying()) {
                    HXMusic.stopMusic();
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
                    if (HXMusic.isPlaying()) {
                        HXMusic.stopMusic();
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
                HXAudioPreferences.setMusicOn(musicOn, hxAudioPreferences);

                // Sets the musicOn value in the HXGSEMusic class.
                HXMusic.enable(musicOn);
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
                    HXSound.reinitialize();

                    soundOn = true;
                    soundEnableButton.setText("SOUND ON");
                }

                // Sets the updated sound value in SharedPreferences.
                HXAudioPreferences.setSoundOn(soundOn, hxAudioPreferences);

                // Sets the soundOn value in the HXGSEMusic class.
                HXSound.enable(soundOn);
            }
        });
    }

    // toggleStar(): Toggles the 'star' in the selected position.
    private void toggleStar(int starNo) {

        // References the star toggle buttons.
        ImageView firstSongStar = (ImageView) findViewById(R.id.activity_hx_audio_first_song_star);
        ImageView secondSongStar = (ImageView) findViewById(R.id.activity_hx_audio_second_song_star);
        ImageView thirdSongStar = (ImageView) findViewById(R.id.activity_hx_audio_third_song_star);

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
                break;

            // Second Song 'STAR':
            case 2:
                firstSongStar.setImageResource(starOff);
                secondSongStar.setImageResource(starOn);
                thirdSongStar.setImageResource(starOff);
                break;

            // Third Song 'STAR':
            case 3:
                firstSongStar.setImageResource(starOff);
                secondSongStar.setImageResource(starOff);
                thirdSongStar.setImageResource(starOn);
                break;

            // No song selected:
            default:
                firstSongStar.setImageResource(starOff);
                secondSongStar.setImageResource(starOff);
                thirdSongStar.setImageResource(starOff);
                break;
        }
    }

    /** PREFERENCES FUNCTIONALITY ______________________________________________________________ **/

    // loadPreferences(): Loads the shared preference values.
    private void loadPreferences() {

        // PREFERENCES: Retrieves all values from the main SharedPreferences object.
        hxAudioPreferences = HXAudioPreferences.initializePreferences(this);
        musicOn = HXAudioPreferences.getMusicOn(hxAudioPreferences);
        soundOn = HXAudioPreferences.getSoundOn(hxAudioPreferences);

        // Assigns the retrieved preference values to the class objects.
        HXMusic.enable(musicOn);
        HXSound.enable(soundOn);
    }
}