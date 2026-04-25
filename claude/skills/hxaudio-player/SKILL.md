---
name: hxaudio-player
description: HX Audio Player for music and sound playback. Use when implementing music playback, sound effects, HXMusic, HXSound, or audio in HXAudioPlayer. Library is in the hxaudio module.
---

# HX Audio Player

Use HX Audio Player for music and sound in HXAudioPlayer. The library is in the `hxaudio` module. Targets Android 5.0+ (API 21) and uses modern MediaPlayer/SoundPool initialization paths.

**Audio files:** Place in `res/raw/` (e.g. `res/raw/my_song.mp3` → `R.raw.my_song`).

## Music Playback (HXMusic)

**Play from resource:**
```java
HXMusic.music()
    .load(R.raw.my_song_name)    // [REQUIRED]
    .title("My Awesome Song")    // [OPTIONAL]
    .artist("Mr. Anonymous")     // [OPTIONAL]
    .date("January 1, 1998")     // [OPTIONAL]
    .at(5000)                    // Start position (milliseconds)
    .gapless(true)               // Gapless loop playback
    .looped(true)                // Loop
    .play(this);                 // [REQUIRED]
```

**Shortcut helpers (optional):**
```java
HXMusic.play(this, R.raw.my_song_name);            // Play once
HXMusic.play(this, R.raw.my_song_name, true);      // Loop
HXMusic.playGaplessLoop(this, R.raw.my_song_name); // Gapless loop
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
- `HXMusic.getPosition()` — Current position (milliseconds)
- `HXMusic.setListener(this)` — HXMusicListener
- `HXMusic.getStatus()` — Status string
- `HXMusic.enable(true)` — Enable/disable
- `HXMusic.logging(true)` — Log output
- `HXMusic.clear()` — Clear when app is terminating

**Optional error callback (`HXMusicListener`):**
```java
@Override
public void onMusicError(HXMusicItem music, int what, int extra) {
    // Optional: monitor MediaPlayer failures for diagnostics/recovery logic.
}
```

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

**Shortcut helpers (optional):**
```java
HXSound.play(this, R.raw.my_sound_effect);        // Play once
HXSound.play(this, R.raw.my_sound_effect, true);  // Loop
```

**Control:**
- `HXSound.pause()` — Pause looping sounds
- `HXSound.resume()` — Resume
- `HXSound.load(soundResourceList, context)` — Pre-load resources
- `HXSound.engines(2)` — Retained for compatibility, ignored on API 21+
- `HXSound.enable(true)` — Enable/disable
- `HXSound.reinitialize(this)` — Deprecated compatibility API for explicit SoundPool reset
- `HXSound.logging(true)` — Log output
- `HXSound.clear()` — Clear when no longer needed

## Module

Add dependency in `build.gradle`:
```gradle
implementation project(':hxaudio')
```

## Notes

- **Min SDK:** `hxaudio` targets API 21+; legacy Gingerbread/Honeycomb compatibility branches are removed.
- **Gapless:** `.gapless(true)` enables seamless loop behavior for looped tracks in the modern playback path.
- **Threading:** `HXMusic.play()/resume()` and `HXSound.play()/load()/reinitialize()` are dispatched on serialized background executors.
- **Release:** Call `HXMusic.clear()` and `HXSound.clear()` in `onDestroy()` when audio is no longer needed.
