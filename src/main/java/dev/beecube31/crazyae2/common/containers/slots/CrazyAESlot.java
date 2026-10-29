package dev.beecube31.crazyae2.common.containers.slots;

import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.core.AELog;
import dev.beecube31.crazyae2.client.gui.sprite.ISpriteProvider;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CrazyAESlot extends Slot {
    protected final InternalInventory inventory;
    protected final int invSlotIdx;

    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private boolean hideAmount;
    private boolean isEnabled = true;
    private Supplier<@Nullable List<Component>> emptyTooltip = () -> null;

    private boolean isDraggable = true;
    public CrazyAEBaseContainer container = null;
    private boolean active = true;

    @Nullable
    private ISpriteProvider icon;
    private Boolean validState = null;

    public CrazyAESlot(InternalInventory inv, int invSlotIdx) {
        super(EMPTY_INVENTORY, invSlotIdx, 0, 0);
        this.inventory = inv;
        this.invSlotIdx = invSlotIdx;
    }

    public Slot setNotDraggable() {
        this.setDraggable(false);
        return this;
    }

    public void clearStack() {
        this.inventory.setItemDirect(this.invSlotIdx, ItemStack.EMPTY);
    }

    @Nullable
    public List<Component> getCustomTooltip(ItemStack carriedItem) {
        if (getDisplayStack().isEmpty()) {
            var tooltip = emptyTooltip.get();
            if (tooltip != null) {
                return tooltip;
            }
        }
        return null;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (containsWrapperItem()) {
            return false;
        }
        if (this.isSlotEnabled()) {
            return this.inventory.isItemValid(this.invSlotIdx, stack);
        }
        return false;
    }

    @Override
    public ItemStack getItem() {
        if (!this.isSlotEnabled()) {
            return ItemStack.EMPTY;
        }

        return this.inventory.getStackInSlot(this.invSlotIdx);
    }

    @Override
    public void set(ItemStack stack) {
        if (this.isSlotEnabled()) {
            this.inventory.setItemDirect(this.invSlotIdx, stack);
            this.setChanged();
        }
    }

    public void initialize(ItemStack stack) {
        if (!this.isSlotEnabled() && this.invSlotIdx >= this.inventory.size() && stack.isEmpty()) {
            return;
        }
        this.inventory.setItemDirect(this.invSlotIdx, stack);
    }

    private void notifyContainerSlotChanged() {
        if (this.getContainer() != null) {
            this.getContainer().onSlotChange(this);
        }
    }

    public InternalInventory getInventory() {
        return this.inventory;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.validState = null;
        notifyContainerSlotChanged();
    }

    @Override
    public int getMaxStackSize() {
        return this.inventory.getSlotLimit(this.invSlotIdx);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public boolean mayPickup(Player player) {
        if (containsWrapperItem()) {
            return false;
        }
        if (this.isSlotEnabled()) {
            return !this.inventory.extractItem(this.invSlotIdx, 1, true).isEmpty();
        }
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (containsWrapperItem()) {
            return ItemStack.EMPTY;
        }

        return this.inventory.extractItem(this.invSlotIdx, amount, false);
    }

    private boolean containsWrapperItem() {
        return GenericStack.isWrapped(getItem());
    }

    public boolean isSameInventory(Slot other) {
        return other instanceof CrazyAESlot && ((CrazyAESlot) other).inventory == this.inventory;
    }

    public InternalInventory getSlotInv() {
        return inventory.getSlotInv(invSlotIdx);
    }

    @Override
    public boolean isActive() {
        return this.isSlotEnabled() && active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSlotEnabled() {
        return isEnabled;
    }

    public void setSlotEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public ItemStack getDisplayStack() {
        var is = getItem();
        if (hideAmount) {
            var unwrapped = GenericStack.unwrapItemStack(is);
            if (unwrapped != null) {
                return GenericStack.wrapInItemStack(unwrapped.what(), 0);
            } else {
                is = is.copy();
                is.setCount(1);
            }
        }
        return is;
    }

    public float getOpacityOfIcon() {
        return 0.4f;
    }

    public boolean renderIconWithItem() {
        return false;
    }

    @Nullable
    public ISpriteProvider getIcon() {
        return this.icon;
    }

    public void setIcon(ISpriteProvider icon) {
        this.icon = icon;
    }

    public boolean isDraggable() {
        return this.isDraggable;
    }

    private void setDraggable(boolean isDraggable) {
        this.isDraggable = isDraggable;
    }

    protected CrazyAEBaseContainer getContainer() {
        return this.container;
    }

    public void setContainer(CrazyAEBaseContainer container) {
        this.container = container;
    }

    protected boolean isRemote() {
        return container == null || container.isClientSide();
    }

    public final boolean isValid() {
        // Lazily compute whether the currently held item is valid or not
        if (validState == null) {
            try {
                validState = getCurrentValidationState();
            } catch (Exception e) {
                validState = false;
                AELog.warn("Failed to update validation state for slot %s: %s", this, e);
            }
        }
        return validState;
    }

    protected boolean getCurrentValidationState() {
        return true;
    }

    public void resetCachedValidation() {
        this.validState = null;
    }

    public boolean isHideAmount() {
        return hideAmount;
    }

    public void setHideAmount(boolean hideAmount) {
        this.hideAmount = hideAmount;
    }

    public void setEmptyTooltip(Supplier<@Nullable List<Component>> emptyTooltip) {
        this.emptyTooltip = emptyTooltip;
    }
}
