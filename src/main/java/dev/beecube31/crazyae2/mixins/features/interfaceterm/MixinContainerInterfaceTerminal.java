package dev.beecube31.crazyae2.mixins.features.interfaceterm;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerInterfaceTerminal;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketCompressedNBT;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.InventoryAction;
import appeng.helpers.ItemStackHelper;
import appeng.items.misc.ItemEncodedPattern;
import appeng.parts.misc.PartInterface;
import appeng.tile.misc.TileInterface;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.AdaptorItemHandler;
import appeng.util.inv.WrapperCursorItemHandler;
import appeng.util.inv.WrapperFilteredItemHandler;
import appeng.util.inv.WrapperRangeItemHandler;
import appeng.util.inv.filter.IAEItemFilter;
import dev.beecube31.crazyae2.common.duality.PatternsInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEInterfaceHost;
import dev.beecube31.crazyae2.common.interfaces.IGridHostMonitorable;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.common.tile.networking.TilePatternsInterface;
import dev.beecube31.crazyae2.common.util.InvTracker;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Mixin(value = ContainerInterfaceTerminal.class, remap = false, priority = 990)
public abstract class MixinContainerInterfaceTerminal extends AEBaseContainer {
    @Shadow private IGrid grid;

    @Shadow private NBTTagCompound data;

