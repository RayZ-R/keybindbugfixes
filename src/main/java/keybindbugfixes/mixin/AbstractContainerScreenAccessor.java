package keybindbugfixes.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("hoveredSlot")
    Slot getHoveredSlot();
    @Accessor("quickCraftSlots")
    Set<Slot> getQuickCraftSlots();
    @Accessor("isQuickCrafting")
    boolean isIsQuickCrafting();
    @Accessor("quickCraftingType")
    int getQuickCraftingType();
    @Accessor("quickCraftingButton")
    int getQuickCraftingButton();
    @Accessor("skipNextRelease")
    boolean getSkipNextRelease();

    @Accessor("isQuickCrafting")
    void setIsQuickCrafting(boolean isQuickCrafting);
    @Accessor("quickCraftingType")
    void setQuickCraftingType(int quickCraftingType);
    @Accessor("quickCraftingButton")
    void setQuickCraftingButton(int quickCraftingButton);
    @Accessor("skipNextRelease")
    void setSkipNextRelease(boolean skipNextRelease);

    @Invoker("slotClicked")
    void invokeSlotClicked(Slot slot, int slotId, int buttonNum, ClickType clickType);
}