# RnfMusic: changes from SimpMusic 2.3.0

## Rebrand
- App name `RnfMusic`; `simpmusic`/`SimpMusic` -> `rnfmusic`/`RnfMusic`; `maxrave` -> `rnfverse` in code, packages, resources, folders and file names.
- Application id / namespace: `com.rnfverse.rnfmusic` (separate app + separate data from SimpMusic).
- Logo: launcher (adaptive, legacy, round, themed/monochrome), notification small icon, in-app logo, desktop .ico/.icns/AppImage, store icons.
- `core` submodule vendored into `core/`.

## No data sent to the SimpMusic project
| Was | Now |
|---|---|
| Lyrics server `api-lyrics.simpmusic.org` (lookups, votes, uploads) | `LYRICS_SERVER_URL` = `http://127.0.0.1:9/v1/` (nothing leaves the device). Set to your own server if you host one. |
| `remote-config.json` on upstream GitHub (TIDAL credentials, launch promos), fetched every launch | Disabled, returns empty config. TIDAL metadata therefore off. |
| Update checks against upstream releases / F-Droid | Disabled. |
| "Official build" signature check (F-Droid lookup) | Disabled. |
| Blog RSS notifications from simpmusic.org | Removed (worker cancelled). |
| Sentry / Crashlytics | Only active if you add `SENTRY_DSN` to `local.properties`; empty by default. |

Notifications still present are the app's own (followed-artist releases, downloads, playback), all branded RnfMusic.

## Still contacts third parties / upstream-hosted files (decide yourself)
- `raw.githubusercontent.com/maxrave-dev/simpmusic-files/.../player_configs.json`: YouTube player-config registry used for playback. Reveals only your IP, but breaks if you remove it. Mirror it in your own repo to fully cut ties.
- Desktop build downloads mpv natives and the Kuromoji dictionary from `maxrave-dev/simpmusic-files` releases.
- Third-party services: YouTube Music, LRCLIB, boidu lyrics API, Apple Music web API (word-synced lyrics), SponsorBlock, Last.fm (full build), Discord RPC.
- Dependencies published under upstream coordinates (`com.github.maxrave-dev:*`, `io.github.maxrave-dev:*`, `org.simpmusic:*`) were intentionally NOT renamed or the build would fail.

## Placeholders to replace
- `rnfmusic.org` links/deep-link host and `github.com/rnfverse-dev/RnfMusic` are placeholders (you may not own them).
- `vivoAndroidApp` uses applicationId `com.spotify.music` (upstream vivo variant). Delete that module if you don't need it.
- Not compiled here (no network in the sandbox). Expect to fix small build errors on first Gradle sync.
