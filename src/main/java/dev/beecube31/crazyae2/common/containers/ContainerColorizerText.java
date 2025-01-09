package dev.beecube31.crazyae2.common.containers;

import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.items.ColorizerObj;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerColorizerText extends CrazyAEBaseContainer {
    @SideOnly(Side.CLIENT)
    private GuiTextField textField;

    @GuiSync(2)
    private String text;

    public ContainerColorizerText(final InventoryPlayer ip, final ColorizerObj obj) {
        super(ip, obj);
    }

    @SideOnly(Side.CLIENT)
    public void setTextField(final GuiTextField level) {
        this.textField = level;
    }

    public void setText(final String s) {
        this.text = s;
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        if (field.equals("HexText")) {
            if (this.textField != null) {
                this.textField.setText(this.text);
            }
        }

        super.onUpdate(field, oldValue, newValue);
    }
}
