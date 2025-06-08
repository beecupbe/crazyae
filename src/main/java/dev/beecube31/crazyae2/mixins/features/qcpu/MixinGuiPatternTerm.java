package dev.beecube31.crazyae2.mixins.features.qcpu;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.interfaces.IJEIGhostIngredients;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = GuiPatternTerm.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiPatternTerm extends GuiMEMonitorable implements IJEIGhostIngredients {

    @Shadow @Final private ContainerPatternEncoder container;

    @Unique private GuiTabButton crazyae$fastPlaceQCpuBtn;

    public MixinGuiPatternTerm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(
            method = "actionPerformed",
            at = @At("RETURN")
    )
    private void crazyae$patchActionPerformed(final GuiButton btn, CallbackInfo ci) {
        try {
            if (this.crazyae$fastPlaceQCpuBtn == btn && this.container.isCraftingMode()) {
                TileEntity te = this.container.getPart().getHost().getTile();
                NetworkHandler.instance().sendToServer(new PacketToggleGuiObject(
                        "CRAZYAE.GUI.patternTerm.fastPlaceQCpu",
                        te.getPos().getX() + ";" + te.getPos().getY() + ";" + te.getPos().getZ())
                );
            }
        } catch (IOException e) {
            CrazyAE.logger().error(e);
        }
    }

    @Inject(
            method = "initGui",
            at = @At("TAIL")
    )
    private void crazyae$addOwnButton(CallbackInfo ci) {
        this.crazyae$fastPlaceQCpuBtn = new GuiTabButton(
                this.guiLeft + 173,
                this.guiTop + this.ySize - (Features.IMPROVED_MOLECULAR_ASSEMBLER.isEnabled() ? 131 : 154),
                CrazyAE.definitions().blocks().quantumCPU().maybeStack(1).orElse(ItemStack.EMPTY),
                String.format(
                        CrazyAEGuiText.BUTTON_PATTERN_FAST_PLACE.getLocal(),
                        CrazyAEGuiText.QUANTUM_CPU.getLocal()
                ),
                this.itemRender
        );

        this.buttonList.add(this.crazyae$fastPlaceQCpuBtn);
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void crazyae$drawOwnButton(final int offsetX, final int offsetY, final int mouseX, final int mouseY, final CallbackInfo ci) {
        this.crazyae$fastPlaceQCpuBtn.visible = this.container.isCraftingMode();
    }
}
