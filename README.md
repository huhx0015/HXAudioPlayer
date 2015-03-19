HuhX_Game_Sound_Engine
==================

PROGRAMMER: huhx0015

STATUS: Open-Source Engine

The HuhX Game Sound Engine is a custom game audio engine for Android 2.3 - Android 5.1. This is an easy-to-use sound and music engine that is focused on providing audio for Android game-related apps. This audio engine was utilized in apps such as StepBOT, Dragon Geo, Cid's Aerial Tours, and Chrono Maps.

The demo activity provided with the project provides an example how the HuhX GSE engine works.

INSTRUCTIONS:

1. Define your song and sound effect names and resources in HXGSEMusicList and HXGSESoundList classes.

2. Declare HXGSEMusic / HXGSESound objects as universal variables. 
 
3. Initialize HXGSEMusic / HXGSESound objects in the onCreate() function of the first activity / fragment of your app that requires sound playback. 
  - hxgse_music.getInstance().initializeAudio(this);
  - hxgse_sound.getInstance().initializeAudio(this);
  
4. Status of the music and sound engines will be outputted to logcat. Once fully initialized, all methods of HXGSEMusic and HXGSESound are available to use.

NOTES: 
- INITIALIZATION: Intialize the HXGSEMusic / HXGSESound objects once, in the first activity of your app that requires sound playback. No need to re-initialize these objects in other activity instances (unless releaseAudio()/releaseMedia() is called, which is not recommended until the end of app life), as a single instance is active until releaseAudio()/releaseMedia() is called. Initializing HXGSEMusic / HXGSESound more than once may result in more than one audio streams running at once.

- RELEASE: It is recommended not to call releaseAudio()/releaseMedia() in HXGSESound and HXGSEMusic unless your application is about to be terminated. If releaseAudio()/releaseMedia() is called and sound or music functionality is needed after such calls have been made, a new instance of HXGSESound/ HXGSEMusic must be initialized before audio is able to function.

- SCREEN ORIENTATION CHANGE: If your application makes use of screen orientation changes, it is important not to call the releaseAudio()/releaseMedia() methods in HXGSESound and HXGSEMusic in onPause()/onStop()/onDestroy() states. It is highly suggested to make your activity to use the following property in the AndroidManifest.xml file and make use of the onConfigurationChanged() function. <android:configChanges="orientation|screenSize|keyboardHidden">
