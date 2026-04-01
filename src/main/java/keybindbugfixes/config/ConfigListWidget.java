package keybindbugfixes.config;

import com.google.common.collect.Lists;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.ConfigListWidget.Entry;
import keybindbugfixes.config.option.BooleanOption;
import keybindbugfixes.config.option.KeybindOption;
import keybindbugfixes.config.option.Option;
import keybindbugfixes.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ConfigListWidget extends ElementListWidget<Entry> {
    private static final Identifier RESET_ICON
            = Identifier.of(KeybindBugFixes.MOD_ID, "icon/reset");

    private static final Identifier LINK_ICON
            = Identifier.of(KeybindBugFixes.MOD_ID, "icon/link");

    private static final Text RESET_TEXT =
            Text.translatable(KeybindBugFixes.MOD_ID + ".config.reset");

    private static final Text LINK_TEXT =
            Text.translatable(KeybindBugFixes.MOD_ID + ".config.link");

    private static final Text BUGFIXES_TEXT =
            Text.translatable(KeybindBugFixes.MOD_ID + ".config.category.bugfixes");

    private static final Text TWEAKS_TEXT =
            Text.translatable(KeybindBugFixes.MOD_ID + ".config.category.tweaks");

    private final ConfigScreen parent;
    private final List<KeybindEntry> keybindEntries = Lists.newArrayList();

    private void addCategoryEntry(Text label) {
        this.addEntry(new CategoryEntry(label));
    }

    private void addOptionEntry(Option<?> option) {
        if (option.isDisabled) {
            this.addEntry(new LabeledEntry(option.label.copy().formatted(Formatting.GRAY, Formatting.STRIKETHROUGH)));
            return;
        }

        if (option instanceof BooleanOption booleanOption) {
            this.addEntry(new BooleanEntry(booleanOption));
        } else if (option instanceof KeybindOption keybindOption) {
            KeybindEntry keybindEntry = new KeybindEntry(keybindOption);
            this.keybindEntries.add(keybindEntry);
            this.addEntry(keybindEntry);
        }
    }

    public ConfigListWidget(ConfigScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.layout.getContentHeight(), parent.layout.getHeaderHeight(), 24);
        this.parent = parent;

        this.addCategoryEntry(BUGFIXES_TEXT);
        this.addOptionEntry(Config.FIX_PICK_KEY_DRAGGING);
        this.addOptionEntry(Config.FIX_DISMOUNT_TOGGLE_SNEAK);
        this.addOptionEntry(Config.FIX_SCREEN_STICKY_KEY_RESET);
        this.addOptionEntry(Config.FIX_MODIFIER_STICKY_KEY);
        this.addOptionEntry(Config.FIX_REBIND_TO_F3);

        this.addCategoryEntry(TWEAKS_TEXT);
        this.addOptionEntry(Config.DROP_WHEN_HOLDING_ITEM);
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    public void updateKeybindEntries() {
        for (KeybindEntry entry : this.keybindEntries) {
            entry.update();
        }
    }

    public abstract static class Entry extends ElementListWidget.Entry<Entry> {

    }

    public class CategoryEntry extends Entry {
        private final Text label;

        public CategoryEntry(Text label) {
            this.label = label;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            context.drawCenteredTextWithShadow(
                    ConfigListWidget.this.client.textRenderer,
                    this.label,
                    this.getContentMiddleX(),
                    this.getContentMiddleY() - ConfigListWidget.this.client.textRenderer.fontHeight / 2,
                    Colors.WHITE
            );

//            context.drawStrokedRectangle(
//                    this.getContentX(),
//                    this.getContentY(),
//                    this.getContentWidth(),
//                    this.getContentHeight(),
//                    Colors.LIGHT_RED
//            );
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Collections.emptyList();
        }
    }

    public class LabeledEntry extends Entry {
        private final List<ButtonWidget> widgets = Lists.newArrayList();
        private final Text label;

        private static final int BUTTONS_WIDTH = 150;
        private static final int GAP_WIDTH = 2;

        public LabeledEntry(Text label) {
            this.label = label;
        }

        public void init(ButtonWidget centerWidget, List<ButtonWidget> leftWidgets, List<ButtonWidget> rightWidgets) {
            int centerWidgetWidth = BUTTONS_WIDTH;
            for (ButtonWidget widget : leftWidgets) centerWidgetWidth -= widget.getWidth() + GAP_WIDTH;
            for (ButtonWidget widget : rightWidgets) centerWidgetWidth -= widget.getWidth() + GAP_WIDTH;
            centerWidget.setWidth(centerWidgetWidth);

            this.widgets.addAll(leftWidgets);
            this.widgets.add(centerWidget);
            this.widgets.addAll(rightWidgets);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int textWidth = this.getContentWidth() - BUTTONS_WIDTH - GAP_WIDTH;

            TextRenderer textRenderer = ConfigListWidget.this.client.textRenderer;
            List<OrderedText> textLines = textRenderer.wrapLines(this.label, textWidth);

            int textPosY = this.getContentMiddleY() - textLines.size() * textRenderer.fontHeight / 2;
            for (OrderedText line : textLines) {
                context.drawText(textRenderer, line, this.getContentX(), textPosY, Colors.WHITE, true);
                textPosY += textRenderer.fontHeight;
            }

            int widgetPosX = this.getContentRightEnd() - BUTTONS_WIDTH;
            for (ButtonWidget widget : this.widgets) {
                widget.setPosition(widgetPosX, this.getContentY());
                widget.render(context, mouseX, mouseY, tickProgress);
                widgetPosX += widget.getWidth() + GAP_WIDTH;
            }

//            context.drawStrokedRectangle(
//                    this.getContentX(),
//                    this.getContentY(),
//                    textWidth,
//                    this.getContentHeight(),
//                    Colors.LIGHT_RED
//            );

//            context.drawStrokedRectangle(
//                    this.getContentRightEnd() - BUTTONS_WIDTH,
//                    this.getContentY(),
//                    BUTTONS_WIDTH,
//                    this.getContentHeight(),
//                    Colors.LIGHT_RED
//            );
        }

        @Override
        public List<? extends Element> children() {
            return this.widgets;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.widgets;
        }
    }

    public abstract class OptionEntry<T extends Option<?>> extends LabeledEntry {
        protected final ButtonWidget editButton;
        protected final ButtonWidget resetButton;
        public final T option;

        public OptionEntry(T option) {
            super(option.label);
            this.option = option;

            this.editButton = ButtonWidget.builder(ScreenTexts.EMPTY, this::editClicked)
                    .narrationSupplier(this.narrationSupplier())
                    .build();

            this.resetButton = TextIconButtonWidget.builder(RESET_TEXT, this::resetClicked, true)
                    .width(20)
                    .texture(RESET_ICON, 16, 16)
                    .build();

            List<ButtonWidget> rightWidgets = Lists.newArrayList();

            if (this.option.link != null) {
                ButtonWidget.PressAction onPress = button -> {
                    ConfirmLinkScreen.open(ConfigListWidget.this.parent, this.option.link);
                };

                ButtonWidget linkButton = TextIconButtonWidget.builder(LINK_TEXT, onPress, true)
                        .width(20)
                        .texture(LINK_ICON, 16, 16)
                        .build();

                rightWidgets.add(linkButton);
            }

            this.init(this.editButton, List.of(this.resetButton), rightWidgets);
            this.update();
        }

        protected ButtonWidget.NarrationSupplier narrationSupplier() {
            return Supplier::get;
        }

        protected abstract void editClicked(ButtonWidget button);

        protected abstract void resetClicked(ButtonWidget button);

        public abstract void update();
    }

    public class BooleanEntry extends OptionEntry<BooleanOption> {
        public BooleanEntry(BooleanOption option) {
            super(option);
        }

        @Override
        protected ButtonWidget.NarrationSupplier narrationSupplier() {
            return textSupplier ->
                    this.option.label.copy().append(": ").append(textSupplier.get());
        }

        @Override
        protected void editClicked(ButtonWidget button) {
            this.option.toggle();
            this.update();
        }

        @Override
        protected void resetClicked(ButtonWidget button) {
            this.option.reset();
            this.update();
        }

        @Override
        public void update() {
            Text message = (this.option.value ? ScreenTexts.YES : ScreenTexts.NO)
                    .copy().formatted(this.option.value ? Formatting.GREEN : Formatting.RED);

            Tooltip tooltip = Tooltip.of(this.option.description);

            this.editButton.setMessage(message);
            this.editButton.setTooltip(tooltip);
            this.resetButton.active = !this.option.isDefault();
        }
    }

    public class KeybindEntry extends OptionEntry<KeybindOption> {
        public KeybindEntry(KeybindOption option) {
            super(option);
        }

        @Override
        protected ButtonWidget.NarrationSupplier narrationSupplier() {
            return textSupplier -> this.option.isUnbound()
                    ? Text.translatable("narrator.controls.unbound", this.option.label)
                    : Text.translatable("narrator.controls.bound", this.option.label, textSupplier.get());
        }

        @Override
        protected void editClicked(ButtonWidget button) {
            ConfigListWidget.this.parent.selectedKeybindEntry = this;
            this.update();
        }

        @Override
        protected void resetClicked(ButtonWidget button) {
            this.option.reset();
            ConfigListWidget.this.updateKeybindEntries();
        }

        private Text duplicateText() {
            MutableText duplicateText = Text.empty();
            boolean duplicate = false;

            if (this.option.isUnbound()) {
                return duplicateText;
            }

            if (this.option.modifier == null) {
                for (KeyBinding keyBinding : ConfigListWidget.this.client.options.allKeys) {
                    if (((KeyBindingAccessor) keyBinding).getBoundKey().equals(this.option.value)) {
                        if (duplicate) {
                            duplicateText.append(", ");
                        }

                        duplicate = true;
                        duplicateText.append(Text.translatable(keyBinding.getId()));
                    }
                }
            }

            for (KeybindOption option : Config.KEYBIND_OPTIONS) {
                if (option == this.option) continue;

                boolean sameModifiers = option.modifier != null && option.modifier.value != null
                        && this.option.modifier != null && this.option.modifier.value != null
                        && option.modifier.value.equals(this.option.modifier.value);

                boolean noModifiers = option.modifier == null && this.option.modifier == null;

                if (option.value.equals(this.option.value) && (sameModifiers || noModifiers)) {
                    if (duplicate) {
                        duplicateText.append(", ");
                    }

                    duplicate = true;
                    duplicateText.append(option.label);
                }
            }

            return duplicateText;
        }

        @Override
        public void update() {
            Text message = this.option.value.getLocalizedText();
            Tooltip tooltip = Tooltip.of(this.option.description);

            Text duplicateText = this.duplicateText();
            boolean duplicate = !duplicateText.equals(Text.empty());

            boolean unboundModifier = !this.option.isUnbound()
                    && this.option.modifier != null
                    && this.option.modifier.isUnbound();

            boolean sameModifier = !this.option.isUnbound()
                    && this.option.modifier != null
                    && this.option.modifier.value.equals(this.option.value);

            Formatting warningColor = duplicate ? Formatting.YELLOW : Formatting.RED;

            if (duplicate || unboundModifier || sameModifier) {
                message = Text.literal("[ ")
                        .append(message.copy().formatted(Formatting.WHITE))
                        .append(" ]")
                        .formatted(warningColor);
            }

            if (ConfigListWidget.this.parent.selectedKeybindEntry == this) {
                message = Text.literal("> ")
                        .append(message.copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                        .append(" <")
                        .formatted(Formatting.YELLOW);
            }

            if (duplicate) {
                tooltip = Tooltip.of(Text.translatable("controls.keybinds.duplicateKeybinds", duplicateText));
            } else if (unboundModifier) {
                tooltip = Tooltip.of(Text.translatable(
                        KeybindBugFixes.MOD_ID + ".config.keybinds.unboundModifier",
                        this.option.modifier.label));
            } else if (sameModifier) {
                tooltip = Tooltip.of(Text.translatable(
                        KeybindBugFixes.MOD_ID + ".config.keybinds.sameModifier",
                        this.option.modifier.label));
            }

            this.editButton.setMessage(message);
            this.editButton.setTooltip(tooltip);
            this.resetButton.active = !this.option.isDefault();
        }
    }
}