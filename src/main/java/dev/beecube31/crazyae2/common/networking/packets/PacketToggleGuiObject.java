package dev.beecube31.crazyae2.common.networking.packets;

import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.security.IActionHost;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.sync.network.INetworkInfo;
import appeng.me.GridAccessException;
import appeng.parts.reporting.AbstractPartEncoder;
import dev.beecube31.crazyae2.common.containers.ContainerMechanicalBotaniaTileBase;
import dev.beecube31.crazyae2.common.containers.ContainerQuantumCPU;
import dev.beecube31.crazyae2.common.networking.CrazyAEPacket;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.mixins.features.patternterm.fastplace.AccessorContainerPatternEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketToggleGuiObject extends CrazyAEPacket {
    private final String Name;
    private final String Value;

    public PacketToggleGuiObject(final ByteBuf stream) throws IOException {
        final DataInputStream dis = new DataInputStream(this.getPacketByteArray(stream, stream.readerIndex(), stream.readableBytes()));
        this.Name = dis.readUTF();
        this.Value = dis.readUTF();
    }

    public PacketToggleGuiObject(final String name) throws IOException {
        this.Name = name;
        this.Value = "";
        final ByteBuf data = Unpooled.buffer();
        data.writeInt(this.getPacketID());
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(name);
        dos.writeUTF("");
        data.writeBytes(bos.toByteArray());
        this.configureWrite(data);
    }

    public PacketToggleGuiObject(final String name, final String value) throws IOException {
        this.Name = name;
        this.Value = value;
        final ByteBuf data = Unpooled.buffer();
        data.writeInt(this.getPacketID());
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF(name);
        dos.writeUTF(value);
        data.writeBytes(bos.toByteArray());
        this.configureWrite(data);
    }

    @Override
    public void serverPacketData(final INetworkInfo manager, final CrazyAEPacket packet, final EntityPlayer player) {
        final Container c = player.openContainer;

        if (this.Name.startsWith("CRAZYAE.GUI")) {
            switch (this.Name) {
                case "CRAZYAE.GUI.QCPU.page.change" -> {
                    if (c instanceof ContainerQuantumCPU e) {
                        e.setCurrentPage(Integer.parseInt(this.Value));
                    }
                }

                case "CRAZYAE.GUI.encodeBtn.pressed" -> {
                    if (c instanceof ContainerMechanicalBotaniaTileBase e && e.validateRecipe()) {
                        e.encodePattern();
                    }
                }

                case "CRAZYAE.GUI.patternTerm.clearnbt" -> {
                    if (c instanceof ContainerPatternTerm t) {
                        final NBTTagCompound encodedValue = new NBTTagCompound();

                        final NBTTagList tagIn = new NBTTagList();
                        final NBTTagList tagOut = new NBTTagList();

                        for (int i = 0; i < ((AccessorContainerPatternEncoder) t).getCrafting().getSlots(); i++) {
                            tagIn.appendTag(Utils.createItemTag(((AccessorContainerPatternEncoder) t).getCrafting().getStackInSlot(i), true));
                        }

                        tagOut.appendTag(Utils.createItemTag(((AccessorContainerPatternEncoder) t).getCOut().getStackInSlot(0), true));

                        encodedValue.setTag("in", tagIn);
                        encodedValue.setTag("out", tagOut);
                        encodedValue.setBoolean("crafting", true);
                        encodedValue.setBoolean("substitute", t.substitute);

                        ((AccessorContainerPatternEncoder) t).getPatternSlotOUT().getStack().setTagCompound(encodedValue);
                    }
                }

                case "CRAZYAE.GUI.encoder.syncRecipe" -> {
                    if (c instanceof ContainerMechanicalBotaniaTileBase e) {
                        e.syncClientOnFirstLoad();
                    }
                }

                case "CRAZYAE.GUI.patternTerm.fastPlace" -> {
                    if (c instanceof ContainerPatternTerm t) {
                        SlotRestrictedInput patternSlotOUT = ((AccessorContainerPatternEncoder) t).getPatternSlotOUT();
                        ItemStack patternStack = patternSlotOUT.getStack();
                        if (patternStack.isEmpty()) {
                            return;
                        }

                        try {
                            AbstractPartEncoder part = t.getPart();
                            IGuiItemObject itemObject = ((AccessorContainerPatternEncoder) t).getIGuiItemObject();
                            IMachineSet availableTiles;
                            if (part != null) {
                                availableTiles = part.getProxy().getGrid().getMachines(TileImprovedMAC.class);
                            } else if (itemObject instanceof IActionHost wirelessTerm) {
                                availableTiles = wirelessTerm.getActionableNode().getGrid().getMachines(TileImprovedMAC.class);
                            } else {
                                return;
                            }

                            for (final IGridNode channelNode : availableTiles) {
                                TileImprovedMAC te = (TileImprovedMAC) channelNode.getMachine();
                                if (te.acceptPatternFromTerm(patternStack)) {
                                    patternStack.shrink(1);
                                    break;
                                }
                            }
                        } catch (GridAccessException e) {
                            CrazyAE.logger().error(e);
                        }
                    }
                }

                case "CRAZYAE.GUI.patternTerm.fastPlaceQCpu" -> {
                    if (c instanceof ContainerPatternTerm t) {
                        SlotRestrictedInput patternSlotOUT = ((AccessorContainerPatternEncoder) t).getPatternSlotOUT();
                        ItemStack patternStack = patternSlotOUT.getStack();
                        if (patternStack.isEmpty()) {
                            return;
                        }

                        try {
                            AbstractPartEncoder part = t.getPart();
                            IGuiItemObject itemObject = ((AccessorContainerPatternEncoder) t).getIGuiItemObject();
                            IMachineSet availableTiles;
                            if (part != null) {
                                availableTiles = part.getProxy().getGrid().getMachines(TileQuantumCPU.class);
                            } else if (itemObject instanceof IActionHost wirelessTerm) {
                                availableTiles = wirelessTerm.getActionableNode().getGrid().getMachines(TileQuantumCPU.class);
                            } else {
                                return;
                            }

                            for (final IGridNode channelNode : availableTiles) {
                                TileQuantumCPU te = (TileQuantumCPU) channelNode.getMachine();
                                if (te.acceptPatternFromTerm(patternStack)) {
                                    patternStack.shrink(1);
                                    break;
                                }
                            }
                        } catch (GridAccessException e) {
                            CrazyAE.logger().error(e);
                        }
                    }
                }
            }
        }
    }
}

