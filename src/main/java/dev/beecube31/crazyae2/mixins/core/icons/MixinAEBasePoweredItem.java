package dev.beecube31.crazyae2.mixins.core.icons;

import appeng.api.config.PowerUnits;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.localization.GuiText;
import appeng.items.AEBaseItem;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.util.Platform;
import dev.beecube31.crazyae2.client.gui.sprites.Sprite;
import dev.beecube31.crazyae2.common.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.text.MessageFormat;
import java.util.List;

@Mixin(value = AEBasePoweredItem.class, remap = false)
public abstract class MixinAEBasePoweredItem extends AEBaseItem implements IAEItemPowerStorage {
    @Shadow @Final private static String CURRENT_POWER_NBT_KEY;

    /**
     * @author Beecube31
     * @reason Add informational icons
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        final NBTTagCompound tag = stack.getTagCompound();
        double internalCurrentPower = 0;
        final double internalMaxPower = this.getAEMaxPower(stack);

        if (tag != null) {
            internalCurrentPower = tag.getDouble(CURRENT_POWER_NBT_KEY);
        }

        final double percent = internalCurrentPower / internalMaxPower;

        lines.add(Utils.writeSpriteFlag(Sprite.ENERGY) + GuiText.StoredEnergy.getLocal() + ':' + MessageFormat.format(" {0,number,#} ", internalCurrentPower) + Platform
                .gui_localize(PowerUnits.AE.unlocalizedName) + " - " + MessageFormat.format(" {0,number,#.##%} ", percent));
    }
}
