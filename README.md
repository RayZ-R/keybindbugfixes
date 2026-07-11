# Keybind Bug Fixes

Keybind Bug Fixes is a **100% client-side** Minecraft mod that
fixes keybind-related bugs, adds keybind-related tweaks, and adds new keybinds.

**Every feature is configurable.**

## Features

### Bug Fixes:

- Fix pick key dragging in inventory [[MC-117771]](https://bugs.mojang.com/browse/MC-117771)
- Fix toggle sneak state on dismount [[MC-169163]](https://bugs.mojang.com/browse/MC-169163)
- Fix toggle key resetting when viewing any screen [[MC-301281]](https://bugs.mojang.com/browse/MC-301281)
- Fix toggle key state when used as a modifier
- Fix toggle debug screen when rebinding F3
- Fix debug keybind conflicts

### Tweaks:

- Drop when holding an item

For a more detailed description, see the [translations file](src/main/resources/assets/keybindbugfixes/lang/en_us.json)
or the config screen by using [Mod Menu](https://modrinth.com/mod/modmenu) alongside the mod.

## Configuration

- Configurations can be changed in-game using [Mod Menu](https://modrinth.com/mod/modmenu). 
  Changes apply without restarting Minecraft.
- If you don't want to use Mod Menu, configurations can be found in `.minecraft/config/keybindbugfixes.json`.
  However, changes will require restarting Minecraft to apply.

When loaded alongside another mod, conflicting features will be automatically disabled.<br>
If you encounter an incompatibility with a popular mod, please [submit an issue](https://github.com/RayZ-R/keybindbugfixes/issues).

## Downloading

Stable releases can be downloaded from the [Modrinth page](https://modrinth.com/mod/keybindbugfixes) or the [GitHub releases page](https://github.com/RayZ-R/keybindbugfixes/releases).
You can also build the project from sources by running `./gradlew build`.

## License

Keybind Bug Fixes is licensed under the [LGPL-3.0-only license](LICENSE).