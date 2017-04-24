HX Audio Player
===============

DEVELOPER: huhx0015

## Description

The HX Audio Player is a custom audio wrapper library for Android 2.3 and above. Originally designed as an audio library for games, HX Audio Player is an easy-to-use, alternative approach to implementing music and sound playback into Android applications. HX Audio Player also implements seamless workarounds to address the existing bugs and shortcomings of MediaPlayer and SoundPool APIs.

Previous versions of this audio library was utilized in apps such as Dragon Geo, Cid's Aerial Tours, Chrono Maps, and StepBOT.

The demo application provided with the project provides an example how the HX Audio Player library works.

## Instructions

### Music Playback

To load and play music files, simply declare the following in your code:

#### Play Music (Resource):

```
HXMusic.music()
       .load(R.raw.my_song_name)    // Sets the resource of the song. [REQUIRED]
       .title("My Awesome Song")    // Sets the title of the song. [OPTIONAL]
       .artist("Mr. Anonymous")     // Sets the artist of the song. [OPTIONAL]
       .date("January 1, 1998")     // Sets the date of the song. [OPTIONAL]
       .at(5)                       // Sets the position for where the song should start. [OPTIONAL]
       .gapless(true)               // Enables gapless playback for this song. [OPTIONAL]
       .looped(true)                // Sets the song to be looped. [OPTIONAL]
       .play(this);                 // Plays the song. [REQUIRED]
```

#### Play Music (URL):

```
HXMusic.music()
       .load("http://some-fake-url.com/song.mp3")   // Sets the URL location of the song. [REQUIRED]
       .title("My Awesome Song")                    // Sets the title of the song. [OPTIONAL]
       .artist("Mr. Anonymous")                     // Sets the artist of the song. [OPTIONAL]
       .date("January 1, 1998")                     // Sets the date of the song. [OPTIONAL]
       .at(5)                                       // Sets the position for where the song should start. [OPTIONAL]
       .gapless(true)                               // Enables gapless playback for this song. [OPTIONAL]
       .looped(true)                                // Sets the song to be looped. [OPTIONAL]
       .play(this);                                 // Plays the song. [REQUIRED]
```

#### Pause Music:

```
HXMusic.pauseMusic();               // Pauses any song that is playing in the background.
```

#### Resume Music:

```
HXMusic.resumeMusic(this);          // Resumes playback of the last played song at the position where it left off.
```

#### Stop Music:

```
HXMusic.stopMusic();                // Stops all music playing in the background.
```

#### Song Playing

```
HXMusic.isPlaying();                // Returns a boolean to determine if the song is currently playing or not.
```

#### Song Position

```
HXMusic.getPosition();              // Returns the current position of the song, represented as an int value.
HXMusic.setPosition(0);             // Sets the current position of the song.
```

#### Music Listener

```
HXMusic.setListener(this);          // Sets a HXMusicListener interface to HXMusic. Use this if you need to monitor events from HXMusic.
```

#### Music Status

```
HXMusic.getStatus();                // Returns a String text message regarding the status of HXMusic.
```

#### Enable/Disable Music:

```
HXMusic.enable(true);               // Enables/disables music playback.
```

#### Clear Music:

```
HXMusic.clear();                    // Clears the HXMusic instance. Should be called when HXMusic is no longer in use.
```

It's just that simple! No need to write complicated code to initialize MediaPlayer, HX Audio Player handles all of this!

### Sound Playback

As for loading and playing sound effects, declare the following in your code:

#### Play Sound:

```
HXSound.sound()
       .load(R.raw.my_sound_effect) // Sets the resource of the sound effect. [REQUIRED]
       .looped(true)                // Sets the sound effect to be looped. [OPTIONAL]
       .play(this);                 // Plays the sound effect. [REQUIRED]
```

#### Pause Sound:

```
HXSound.pauseSounds();              // Pauses all looping sounds playing in the background.
```

#### Resume Sound:

```
HXSound.resumeSounds();              // Resumes playback of all looping sounds previously played in the background.
```

#### Enable Multiple Sound Engines:

```
HXSound.engines(2);                  // Specifies the number of sound engines (2 is recommended) to be enabled. This feature works on API 9 - 10 devices only.
```

#### Enable/Disable Sound:

```
HXSound.enable(true);               // Enables/disables sound playback.
```

#### Clear Sound:

```
HXSound.clear();                    // Clears the HXSound instance. Should be called when HXSound is no longer in use.
```

Voilà! Also very simple! No need to deal with SoundPool!

## Notes

- ANDROID API 9 - 10: For devices running on Android API 9 - 10, HXSound class creates two instances of HXSoundEngine by default. This is to help minimize the SoundPool out of memory issue that is present in older versions of Android. As a suggestion to help minimize the issue, make sure that loaded sound effects are small in size and bitrate (recommended to be less than 100 KB and 64kbps or less). Please note that for devices running Android API 11 or greater, only a single instance of HXSoundEngine is used, as the 1 MB sound buffer limit issue is not present on newer versions of Android.

- GAPLESS PLAYBACK: For devices running on Android 16 and above, gapless audio playback is available by adding the ```.gapless(true)``` flag. This enables a workaround for an existing issue with MediaPlayer in which noticeable gaps or audio skips are present when ```MediaPlayer.setLooping()``` is enabled.

- RELEASE: As HXMusic and HXSound are singleton objects, it is recommended to call HXMusic.clear() & HXSound.clear() when audio playback is no longer needed. It is recommended to call these in the onDestroy() method of your Activity or Fragment.

## License

    Copyright 2017 Michael Huh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
