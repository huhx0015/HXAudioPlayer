HX Audio Player
===============

DEVELOPER: huhx0015

## Description

The HX Audio Player is a custom audio wrapper library for Android 5.0 (API 21) and above. Originally designed as an audio library for games, HX Audio Player is an easy-to-use approach to implementing music and sound playback in Android applications.

HX Audio Player has been utilized in apps such as Chrono Maps, Cid's Aerial Views, DexTrail, Dragon Geo, Link to the Map, Map of the Nocturne, Robot Master Database and Suiko Cartography.

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
       .at(5000)                    // Sets start position in milliseconds. [OPTIONAL]
       .gapless(true)               // Enables gapless playback for this song. [OPTIONAL]
       .looped(true)                // Sets the song to be looped. [OPTIONAL]
       .play(this);                 // Plays the song. [REQUIRED]
```

Shortcut helpers are also available:

```
HXMusic.play(this, R.raw.my_song_name);              // Play once.
HXMusic.play(this, R.raw.my_song_name, true);        // Play looped.
HXMusic.playGaplessLoop(this, R.raw.my_song_name);   // Gapless loop helper.
```

#### Play Music (Path):

```
HXMusic.music()
       .load("https://some-fake-url.com/song.mp3")  // Sets the path location of the song. [REQUIRED]
       .title("My Awesome Song")                    // Sets the title of the song. [OPTIONAL]
       .artist("Mr. Anonymous")                     // Sets the artist of the song. [OPTIONAL]
       .date("January 1, 1998")                     // Sets the date of the song. [OPTIONAL]
       .at(5000)                                    // Sets start position in milliseconds. [OPTIONAL]
       .gapless(true)                               // Enables gapless playback for this song. [OPTIONAL]
       .looped(true)                                // Sets the song to be looped. [OPTIONAL]
       .play(this);                                 // Plays the song. [REQUIRED]
```

#### Pause Music:

```
HXMusic.pause();               // Pauses any song that is playing in the background.
```

#### Resume Music:

```
HXMusic.resume(this);          // Resumes playback of the last played song at the position where it left off.
```

#### Stop Music:

```
HXMusic.stop();                // Stops all music playing in the background.
```

#### Song Playing

```
HXMusic.isPlaying();                // Returns a boolean to determine if the song is currently playing or not.
```

#### Song Position

```
HXMusic.getPosition();              // Returns the current position of the song, represented as an int value.
```

#### Music Listener

```
HXMusic.setListener(this);          // Sets a HXMusicListener interface to HXMusic. Use this if you need to monitor events from HXMusic.
```

`HXMusicListener` now supports an optional error callback:

```
@Override
public void onMusicError(HXMusicItem music, int what, int extra) {
    // Optional: monitor MediaPlayer failures for diagnostics/retry logic.
}
```

#### Music Status

```
HXMusic.getStatus();                // Returns a String text message regarding the status of HXMusic.
```

#### Enable/Disable Music:

```
HXMusic.enable(true);               // Enables/disables music playback.
```

#### Logging:

```
HXMusic.logging(true);               // Enables/disables log output.
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

Shortcut helpers are also available:

```
HXSound.play(this, R.raw.my_sound_effect);           // Play once.
HXSound.play(this, R.raw.my_sound_effect, true);     // Play looped.
```

#### Pause Sound:

```
HXSound.pause();              // Pauses all looping sounds playing in the background.
```

#### Resume Sound:

```
HXSound.resume();              // Resumes playback of all looping sounds previously played in the background.
```

#### Load Sound:

```
HXSound.load(soundResourceList, this);   // Pre-loads a list of sound resources into HXSound.
```


#### Enable Multiple Sound Engines (Deprecated Compatibility API):

```
HXSound.engines(2);                  // Retained for compatibility. Ignored on API 21+.
```

#### Enable/Disable Sound:

```
HXSound.enable(true);               // Enables/disables sound playback.
```

