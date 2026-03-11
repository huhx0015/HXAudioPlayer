---
name: hxaudio-player
description: HX Audio Player for music and sound playback. Use when implementing music playback, sound effects, HXMusic, HXSound, or audio in HXAudioPlayer. Library is in the hxaudio module.
---

# HX Audio Player

Use HX Audio Player for music and sound in HXAudioPlayer. The library is in the `hxaudio` module. Supports Android 2.3+ with workarounds for MediaPlayer/SoundPool bugs.

**Audio files:** Place in `res/raw/` (e.g. `res/raw/my_song.mp3` → `R.raw.my_song`).

## Music Playback (HXMusic)

**Play from resource:**
```java
HXMusic.music()
    .load(R.raw.my_song_name)    // [REQUIRED]
    .title("My Awesome Song")    // [OPTIONAL]
    .artist("Mr. Anonymous")     // [OPTIONAL]
    .date("January 1, 1998")     // [OPTIONAL]
    .at(5)                       // Start position (seconds)
    .gapless(true)               // Gapless playback (API 16+)
    .looped(true)                // Loop
    .play(this);                 // [REQUIRED]
```

**Play from path/URL:**
```java
HXMusic.music()
    .load("http://example.com/song.mp3")
    .title("My Awesome Song")
    .play(this);
```

**Control:**
- `HXMusic.pause()` — Pause
- `HXMusic.resume(this)` — Resume
- `HXMusic.stop()` — Stop all
- `HXMusic.isPlaying()` — Boolean
- `HXMusic.getPosition()` — Current position (seconds)
- `HXMusic.setListener(this)` — HXMusicListener
- `HXMusic.getStatus()` — Status string
- `HXMusic.enable(true)` — Enable/disable
- `HXMusic.logging(true)` — Log output
- `HXMusic.clear()` — Clear when app is terminating

**Lifecycle (Activity/Fragment):**
- `onPause()` → `HXMusic.pause()` — Pause when backgrounded
- `onResume()` → `HXMusic.resume(this)` — Resume when foreground
- App terminating → `HXMusic.clear()` — Clean up in `onDestroy()`

## Sound Playback (HXSound)

**Play sound:**
```java
HXSound.sound()
    .load(R.raw.my_sound_effect)  // [REQUIRED]
    .looped(true)                 // [OPTIONAL]
    .play(this);                  // [REQUIRED]
```

**Control:**
- `HXSound.pause()` — Pause looping sounds
- `HXSound.resume()` — Resume
- `HXSound.load(soundResourceList, context)` — Pre-load resources
- `HXSound.engines(2)` — Multiple sound engines (API 9–10 only)
- `HXSound.enable(true)` — Enable/disable
- `HXSound.reinitialize(this)` — Re-init (API 9–10)
- `HXSound.logging(true)` — Log output
- `HXSound.clear()` — Clear when no longer needed

## Module

Add dependency in `build.gradle`:
```gradle
implementation project(':hxaudio')
```

## Notes

- **API 9–10:** HXSound creates 2 HXSoundEngine instances by default to work around SoundPool buffer limits. Keep sound effects small (<100 KB, 64 kbps or less).
- **Gapless:** `.gapless(true)` only on API 16+; falls back to standard loop on older devices.
- **Release:** Call `HXMusic.clear()` and `HXSound.clear()` in `onDestroy()` when audio is no longer needed.
