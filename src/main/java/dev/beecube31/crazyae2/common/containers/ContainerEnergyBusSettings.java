package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPart;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.interfaces.IEnergyBus;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerEnergyBusSettings extends CrazyAEBaseContainer {

    private final IEnergyBus host;

    @SideOnly(Side.CLIENT)
    private GuiTextField textField;

    @GuiSync(2)
    public long energy = -1;

    public ContainerEnergyBusSettings(final InventoryPlayer ip, final IEnergyBus te) {
        super(ip, (TileEntity) (te instanceof TileEntity ? te : null), (IPart) (te instanceof IPart ? te : null));
        this.host = te;
    }

    @SideOnly(Side.CLIENT)
    public void setTextField(final GuiTextField level) {
        this.textField = level;
        this.textField.setText(String.valueOf(this.energy));
    }

    public void setSettings(final long newValue, final EntityPlayer player) {
        this.host.setMaxConfigEnergy(newValue);
        this.energy = newValue;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.energy = this.host.getMaxConfigEnergy();
        }
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        if (field.equals("energy")) {
            if (this.textField != null) {
                this.textField.setText(String.valueOf(this.energy));
            }
        }

        super.onUpdate(field, oldValue, newValue);
    }

    public IEnergyBus getHost() {
        return this.host;
    }
}
