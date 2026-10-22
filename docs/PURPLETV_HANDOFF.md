# PurpleTV VOD playback handoff

## Published, working feature

`FineGrainedVodPlaybackSpeedPatch.kt` is the completed Morphe patch for **PurpleTV
25.3.0** (`tv.orange`). It replaces the application's `PlaybackRateSetting` value
list with the exact rates from **0.50x through 2.00x, in 0.05x increments**.

The patch is intentionally specific to PurpleTV 25.3.0, whose original APK is:

`https://purpletv.aeong.win/Beta/2738.apk`

The implementation targets:

```text
Ltv/twitch/android/shared/preferences/PlaybackRateSetting;-><clinit>()V
```

It creates an `ArrayList` and emits every speed as an integer divided by 20. That
avoids floating-point accumulation, so labels such as `1.05x` stay exact. The
patch source is:

```text
patches/src/main/kotlin/app/template/patches/purpletv/playback/
FineGrainedVodPlaybackSpeedPatch.kt
```

It was built, signed, installed via ADB, and confirmed working on-device. The
published bundle is available through the repository's `v1.0.0-dev.2` release.

## Hold-to-2x VOD gesture: investigation status

This feature is **not yet published or released**. The desired behavior is:

1. In a landscape VOD player, hold a finger on the unobstructed player for
   roughly 400 ms.
2. Temporarily set speed to `2.0x`.
3. On `ACTION_UP` or `ACTION_CANCEL`, restore the precise speed that was active
   before the hold (for example, `1.15x`).
4. A normal tap, a drag, a multi-touch gesture, chat, or visible control must
   not activate it.

### Relevant implementation points discovered

PurpleTV already has an app-owned player gesture layer. It is a better hook than
Twitch's general player UI because it already rejects overlays, chat, controls,
and vertical swipes:

```text
tv.purple.monolith.features.swipper.bridge.PlayerWrapper
tv.purple.monolith.features.swipper.bridge.RefactorPlayerWrapper
tv.purple.monolith.features.swipper.bridge.WrapperDelegate
```

`WrapperDelegate` receives `MotionEvent` actions and owns a main-thread
`Handler`, touch-slop detection, collision checks, and player-area checks. A
safe implementation should schedule the 400 ms callback on `ACTION_DOWN`,
cancel it when movement exceeds touch slop or another pointer appears, and
always restore on `ACTION_UP`/`ACTION_CANCEL`.

The VOD playback route is:

```text
VodPresenter$observeSelectedPlaybackRateUpdates$2
  -> IVodPlayerPresenter / PlayerPresenter.setPlaybackRate(Float)
```

For a hold gesture, retain the last user-selected rate separately from the
temporary `2.0x` rate. Do not update that saved rate while the temporary rate is
active; otherwise releasing would restore `2.0x` instead of the user's prior
selection.

### Prior local experiment and caveat

A local smali proof-of-concept attempted to cache the active VOD presenter and
current user rate in `VodPresenter$observeSelectedPlaybackRateUpdates$2`, then
use `WrapperDelegate` as a delayed `Runnable`. It was never packaged into a
verified APK: Apktool's full 200 MB repack stalled/corrupted its disposable
resource archive. Treat that experiment as design research only, not code to
publish or ship.

For the production Morphe patch, prefer a small extension/helper class (or an
existing stable patcher API for adding fields/methods) over large inline smali
injections. The patch must be validated against PurpleTV 25.3.0 and then
installed with `adb install -r` before release.

## Suggested next steps

1. Add a dedicated PurpleTV hold-speed patch beside the fine-grained speed
   patch.
2. Locate/confirm the Morphe mechanism for adding a helper class or extending a
   class, then inject the gesture only in the PurpleTV wrapper layer.
3. Compile the patch bundle, apply it to the original 25.3.0 APK, sign it, and
   test the hold, release, cancel, tap, drag, chat, and speed-restoration paths.
4. Only after on-device confirmation, merge/push to `dev` so the release action
   publishes the next `.mpp` bundle.
