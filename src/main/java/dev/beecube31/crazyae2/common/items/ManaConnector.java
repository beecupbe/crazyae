package dev.beecube31.crazyae2.common.items;

import appeng.items.AEBaseItem;
import appeng.tile.networking.TileCableBus;
import dev.beecube31.crazyae2.common.interfaces.mana.IManaLinkableDevice;
import dev.beecube31.crazyae2.common.sync.CrazyAETooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.IManaPool;

import java.util.List;

public class ManaConnector extends AEBaseItem {
    public ManaConnector() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        if (stack.getTagCompound() != null
            && stack.getTagCompound().hasKey("connectFrom")) {
            int[] pos = stack.getTagCompound().getIntArray("connectFrom");
            lines.add(String.format(CrazyAETooltip.LINKED_WITH_MANA_POOL_AT_POS.getLocalWithSpaceAtEnd(), pos[0], pos[1], pos[2]));
            lines.add(CrazyAETooltip.MANA_CONNECTOR_LETS_CONNECT_TO_BUS.getLocal());
            return;
        }

        lines.add(CrazyAETooltip.MANA_CONNECTOR_DESC.getLocal());
    }

    @Override
    @NotNull
    public EnumActionResult onItemUse(@NotNull EntityPlayer player, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        final TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof final TileCableBus cb) {
            NBTTagCompound nbt = player.getHeldItem(hand).getTagCompound();
            if (nbt != null && nbt.hasKey("connectFrom")) {
                for (EnumFacing i : EnumFacing.VALUES) {
                    if (cb.getCableBus().getPart(i) instanceof final IManaLinkableDevice p) {
                        int[] poz = nbt.getIntArray("connectFrom");
                        p.link(poz[0], poz[1], poz[2]);
                        player.getHeldItem(hand).setTagCompound(null);
                        player.sendStatusMessage(new TextComponentString(CrazyAETooltip.MANA_CONNECTOR_BLOCK_LINKED.getLocalWithSpaceAtEnd()), true);
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        } else if (te instanceof IManaPool) {
            NBTTagCompound nbt = new NBTTagCompound();
            int[] poz = new int[3];
            poz[0] = pos.getX();
            poz[1] = pos.getY();
            poz[2] = pos.getZ();

            nbt.setIntArray("connectFrom", poz);
            player.getHeldItem(hand).setTagCompound(nbt);
            player.sendStatusMessage(new TextComponentString(CrazyAETooltip.MANA_CONNECTOR_BLOCK_SAVED.getLocalWithSpaceAtEnd()), true);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    @NotNull
    public ActionResult<ItemStack> onItemRightClick(final @NotNull World w, final EntityPlayer player, final @NotNull EnumHand hand) {
        if (player.isSneaking()) {
            player.getHeldItem(hand).setTagCompound(null);
            player.sendStatusMessage(new TextComponentString(CrazyAETooltip.MANA_CONNECTOR_CLEAR.getLocal()), true);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
