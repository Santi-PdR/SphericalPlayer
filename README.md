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

When a moving player in sphere mode contacts a living entity, the mod performs a normal player attack with the main-hand item. This preserves unarmed damage, weapon damage (including modded items), Strength and other attribute/effect modifiers, enchantments, item hit hooks, and the held weapon's normal attack cooldown. The attack is performed on the server.
