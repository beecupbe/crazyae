package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.block.crafting.ItemCraftingStorage;
import appeng.items.misc.ItemEncodedPattern;
import appeng.util.Platform;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.containers.base.slot.filter.IBotaniaCustomPatternFilter;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.CrazyAESidedHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class RestrictedSlot extends CrazyAESlot {

    private final PlaceableItemType which;
    private boolean allowEdit = true;

    public RestrictedSlot(final PlaceableItemType valid, final IItemHandler i, final int slotIndex, final int x, final int y, final InventoryPlayer playerInv) {
        super(i, slotIndex, x, y);
        this.which = valid;
        this.setIIcon(valid.IIcon);
    }

    public PlaceableItemType getPlaceableItemType() {
        return which;
    }

    public boolean isValid(final ItemStack is, final World theWorld) {
        return true;
    }

    @Override
    public boolean isItemValid(final @NotNull ItemStack i) {
        if (!this.getContainer().isValidForSlot(this, i)) return false;
        if (i.isEmpty()) return false;
        if (i.getItem() == Items.AIR) return false;
        if (!super.isItemValid(i)) return false;
        if (!this.isAllowEdit()) return false;

        return this.which.associatedFilter.allowInsert(null, -1, i);
    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return this.isAllowEdit();
    }

    @Override
    public ItemStack getDisplayStack() {
//        if (Platform.isClient() && (this.which == PlaceableItemType.ENCODED_CRAFTING_PATTERN || this.which == PlaceableItemType.ENCODED_PATTERN)) {
//            final ItemStack is = super.getStack();
//            if (!is.isEmpty() && is.getItem() instanceof ItemEncodedPattern iep) {
//                final ItemStack out = iep.getOutput(is);
//                if (!out.isEmpty()) {
//                    return out;
//                }
//            }
//        } else if (Platform.isClient() && this.which.associatedFilter instanceof IBotaniaCustomPatternFilter) {
//            final ItemStack is = super.getStack();
//            if (!is.isEmpty() && is.getItem() instanceof ItemCustomEncodedPatternBase iep) {
//                final ItemStack out = iep.getOutput(is);
//                if (!out.isEmpty()) {
//                    return out;
//                }
//            }
//        }
        return super.getStack();
    }

    private boolean isAllowEdit() {
        return this.allowEdit;
    }

    public enum PlaceableItemType {
        NONE(StateSprite.TRASH_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return true;
            }
        }),
        STORAGE_CELLS(StateSprite.STORAGE_CELLS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return AEApi.instance().registries().cell().isCellHandled(stack);
            }
        }),
        ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_OLD, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemEncodedPattern;
            }
        }),
        ENCODED_CRAFTING_PATTERN(StateSprite.PATTERNS_SLOT_OLD, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemEncodedPattern iep && iep.getPatternForItem(stack, null).isCraftable();
            }
        }),
        TRASH(StateSprite.TRASH_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return false;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return true;
            }
        }),

        CONDENSER_INPUT(StateSprite.TRASH_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return false;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return !AEApi.instance().registries().cell().isCellHandled(stack) && !(
                        stack.getItem() instanceof IStorageComponent && ((IStorageComponent) stack.getItem()).isStorageComponent(stack
                ));
            }
        }),


        STORAGE_COMPONENT(StateSprite.STORAGE_COMPONENTS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof IStorageComponent && ((IStorageComponent)stack.getItem()).isStorageComponent(stack);
            }
        }),


        CRAFTING_ACCELERATORS(StateSprite.CRAFTING_ACCELERATOR_128X, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return Utils.getAcceleratorsCountOf(stack) > 0;
            }
        }),
        CRAFTING_STORAGES(StateSprite.CRAFTING_STORAGE_128X, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemCraftingStorage;
            }
        }),
        CRAFTING_UNITS(StateSprite.CRAFTING_BLOCKS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemCraftingStorage || Utils.getAcceleratorsCountOf(stack) > 0;
            }
        }),
        CERTUS_QUARTZ_CRYSTALS(StateSprite.CERTUS_QUARTZ, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return AEApi.instance().definitions().materials().certusQuartzCrystal().isSameAs(stack);
            }
        }),
        CHARGED_CERTUS_QUARTZ_CRYSTALS(StateSprite.CHARGED_CERTUS_QUARTZ, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return AEApi.instance().definitions().materials().certusQuartzCrystalCharged().isSameAs(stack);
            }
        }),
        AE_UPGRADES(StateSprite.AE_UPGRADE_CARDS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof IUpgradeModule && ((IUpgradeModule) stack.getItem()).getType(stack) != null;
            }
        }),
        CRAZYAE_UPGRADES(StateSprite.CRAZYAE_UPGRADE_CARDS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof ICrazyAEUpgradeModule;
            }
        }),
        UPGRADES(StateSprite.CRAZYAE_UPGRADE_CARDS_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return stack.getItem() instanceof IUpgradeModule && ((IUpgradeModule) stack.getItem()).getType(stack) != null
                        || stack.getItem() instanceof CrazyAEUpgradeModule;
            }
        }),
        ENERGY_STACKS(StateSprite.ENERGY, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                for (IItemDefinition h : CrazyAESidedHandler.availableEnergyTypes) {
                    if (h.isSameAs(stack)) return true;
                }

                return false;
            }
        }),


        ELVENTRADE_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().elventradeBlankPattern().isSameAs(stack);
            }
        }),
        MANAPOOL_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().manapoolBlankPattern().isSameAs(stack);
            }
        }),
        RUNEALTAR_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().runealtarBlankPattern().isSameAs(stack);
            }
        }),
        TERAPLATE_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().teraplateBlankPattern().isSameAs(stack);
            }
        }),
        BREWERY_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().breweryBlankPattern().isSameAs(stack);
            }
        }),
        PUREDAISY_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().puredaisyBlankPattern().isSameAs(stack);
            }
        }),
        PETAL_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().materials().petalBlankPattern().isSameAs(stack);
            }
        }),

        ELVENTRADE_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().elventradeEncodedPattern().isSameAs(stack);
            }
        }),
        MANAPOOL_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().manapoolEncodedPattern().isSameAs(stack);
            }
        }),
        RUNEALTAR_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().runealtarEncodedPattern().isSameAs(stack);
            }
        }),
        PUREDAISY_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().puredaisyEncodedPattern().isSameAs(stack);
            }
        }),
        TERAPLATE_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().teraplateEncodedPattern().isSameAs(stack);
            }
        }),
        BREWERY_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().breweryEncodedPattern().isSameAs(stack);
            }
        }),
        PETAL_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW, new IBotaniaCustomPatternFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return CrazyAE.definitions().items().petalEncodedPattern().isSameAs(stack);
            }
        }),

        INSCRIBER_PLATE(StateSprite.INSCRIBER_TOP_BOTTOM_INGREDIENT_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                if (stack.isEmpty()) {
                    return false;
                }

                if (AEApi.instance().definitions().materials().namePress().isSameAs(stack)) {
                    return true;
                }

                for (final ItemStack optional : AEApi.instance().registries().inscriber().getOptionals()) {
                    if (Platform.itemComparisons().isSameItem(stack, optional)) {
                        return true;
                    }
                }
                return false;
            }
        }),
        INSCRIBER_INPUT(StateSprite.INGOT_SLOT, new IAEItemFilter() {
            @Override
            public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                return true;
            }

            @Override
            public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                return !stack.isEmpty() && slot != 1;
            }
        });



        public final StateSprite IIcon;
        public final IAEItemFilter associatedFilter;

        PlaceableItemType(final StateSprite o, IAEItemFilter associatedFilter) {
            this.IIcon = o;
            this.associatedFilter = associatedFilter;
        }

        public IAEItemFilter outputLockedFilter() {
            IAEItemFilter filter = this.associatedFilter;

            return new IAEItemFilter() {
                @Override
                public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                    return false;
                }

                @Override
                public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                    return filter.allowInsert(inv, slot, stack);
                }
            };
        }

        public IAEItemFilter inputLockedFilter() {
            IAEItemFilter filter = this.associatedFilter;

            return new IAEItemFilter() {
                @Override
                public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                    return filter.allowExtract(inv, slot, amount);
                }

                @Override
                public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                    return false;
                }
            };
        }
    }
}
