package keybindbugfixes.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OptionListWidget extends ElementListWidget<OptionListWidget.WidgetEntry> {
    private final Set<KeybindWidgetEntry> keybindWidgets = Sets.newHashSet();
    private final ConfigScreen configScreen;

    private static final int OPTION_WIDTH = 174;
    private static final int BUTTON_WIDTH = 20;
    private static final int PADDING = 7;
    private static final int GAP = 4;

    public OptionListWidget(MinecraftClient client, ConfigScreen screen) {
        super(client, screen.width, screen.layout.getContentHeight(), screen.layout.getHeaderHeight(), 25);
        this.centerListVertically = false;
        this.configScreen = screen;
    }

    private void addTitle(String key) {
        TitleWidgetEntry widget = new TitleWidgetEntry(this.client, this.configScreen, key);
        this.addEntry(widget);
    }

    private void addOption(ConfigManager.Option<?> option) {
        if (option instanceof ConfigManager.BugOption bugOption) {
            BugWidgetEntry widget = new BugWidgetEntry(this.client, this.configScreen, bugOption);
            this.addEntry(widget);

        } else if (option instanceof ConfigManager.TweakOption tweakOption) {
            TweakWidgetEntry widget = new TweakWidgetEntry(this.client, this.configScreen, tweakOption);
            this.addEntry(widget);

        } else if (option instanceof ConfigManager.KeybindOption keybindOption) {
            KeybindWidgetEntry widget = new KeybindWidgetEntry(this.client, this.configScreen, keybindOption);
            this.keybindWidgets.add(widget);
            this.addEntry(widget);
        }
    }

    public void init(List<ConfigManager.Category> categories) {
        for (ConfigManager.Category category : categories) {
            boolean titleAdded = false;

            for (ConfigManager.Option<?> option : category.options()) {
                if (KeybindBugFixes.shouldAddOption(option)) {
                    if (!titleAdded) {
                        this.addTitle(category.translationKey());
                        titleAdded = true;
                    }

                    this.addOption(option);
                }
            }
        }
    }

    private void updateKeybindWarnings() {
        for (KeybindWidgetEntry widget : this.keybindWidgets) {
            widget.updateButtonWarning();
        }
    }

    @Override
    public int getRowWidth() {
        return this.configScreen.width;
    }

    @Override
    protected int getScrollbarX() {
        return this.width - 7;
    }

    public static class OptionWidget {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ConfigManager.Option<?> option;
        private final List<ButtonWidget> widgets;
        private final List<Integer> offsets;

        public OptionWidget(MinecraftClient client,
                            ConfigScreen configScreen,
                            ConfigManager.Option<?> option,
                            List<ButtonWidget> widgets,
                            List<Integer> offsets) {
            this.client = client;
            this.configScreen = configScreen;
            this.option = option;
            this.widgets = widgets;
            this.offsets = offsets;
        }

        private static void drawText(TextRenderer textRenderer, DrawContext context, Text text, int wrapX, int y) {
            int textX = PADDING + GAP;
            int textY = y + 6;

            int textWidth = wrapX - textX;

            for (OrderedText orderedText : textRenderer.wrapLines(text, textWidth)) {
                context.drawText(textRenderer, orderedText, textX, textY, Colors.WHITE, true);
                textY += 9;
            }
        }

        public void render(DrawContext context, int y, int mouseX, int mouseY, float tickDelta) {
            for (int i = 0; i < this.widgets.size(); i++) {
                ButtonWidget widget = this.widgets.get(i);
                int offset = this.offsets.get(i);

                widget.setPosition(this.configScreen.width - offset, y);
                widget.render(context, mouseX, mouseY, tickDelta);
            }

            int wrapX = this.configScreen.width - PADDING - GAP - OPTION_WIDTH - GAP;
            Text text = Text.translatable(this.option.translationKey());
            drawText(this.client.textRenderer, context, text, wrapX, y);
        }

        public List<ButtonWidget> widgets() {
            return this.widgets;
        }
    }

    public static class OptionWidgetBuilder {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ConfigManager.Option<?> option;

        private final ButtonWidget centerWidget;
        private final List<ButtonWidget> leftWidgets = Lists.newArrayList();
        private final List<ButtonWidget> rightWidgets = Lists.newArrayList();

        public OptionWidgetBuilder(MinecraftClient client,
                                   ConfigScreen configScreen,
                                   ConfigManager.Option<?> option,
                                   ButtonWidget centerWidget) {
            this.client = client;
            this.configScreen = configScreen;
            this.option = option;

            this.centerWidget = centerWidget;
        }

        public OptionWidgetBuilder addLeft(ButtonWidget widget) {
            this.leftWidgets.addFirst(widget);
            return this;
        }

        public OptionWidgetBuilder addRight(ButtonWidget widget) {
            this.rightWidgets.addLast(widget);
            return this;
        }

        public OptionWidget build() {
            List<ButtonWidget> widgets = Lists.newArrayList();
            List<Integer> offsets = Lists.newArrayList();

            widgets.addAll(this.rightWidgets);
            widgets.add(this.centerWidget);
            widgets.addAll(this.leftWidgets);

            int centerWidgetWidth = OPTION_WIDTH;
            for (ButtonWidget widget : this.leftWidgets) centerWidgetWidth -= widget.getWidth() + GAP;
            for (ButtonWidget widget : this.rightWidgets) centerWidgetWidth -= widget.getWidth() + GAP;
            this.centerWidget.setWidth(centerWidgetWidth);

            int lastOffset = PADDING;

            for (ButtonWidget widget : widgets) {
                lastOffset += widget.getWidth() + GAP;
                offsets.add(lastOffset);
            }

            return new OptionWidget(this.client, this.configScreen, this.option, widgets, offsets);
        }
    }

    public static class BugWidgetEntry extends WidgetEntry {
        private final ConfigManager.BugOption option;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final ButtonWidget linkButton;
        private final OptionWidget optionWidget;

        private Text message() {
            boolean value = this.option.value();

            return Text.translatable(value ? "gui.yes" : "gui.no")
                    .formatted(value ? Formatting.GREEN : Formatting.RED);
        }

        private Tooltip tooltip() {
            return Tooltip.of(Text.translatable(
                    this.option.translationKey() + ".tooltip"));
        }

        private void updateButton() {
            this.optionButton.setMessage(this.message());
            this.resetButton.active = !this.option.isDefault();
        }

        public void toggleValue() {
            this.option.toggleValue();
            this.updateButton();
        }

        public void resetValue() {
            this.option.resetValue();
            this.updateButton();
        }

        public BugWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.BugOption option) {
            this.option = option;

            this.linkButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY,
                            button -> this.option.openLink(screen), true)
                    .width(BUTTON_WIDTH)
                    .texture(Identifier.of(KeybindBugFixes.MOD_ID +  ":icon/link"), 16, 16)
                    .build();

            this.optionButton = ButtonWidget.builder(this.message(), button -> this.toggleValue())
                    .tooltip(this.tooltip())
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY,
                            button -> this.resetValue(), true)
                    .width(BUTTON_WIDTH)
                    .texture(Identifier.of(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();
            this.linkButton.active = this.option.link() != null;

            this.optionWidget = new OptionWidgetBuilder(client, screen, option, this.optionButton)
                    .addLeft(this.resetButton)
                    .addRight(this.linkButton)
                    .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionWidget.render(context, y, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.optionWidget.widgets();
        }

        @Override
        public List<? extends Element> children() {
            return this.optionWidget.widgets();
        }
    }

    public static class TweakWidgetEntry extends WidgetEntry {
        private final ConfigManager.TweakOption option;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final OptionWidget optionWidget;

        private Text message() {
            boolean value = this.option.value();

            return Text.translatable(value ? "gui.yes" : "gui.no")
                    .formatted(value ? Formatting.GREEN : Formatting.RED);
        }

        private Tooltip tooltip() {
            return Tooltip.of(Text.translatable(
                    this.option.translationKey() + ".tooltip"));
        }

        private void updateButton() {
            this.optionButton.setMessage(this.message());
            this.resetButton.active = !this.option.isDefault();
        }

        public void toggleValue() {
            this.option.toggleValue();
            this.updateButton();
        }

        public void resetValue() {
            this.option.resetValue();
            this.updateButton();
        }

        public TweakWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.TweakOption option) {
            this.option = option;

            this.optionButton = ButtonWidget.builder(this.message(), button -> this.toggleValue())
                    .tooltip(this.tooltip())
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY,
                            button -> this.resetValue(), true)
                    .width(BUTTON_WIDTH)
                    .texture(Identifier.of(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();

            this.optionWidget = new OptionWidgetBuilder(client, screen, option, this.optionButton)
                    .addLeft(this.resetButton)
                    .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionWidget.render(context, y, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.optionWidget.widgets();
        }

        @Override
        public List<? extends Element> children() {
            return this.optionWidget.widgets();
        }
    }

    public static class KeybindWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final ConfigManager.KeybindOption option;
        private final ButtonWidget optionButton;
        private final ButtonWidget resetButton;
        private final OptionWidget optionWidget;

        public enum Warning {
            DUPLICATE,
            UNKNOWN_MODIFIER,
            SAME_MODIFIER,
            NONE;
        }

        private MutableText duplicateText;
        private Warning warning;

        private void updateWarning() {
            this.warning = Warning.NONE;
            this.duplicateText = Text.empty();

            if (this.option.isUnbound()) return;

            ConfigManager.KeybindOption modifier = this.option.modifier();
            InputUtil.Key modifierKey = modifier != null ? modifier.value() : null;
            InputUtil.Key key = this.option.value();

            if (modifier != null) {
                if (modifierKey.getCode() == GLFW.GLFW_KEY_UNKNOWN) {
                    this.warning = Warning.UNKNOWN_MODIFIER;
                    return;

                } else if (modifierKey.equals(key)) {
                    this.warning = Warning.SAME_MODIFIER;
                    return;
                }
            } else {
                for (KeyBinding keyBinding : this.client.options.allKeys) {
                    if (((KeyBindingAccessor) keyBinding).getBoundKey().equals(key)) {
                        if (this.warning != Warning.DUPLICATE) {
                            this.warning = Warning.DUPLICATE;
                        } else {
                            this.duplicateText.append(", ");
                        }

                        this.duplicateText.append(Text.translatable(keyBinding.getTranslationKey()));
                    }
                }
            }

            for (ConfigManager.KeybindOption option : ConfigManager.KEYBIND_OPTIONS) {
                if (option == this.option) continue;

                ConfigManager.KeybindOption optionModifier = option.modifier();
                InputUtil.Key optionModifierKey = optionModifier != null ? optionModifier.value() : null;

                if (key.equals(option.value()) && Objects.equals(modifierKey, optionModifierKey)) {
                    if (this.warning != Warning.DUPLICATE) {
                        this.warning = Warning.DUPLICATE;
                    } else {
                        this.duplicateText.append(", ");
                    }

                    this.duplicateText.append(Text.translatable(option.translationKey()));
                }
            }
        }

        private Text message() {
            if (this.warning != Warning.NONE) {
                return Text.literal("[ ")
                        .append(this.option.value().getLocalizedText().copy().formatted(Formatting.WHITE))
                        .append(" ]")
                        .formatted(Formatting.RED);
            } else {
                return this.option.value().getLocalizedText();
            }
        }

        private Tooltip tooltip() {
            ConfigManager.KeybindOption modifier = this.option.modifier();

            if (this.warning == Warning.DUPLICATE) {
                return Tooltip.of(Text.translatable(
                        "controls.keybinds.duplicateKeybinds",
                        this.duplicateText));

            } else if (modifier != null) {
                if (this.warning == Warning.UNKNOWN_MODIFIER) {
                    return Tooltip.of(Text.translatable(
                            KeybindBugFixes.MOD_ID + ".config.keybinds.unknownModifier",
                            Text.translatable(modifier.translationKey())));

                } else if (this.warning == Warning.SAME_MODIFIER) {
                    return Tooltip.of(Text.translatable(
                            KeybindBugFixes.MOD_ID + ".config.keybinds.sameModifier",
                            Text.translatable(modifier.translationKey())));

                }
            }

            return null;
        }

        public void updateButton() {
            this.configScreen.list.updateKeybindWarnings();
            this.resetButton.active = !this.option.isDefault();
        }

        public void updateButtonWarning() {
            this.updateWarning();
            this.optionButton.setMessage(this.message());
            this.optionButton.setTooltip(this.tooltip());
        }

        public void selectButton() {
            this.configScreen.selectedKeybindWidget = this;
            this.optionButton.setMessage(Text.literal("> ")
                    .append(this.optionButton.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <")
                    .formatted(Formatting.YELLOW));
        }

        public void resetValue() {
            this.option.resetValue();
            this.updateButton();
        }

        public KeybindWidgetEntry(MinecraftClient client, ConfigScreen screen, ConfigManager.KeybindOption option) {
            this.client = client;
            this.configScreen = screen;
            this.option = option;

            this.updateWarning();

            this.optionButton = ButtonWidget.builder(this.message(), button -> this.selectButton())
                    .tooltip(this.tooltip())
                    .build();

            this.resetButton = TextIconButtonWidget.builder(ScreenTexts.EMPTY,
                            button -> this.resetValue(), true)
                    .width(BUTTON_WIDTH)
                    .texture(Identifier.of(KeybindBugFixes.MOD_ID + ":icon/reset"), 16, 16)
                    .build();

            this.resetButton.active = !this.option.isDefault();

            this.optionWidget = new OptionWidgetBuilder(client, screen, option, this.optionButton)
                    .addLeft(this.resetButton)
                    .build();
        }

        public ConfigManager.KeybindOption option() {
            return this.option;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.optionWidget.render(context, y, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.optionWidget.widgets();
        }

        @Override
        public List<? extends Element> children() {
            return this.optionWidget.widgets();
        }
    }

    public static class TitleWidgetEntry extends WidgetEntry {
        private final MinecraftClient client;
        private final ConfigScreen configScreen;
        private final String translationKey;

        public TitleWidgetEntry(MinecraftClient client, ConfigScreen screen, String translationKey) {
            this.client = client;
            this.configScreen = screen;
            this.translationKey = translationKey;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Text text = Text.translatable(this.translationKey);
            int centerX = this.configScreen.width / 2;

            context.drawCenteredTextWithShadow(this.client.textRenderer, text, centerX, y + 5, Colors.WHITE);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }
    }

    public static abstract class WidgetEntry extends Entry<WidgetEntry> {}
}