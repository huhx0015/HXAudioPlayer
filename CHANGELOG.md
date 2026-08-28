# Changelog

All notable changes to HXAudioPlayer are documented in this file.

## 4.0.1

### Fixed

- **Fatal `IllegalStateException` crash during gapless playback.** Apps using `.gapless(true)` (or
  `HXMusic.playGaplessLoop(...)`) could crash with an uncatchable `IllegalStateException` originating
  entirely inside `android.media.MediaPlayer`, with no HXAudio frames in the stack trace:

  ```
  java.lang.IllegalStateException
      at android.media.MediaPlayer._start(MediaPlayer.java)
      at android.media.MediaPlayer.startImpl(MediaPlayer.java:1419)
      at android.media.MediaPlayer.start(MediaPlayer.java:1393)
      at android.media.MediaPlayer$6.run(MediaPlayer.java:3830)
      at java.lang.Thread.run(Thread.java:1564)
  ```

  When a looped track reached its end, the native media framework promoted the queued
  `setNextMediaPlayer()` player and called `start()` on it from a detached thread that has no
  exception handler. If the app paused, stopped, cleared, or switched tracks at that same instant,
  `HXMusicEngine` had already reset and released that player, so the framework's `start()` call
  terminated the process. Unlinked players are now muted and torn down after a short grace period
  instead of immediately, so an unavoidable auto-start is silent and harmless rather than fatal.

- **Playback teardown is now correctly ordered.** `HXMusic.pause()`, `HXMusic.stop()`,
  `HXMusic.clear()`, and switching tracks now halt the outgoing player *before* unlinking the gapless
  chain, so the framework can no longer hand playback off to a player that is being torn down.
  `release()` now also unlinks the chain before releasing, which it previously skipped.

- **`HXMusic.pause()` now always unlinks the queued player.** The unlink was previously skipped when
  the outgoing player reported that it was not playing, which was the case at exactly the moment of a
  gapless handoff.

- **Stale MediaPlayer callbacks are now discarded.** Prepared and completion callbacks arriving from
  a player that has since been replaced or torn down are ignored, rather than being applied to
  whichever player is current. This also prevents a superseded track from starting playback after a
  fast track switch.

### Changed

- **Thread safety.** All access to the engine's MediaPlayer references and playback state is now
  guarded by a single internal lock. MediaPlayer callbacks, audio focus changes, and the HXMusic
  operation executor reach the engine from three different threads and previously mutated this state
  unsynchronized. Listener callbacks are invoked outside the lock so application code can safely call
  back into `HXMusic`.

- **Internal cleanup.** The two near-duplicate completion listeners were consolidated into one,
  removing the listener-swapping that made the gapless handoff difficult to follow.

### Notes

- No public API changes. No source or behavioral changes are required in apps upgrading from 4.0.0.
- Apps on 4.0.0 that cannot upgrade immediately can avoid the crash by using `.looped(true)` instead
  of `.gapless(true)`, which never engages `setNextMediaPlayer()`.

## 4.0.0

### Changed

- **API 21+ baseline.** `minSdk` is now 21 and the legacy API 9-20 SoundPool and MediaPlayer
  compatibility branches were removed. Apps requiring API 9-20 should stay on the last 3.x release.
- **Target SDK 37.** The library and demo app compile and target SDK 37, including edge-to-edge
  changes in the demo app.
- **Threading.** Builder `play()` and `resume()` operations are serialized onto a background executor
  so resource and network setup no longer blocks the main thread. Call sites do not change, but
  timing assumptions should rely on `onMusicPrepared` rather than synchronous setup.
- **Java 11** source and target compatibility.
- `HXSound.engines(int)` is now compatibility-only and ignored on API 21+. `HXSound.reinitialize(Context)`
  is retained for manual resets but deprecated for normal usage.

### Added

- `HXMusicListener.onMusicError(HXMusicItem music, int what, int extra)`, an optional callback with a
  default implementation, so existing listeners remain source-compatible.
- Convenience playback methods: `HXMusic.play(context, resource)`,
  `HXMusic.play(context, resource, looped)`, `HXMusic.play(context, url)`,
  `HXMusic.play(context, url, looped)`, `HXMusic.playGaplessLoop(context, resourceOrUrl)`,
  `HXSound.play(context, resource)`, and `HXSound.play(context, resource, looped)`.
- Gapless playback falls back to `MediaPlayer.setLooping(true)` when the secondary MediaPlayer cannot
  be prepared.

### Notes

- Remote streaming should use `https://` URLs. On targetSdk 37, cleartext `http://` traffic is blocked
  by default unless allowed via `android:usesCleartextTraffic` or a `networkSecurityConfig` domain
  exception.
- `HXMusicBuilder.at(int)` uses milliseconds.
