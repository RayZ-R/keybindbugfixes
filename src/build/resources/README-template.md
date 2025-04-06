# \{mod.name}

\{mod.name} is a **100% client side** Minecraft mod, that \{description}

**Every feature is configurable.**

## Features
\{features}

For a more detailed description, see the [translations file](\[modrinth-only]\{mod.contact.sources}/blob/\{gradle.minecraft_version}/\[modrinth-only]src/main/resources/assets/\{mod.id}/lang/en_us.json) or use [Mod Menu](https://modrinth.com/mod/modmenu) alongside the mod.

## Configuration
- Configurations can be changed in-game using [Mod Menu](https://modrinth.com/mod/modmenu). Changes apply without the need to restart Minecraft.
- If you don't want to use Mod Menu, configurations can be found in: `.minecraft/config/\{mod.id}.json`, however, changes will require a restart of Minecraft to apply.

When loaded alongside another mod, conflicting features will be automatically disabled.<br>
If you encounter an incompatibility with a popular mod, please [submit an issue](\{mod.contact.issues}).

Additionally, features can be manually disabled by setting the value to `null` in the `.minecraft/config/\{mod.id}.json` file.
For example: `"bugfix.fix_pressing_f3_twice": null`.<br>
It will remove the feature from the config screen and prevent used mixins from being applied.
This can be used to disable specific features in a modpack or as a quick fix to avoid crashes caused by incompatible mods.
\[github-only]

## Downloading
Stable releases can be downloaded from the [Modrinth](\{mod.contact.homepage}) page.
Alternatively, you can build the project from sources by running `./gradlew build`.

## License
\{mod.name} is licensed under [\{mod.license} license](LICENSE).
\[github-only]