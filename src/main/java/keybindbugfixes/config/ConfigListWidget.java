package keybindbugfixes.config;

import com.google.common.collect.Lists;
import keybindbugfixes.KeybindBugFixes;
import keybindbugfixes.config.ConfigListWidget.Entry;
import keybindbugfixes.config.option.BooleanOption;
import keybindbugfixes.config.option.KeyOption;
import keybindbugfixes.config.option.Option;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ConfigListWidget extends ContainerObjectSelectionList<Entry> {
    private static final Identifier RESET_SPRITE =
            Identifier.fromNamespaceAndPath(KeybindBugFixes.MOD_ID, "icon/reset");

    private static final Identifier LINK_SPRITE =
            Identifier.fromNamespaceAndPath(KeybindBugFixes.MOD_ID, "icon/link");

    private static final Component RESET_LABEL =
            Component.translatable(KeybindBugFixes.MOD_ID + ".config.reset");

    private static final Component LINK_LABEL =
            Component.translatable(KeybindBugFixes.MOD_ID + ".config.link");

    private static final Component BUGFIXES_LABEL =
            Component.translatable(KeybindBugFixes.MOD_ID + ".config.category.bugfixes");

    private static final Component TWEAKS_LABEL =
            Component.translatable(KeybindBugFixes.MOD_ID + ".config.category.tweaks");

    private final ConfigScreen configScreen;
    private final List<KeyEntry> keyEntries = Lists.newArrayList();

    private void addCategoryEntry(Component label) {
        this.addEntry(new CategoryEntry(label));
    }

    private void addOptionEntry(Option<?> option) {
        if (option.isDisabled) {
            Component label = option.label.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.STRIKETHROUGH);
            this.addEntry(new LabeledEntry(label));
            return;
        }

        if (option instanceof BooleanOption booleanOption) {
            this.addEntry(new BooleanEntry(booleanOption));
        } else if (option instanceof KeyOption keyOption) {
            KeyEntry keyEntry = new KeyEntry(keyOption);
            this.keyEntries.add(keyEntry);
            this.addEntry(keyEntry);
        }
    }

    public ConfigListWidget(ConfigScreen configScreen, Minecraft minecraft) {
        super(minecraft, configScreen.width, configScreen.layout.getContentHeight(), configScreen.layout.getHeaderHeight(), 24);
        this.configScreen = configScreen;

        this.addCategoryEntry(BUGFIXES_LABEL);
        this.addOptionEntry(Config.FIX_PICK_KEY_DRAGGING);
        this.addOptionEntry(Config.FIX_DISMOUNT_TOGGLE_SNEAK);
        this.addOptionEntry(Config.FIX_SCREEN_STICKY_KEY_RESET);
        this.addOptionEntry(Config.FIX_MODIFIER_STICKY_KEY);
        this.addOptionEntry(Config.FIX_REBIND_TO_F3);
        this.addOptionEntry(Config.FIX_DEBUG_CONFLICTS);

        this.addCategoryEntry(TWEAKS_LABEL);
        this.addOptionEntry(Config.DROP_WHEN_HOLDING_ITEM);
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    public void refreshKeyEntries() {
        for (KeyEntry entry : this.keyEntries) {
            entry.refreshEntry();
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

    }

    public class CategoryEntry extends Entry {
        private final Component label;

        public CategoryEntry(Component label) {
            this.label = label;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.centeredText(
                    ConfigListWidget.this.minecraft.font,
                    this.label,
                    this.getContentXMiddle(),
                    this.getContentYMiddle() - ConfigListWidget.this.minecraft.font.lineHeight / 2,
                    CommonColors.WHITE
            );

//            graphics.outline(
//                    this.getContentX(),
//                    this.getContentY(),
//                    this.getContentWidth(),
//                    this.getContentHeight(),
//                    CommonColors.SOFT_RED
//            );
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }
    }

    public class LabeledEntry extends Entry {
        private final List<Button> buttons = Lists.newArrayList();
        private final Component label;

        private static final int BUTTONS_WIDTH = 150;
        private static final int GAP_WIDTH = 2;

        public LabeledEntry(Component label) {
            this.label = label;
        }

        public void init(Button centerButton, List<Button> leftButtons, List<Button> rightButtons) {
            int centerButtonWidth = BUTTONS_WIDTH;
            for (Button button : leftButtons) centerButtonWidth -= button.getWidth() + GAP_WIDTH;
            for (Button button : rightButtons) centerButtonWidth -= button.getWidth() + GAP_WIDTH;
            centerButton.setWidth(centerButtonWidth);

            this.buttons.addAll(leftButtons);
            this.buttons.add(centerButton);
            this.buttons.addAll(rightButtons);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            int labelWidth = this.getContentWidth() - BUTTONS_WIDTH - GAP_WIDTH;

            Font font = ConfigListWidget.this.minecraft.font;
            List<FormattedCharSequence> lines = font.split(this.label, labelWidth);

            int lineY = this.getContentYMiddle() - lines.size() * font.lineHeight / 2;
            for (FormattedCharSequence line : lines) {
                graphics.text(font, line, this.getContentX(), lineY, CommonColors.WHITE, true);
                lineY += font.lineHeight;
            }

            int buttonX = this.getContentRight() - BUTTONS_WIDTH;
            for (Button button : this.buttons) {
                button.setPosition(buttonX, this.getContentY());
                button.extractRenderState(graphics, mouseX, mouseY, a);
                buttonX += button.getWidth() + GAP_WIDTH;
            }

//            graphics.outline(
//                    this.getContentX(),
//                    this.getContentY(),
//                    labelWidth,
//                    this.getContentHeight(),
//                    CommonColors.SOFT_RED
//            );

//            graphics.outline(
//                    this.getContentRight() - BUTTONS_WIDTH,
//                    this.getContentY(),
//                    BUTTONS_WIDTH,
//                    this.getContentHeight(),
//                    CommonColors.SOFT_RED
//            );
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.buttons;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.buttons;
        }
    }

    public abstract class OptionEntry<T extends Option<?>> extends LabeledEntry {
        protected final Button editButton;
        protected final Button resetButton;
        public final T option;

        public OptionEntry(T option) {
            super(option.label);
            this.option = option;

            this.editButton = Button.builder(CommonComponents.EMPTY, this::editClicked)
                    .createNarration(this.narrationSupplier())
                    .build();

            this.resetButton = SpriteIconButton.builder(RESET_LABEL, this::resetClicked, true)
                    .width(20)
                    .sprite(RESET_SPRITE, 16, 16)
                    .build();

            List<Button> rightButtons = Lists.newArrayList();

            if (this.option.link != null) {
                Button.OnPress onPress = button -> {
                    ConfirmLinkScreen.confirmLinkNow(ConfigListWidget.this.configScreen, this.option.link);
                };

                Button linkButton = SpriteIconButton.builder(LINK_LABEL, onPress, true)
                        .width(20)
                        .sprite(LINK_SPRITE, 16, 16)
                        .build();

                rightButtons.add(linkButton);
            }

            this.init(this.editButton, List.of(this.resetButton), rightButtons);
            this.refreshEntry();
        }

        protected Button.CreateNarration narrationSupplier() {
            return Supplier::get;
        }

        protected abstract void editClicked(Button button);

        protected abstract void resetClicked(Button button);

        public abstract void refreshEntry();
    }

    public class BooleanEntry extends OptionEntry<BooleanOption> {
        public BooleanEntry(BooleanOption option) {
            super(option);
        }

        @Override
        protected Button.CreateNarration narrationSupplier() {
            return supplier ->
                    this.option.label.copy().append(": ").append(supplier.get());
        }

        @Override
        protected void editClicked(Button button) {
            this.option.toggle();
            this.refreshEntry();
        }

        @Override
        protected void resetClicked(Button button) {
            this.option.reset();
            this.refreshEntry();
        }

        @Override
        public void refreshEntry() {
            Component message = (this.option.value ? CommonComponents.GUI_YES : CommonComponents.GUI_NO)
                    .copy().withStyle(this.option.value ? ChatFormatting.GREEN : ChatFormatting.RED);

            Tooltip tooltip = Tooltip.create(this.option.description);

            this.editButton.setMessage(message);
            this.editButton.setTooltip(tooltip);
            this.resetButton.active = !this.option.isDefault();
        }
    }

    public class KeyEntry extends OptionEntry<KeyOption> {
        public KeyEntry(KeyOption option) {
            super(option);
        }

        @Override
        protected Button.CreateNarration narrationSupplier() {
            return supplier -> this.option.isUnbound()
                    ? Component.translatable("narrator.controls.unbound", this.option.label)
                    : Component.translatable("narrator.controls.bound", this.option.label, supplier.get());
        }

        @Override
        protected void editClicked(Button button) {
            ConfigListWidget.this.configScreen.selectedKeyEntry = this;
            this.refreshEntry();
        }

        @Override
        protected void resetClicked(Button button) {
            this.option.reset();
            ConfigListWidget.this.refreshKeyEntries();
        }

        private Component duplicateComponent() {
            MutableComponent duplicateComponent = Component.empty();
            boolean duplicate = false;

            if (this.option.isUnbound()) {
                return duplicateComponent;
            }

            if (this.option.modifier == null) {
                for (KeyMapping keyMapping : ConfigListWidget.this.minecraft.options.keyMappings) {
                    if (keyMapping.matches(this.option.value)) {
                        if (duplicate) {
                            duplicateComponent.append(", ");
                        }

                        duplicate = true;
                        duplicateComponent.append(Component.translatable(keyMapping.getName()));
                    }
                }
            }

            for (KeyOption option : Config.KEY_OPTIONS) {
                if (option == this.option) continue;

                boolean sameModifiers = option.modifier != null && option.modifier.value != null
                        && this.option.modifier != null && this.option.modifier.value != null
                        && option.modifier.value.equals(this.option.modifier.value);

                boolean noModifiers = option.modifier == null && this.option.modifier == null;

                if (option.value.equals(this.option.value) && (sameModifiers || noModifiers)) {
                    if (duplicate) {
                        duplicateComponent.append(", ");
                    }

                    duplicate = true;
                    duplicateComponent.append(option.label);
                }
            }

            return duplicateComponent;
        }

        @Override
        public void refreshEntry() {
            Component message = this.option.value.getDisplayName();
            Tooltip tooltip = Tooltip.create(this.option.description);

            Component duplicateComponent = this.duplicateComponent();
            boolean duplicate = !duplicateComponent.equals(Component.empty());

            boolean unboundModifier = !this.option.isUnbound()
                    && this.option.modifier != null
                    && this.option.modifier.isUnbound();

            boolean sameModifier = !this.option.isUnbound()
                    && this.option.modifier != null
                    && this.option.modifier.value.equals(this.option.value);

            ChatFormatting warningColor = duplicate ? ChatFormatting.YELLOW : ChatFormatting.RED;

            if (duplicate || unboundModifier || sameModifier) {
                message = Component.literal("[ ")
                        .append(message.copy().withStyle(ChatFormatting.WHITE))
                        .append(" ]")
                        .withStyle(warningColor);
            }

            if (ConfigListWidget.this.configScreen.selectedKeyEntry == this) {
                message = Component.literal("> ")
                        .append(message.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                        .append(" <")
                        .withStyle(ChatFormatting.YELLOW);
            }

            if (duplicate) {
                tooltip = Tooltip.create(Component.translatable(
                        "controls.keybinds.duplicateKeybinds",
                        duplicateComponent));
            } else if (unboundModifier) {
                tooltip = Tooltip.create(Component.translatable(
                        KeybindBugFixes.MOD_ID + ".config.keybinds.unboundModifier",
                        this.option.modifier.label));
            } else if (sameModifier) {
                tooltip = Tooltip.create(Component.translatable(
                        KeybindBugFixes.MOD_ID + ".config.keybinds.sameModifier",
                        this.option.modifier.label));
            }

            this.editButton.setMessage(message);
            this.editButton.setTooltip(tooltip);
            this.resetButton.active = !this.option.isDefault();
        }
    }
}