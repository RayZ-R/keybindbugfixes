package readmegenerator;

import com.google.gson.*;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.ConfigManager;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMeGenerator {
    private static String getString(JsonObject json, String key) {
        JsonPrimitive primitive = json.getAsJsonPrimitive(key);

        if (primitive != null && primitive.isString()) {
            return primitive.getAsString();
        } else {
            return "`unknown`";
        }
    }

    private static String getProperty(Properties properties, String key) {
        String property = properties.getProperty(key);
        return property != null ? property : "`unknown`";
    }

    private static JsonObject getJsonObject(JsonObject json, String key) {
        JsonElement jsonElement = json.get(key);

        if (jsonElement != null && jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else {
            return new JsonObject();
        }
    }

    private static JsonObject parseJson(String path) throws FileNotFoundException {
        File file = new File(path);

        if (!file.exists()) {
            throw new Error("Couldn't create README.md, \"" + file.getName() + "\" doesn't exist");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        return JsonParser.parseReader(reader).getAsJsonObject();
    }

    private static JsonObject getModLangJson() throws FileNotFoundException {
        return parseJson(MOD_LANG_FILE_PATH);
    }

    private static JsonObject getLangJson() {
        InputStream inputStream = ReadMeGenerator.class.getResourceAsStream(MINECRAFT_LANG_FILE_PATH);
        assert inputStream != null;
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return GSON.fromJson(reader, JsonObject.class);
    }

    private static JsonObject getModJson() throws FileNotFoundException {
        return parseJson(FABRIC_MOD_JSON_PATH);
    }

    private static Properties getGradleProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(GRADLE_PROPERTIES_PATH));
        return properties;
    }

    private static String getReadMeTemplateString() throws URISyntaxException, IOException {
        URL url = ReadMeGenerator.class.getClassLoader().getResource(README_TEMPLATE_PATH);
        assert url != null;

        return Files.readString(Paths.get(url.toURI()));
    }

    private static final String MOD_LANG_FILE_PATH = "src/main/resources/assets/" + KeybindBugFixes.MOD_ID + "/lang/en_us.json";
    private static final String MINECRAFT_LANG_FILE_PATH = "/assets/minecraft/lang/en_us.json";
    private static final String FABRIC_MOD_JSON_PATH = "src/main/resources/fabric.mod.json";
    private static final String GRADLE_PROPERTIES_PATH = "gradle.properties";
    private static final String README_TEMPLATE_PATH = "README-template.md";
    private static final String GITHUB_README_PATH = "README.md";
    private static final String MODRINTH_README_PATH = "build/README-modrinth.md";

    private static final Gson GSON = new Gson();
    private static final String TEMPLATE_STRING;
    private static final Properties GRADLE_PROPERTIES;
    private static final JsonObject MOD_LANG_JSON;
    private static final JsonObject LANG_JSON;
    private static final JsonObject MOD_JSON;

    static {
        ConfigManager.initOptionInfos();
        ConfigManager.initOptions();

        try {
            TEMPLATE_STRING = getReadMeTemplateString();
            GRADLE_PROPERTIES = getGradleProperties();
            MOD_LANG_JSON = getModLangJson();
            LANG_JSON = getLangJson();
            MOD_JSON = getModJson();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String commonString;
    private String githubString;
    private String modrinthString;

    private enum ToggleWriterAction {
        NotProcessed,
        ToggleGitHubWriter,
        ToggleModrinthWriter
    }

    private static String getFeaturesString() {
        List<ConfigManager.Category> categories = ConfigManager.CATEGORIES;
        StringBuilder stringBuilder = new StringBuilder();

        for (int categoryIndex = 0; categoryIndex < categories.size(); categoryIndex++) {
            ConfigManager.Category category = categories.get(categoryIndex);
            String categoryName = getString(MOD_LANG_JSON, category.translationKey());
            stringBuilder.append("### ").append(categoryName).append(":\n");

            List<ConfigManager.Option<?>> options = category.options();

            for (int optionIndex = 0; optionIndex < options.size(); optionIndex++) {
                ConfigManager.Option<?> option = options.get(optionIndex);
                String optionName = getString(MOD_LANG_JSON, option.translationKey());
                stringBuilder.append("- ");

                if (option instanceof ConfigManager.BugOption bugOption) {
                    String link = bugOption.link();

                    if (link != null) {
                        String name = link.substring(link.lastIndexOf('/') + 1);
                        stringBuilder.append(optionName)
                                .append(" [[")
                                .append(name)
                                .append("]](")
                                .append(bugOption.link())
                                .append(")");
                    } else {
                        stringBuilder.append(optionName);
                    }
                } else if (option instanceof ConfigManager.KeybindOption keybindOption) {
                    String defaultValueName = getString(LANG_JSON, keybindOption.defaultValue().getTranslationKey());
                    stringBuilder.append("`").append(defaultValueName).append("` ").append(optionName);
                } else {
                    stringBuilder.append(optionName);
                }

                if (optionIndex < options.size() - 1 || categoryIndex < categories.size() - 1) {
                    stringBuilder.append('\n');
                }
            }
        }

        return stringBuilder.toString();
    }

    private void pasteCommonBlocks(String regex, BiFunction<StringBuilder, String, Boolean> function) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.commonString);

        StringBuilder builder = new StringBuilder();
        int endIndex = 0;

        while (matcher.find()) {
            int startIndex = matcher.start();
            builder.append(this.commonString, endIndex, startIndex);
            endIndex = matcher.end();
            String content = matcher.group(1);

            if (!function.apply(builder, content)) {
                builder.append(this.commonString, startIndex, endIndex);
            }
        }

        String stringAfter = this.commonString.substring(endIndex);
        builder.append(stringAfter);

        this.commonString = builder.toString();
    }

    private void separateCommonString(String regex, Function<String, ToggleWriterAction> function) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.commonString);

        StringBuilder githubStringBuilder = new StringBuilder();
        StringBuilder modrinthStringBuilder = new StringBuilder();
        int endIndex = 0;

        boolean writeGitHubReadMe = true;
        boolean writeModrinthReadMe = true;

        while (matcher.find()) {
            int startIndex = matcher.start();
            String stringBefore = this.commonString.substring(endIndex, startIndex);
            endIndex = matcher.end();
            String content = matcher.group(1);
            if (content == null) content = matcher.group(2);

            if (writeGitHubReadMe) {
                githubStringBuilder.append(stringBefore);
            }

            if (writeModrinthReadMe) {
                modrinthStringBuilder.append(stringBefore);
            }

            switch (function.apply(content)) {
                case ToggleGitHubWriter -> writeGitHubReadMe = !writeGitHubReadMe;
                case ToggleModrinthWriter -> writeModrinthReadMe = !writeModrinthReadMe;
                case NotProcessed -> {
                    String notProcessedString = this.commonString.substring(startIndex, endIndex);

                    if (writeGitHubReadMe) {
                        githubStringBuilder.append(notProcessedString);
                    }

                    if (writeModrinthReadMe) {
                        modrinthStringBuilder.append(notProcessedString);
                    }
                }
            }
        }

        String stringAfter = this.commonString.substring(endIndex);

        if (writeGitHubReadMe) {
            githubStringBuilder.append(stringAfter);
        }

        if (writeModrinthReadMe) {
            modrinthStringBuilder.append(stringAfter);
        }

        this.githubString = githubStringBuilder.toString();
        this.modrinthString = modrinthStringBuilder.toString();
    }

    private void writeReadMe() throws IOException {
        Files.write(Paths.get(GITHUB_README_PATH), this.githubString.getBytes());
        Files.write(Paths.get(MODRINTH_README_PATH), this.modrinthString.getBytes());
    }

    private void generate() throws IOException {
        this.commonString = TEMPLATE_STRING;

        String blockRegex = "\\\\\\{([a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)*)}";
        pasteCommonBlocks(blockRegex, (builder, content) -> {
            switch (content) {
                case "features" -> {
                    builder.append(getFeaturesString());
                    return true;
                }
                case "description" -> {
                    builder.append(StringUtils.uncapitalize(getString(MOD_JSON, "description")));
                    return true;
                }
                default -> {
                    String[] words = content.split("\\.");

                    if (words.length > 1) {
                        if (words[0].equals("mod")) {
                            JsonObject currentJsonObject = MOD_JSON;
                            for (int i = 1; i < words.length - 1; i++) {
                                currentJsonObject = getJsonObject(currentJsonObject, words[i]);
                            }

                            builder.append(getString(currentJsonObject, words[words.length - 1]));
                            return true;
                        } else if (words[0].equals("gradle")) {
                            builder.append(getProperty(GRADLE_PROPERTIES, words[1]));
                            return true;
                        }
                    }

                    return false;
                }
            }
        });

        String destinationRegex = "\\r\\n\\\\\\[([a-zA-Z0-9_-]+)]|\\\\\\[([a-zA-Z0-9_-]+)]";
        separateCommonString(destinationRegex, content -> switch (content) {
            case "github-only" -> ToggleWriterAction.ToggleModrinthWriter;
            case "modrinth-only" -> ToggleWriterAction.ToggleGitHubWriter;
            default -> ToggleWriterAction.NotProcessed;
        });

        writeReadMe();
    }

    public static void main(String[] args) throws IOException {
        new ReadMeGenerator().generate();
    }
}