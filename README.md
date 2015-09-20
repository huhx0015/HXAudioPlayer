HuhX_Game_Sound_Engine
==================

DEVELOPER: Michael Yoon Huh (HUHX0015)

STATUS: Open-Source

The HuhX Game Sound Engine is a custom game audio engine for Android 2.3 - Android 6.0. This is an sound and music engine that is focused on providing audio for Android game-related apps. This audio engine was utilized in apps such as StepBOT, Dragon Geo, Cid's Aerial Tours, and Chrono Maps.

The demo activity provided with the project provides an example how the HuhX GSE engine works.

INSTRUCTIONS:

1. Define your song and sound effect names and resources in HXGSEMusicList and HXGSESoundList classes.

2. Declare HXGSEMusicEngine / HXGSESoundHander objects as universal variables. 
 
3. Initialize HXGSEMusic / HXGSESound objects in the onCreate() function of the first activity / fragment of your app that requires sound playback. 
  - hxgse_music.getInstance().initializeAudio(getApplicationContext());
  - hxgse_sound.getInstance().initializeAudio(getApplicationContext(), 2);
  
4. Status of the music and sound engines will be outputted to logcat. Once fully initialized, all methods of HXGSEMusicEngine and HXGSESoundHandler are available to use.

5. (OPTIONAL) If your device supports Dolby Audio, you can enable Dolby Audio Processing by initializing the HXGSEDolbyEffects class:
  - hxgse_music.getInstance().intializeDolby(getApplicationContext());

NOTES: 
- INITIALIZATION: Intialize the HXGSEMusicEngine / HXGSESoundHander objects once, in the first activity of your app that requires sound playback. No need to re-initialize these objects in other activity instances (unless releaseAudio()/releaseMedia() is called, which is not recommended until the end of app life), as a single instance is active until releaseAudio()/releaseMedia() is called. Initializing HXGSEMusicEngine / HXGSESoundHandler more than once may result in more than one audio streams running at once.

- ANDROID API 1 - 11: HXGSESoundHandler class creates multiple instances of HXGSESoundEngine, based on the second parameter inputted for the initializeAudio method. This is to help minimize the SoundPool out of memory issue that is present in older versions of Android. As a suggestion to help minimize the issue, make sure that loaded sound effects are small in size and bitrate (recommended to be less than 100 KB and 64kbps or less). Please note that for devices running Android API 12 or greater, only a single instance of HXGSESoundEngine is used, as the 1 MB sound buffer limit issue is not present on newer versions of Android.

- RELEASE: It is recommended not to call releaseAudio()/releaseMedia() in HXGSESoundHandler and HXGSEMusicEngine unless your application is about to be terminated. If releaseAudio()/releaseMedia() is called and sound or music functionality is needed after such calls have been made, a new instance of HXGSESoundHandler / HXGSEMusicEngine must be initialized before audio is able to function.

- SCREEN ORIENTATION CHANGE: If your application makes use of screen orientation changes, it is important not to call the releaseAudio()/releaseMedia() methods in HXGSESoundHandler and HXGSEMusicEngine in onPause()/onStop()/onDestroy() states. It is highly suggested to make your activity to use the following property in the AndroidManifest.xml file and make use of the onConfigurationChanged() function. <android:configChanges="orientation|screenSize|keyboardHidden">
