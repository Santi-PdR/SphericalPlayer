# SphericalPlayer for Forge 1.20.1

This project ports SphericalPlayer by TACOWASA_059 to Forge 1.20.1. The MIT license and attribution from the original project are preserved.

## Quick test commands

Requires permission level 2 (operator):

- `/sphericalplayer on` — turn sphere mode on for yourself.
- `/sphericalplayer off` — turn sphere mode off for yourself.
- `/sphericalplayer on <player>` and `/sphericalplayer off <player>` — set another player's mode.

The original `/setSphere isBall set <targets> <true|false>` command remains available for compatibility. The quick commands change the player's synced mode and dimensions at runtime; they do not reload Mixins. Launch Minecraft with the full modpack first, then toggle the mode to check for crashes or visual conflicts.

## Build

Use Java 17 and run `./gradlew build`. The GitHub Actions workflow also launches a headless client to check startup and Mixin application.


### Damage while rolling

While sphere mode is enabled, colliding with a living entity deals damage equal to the player's Strength effect level. For example, Strength 400 (effect amplifier 399) deals 400 base damage. A short per-target cooldown prevents repeated damage every tick while contact continues. This is evaluated on the server.
