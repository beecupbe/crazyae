package dev.beecube31.crazyae2.mixins.client;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import dev.beecube31.crazyae2.client.gui.ICrazyGui;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IconButton.class)
public abstract class MixinIconButton {
    @Redirect(
            method = "renderWidget",
            at = @At(value = "INVOKE", target = "Lappeng/client/gui/Icon;getBlitter()Lappeng/client/gui/style/Blitter;", remap = false)
    )
    private Blitter crazyae$styledBackground(Icon icon) {
        if (icon == Icon.TOOLBAR_BUTTON_BACKGROUND && Minecraft.getInstance().screen instanceof ICrazyGui screen) {
            var hue = screen.getGuiHue();
            return StateSprite.IMAGE_BUTTON.getBlitter().copy()
                    .color(hue.getRed(), hue.getGreen(), hue.getBlue(), 1F);
        }
        return icon.getBlitter();
    }
}
