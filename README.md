# Keybind Bug Fixes

Keybind Bug Fixes is a **100% client-side** Minecraft mod that fixes keybind-related bugs, adds keybind-related tweaks, and adds new keybinds.

**Every feature is configurable.**

## Features
### Bug Fixes:
- Fix pressing F3 twice to open the debug screen [[MC-183776]](https://bugs.mojang.com/browse/MC-183776)
- Fix game mode switcher resetting on death [[MC-259571]](https://bugs.mojang.com/browse/MC-259571)
- Fix toggle key resetting on death [[MC-263293]](https://bugs.mojang.com/browse/MC-263293)
- Fix pick key dragging in inventory [[MC-117771]](https://bugs.mojang.com/browse/MC-117771)
- Fix toggle sneak state on dismount [[MC-169163]](https://bugs.mojang.com/browse/MC-169163)
- Fix toggle key state when used as a modifier
### Tweaks:
- Remove keybind conflicts
- Reload resources anywhere [[MC-269020]](https://bugs.mojang.com/browse/MC-269020)
### Keybinds:
- `F3` Debug Screen
- `F4` Game Mode Switcher

For a more detailed description, see the [translations file](src/main/resources/assets/keybindbugfixes/lang/en_us.json) or use [Mod Menu](https://modrinth.com/mod/modmenu) alongside the mod.

## Configuration
- Configurations can be changed in-game using [Mod Menu](https://modrinth.com/mod/modmenu). Changes apply without restarting Minecraft.
- If you don't want to use Mod Menu, configurations can be found in `.minecraft/config/keybindbugfixes.json`. However, changes will require restarting Minecraft to apply.

When loaded alongside another mod, conflicting features will be automatically disabled.<br>
If you encounter an incompatibility with a popular mod, please [submit an issue](https://github.com/RayZ-R/keybindbugfixes/issues).

Additionally, features can be manually disabled by setting their value to `null` in the `.minecraft/config/keybindbugfixes.json` file.
For example: `"bugfix.fix_pressing_f3_twice": null`.<br>
This will remove the feature from the config screen and prevent its mixins from being applied.
It can be used to disable specific features in a modpack or as a quick fix to avoid crashes caused by incompatible mods.

## Downloading
Stable releases can be downloaded from the [Modrinth](https://modrinth.com/mod/keybindbugfixes) page.
Alternatively, you can build the project from sources by running `./gradlew build`.

## License
Keybind Bug Fixes is licensed under [LGPL-3.0-only license](LICENSE).