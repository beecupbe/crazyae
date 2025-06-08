package dev.beecube31.crazyae2.mixins.features.patternterm.fastplace;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.interfaces.IJEIGhostIngredients;
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

    @Unique private GuiTabButton crazyae$fastPlaceBtn;

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
            if (this.crazyae$fastPlaceBtn == btn && this.container.isCraftingMode()) {
                TileEntity te = this.container.getPart().getHost().getTile();
                NetworkHandler.instance().sendToServer(new PacketToggleGuiObject(
                        "CRAZYAE.GUI.patternTerm.fastPlace",
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
        this.crazyae$fastPlaceBtn = new GuiTabButton(
                this.guiLeft + 173,
                this.guiTop + this.ySize - 154,
                CrazyAE.definitions().blocks().improvedMolecularAssembler().maybeStack(1).orElse(ItemStack.EMPTY),
                String.format(
                        CrazyAEGuiText.BUTTON_PATTERN_FAST_PLACE.getLocal(),
                        CrazyAEGuiText.IMPROVED_MAC_GUI.getLocal()
                ),
                this.itemRender
        );

        this.crazyae$fastPlaceQCpuBtn = new GuiTabButton(
                this.guiLeft + 173,
                this.guiTop + this.ySize - 131,
                CrazyAE.definitions().blocks().quantumCPU().maybeStack(1).orElse(ItemStack.EMPTY),
                String.format(
                        CrazyAEGuiText.BUTTON_PATTERN_FAST_PLACE.getLocal(),
                        CrazyAEGuiText.IMPROVED_MAC_GUI.getLocal()
                ),
                this.itemRender
        );

        this.buttonList.add(this.crazyae$fastPlaceBtn);
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void crazyae$drawOwnButton(final int offsetX, final int offsetY, final int mouseX, final int mouseY, final CallbackInfo ci) {
        this.crazyae$fastPlaceBtn.visible = this.container.isCraftingMode();
    }
}
