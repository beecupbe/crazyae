package dev.beecube31.crazyae2.mixins.core.crafting.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.MathExpressionParser;
import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.networking.packets.PacketLongCraftRequest;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(value = GuiCraftAmount.class, remap = false)
public abstract class MixinGuiCraftAmount extends AEBaseGui {
    @Shadow private GuiTabButton originalGuiBtn;

    @Shadow private GuiBridge originalGui;

    @Shadow private GuiButton next;

    @Shadow private GuiTextField amountToCraft;

    @Shadow private GuiButton plus1;

    @Shadow private GuiButton plus10;

    @Shadow private GuiButton plus100;

    @Shadow private GuiButton plus1000;

    @Shadow private GuiButton minus1;

    @Shadow private GuiButton minus10;

    @Shadow private GuiButton minus100;

    @Shadow private GuiButton minus1000;

    @Shadow protected abstract void addQty(int i);

    public MixinGuiCraftAmount(Container container) {
        super(container);
    }

    /**
     * @author Beecube31
     * @reason The PacketCraftRequest constructor uses an int craftAmt variable instead of a long, and this fix will remove that limitation by sending my packet instead of the original packet
     * @since v0.5.1
     */
    @Overwrite
    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        try {

            if (btn == this.originalGuiBtn) {
                NetworkHandler.instance().sendToServer(new PacketSwitchGuis(this.originalGui));
            }

            if (btn == this.next) {
                double resultD = MathExpressionParser.parse(this.amountToCraft.getText());
                long result;
                if (resultD <= 0 || Double.isNaN(resultD)) {
                    result = 1;
                } else {
                    result = (long) MathExpressionParser.round(resultD, 0);
                }

                dev.beecube31.crazyae2.common.networking.network.NetworkHandler.instance().sendToServer(new PacketLongCraftRequest(result, isShiftKeyDown()));
            }
        } catch (final NumberFormatException e) {
            // nope..
            this.amountToCraft.setText("1");
        }

        final boolean isPlus = btn == this.plus1 || btn == this.plus10 || btn == this.plus100 || btn == this.plus1000;
        final boolean isMinus = btn == this.minus1 || btn == this.minus10 || btn == this.minus100 || btn == this.minus1000;

        if (isPlus || isMinus) {
            this.addQty(this.getQty(btn));
        }
    }
}
