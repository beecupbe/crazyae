package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.interfaces.IChangeablePriorityHost;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerPriority extends AEBaseContainer {

    private final IChangeablePriorityHost priHost;

    @SideOnly(Side.CLIENT)
    private GuiTextField textField;
    @GuiSync(2)
    public long PriorityValue = -1;

    public ContainerPriority(final InventoryPlayer ip, final IChangeablePriorityHost te) {
        super(ip, (TileEntity) (te instanceof TileEntity ? te : null), (IPart) (te instanceof IPart ? te : null));
        this.priHost = te;
    }

    @SideOnly(Side.CLIENT)
    public void setTextField(final GuiTextField level) {
        this.textField = level;
        this.textField.setText(String.valueOf(this.PriorityValue));
    }

    public void setPriority(final int newValue, final EntityPlayer player) {
        this.priHost.setPriority(newValue);
        this.PriorityValue = newValue;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.PriorityValue = this.priHost.getPriority();
        }
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        if (field.equals("PriorityValue")) {
            if (this.textField != null) {
                this.textField.setText(String.valueOf(this.PriorityValue));
            }
        }

        super.onUpdate(field, oldValue, newValue);
    }

    public IChangeablePriorityHost getPriorityHost() {
        return this.priHost;
    }
}
