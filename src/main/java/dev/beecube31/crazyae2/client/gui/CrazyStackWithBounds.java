package dev.beecube31.crazyae2.client.gui;

import appeng.api.stacks.GenericStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public record CrazyStackWithBounds(GenericStack stack, Rect2i bounds) {
    @Nullable
    public static CrazyStackWithBounds fromSlot(CrazyAEBaseGui<?> screen, Slot slot) {
        var item = slot.getItem();
        var stack = GenericStack.unwrapItemStack(item);
        if (stack != null) {
            return new CrazyStackWithBounds(
                    stack,
                    new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16));
        }
        return null;
    }
}
