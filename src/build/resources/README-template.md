# \{mod.name}

\{mod.name} is a **100% client-side** Minecraft mod that \{description}

**Every feature is configurable.**

## Features
\{features}

For a more detailed description, see the [translations file](\[modrinth-only]\{mod.contact.sources}/blob/\{gradle.minecraft_version}/\[modrinth-only]src/main/resources/assets/\{mod.id}/lang/en_us.json) or the config screen by using [Mod Menu](https://modrinth.com/mod/modmenu) alongside the mod.

## Configuration
- Configurations can be changed in-game using [Mod Menu](https://modrinth.com/mod/modmenu). Changes apply without restarting Minecraft.
- If you don't want to use Mod Menu, configurations can be found in `.minecraft/config/\{mod.id}.json`. However, changes will require restarting Minecraft to apply.

When loaded alongside another mod, conflicting features will be automatically disabled.<br>
If you encounter an incompatibility with a popular mod, please [submit an issue](\{mod.contact.issues}).

Features can be manually disabled by setting their value to `null` in the `.minecraft/config/\{mod.id}.json` file.
For example: `"bugfix.fix_pressing_f3_twice": null`.<br>
This will remove the feature from the config screen and its mixins won't be applied.
This can be used to hide disabled features, preventing the player from changing them in-game, or as a temporary fix to avoid crashes caused by mod conflicts.
\[github-only]

## Downloading
Stable releases can be downloaded from the [Modrinth](\{mod.contact.homepage}) page or the [Github](\{mod.contact.sources}/releases) releases page.
You can also build the project from sources by running `./gradlew build`.

## License
\{mod.name} is licensed under [\{mod.license} license](LICENSE).
\[github-only]