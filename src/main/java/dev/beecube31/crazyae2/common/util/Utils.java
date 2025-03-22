package dev.beecube31.crazyae2.common.util;

import appeng.api.definitions.IItemDefinition;
import cofh.core.item.ItemMultiRF;
import cofh.thermaldynamics.block.ItemBlockDuct;
import com.denfop.componets.AbstractComponent;
import com.denfop.componets.ComponentBaseEnergy;
import com.denfop.componets.Energy;
import com.denfop.items.transport.ItemCable;
import com.denfop.items.transport.ItemQCable;
import com.denfop.items.transport.ItemSCable;
import com.denfop.items.transport.ItemUniversalCable;
import com.denfop.tiles.base.TileEntityBlock;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.CrazyAESidedHandler;
import ic2.core.item.block.ItemBlockTileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.beecube31.crazyae2.common.util.ModsChecker.*;

@SuppressWarnings("unused")
public class Utils {

    public static String energyToFormattedString(double amt) {
        String out = "0";
        double i = Math.log10(amt);
        if (i > -3 && i < 0) {
            out = String.format("%.0fm", amt * 10E2D);
        } else if (i <= -3 && i > -6) {
            out = String.format("%.0fµ", amt * 10E5D);
        } else if (i <= -6 && i > -9) {
            out = String.format("%.0fn", amt * 10E8D);
        } else if (i <= -9 && i > -12) {
            out = String.format("%.0fp", amt * 10E11D);
        } else if (i < 3) {
            out = String.format("%.0f", amt);
        } else if (i < 6) {
            out = String.format("%.4fK", amt / 10E2D);
        } else if (i < 9) {
            out = String.format("%.4fM", amt / 10E5D);
        } else if (i < 12) {
            out = String.format("%.4fG", amt / 10E8D);
        } else if (i < 15) {
            out = String.format("%.4fT", amt / 10E11D);
        } else if (i < 18) {
            out = String.format("%.4fP", amt / 10E14D);
        } else if (i < 21) {
            out = String.format("%.4fE", amt / 10E17D);
        } else if (i < 24) {
            out = String.format("%.4fZ", amt / 10E20D);
        } else if (i < 27) {
            out = String.format("%.4fY", amt / 10E23D);
        }
        return out;

    }

    public static boolean isIUBlock(TileEntity te) {
        return IU_LOADED && te instanceof TileEntityBlock;
    }

    public static double getPowerFromTier(int tier) {
        return tier < 14 ? 8.0 * Math.pow(4.0, tier) : 9.223372036854776E18;
    }

    public static long getLongPowerFromTier(int tier) {
        return tier < 14 ? (long) (8.0 * Math.pow(4.0, tier)) : Long.MAX_VALUE;
    }

    public static int getIntPowerFromTier(int tier) {
        return tier < 14 ? (int) (8.0 * Math.pow(4.0, tier)) : Integer.MAX_VALUE;
    }

    public static int getTierFromPower(double power) {
        return power <= 0.0 ? 0 : Math.min(30, (int)Math.ceil(Math.log(power / 8.0) / Math.log(4.0)));
    }

    public static ItemStack getItemStack(IItemStack item) {
        if(item == null)
            return ItemStack.EMPTY;

        Object internal = item.getInternal();
        if(!(internal instanceof ItemStack)) {
            CraftTweakerAPI.logError("Not a valid item stack: " + item);
            throw new IllegalArgumentException("Not a valid item stack: " + item);
        }
        return ((ItemStack) internal).copy();
    }

    public static void setStackInSlot(final IItemHandler inv, final int slot, final ItemStack stack) {
        if (inv instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) inv).setStackInSlot(slot, stack);
        } else {
            inv.extractItem(slot, Integer.MAX_VALUE, false);
            inv.insertItem(slot, stack, false);
        }
    }

    public static List<Object> findEnergyComponents(final TileEntity tile) {
        List<Object> out = new ArrayList<>();
        for (AbstractComponent comp : ((TileEntityBlock) tile).getComponentList()) {
            if (comp instanceof ComponentBaseEnergy || comp instanceof Energy) {
                out.add(comp);
            }
        }

        return out;
    }

    public static void copy(final IItemHandler from, final IItemHandler to, boolean deepCopy) {
        for (int i = 0; i < Math.min(from.getSlots(), to.getSlots()); ++i) {
            setStackInSlot(to, i, deepCopy ? from.getStackInSlot(i).copy() : from.getStackInSlot(i));
        }
    }

    public static void updateEnergyHandler(IItemHandler from, World w) {
        for (int i = 0; i < from.getSlots(); ++i) {
            List<IItemDefinition> found = getItemEnergyType(from.getStackInSlot(i), w);
            if (found != null && !found.isEmpty()) {
                setStackInSlot(from, i, found.get(0).maybeStack(1).orElse(ItemStack.EMPTY));
            } else {
                setStackInSlot(from, i, ItemStack.EMPTY);
            }
        }
    }

    public static void copyExcluded(final IItemHandler from, final IItemHandler to, boolean deepCopy, int... excludedList) {
        for (int i = 0; i < Math.min(from.getSlots(), to.getSlots()); ++i) {
            boolean skip = false;
            for (int j : excludedList) {
                if (i == j) {
                    skip = true;
                    break;
                }
            }
            if (!skip) {
                setStackInSlot(to, i, deepCopy ? from.getStackInSlot(i).copy() : from.getStackInSlot(i));
            }
        }
    }

    public static List<IItemDefinition> getItemEnergyType(ItemStack is, World w) {
        for (IItemDefinition candidate : CrazyAESidedHandler.availableEnergyTypes) {
            if (candidate.isSameAs(is)) {
                return Collections.singletonList(candidate);
            }
        }

        Item item = is.getItem();

        if (
                IU_LOADED &&
                item instanceof ItemCable
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().EFEnergyAsAeStack());
        } else if (
                IU_LOADED &&
                item instanceof ItemQCable
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().QEEnergyAsAeStack());
        } else if (
                IU_LOADED &&
                item instanceof ItemSCable
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().SEEnergyAsAeStack());
        } else if (
                IU_LOADED &&
                item instanceof ItemUniversalCable
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().EFEnergyAsAeStack());
        } else if (
                COFHCORE_LOADED
                && item instanceof ItemMultiRF
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().FEEnergyAsAeStack());
        } else if (
                TD_LOADED
                && item instanceof ItemBlockDuct
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().FEEnergyAsAeStack());
        } else if (
                IC2_LOADED
                && (item instanceof ic2.core.item.block.ItemCable || (item instanceof ItemBlockTileEntity))
        ) {
            return Collections.singletonList(CrazyAE.definitions().items().EUEnergyAsAeStack());
        }

        return null;
    }
}
