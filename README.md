HX Audio Player
===============

DEVELOPER: huhx0015

## Description

The HX Audio Player is a custom audio wrapper library for Android 2.3 - Android 7.0. It is designed to be an easy-to-use, alternative approach to implementing music and sound playback into Android applications. This audio library was utilized in apps such as Dragon Geo, Cid's Aerial Tours, Chrono Maps, and StepBOT.

The demo application provided with the project provides an example how the HX Audio Player library works.

## Instructions

### Music Playback

To load and play music files, simply declare the following in your code:

```
HXMusic.music()
       .load(R.raw.my_song_name)    // Sets the resource of the song. [REQUIRED]
       .title("My Awesome Song")    // Sets the title of the song. [OPTIONAL]
       .artist("Mr. Anonymous")     // Sets the artist of the song. [OPTIONAL]
       .date("January 1, 1998")     // Sets the date of the song. [OPTIONAL]
       .at(5)                       // Sets the position for where the song should start. [OPTIONAL]
       .looped(true)                // Sets the song to be looped. [OPTIONAL]
       .play(this);                 // Plays the song. [REQUIRED]
```

It's just that simple! No need to write complicated code to initialize MediaPlayer, HX Audio Player handles all of this!

### Sound Playback

As for loading and playing sound effects, declare the following in your code:

```
HXSound.sound()
       .load(R.raw.my_sound_effect) // Sets the resource of the sound effect. [REQUIRED]
       .looped(true)                // Sets the sound effect to be looped. [OPTIONAL]
       .play(this);                 // Plays the sound effect. [REQUIRED]
```

Voilà! Also very simple! No need to deal with SoundPool!

## Notes

- ANDROID API 9 - 10: HXSound class creates multiple instances of HXSoundEngine. This is to help minimize the SoundPool out of memory issue that is present in older versions of Android. As a suggestion to help minimize the issue, make sure that loaded sound effects are small in size and bitrate (recommended to be less than 100 KB and 64kbps or less). Please note that for devices running Android API 11 or greater, only a single instance of HXGSESoundEngine is used, as the 1 MB sound buffer limit issue is not present on newer versions of Android.

- RELEASE: As HXMusic and HXSound are singleton objects, it is recommended to call HXMusic.clear() & HXSound.clear() when audio playback is no longer needed. It is recommended to call these in the onDestroy() method of your activity or fragment.

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
    
