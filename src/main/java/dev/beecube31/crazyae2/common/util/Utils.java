package dev.beecube31.crazyae2.common.util;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import cofh.core.item.ItemMultiRF;
import cofh.thermaldynamics.block.ItemBlockDuct;
import com.denfop.componets.AbstractComponent;
import com.denfop.componets.ComponentBaseEnergy;
import com.denfop.componets.Energy;
import com.denfop.items.ItemBaseCircuit;
import com.denfop.items.resource.ItemCraftingElements;
import com.denfop.items.transport.ItemCable;
import com.denfop.items.transport.ItemQCable;
import com.denfop.items.transport.ItemSCable;
import com.denfop.items.transport.ItemUniversalCable;
import com.denfop.tiles.base.TileEntityBlock;
import com.google.common.collect.ImmutableList;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import dev.beecube31.crazyae2.client.gui.sprites.ISpriteProvider;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.CrazyAESidedHandler;
import dev.beecube31.crazyae2.core.client.CrazyAEClientConfig;
import ic2.core.item.block.ItemBlockTileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.*;

import static dev.beecube31.crazyae2.common.util.ModsChecker.*;

@SuppressWarnings("unused")
public class Utils {
    public static final String RENDER_FLAG_BASE = "RENDERFLAG~";
    public static final String RENDER_FLAG_BASE_TOOLTIPED = "§7RENDERFLAG~";
    public static final String SPRITE_FLAG = "SPRITE~";
    public static final String ITEMSTACK_FLAG = "STACK~";

    public static ISpriteProvider decodeSpriteFlag(String v) {
        String[] data = v.split("\\|");
        return new ISpriteProvider() {
            @Override
            public int getTextureX() {
                return Integer.parseInt(data[3]);
            }

            @Override
            public int getTextureY() {
                return Integer.parseInt(data[4]);
            }

            @Override
            public int getSizeX() {
                return Integer.parseInt(data[1]);
            }

            @Override
            public int getSizeY() {
                return Integer.parseInt(data[2]);
            }

            @Override
            public String getTextureStr() {
                return data[0];
            }

            @Override
            public ResourceLocation getTexture() {
                return new ResourceLocation(data[5]);
            }
        };
    }

