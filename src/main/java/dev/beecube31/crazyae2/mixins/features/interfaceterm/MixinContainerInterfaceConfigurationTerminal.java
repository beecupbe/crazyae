package dev.beecube31.crazyae2.mixins.features.interfaceterm;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerInterfaceConfigurationTerminal;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketCompressedNBT;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.InventoryAction;
import appeng.parts.misc.PartInterface;
import appeng.tile.misc.TileInterface;
import appeng.util.Platform;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.WrapperRangeItemHandler;
import dev.beecube31.crazyae2.common.parts.implementations.PartPerfectInterface;
import dev.beecube31.crazyae2.common.tile.networking.TilePerfectInterface;
import dev.beecube31.crazyae2.common.util.InvTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static appeng.helpers.ItemStackHelper.stackWriteToNBT;

@Mixin(value = ContainerInterfaceConfigurationTerminal.class, remap = false, priority = 990)
public abstract class MixinContainerInterfaceConfigurationTerminal extends AEBaseContainer {

    @Shadow private IGrid grid;

    @Shadow protected abstract boolean isDifferent(ItemStack a, ItemStack b);

    @Shadow private NBTTagCompound data;

    @Unique private Map<IInterfaceHost, InvTracker> crazyae$diList = new HashMap<>();

    @Unique private final Map<Long, InvTracker> crazyae$byId = new HashMap<>();

    public MixinContainerInterfaceConfigurationTerminal(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Override
    @Overwrite
    public void detectAndSendChanges() {
        if (Platform.isClient()) {
            return;
        }

        super.detectAndSendChanges();

        if (this.grid == null) {
            return;
        }

        int total = 0;
        boolean missing = false;

        final IActionHost host = this.getActionHost();
        if (host != null) {
            final IGridNode agn = host.getActionableNode();
            if (agn.isActive()) {
                for (final IGridNode gn : this.grid.getMachines(TileInterface.class)) {
                    if (gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if (ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) {
                            continue;
                        }

                        final InvTracker t = this.crazyae$diList.get(ih);

                        if (t == null) {
                            missing = true;
                        } else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if (!t.unlocalizedName.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }

                        total++;
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(PartInterface.class)) {
                    if (gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if (ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) {
                            continue;
                        }

                        final InvTracker t = this.crazyae$diList.get(ih);

                        if (t == null) {
                            missing = true;
                        } else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if (!t.unlocalizedName.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }

                        total++;
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(TilePerfectInterface.class)) {
                    if (gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if (ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) {
                            continue;
                        }

                        final InvTracker t = this.crazyae$diList.get(ih);

                        if (t == null) {
                            missing = true;
                        } else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if (!t.unlocalizedName.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }

                        total++;
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(PartPerfectInterface.class)) {
                    if (gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if (ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) {
                            continue;
                        }

                        final InvTracker t = this.crazyae$diList.get(ih);

                        if (t == null) {
                            missing = true;
                        } else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if (!t.unlocalizedName.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }

                        total++;
                    }
                }
            }
        }

        if (total != this.crazyae$diList.size() || missing) {
            this.regenList(this.data);
        } else {
            for (final Map.Entry<IInterfaceHost, InvTracker> en : this.crazyae$diList.entrySet()) {
                final InvTracker inv = en.getValue();
                for (int x = 0; x < inv.server.getSlots(); x++) {
                    if (this.isDifferent(inv.server.getStackInSlot(x), inv.client.getStackInSlot(x))) {
                        this.crazyae$addItems(this.data, inv, x, 1);
                    }
                }
            }
        }

        if (!this.data.isEmpty()) {
            try {
                NetworkHandler.instance().sendTo(new PacketCompressedNBT(this.data), (EntityPlayerMP) this.getPlayerInv().player);
            } catch (final IOException e) {
                // :P
            }

            this.data = new NBTTagCompound();
        }
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Override
    @Overwrite
    public void doAction(final EntityPlayerMP player, final InventoryAction action, final int slot, final long id) {
        final InvTracker inv = this.crazyae$byId.get(id);
        if (inv != null) {
            final boolean hasItemInHand = !player.inventory.getItemStack().isEmpty();
            final IItemHandler theSlot = new WrapperRangeItemHandler(inv.server, slot, slot + 1);

            ItemStack inSlot = theSlot.getStackInSlot(0);

            switch (action) {
                case PICKUP_OR_SET_DOWN:
                    if (hasItemInHand) {
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, player.inventory.getItemStack().copy());
                    } else {
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, ItemStack.EMPTY);
                    }
                    break;
                case PLACE_SINGLE:
                    if (inSlot.getCount() < inSlot.getMaxStackSize() * 8) {
                        inSlot.grow(1);
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, inSlot);
                    }
                    break;
                case PICKUP_SINGLE:
                    if (theSlot.getStackInSlot(0).getCount() > 1) {
                        inSlot.shrink(1);
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, inSlot);
                    }
                    break;
                case SPLIT_OR_PLACE_SINGLE:
                    if (hasItemInHand) {
                        if (ItemStack.areItemsEqual(inSlot, player.inventory.getItemStack()) && ItemStack.areItemStackTagsEqual(inSlot, player.inventory.getItemStack())) {
                            inSlot.grow(1);
                            ItemHandlerUtil.setStackInSlot(theSlot, 0, inSlot.copy());
                        } else {
                            ItemStack configuredStack = player.inventory.getItemStack().copy();
                            configuredStack.setCount(1);
                            ItemHandlerUtil.setStackInSlot(theSlot, 0, configuredStack);
                        }

                    } else if (!inSlot.isEmpty()) {
                        inSlot.shrink(1);
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, inSlot.copy());
                    }

                    break;
                case SHIFT_CLICK:
                    ItemHandlerUtil.setStackInSlot(theSlot, 0, ItemStack.EMPTY);
                    break;

                case CREATIVE_DUPLICATE:
                    if (player.capabilities.isCreativeMode && hasItemInHand) {
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, player.inventory.getItemStack().copy());
                    }
                    break;
                case HALVE:
                    if (inSlot.getCount() > 1) {
                        ItemStack halved = inSlot.copy();
                        halved.setCount(inSlot.getCount() / 2);
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, halved);
                    }
                    break;
                case DOUBLE:
                    ItemStack doubled = inSlot.copy();
                    doubled.setCount(Math.min(512, inSlot.getCount() * 2));
                    ItemHandlerUtil.setStackInSlot(theSlot, 0, doubled);
                    break;
                default:
                    return;
            }