    public MixinContainerInterfaceTerminal(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Shadow protected abstract boolean isDifferent(ItemStack a, ItemStack b);

    @Unique private final Map<IInterfaceHost, InvTracker> crazyae$diList = new HashMap<>();

    @Unique private final Map<ICrazyAEInterfaceHost, InvTracker> crazyae$patternsDiList = new HashMap<>();

    @Unique private final Map<IGridHostMonitorable, InvTracker> crazyae$macDiList = new HashMap<>();

    @Unique private final Map<Long, InvTracker> crazyae$byId = new HashMap<>();

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    @Override
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

                for (final IGridNode gn : this.grid.getMachines(TilePatternsInterface.class)) {
                    if (gn.isActive()) {
                        final ICrazyAEInterfaceHost ih = (ICrazyAEInterfaceHost) gn.getMachine();
                        if (ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) {
                            continue;
                        }

                        final InvTracker t = this.crazyae$patternsDiList.get(ih);

                        if (t == null) {
                            missing = true;
                        } else {
                            final PatternsInterfaceDuality dual = ih.getInterfaceDuality();
                            if (!t.unlocalizedName.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }

                        total++;
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(TileImprovedMAC.class)) {
                    if (gn.isActive()) {
                        final IGridHostMonitorable node = (IGridHostMonitorable) gn.getMachine();
                        final InvTracker t = this.crazyae$macDiList.get(node);

                        missing = t == null;

                        total++;
                    }
                }
            }
        }


        if (total != this.crazyae$diList.size() + this.crazyae$patternsDiList.size() + this.crazyae$macDiList.size() || missing) {
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

            for (final Map.Entry<ICrazyAEInterfaceHost, InvTracker> en : this.crazyae$patternsDiList.entrySet()) {
                final InvTracker inv = en.getValue();
                for (int x = 0; x < inv.server.getSlots(); x++) {
                    if (this.isDifferent(inv.server.getStackInSlot(x), inv.client.getStackInSlot(x))) {
                        this.crazyae$addItems(this.data, inv, x, 1);
                    }
                }
            }

            for (final Map.Entry<IGridHostMonitorable, InvTracker> en : this.crazyae$macDiList.entrySet()) {
                final InvTracker inv = en.getValue();
                for (int x = 0; x < inv.server.getSlots(); x++) {
                    if (this.isDifferent(inv.server.getStackInSlot(x), inv.client.getStackInSlot(x))) {
                        this.crazyae$addItems(this.data, inv, x, 1);
                    }
                }
            }
        }

        if (!data.isEmpty()) {
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
     * @reason Support Patterns interface
     */
    @Override
    @Overwrite
    public void doAction(EntityPlayerMP player, InventoryAction action, int slot, long id) {
        final InvTracker inv = this.crazyae$byId.get(id);
        if (inv != null) {
            final ItemStack is = inv.server.getStackInSlot(slot);
            final boolean hasItemInHand = !player.inventory.getItemStack().isEmpty();

            final InventoryAdaptor playerHand = new AdaptorItemHandler(new WrapperCursorItemHandler(player.inventory));

            final IItemHandler theSlot = new WrapperFilteredItemHandler(new WrapperRangeItemHandler(inv.server, slot, slot + 1),
                    new IAEItemFilter() {
                        @Override
                        public boolean allowExtract(IItemHandler inv, int slot, int amount) {
                            return true;
                        }

                        @Override
                        public boolean allowInsert(IItemHandler inv, int slot, ItemStack stack) {
                            return !stack.isEmpty() && stack.getItem() instanceof ItemEncodedPattern;
                        }
                    });
            final InventoryAdaptor interfaceSlot = new AdaptorItemHandler(theSlot);

            switch (action) {
                case PICKUP_OR_SET_DOWN:

                    if (hasItemInHand) {
                        ItemStack inSlot = theSlot.getStackInSlot(0);
                        if (inSlot.isEmpty()) {
                            player.inventory.setItemStack(interfaceSlot.addItems(player.inventory.getItemStack()));
                        } else {
                            inSlot = inSlot.copy();
                            final ItemStack inHand = player.inventory.getItemStack().copy();

                            ItemHandlerUtil.setStackInSlot(theSlot, 0, ItemStack.EMPTY);
                            player.inventory.setItemStack(ItemStack.EMPTY);

                            player.inventory.setItemStack(interfaceSlot.addItems(inHand.copy()));

                            if (player.inventory.getItemStack().isEmpty()) {
                                player.inventory.setItemStack(inSlot);
                            } else {
                                player.inventory.setItemStack(inHand);
                                ItemHandlerUtil.setStackInSlot(theSlot, 0, inSlot);
                            }
                        }
                    } else {
                        ItemHandlerUtil.setStackInSlot(theSlot, 0, playerHand.addItems(theSlot.getStackInSlot(0)));
                    }

                    break;
                case SPLIT_OR_PLACE_SINGLE:

                    if (hasItemInHand) {
                        ItemStack extra = playerHand.removeItems(1, ItemStack.EMPTY, null);
                        if (!extra.isEmpty()) {
                            extra = interfaceSlot.addItems(extra);
                        }
                        if (!extra.isEmpty()) {
                            playerHand.addItems(extra);
                        }
                    } else if (!is.isEmpty()) {
                        ItemStack extra = interfaceSlot.removeItems((is.getCount() + 1) / 2, ItemStack.EMPTY, null);
                        if (!extra.isEmpty()) {
                            extra = playerHand.addItems(extra);
                        }
                        if (!extra.isEmpty()) {
                            interfaceSlot.addItems(extra);
                        }
                    }

                    break;
                case SHIFT_CLICK:

                    final InventoryAdaptor playerInv = InventoryAdaptor.getAdaptor(player);

                    ItemHandlerUtil.setStackInSlot(theSlot, 0, playerInv.addItems(theSlot.getStackInSlot(0)));

                    break;
                case MOVE_REGION:

                    final InventoryAdaptor playerInvAd = InventoryAdaptor.getAdaptor(player);
                    for (int x = 0; x < inv.server.getSlots(); x++) {
                        ItemHandlerUtil.setStackInSlot(inv.server, x, playerInvAd.addItems(inv.server.getStackInSlot(x)));
                    }

                    break;
                case CREATIVE_DUPLICATE:

                    if (player.capabilities.isCreativeMode && !hasItemInHand) {
                        player.inventory.setItemStack(is.isEmpty() ? ItemStack.EMPTY : is.copy());
                    }

                    break;
                default:
                    return;
            }

            this.updateHeld(player);
        }
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    private void regenList(final NBTTagCompound data) {
        this.crazyae$byId.clear();
        this.crazyae$diList.clear();
        this.crazyae$patternsDiList.clear();
        this.crazyae$macDiList.clear();


        final IActionHost host = this.getActionHost();
        if (host != null) {
            final IGridNode agn = host.getActionableNode();
            if (agn.isActive()) {
                for (final IGridNode gn : this.grid.getMachines(TileInterface.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$diList.put(ih, new InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(PartInterface.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$diList.put(ih, new InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(TilePatternsInterface.class)) {
                    final ICrazyAEInterfaceHost ih = (ICrazyAEInterfaceHost) gn.getMachine();
                    final PatternsInterfaceDuality dual = ih.getInterfaceDuality();
                    if (gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES) {
                        this.crazyae$patternsDiList.put(ih, new InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                    }
                }

                for (final IGridNode gn : this.grid.getMachines(TileImprovedMAC.class)) {
                    final IGridHostMonitorable ih = (IGridHostMonitorable) gn.getMachine();
                    if (gn.isActive()) {
                        this.crazyae$macDiList.put(ih, new InvTracker(ih, true, ih.getPatternsInv(), ih.getName()));
                    }
                }
            }
        }

        data.setBoolean("clear", true);

        for (final Map.Entry<IInterfaceHost, InvTracker> en : this.crazyae$diList.entrySet()) {
            final InvTracker inv = en.getValue();
            this.crazyae$byId.put(inv.which, inv);
            this.crazyae$addItems(data, inv, 0, inv.server.getSlots());
        }

        for (final Map.Entry<ICrazyAEInterfaceHost, InvTracker> en : this.crazyae$patternsDiList.entrySet()) {
            final InvTracker inv = en.getValue();
            this.crazyae$byId.put(inv.which, inv);
            this.crazyae$addItems(data, inv, 0, inv.server.getSlots());
        }

        for (final Map.Entry<IGridHostMonitorable, InvTracker> en : this.crazyae$macDiList.entrySet()) {
            final InvTracker inv = en.getValue();
            this.crazyae$byId.put(inv.which, inv);
            this.crazyae$addItems(data, inv, 0, inv.server.getSlots());
        }
    }

    @Unique
    private void crazyae$addItems(NBTTagCompound data, InvTracker inv, int offset, int length) {
        String name = '=' + Long.toString(inv.which, 36);
        NBTTagCompound tag = data.getCompoundTag(name);
        if (tag.isEmpty()) {
            tag.setLong("sortBy", inv.sortBy);
            tag.setString("un", inv.unlocalizedName);
            tag.setTag("pos", NBTUtil.createPosTag(inv.pos));
            tag.setInteger("dim", inv.dim);
            tag.setInteger("numUpgrades", inv.numUpgrades);
            tag.setBoolean("isPatternInterface", inv.isPatternInterface);
            tag.setBoolean("isMAC", inv.isMAC);
        }

        for (int x = 0; x < length; ++x) {
            NBTTagCompound itemNBT = new NBTTagCompound();
            ItemStack is = inv.server.getStackInSlot(x + offset);
            ItemHandlerUtil.setStackInSlot(inv.client, x + offset, is.isEmpty() ? ItemStack.EMPTY : is.copy());
            if (!is.isEmpty()) {
                ItemStackHelper.stackWriteToNBT(is, itemNBT);
            }

            tag.setTag(Integer.toString(x + offset), itemNBT);
        }

        data.setTag(name, tag);
    }
}