    @SideOnly(Side.CLIENT)
    public static String encodeSpriteFlag(ISpriteProvider r) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return "";
        return r.getTextureStr() + '|' + r.getSizeX() + '|' + r.getSizeY() + '|' + r.getTextureX() + '|' + r.getTextureY() + '|' + r.getTexture().toString();
    }

    @SideOnly(Side.CLIENT)
    public static String writeItemStackFlag(ItemStack v) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return "";
        return RENDER_FLAG_BASE + ITEMSTACK_FLAG + encodeItemStack(v) + ';';
    }

    @SideOnly(Side.CLIENT)
    public static String writeSpriteFlag(ISpriteProvider v) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return "";
        return RENDER_FLAG_BASE + SPRITE_FLAG + encodeSpriteFlag(v) + ';';
    }

    @SideOnly(Side.CLIENT)
    public static String encodeItemStack(ItemStack toEncode) {
        NBTTagCompound comp = new NBTTagCompound();
        return toEncode.writeToNBT(comp).toString();
    }

    @SideOnly(Side.CLIENT)
    public static ItemStack decodeItemStack(String nbt) {
        try {
            return new ItemStack(JsonToNBT.getTagFromJson(nbt));
        } catch (NBTException ignored) {
            return ItemStack.EMPTY;
        }
    }

    public static boolean isToolChargingEnabled(ItemStack victim) {
        return victim.hasTagCompound() && victim.getTagCompound().hasKey("enableCharging") && victim.getTagCompound().getBoolean("enableCharging");
    }

    @SideOnly(Side.CLIENT)
    public static void addReqChannelTooltip(List<String> tooltip) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return;
        tooltip.add(
                writeItemStackFlag(AEApi.instance().definitions().parts().cableSmart().stack(AEColor.values()[CrazyAE.oneToSixteenTicks], 1))
                + CrazyAETooltip.REQUIRE_CHANNEL.getLocal()
        );
    }

    @SideOnly(Side.CLIENT)
    public static void addReqManaPerJob(List<String> tooltip, double val) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return;
        tooltip.add(
                writeSpriteFlag(Sprite.MANA_TABLET)
                + String.format(
                        CrazyAETooltip.REQUIRE_MANA_PER_JOB.getLocalWithSpaceAtEnd(),
                        val
                )
        );
    }

    @SideOnly(Side.CLIENT)
    public static void addReqManaPerTick(List<String> tooltip, double val) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return;
        tooltip.add(
                writeSpriteFlag(Sprite.MANA_TABLET)
                + String.format(
                    CrazyAETooltip.REQUIRE_MANA_PER_TICK.getLocalWithSpaceAtEnd(), val
                )
        );
    }

    @SideOnly(Side.CLIENT)
    public static void addReqAePerJob(List<String> tooltip, double val) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return;
        tooltip.add(
                writeSpriteFlag(Sprite.ENERGY)
                + String.format(
                    CrazyAETooltip.REQUIRE_AE_PER_JOB.getLocalWithSpaceAtEnd(), val
                )
        );
    }

    @SideOnly(Side.CLIENT)
    public static void addReqAePerTick(List<String> tooltip, double val) {
        if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return;
        tooltip.add(
                writeSpriteFlag(Sprite.ENERGY)
                + String.format(
                    CrazyAETooltip.REQUIRE_AE_PER_TICK.getLocalWithSpaceAtEnd(), val
                )
        );
    }

    public static List<IAEItemStack> getContainerItemsFromInputs(ICraftingPatternDetails details, long batchSize) {
        List<IAEItemStack> containerItemsToReturn = new ArrayList<>();
        if (details == null || batchSize <= 0) return containerItemsToReturn;

        final IAEItemStack[] inputs = details.getInputs();

        for (final IAEItemStack templateInput : inputs) {
            if (templateInput == null || templateInput.getStackSize() <= 0) continue;

            ItemStack inputStackForOneCraft = templateInput.createItemStack();
            if (inputStackForOneCraft.isEmpty()) continue;

            ItemStack containerPerSingleItem = Platform.getContainerItem(inputStackForOneCraft.copy().splitStack(1));

            if (!containerPerSingleItem.isEmpty()) {
                IAEItemStack aeContainerStack = AEItemStack.fromItemStack(containerPerSingleItem.copy());
                if (aeContainerStack == null || aeContainerStack.getStackSize() <= 0) continue;

                long totalUnitsOfThisInputItemUsed = Utils.multiplySafely(templateInput.getStackSize(), batchSize);
                if (totalUnitsOfThisInputItemUsed <= 0) continue;

                long totalContainersFromThisInputType = Utils.multiplySafely(aeContainerStack.getStackSize(), totalUnitsOfThisInputItemUsed);

                if (totalContainersFromThisInputType > 0) {
                    IAEItemStack finalBatchContainer = aeContainerStack.copy();
                    finalBatchContainer.setStackSize(totalContainersFromThisInputType);

                    boolean merged = false;
                    for (IAEItemStack existingContainer : containerItemsToReturn) {
                        if (existingContainer.isSameType(finalBatchContainer)) {
                            existingContainer.setStackSize(existingContainer.getStackSize() + finalBatchContainer.getStackSize());
                            merged = true;
                            break;
                        }
                    }
                    if (!merged) {
                        containerItemsToReturn.add(finalBatchContainer);
                    }
                }
            }
        }
        return containerItemsToReturn;
    }

    public static NBTTagCompound createItemTag(final ItemStack i, boolean ignoreItemNBT) {
        final NBTTagCompound c = new NBTTagCompound();

        if (!i.isEmpty()) {
            stackWriteToNBT(i, c, ignoreItemNBT);
        }

        return c;
    }

    public static void stackWriteToNBT(ItemStack is, NBTTagCompound itemNBT, boolean ignoreItemNBT) {
        if (ignoreItemNBT && !isItemAlwaysContainsNBT(is)) is.setTagCompound(null);
        is.writeToNBT(itemNBT);
        if (is.getCount() > Byte.MAX_VALUE) {
            itemNBT.setInteger("stackSize", is.getCount());
        }
    }

    public static String format4(double amt) {
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

    public static String getFullDecimalOf(double val) {
        return String.format("%.0f", val);
    }

    public static String format1(double amt) {
        if (amt > 4_000_000_000_000L) {
            return String.format("%fT", amt / 1024 / 1024 / 1024 / 1024);
        } else if (amt > 4_000_000_000L) {
            return String.format("%fG", amt / 1024 / 1024 / 1024);
        } else if (amt > 4_000_000L) {
            return String.format("%fM", amt / 1024 / 1024);
        } else if (amt > 4_000L) {
            return String.format("%fk", amt / 1024);
        } else {
            return Double.toString(amt);
        }
    }

    public static long getStorageCountOf(ItemStack item) {
        if (item != null) {
            return AEApi.instance().definitions().blocks().craftingStorage1k().isSameAs(item) ? 1024
                    : AEApi.instance().definitions().blocks().craftingStorage4k().isSameAs(item) ? 1024 * 4
                    : AEApi.instance().definitions().blocks().craftingStorage16k().isSameAs(item) ? 1024 * 16
                    : AEApi.instance().definitions().blocks().craftingStorage64k().isSameAs(item) ? 1024 * 64
                    : CrazyAE.definitions().blocks().craftingStorage256k().isSameAs(item) ? 1024 * 256
                    : CrazyAE.definitions().blocks().craftingStorage1mb().isSameAs(item) ? 1024 * 1024
                    : CrazyAE.definitions().blocks().craftingStorage4mb().isSameAs(item) ? 1024 * 4096
                    : CrazyAE.definitions().blocks().craftingStorage16mb().isSameAs(item) ? 1024 * 16384
                    : CrazyAE.definitions().blocks().craftingStorage64mb().isSameAs(item) ? 1024 * 65536
                    : CrazyAE.definitions().blocks().craftingStorage256mb().isSameAs(item) ? 1024 * 262144
                    : CrazyAE.definitions().blocks().craftingStorage1gb().isSameAs(item) ? 1024 * 1048576
                    : CrazyAE.definitions().blocks().craftingStorage2gb().isSameAs(item) ? 2147483648L
                    : CrazyAE.definitions().blocks().craftingStorage8gb().isSameAs(item) ? 2147483648L * 4
                    : CrazyAE.definitions().blocks().craftingStorage32gb().isSameAs(item) ? 2147483648L * 16
                    : CrazyAE.definitions().blocks().craftingStorage128gb().isSameAs(item) ? 2147483648L * 64
                    : CrazyAE.definitions().blocks().craftingStorageCreative().isSameAs(item) ? Long.MAX_VALUE
                    : 0;
        }

        return 0;
    }

    public static int getAcceleratorsCountOf(ItemStack item) {
        if (item != null) {
            return AEApi.instance().definitions().blocks().craftingAccelerator().isSameAs(item) ? 1
                    : CrazyAE.definitions().blocks().coprocessor4x().isSameAs(item) ? 4
                    : CrazyAE.definitions().blocks().coprocessor16x().isSameAs(item) ? 16
                    : CrazyAE.definitions().blocks().coprocessor64x().isSameAs(item) ? 64
                    : CrazyAE.definitions().blocks().coprocessor256x().isSameAs(item) ? 256
                    : CrazyAE.definitions().blocks().coprocessor1024x().isSameAs(item) ? 1024
                    : CrazyAE.definitions().blocks().coprocessor4096x().isSameAs(item) ? 4096
                    : CrazyAE.definitions().blocks().coprocessor16384x().isSameAs(item) ? 16384
                    : CrazyAE.definitions().blocks().coprocessor65536x().isSameAs(item) ? 65536
                    : CrazyAE.definitions().blocks().coprocessor262144x().isSameAs(item) ? 262144
                    : CrazyAE.definitions().blocks().coprocessor1048576x().isSameAs(item) ? 1048576
                    : CrazyAE.definitions().blocks().coprocessor4194304x().isSameAs(item) ? 4194304
                    : CrazyAE.definitions().blocks().coprocessorCreative().isSameAs(item) ? Integer.MAX_VALUE
                    : 0;
        }

        return 0;
    }

    public static int findSlotIndex(ICraftingPatternDetails details, IAEItemStack templateInput) {
        IAEItemStack[] inputs = details.getInputs();
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] != null && inputs[i].equals(templateInput)) {
                return i;
            }
            if (inputs[i] != null && inputs[i].isSameType(templateInput)) {
                return i;
            }
        }
        return -1;
    }

    public static long multiplySafely(long a, long b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        long result = a * b;
        if (a == result / b) {
            return result;
        }
        return -1;
    }

    public static boolean isIUBlock(TileEntity te) {
        return IU_LOADED && te instanceof TileEntityBlock;
    }

    public static boolean isItemAlwaysContainsNBT(ItemStack is) {
        return IU_LOADED && (is.getItem() instanceof ItemCraftingElements
                || is.getItem() instanceof ItemBaseCircuit);
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

    public static String generateCraftingID(IAEItemStack finalOutput) {
        final long now = System.currentTimeMillis();
        final int hash = System.identityHashCode(Platform.getRandomInt());
        final int itemHash = finalOutput == null ? 0 : finalOutput.hashCode();

        return Long.toString(now, Character.MAX_RADIX) + '-' + Integer.toString(hash, Character.MAX_RADIX) + '-' + Integer.toString(itemHash, Character.MAX_RADIX);
    }

    public static void postChange(final IAEItemStack diff, final IActionSource src, Iterator<Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object>> listeners) {
        if (listeners.hasNext()) {
            final ImmutableList<IAEItemStack> single = ImmutableList.of(diff.copy());

            while (listeners.hasNext()) {
                final Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object> o = listeners.next();
                final IMEMonitorHandlerReceiver<IAEItemStack> receiver = o.getKey();

                if (receiver.isValid(o.getValue())) {
                    receiver.postChange(null, single, src);
                } else {
                    listeners.remove();
                }
            }
        }
    }

    public static NBTTagCompound generateLinkData(final String craftingID, final boolean standalone, final boolean req) {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setString("CraftID", craftingID);
        tag.setBoolean("canceled", false);
        tag.setBoolean("done", false);
        tag.setBoolean("standalone", standalone);
        tag.setBoolean("req", req);

        return tag;
    }

    public static void setStackInSlot(final IItemHandler inv, final int slot, final ItemStack stack) {
        if (inv instanceof IItemHandlerModifiable m) {
            m.setStackInSlot(slot, stack);
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

    public static void updateEnergyHandler(IItemHandler from) {
        for (int i = 0; i < from.getSlots(); ++i) {
            List<IItemDefinition> found = getItemEnergyType(from.getStackInSlot(i));
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

    public static List<IItemDefinition> getItemEnergyType(ItemStack is) {
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
