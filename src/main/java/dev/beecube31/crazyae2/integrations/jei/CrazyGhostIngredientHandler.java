package dev.beecube31.crazyae2.integrations.jei;

import appeng.api.stacks.GenericStack;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.common.containers.slots.FakeSlot;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.common.network.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
public class CrazyGhostIngredientHandler implements IGhostIngredientHandler<CrazyAEBaseGui> {

    @Override
    public <I> @NotNull List<Target<I>> getTargetsTyped(@NotNull CrazyAEBaseGui gui,
                                                         @NotNull ITypedIngredient<I> ingredient,
                                                         boolean doStart) {
        var draggedStack = wrapDraggedItem(ingredient.getType(), ingredient.getIngredient());
        if (draggedStack == null) {
            return List.of();
        }

        List<Target<I>> targets = new ArrayList<>();
        for (var slot : gui.getMenu().slots) {
            if (slot instanceof FakeSlot fakeSlot && fakeSlot.isActive() && fakeSlot.canSetFilterTo(draggedStack)) {
                targets.add(new FakeSlotTarget<>(gui, fakeSlot, draggedStack));
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }

    @Nullable
    private static <T> ItemStack wrapDraggedItem(IIngredientType<T> type, T ingredient) {
        return VanillaTypes.ITEM_STACK.castIngredient(ingredient)
                .or(() -> {
                    var genericStack = GenericEntryStackHelper.ingredientToStack(type, ingredient);
                    return genericStack != null
                            ? Optional.of(GenericStack.wrapInItemStack(genericStack))
                            : Optional.empty();
                })
                .orElse(null);
    }

    private static class FakeSlotTarget<I> implements Target<I> {
        private final FakeSlot slot;
        private final Rect2i area;
        private final ItemStack stack;

        FakeSlotTarget(CrazyAEBaseGui<?> gui, FakeSlot slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
            this.area = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
        }

        @Override
        public @NotNull Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(@NotNull I ignored) {
            CrazyNetworkHandler.instance().sendToServer(
                    new PacketInventoryAction(InventoryAction.SET_FILTER, slot.index, stack.copy()));
        }
    }
}
