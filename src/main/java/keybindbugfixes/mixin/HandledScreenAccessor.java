package keybindbugfixes.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("focusedSlot")
    Slot getFocusedSlot();
    @Accessor("cursorDragSlots")
    Set<Slot> getCursorDragSlots();
    @Accessor("cursorDragging")
    boolean isCursorDragging();
    @Accessor("heldButtonType")
    int getHeldButtonType();
    @Accessor("heldButtonCode")
    int getHeldButtonCode();
    @Accessor("cancelNextRelease")
    boolean getCancelNextRelease();

    @Accessor("cursorDragging")
    void setCursorDragging(boolean cursorDragging);
    @Accessor("heldButtonType")
    void setHeldButtonType(int heldButtonType);
    @Accessor("heldButtonCode")
    void setHeldButtonCode(int heldButtonCode);
    @Accessor("cancelNextRelease")
    void setCancelNextRelease(boolean cancelNextRelease);

    @Invoker("onMouseClick")
    void invokeOnMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);
}