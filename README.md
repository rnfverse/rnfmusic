# RnfMusic

A music app for Android and Desktop (Compose Multiplatform), by **rnfverse**.

RnfMusic is a modified fork of [SimpMusic](https://github.com/maxrave-dev/SimpMusic) by maxrave-dev and its
[core modules](https://github.com/maxrave-dev/core). It is licensed under **GPL-3.0** like the original (see `LICENSE`);
if you distribute it you must provide the source under the same license and keep the original copyright notices.

## What differs from upstream
See [`RNFMUSIC_CHANGES.md`](RNFMUSIC_CHANGES.md): rebrand, own package/app id (`com.rnfverse.rnfmusic`, so its data is
separate from any installed SimpMusic), and removal of calls to the upstream project's servers.

## Build
The former `core` git submodule is vendored in `core/`. Open in Android Studio / run `./gradlew :androidApp:assembleDebug`
(JDK 21+, network access to Maven/JitPack required).
