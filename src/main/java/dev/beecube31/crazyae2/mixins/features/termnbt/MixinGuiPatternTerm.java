package dev.beecube31.crazyae2.mixins.features.termnbt;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.interfaces.IJEIGhostIngredients;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.StaticImageButton;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
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

    @Unique private StaticImageButton crazyae$clearItemNBT;

    public MixinGuiPatternTerm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(
            method = "actionPerformed",
            at = @At("RETURN")
    )
    private void crazyae$patchActionPerformed(final GuiButton btn, CallbackInfo ci) {
        try {
            if (this.crazyae$clearItemNBT == btn && this.container.isCraftingMode()) {
                NetworkHandler.instance().sendToServer(new PacketToggleGuiObject(
                        "CRAZYAE.GUI.patternTerm.clearnbt",
                        ""
                ));
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
        this.crazyae$clearItemNBT = new StaticImageButton(
                this.guiLeft + 94,
                this.guiTop + this.ySize - 163,
                StateSprite.NBT,
                String.format(
                        CrazyAEGuiText.CLEAR_INV_ITEMS_NBT.getLocal(),
                        CrazyAEGuiText.CLEAR_INV_ITEMS_NBT_LINE2.getLocal()
                ),
                null,
                4
        ).setHalfSize(true).setDisableHue(true);

        this.buttonList.add(this.crazyae$clearItemNBT);
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void crazyae$drawOwnButton(final int offsetX, final int offsetY, final int mouseX, final int mouseY, final CallbackInfo ci) {
        this.crazyae$clearItemNBT.visible = this.container.isCraftingMode();
    }
}
