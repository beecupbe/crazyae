package dev.beecube31.crazyae2.common.items.patterns;

import appeng.items.AEBaseItem;
import appeng.util.item.ItemStackHashStrategy;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class ItemCustomEncodedPatternBase extends AEBaseItem {

    public ItemCustomEncodedPatternBase() {
        this.setMaxStackSize(64);
    }

    @Override
    @NotNull
    public ActionResult<ItemStack> onItemRightClick(final World w, final EntityPlayer player, final EnumHand hand) {
        this.clearPattern(player.getHeldItem(hand), player);

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    @NotNull
    public EnumActionResult onItemUseFirst(final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final EnumHand hand) {
        return this.clearPattern(player.getHeldItem(hand), player) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public abstract void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips);

    public abstract ItemStack getOutput(final ItemStack item);

    protected abstract boolean clearPattern(final ItemStack stack, final EntityPlayer player);
}
