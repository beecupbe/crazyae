package dev.beecube31.crazyae2.common.items;

import appeng.api.util.AEPartLocation;
import appeng.items.AEBaseItem;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEGuiItem;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PatternsUSBStick extends AEBaseItem implements ICrazyAEGuiItem<PatternsUSBStickObj> {
    public PatternsUSBStick() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        lines.add(CrazyAETooltip.USB_PATTERNS_STICK_DESC.getLocal());
    }

    @Override
    @NotNull
    public ActionResult<ItemStack> onItemRightClick(final World w, final EntityPlayer player, final EnumHand hand) {
        CrazyAEGuiHandler.openGUI(player, null, AEPartLocation.INTERNAL, CrazyAEGuiBridge.GUI_USB_PATTERNS_STICK);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public PatternsUSBStickObj getGuiObject(ItemStack is, World w, BlockPos bp, AEPartLocation side) {
        return ICrazyAEGuiItem.super.getGuiObject(is, w, bp, side);
    }

    @Override
    public PatternsUSBStickObj getGuiObject(ItemStack is, World w, BlockPos pos) {
        return new PatternsUSBStickObj(is);
    }
}