            this.updateHeld(player);
        }
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Overwrite
    private void regenList(final NBTTagCompound data) {
        this.crazyae$byId.clear();
        this.crazyae$diList.clear();

        final IActionHost host = this.getActionHost();
        if (host != null) {
            final IGridNode agn = host.getActionableNode();
            if (agn.isActive()) {
                for (final IGridNode gn : this.grid.getMachines(TileInterface.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$diList.put(ih, new InvTracker(dual, dual.getConfig(), dual.getTermName()));
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(PartInterface.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$diList.put(ih, new InvTracker(dual, dual.getConfig(), dual.getTermName()));
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(TilePerfectInterface.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$diList.put(ih, new InvTracker(dual, 36, dual.getConfig(), dual.getTermName()));
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(PartPerfectInterface.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$diList.put(ih, new InvTracker(dual, 36, dual.getConfig(), dual.getTermName()));
                    }
                }
            }
        }

        data.setBoolean("clear", true);

        for (final Map.Entry<IInterfaceHost, InvTracker> en : this.crazyae$diList.entrySet()) {
            final InvTracker inv = en.getValue();
            this.crazyae$byId.put(inv.which, inv);
            this.crazyae$addItems(data, inv, 0, inv.forceSlots == 0 ? inv.server.getSlots() : inv.forceSlots);
        }
    }

    @Unique
    private void crazyae$addItems(final NBTTagCompound data, final InvTracker inv, final int offset, final int length) {
        final String name = '=' + Long.toString(inv.which, Character.MAX_RADIX);
        final NBTTagCompound tag = data.getCompoundTag(name);

        if (tag.isEmpty()) {
            tag.setLong("sortBy", inv.sortBy);
            tag.setString("un", inv.unlocalizedName);
            tag.setTag("pos", NBTUtil.createPosTag(inv.pos));
            tag.setInteger("dim", inv.dim);
            tag.setInteger("num", inv.forceSlots == 0 ? length / 9 : inv.forceSlots);
            tag.setInteger("forcedNum", inv.forceSlots);
        }

        for (int x = 0; x < length; x++) {
            final NBTTagCompound itemNBT = new NBTTagCompound();

            final ItemStack is = inv.server.getStackInSlot(x + offset);

            ItemHandlerUtil.setStackInSlot(inv.client, x + offset, is.isEmpty() ? ItemStack.EMPTY : is.copy());

            if (!is.isEmpty()) {
                stackWriteToNBT(is, itemNBT);
            }

            tag.setTag(Integer.toString(x + offset), itemNBT);
        }

        data.setTag(name, tag);
    }
}
