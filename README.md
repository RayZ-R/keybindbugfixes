# Keybind Bug Fixes

Keybind Bug Fixes is a **100% client-side** Minecraft mod that fixes keybind-related bugs, adds keybind-related tweaks, and adds new keybinds.

**Every feature is configurable.**

## Features
### Bug Fixes:
- Fix pick key dragging in inventory [[MC-117771]](https://bugs.mojang.com/browse/MC-117771)
- Fix toggle sneak state on dismount [[MC-169163]](https://bugs.mojang.com/browse/MC-169163)
- Fix toggle key resetting when viewing any screen [[MC-300695]](https://bugs.mojang.com/browse/MC-300695)
- Fix toggle key state when used as a modifier
- Fix toggle debug screen when rebinding F3
### Tweaks:
- Drop when holding an item
### Keybinds:
- `F3` Debug Screen
- `F4` Game Mode Switcher

For a more detailed description, see the [translations file](src/main/resources/assets/keybindbugfixes/lang/en_us.json) or the config screen by using [Mod Menu](https://modrinth.com/mod/modmenu) alongside the mod.

## Configuration
- Configurations can be changed in-game using [Mod Menu](https://modrinth.com/mod/modmenu). Changes apply without restarting Minecraft.
- If you don't want to use Mod Menu, configurations can be found in `.minecraft/config/keybindbugfixes.json`. However, changes will require restarting Minecraft to apply.

When loaded alongside another mod, conflicting features will be automatically disabled.<br>
If you encounter an incompatibility with a popular mod, please [submit an issue](https://github.com/RayZ-R/keybindbugfixes/issues).

Features can be manually disabled by setting their value to `null` in the `.minecraft/config/keybindbugfixes.json` file.
For example: `"bugfix.fix_pressing_f3_twice": null`.<br>
This will remove the feature from the config screen and its mixins won't be applied.
This can be used to hide disabled features, preventing the player from changing them in-game, or as a temporary fix to avoid crashes caused by mod conflicts.

## Downloading
Stable releases can be downloaded from the [Modrinth page](https://modrinth.com/mod/keybindbugfixes) or the [GitHub releases page](https://github.com/RayZ-R/keybindbugfixes/releases).
You can also build the project from sources by running `./gradlew build`.

## License
Keybind Bug Fixes is licensed under the [LGPL-3.0-only license](LICENSE).