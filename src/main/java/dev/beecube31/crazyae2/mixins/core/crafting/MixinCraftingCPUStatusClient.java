package dev.beecube31.crazyae2.mixins.core.crafting;

import appeng.container.implementations.CraftingCPUStatus;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = CraftingCPUStatus.class, remap = false)
public abstract class MixinCraftingCPUStatusClient {
    @Shadow public abstract long getStorage();

    @Unique
    private String crazyae$makeSubstringed(String s) {
        return s.length() > 10 ? s.substring(0, 10) + "..." : s;
    }

    /**
     * @author Beecube31
     * @reason Better viewing
     * @since v0.6
     */
    @Overwrite
    public String formatStorage() {
        long val = getStorage();
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) return this.crazyae$makeSubstringed(Long.toString(val));

        if (val > 4_000_000_000_000L) {
            return String.format("%dT", val / 1024 / 1024 / 1024 / 1024);
        } else if (val > 4_000_000_000L) {
            return String.format("%dG", val / 1024 / 1024 / 1024);
        } else if (val > 4_000_000L) {
            return String.format("%dM", val / 1024 / 1024);
        } else if (val > 4_000L) {
            return String.format("%dk", val / 1024);
        } else {
            return this.crazyae$makeSubstringed(Long.toString(val));
        }
    }
}