#### Re-Initialize Sound:

```
HXSound.reinitialize(this);             // Deprecated compatibility API. Rebuilds SoundPool and reloads cached sound resources.
```

#### Logging:

```
HXSound.logging(true);               // Enables/disables log output.
```

#### Clear Sound:

```
HXSound.clear();                    // Clears the HXSound instance. Should be called when HXSound is no longer in use.
```

Voilà! Also very simple! No need to deal with SoundPool!

## Notes

- MIN SDK: HXAudio now targets API 21+. Legacy API 9-20 SoundPool/MediaPlayer compatibility branches were removed.

- GAPLESS PLAYBACK: Use ```.gapless(true)``` for looped tracks. If the secondary MediaPlayer cannot be prepared, HXAudio automatically falls back to standard `MediaPlayer.setLooping(true)` behavior.

- THREADING: Builder `play()` and `resume()` calls are queued onto a serialized background executor so network/resource setup does not block the main thread.

- NETWORK SECURITY (TARGET SDK 37): Remote streaming should use **HTTPS** URLs. On modern targets, cleartext `http://` traffic is blocked by default unless explicitly allowed via `android:usesCleartextTraffic` or a `networkSecurityConfig` domain exception.

- RELEASE: As HXMusic and HXSound are singleton objects, it is recommended to call HXMusic.clear() & HXSound.clear() when audio playback is no longer needed. It is recommended to call these in the onDestroy() method of your Activity or Fragment.

## Migrating from HXAudio 3.x to HXAudio 4.x

Use this checklist when migrating from 3.x code.

### 1) Android SDK Baseline
- `hxaudio` now requires **minSdk 21+**.
- Any consuming app/module using HXAudio must also set `minSdk` to at least 21.
- The demo/app is configured for **targetSdk 37**.
- HXAudio 4.x supports **API 21 (Lollipop) and above**.
- Support for **API 9-20** (including Gingerbread, Honeycomb, Ice Cream Sandwich, Jelly Bean, and KitKat) has been dropped.
- If you require compatibility with API 9-20, use the last HXAudio **3.x.x** release.

### 1.1) Remote URL Playback on Modern Targets
- Prefer `https://` media URLs for streaming.
- `http://` URLs may fail on targetSdk 37 unless cleartext traffic is explicitly enabled.
- If a legacy endpoint is HTTP-only, add a scoped `networkSecurityConfig` entry (preferred) or set `android:usesCleartextTraffic="true"` (broader, less secure).

### 2) Legacy Compatibility APIs
- `HXSound.engines(int)` is now a compatibility-only API and is ignored on API 21+.
- `HXSound.reinitialize(Context)` is retained for manual reset scenarios, but is deprecated for normal usage.

### 3) Threading Behavior
- Builder `play()`/`resume()` operations are now serialized on background executors for safer, non-blocking setup.
- Existing call sites do not need to change, but timing assumptions around immediate setup should rely on listener callbacks (`onMusicPrepared`) instead of synchronous expectations.

### 4) Error Handling Enhancements
- `HXMusicListener` includes a new optional callback:
  - `onMusicError(HXMusicItem music, int what, int extra)`
- Existing listener implementations remain source-compatible because this method has a default implementation.

### 5) New Convenience APIs (Optional)
- You can keep builder usage as-is, or adopt additive shortcut methods:
  - `HXMusic.play(context, resource)`
  - `HXMusic.play(context, resource, looped)`
  - `HXMusic.play(context, url)`
  - `HXMusic.play(context, url, looped)`
  - `HXMusic.playGaplessLoop(context, resourceOrUrl)`
  - `HXSound.play(context, resource)`
  - `HXSound.play(context, resource, looped)`

### 6) Position Units
- `HXMusicBuilder.at(int)` uses **milliseconds**.
- Ensure any prior seconds-based usage is converted (`5s` -> `5000`).

## License

    Copyright 2026 Michael Huh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
