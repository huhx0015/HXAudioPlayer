HX Game Sound Engine
====================

DEVELOPER: huhx0015

## Description

The HX Game Sound Engine is a custom audio wrapper library for Android 2.3 - Android 6.0. This is an sound and music library that is focused on providing audio for Android game-related apps. This audio library was utilized in apps such as Dragon Geo, Cid's Aerial Tours, Chrono Maps, and StepBOT.

The demo activity provided with the project provides an example how the HXGSE library works.

## Instructions

1. Define your song and sound effect names and resources in HXGSEMusicList and HXGSESoundList classes.

2. Declare HXGSEMusicEngine / HXGSESoundHander objects as universal variables. 
 
3. Initialize HXGSEMusic / HXGSESound objects in the onCreate() function of the first activity / fragment of your app that requires sound playback. 
  - hxgse_music.getInstance().initializeAudio(getApplicationContext());
  - hxgse_sound.getInstance().initializeAudio(getApplicationContext(), 2);
  
4. Status of the music and sound engines will be outputted to logcat. Once fully initialized, all methods of HXGSEMusicEngine and HXGSESoundHandler are available to use.

5. (OPTIONAL) If your device supports Dolby Audio, you can enable Dolby Audio Processing by initializing the HXGSEDolbyEffects class:
  - hxgse_music.getInstance().intializeDolby(getApplicationContext());

## Notes

- INITIALIZATION: Intialize the HXGSEMusicEngine / HXGSESoundHander objects once, in the first activity of your app that requires sound playback. No need to re-initialize these objects in other activity instances (unless releaseAudio()/releaseMedia() is called, which is not recommended until the end of app life), as a single instance is active until releaseAudio()/releaseMedia() is called. Initializing HXGSEMusicEngine / HXGSESoundHandler more than once may result in more than one audio streams running at once.

- ANDROID API 9 - 11: HXGSESoundHandler class creates multiple instances of HXGSESoundEngine, based on the second parameter inputted for the initializeAudio method. This is to help minimize the SoundPool out of memory issue that is present in older versions of Android. As a suggestion to help minimize the issue, make sure that loaded sound effects are small in size and bitrate (recommended to be less than 100 KB and 64kbps or less). Please note that for devices running Android API 12 or greater, only a single instance of HXGSESoundEngine is used, as the 1 MB sound buffer limit issue is not present on newer versions of Android.

- RELEASE: It is recommended not to call releaseAudio()/releaseMedia() in HXGSESoundHandler and HXGSEMusicEngine unless your application is about to be terminated. If releaseAudio()/releaseMedia() is called and sound or music functionality is needed after such calls have been made, a new instance of HXGSESoundHandler / HXGSEMusicEngine must be initialized before audio is able to function.

- SCREEN ORIENTATION CHANGE: If your application makes use of screen orientation changes, it is important not to call the releaseAudio()/releaseMedia() methods in HXGSESoundHandler and HXGSEMusicEngine in onPause()/onStop()/onDestroy() states. It is highly suggested to make your activity to use the following property in the AndroidManifest.xml file and make use of the onConfigurationChanged() function. <android:configChanges="orientation|screenSize|keyboardHidden">

## License

    Copyright 2016 Michael Huh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
