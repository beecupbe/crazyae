package dev.beecube31.crazyae2.mixins.core.crafting.gui;

import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.container.implementations.ContainerCraftingStatus;
import appeng.container.implementations.CraftingCPUStatus;
import com.llamalad7.mixinextras.sugar.Local;
import dev.beecube31.crazyae2.client.gui.components.ComponentHue;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinCraftingCPUStatus;
import dev.beecube31.crazyae2.common.util.TimeUtils;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.ZoneId;

@Mixin(value = GuiCraftingStatus.class, remap = false)
public abstract class MixinGuiCraftingStatus extends GuiCraftingCPU {
    @Shadow @Final private ContainerCraftingStatus status;

    @Shadow protected abstract CraftingCPUStatus hitCpu(int x, int y);

    public MixinGuiCraftingStatus(InventoryPlayer inventoryPlayer, Object te) {
        super(inventoryPlayer, te);
    }

    @ModifyConstant(method = "drawFG(IIII)V", constant = @Constant(intValue = 11, ordinal = 0), require = 1)
    private int crazyae$modifySubstringCpuName(int originalValue) {
        return 15;
    }

    @ModifyConstant(method = "drawFG(IIII)V", constant = @Constant(intValue = 12, ordinal = 0), require = 1)
    private int crazyae$modifySubstringCheckCpuName(int originalValue) {
        return 16;
    }

    @ModifyConstant(method = "drawFG(IIII)V", constant = @Constant(intValue = 5), require = 1)
    private int crazyae$modifySubstringCheckCraftAmt(int originalValue) {
        return 8;
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void crazyae$addProgressBar(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        CraftingCPUStatus selectedDisplayCpu = this.crazyae$getSelectedCpuStatus();

        if (selectedDisplayCpu != null) {
            double maxProgress = selectedDisplayCpu.getTotalItems();
            double remainingProgress = selectedDisplayCpu.getRemainingItems();

            if (maxProgress > 0) {
                double currentProgress = maxProgress - remainingProgress;


                int percentage = (int) Math.max(0, Math.min(100, (currentProgress / maxProgress * 100.0)));

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
                    Minecraft.getMinecraft().fontRenderer.drawString(
                            percentage + "%",
                            196,
                            7,
                            ComponentHue.DEFAULT_TEXT_COLOR,
                            false
                    );
                } else {
                    Minecraft.getMinecraft().fontRenderer.drawString(
                            percentage + "%",
                            196,
                            7,
                            ComponentHue.DEFAULT_TEXT_COLOR,
                            false
                    );

                    this.drawTooltip(
                            -87,
                            7,
                            Utils.getFullDecimalOf(currentProgress)
                                    + "/"
                                    + Utils.getFullDecimalOf(maxProgress)
                                    + " - "
                                    + percentage
                                    + "%"
                    );
                }
            }
        }
    }

    @Unique
    private CraftingCPUStatus crazyae$getSelectedCpuStatus() {
        return this.status.getCPUs().stream()
                .filter(s -> s.getSerial() == this.status.selectedCpuSerial)
                .findFirst()
                .orElse(null);
    }

    @Inject(method = "drawFG", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;length()I", shift = At.Shift.BEFORE), remap = false, cancellable = true)
    private void crazyae$modifyTooltip(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci, @Local StringBuilder tooltip) {
        CraftingCPUStatus selectedDisplayCpu = this.hitCpu(mouseX, mouseY);

        if (selectedDisplayCpu != null && selectedDisplayCpu.getTotalItems() > 0) {
            if (!((IMixinCraftingCPUStatus) selectedDisplayCpu).crazyae$jobInitiator().isEmpty()) {
                tooltip.append(CrazyAEGuiText.INITIATOR.getLocal());
                tooltip.append(": ");
                tooltip.append(((IMixinCraftingCPUStatus) selectedDisplayCpu).crazyae$jobInitiator());
                tooltip.append("\n");
            }

            if (((IMixinCraftingCPUStatus) selectedDisplayCpu).crazyae$whenJobStarted() > 0) {
                tooltip.append(CrazyAEGuiText.CRAFT_STARTED_AT.getLocal());
                tooltip.append(" ");
                ZoneId playerTimeZone = ZoneId.systemDefault();
                tooltip.append(TimeUtils.formatTimeForZone(((IMixinCraftingCPUStatus) selectedDisplayCpu).crazyae$whenJobStarted(), playerTimeZone));
                tooltip.append("\n");
            }

        }

        if (tooltip.length() > 0) {
            this.drawTooltip(mouseX - offsetX, mouseY - offsetY, tooltip.toString());
        }

        ci.cancel();
    }
}
