package dev.beecube31.crazyae2.common.containers.slots;

import appeng.api.ids.AETags;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.StorageCells;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.items.materials.UpgradeCardItem;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeModule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class RestrictedSlot extends CrazyAESlot {
    private static final IAEItemFilter DISABLED_FILTER = new IAEItemFilter() {
        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return false;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return false;
        }
    };

    private PlaceableItemType which;

    private boolean allowEdit = true;
    private int stackLimit = -1;

    public RestrictedSlot(PlaceableItemType valid, InternalInventory inv, int invSlot) {
        super(inv, invSlot);
        this.which = valid;
        this.setIcon(valid.sprite);
    }

    public RestrictedSlot(StateSprite slotIcon, InternalInventory inv, int invSlot) {
        super(inv, invSlot);
        this.setIcon(slotIcon);
    }

    @Override
    public int getMaxStackSize() {
        if (this.stackLimit != -1) {
            return this.stackLimit;
        }
        return super.getMaxStackSize();
    }

    public Slot setStackLimit(int i) {
        this.stackLimit = i;
        return this;
    }

    private Level getLevel() {
        return this.getContainer().getPlayerInventory().player.getCommandSenderWorld();
    }

    @Override
    public boolean mayPlace(ItemStack i) {
        if (!this.getContainer().isValidForSlot(this, i)) return false;
        if (i.isEmpty()) return false;
        if (i.getItem() == Items.AIR) return false;
        if (!super.mayPlace(i)) return false;
        if (!this.isAllowEdit()) return false;

        return this.which != null ? this.which.associatedFilter.allowInsert(null, -1, i)
                : this.inventory.isItemValid(this.invSlotIdx, i);
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.isAllowEdit();
    }

    @Override
    public ItemStack getDisplayStack() {
        if (isRemote() && this.which == PlaceableItemType.ENCODED_PATTERN) {
            final ItemStack is = super.getDisplayStack();
            if (!is.isEmpty() && is.getItem() instanceof EncodedPatternItem iep) {
                final ItemStack out = iep.getOutput(is);
                if (!out.isEmpty()) {
                    return out;
                }
            }
        }
        return super.getDisplayStack();
    }

    public static boolean isMetalIngot(ItemStack i) {
        return i.getItem().builtInRegistryHolder().is(AETags.METAL_INGOTS);
    }

    private boolean isAllowEdit() {
        return this.allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    public enum PlaceableItemType {
        NONE(StateSprite.TRASH_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return true;
            }
        }),
        STORAGE_CELLS(StateSprite.STORAGE_CELLS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return StorageCells.isCellHandled(stack);
            }
        }),
        ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof EncodedPatternItem;
            }
        }),
        ENCODED_CRAFTING_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof EncodedPatternItem;
            }
        }),
        TRASH(StateSprite.TRASH_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return false;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return true;
            }
        }),

        CONDENSER_INPUT(StateSprite.TRASH_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return false;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return !StorageCells.isCellHandled(stack) && !(
                        stack.getItem() instanceof IStorageComponent && ((IStorageComponent) stack.getItem()).isStorageComponent(stack));
            }
        }),


        STORAGE_COMPONENT(StateSprite.STORAGE_COMPONENTS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof IStorageComponent && ((IStorageComponent)stack.getItem()).isStorageComponent(stack);
            }
        }),


        CERTUS_QUARTZ_CRYSTALS(StateSprite.CERTUS_QUARTZ, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return AEItems.CERTUS_QUARTZ_CRYSTAL.isSameAs(stack);
            }
        }),
        CHARGED_CERTUS_QUARTZ_CRYSTALS(StateSprite.CHARGED_CERTUS_QUARTZ, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.isSameAs(stack);
            }
        }),
        AE_UPGRADES(StateSprite.AE_UPGRADE_CARDS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof UpgradeCardItem;
            }
        }),
        CRAZYAE_UPGRADES(StateSprite.CRAZYAE_UPGRADE_CARDS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof ICrazyAEUpgradeModule;
            }
        }),
        UPGRADES(StateSprite.CRAZYAE_UPGRADE_CARDS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(InternalInventory inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof UpgradeCardItem || stack.getItem() instanceof ICrazyAEUpgradeModule;
            }
        });



        public final StateSprite sprite;
        public final IAEItemFilter associatedFilter;

        PlaceableItemType(final StateSprite o, IAEItemFilter associatedFilter) {
            this.sprite = o;
            this.associatedFilter = associatedFilter;
        }

        public static IAEItemFilter disabledFilter() {
            return DISABLED_FILTER;
        }
    }
}
