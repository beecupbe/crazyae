package dev.beecube31.crazyae2.common.items;

import appeng.core.localization.GuiText;
import appeng.helpers.ItemStackHelper;
import appeng.items.AEBaseItem;
import dev.beecube31.crazyae2.common.sync.CrazyAETooltip;
import dev.beecube31.crazyae2.common.tile.botania.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import org.lwjgl.input.Keyboard;

import java.util.List;

public class AdvancedMemoryCard extends AEBaseItem {
    public AdvancedMemoryCard() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey("saveType")) {
            switch (nbt.getInteger("saveType")) {
                case 1 -> lines.add(CrazyAETooltip.MEMORYCARD_MANAPOOL_SAVED.getLocal());
                case 2 -> lines.add(CrazyAETooltip.MEMORYCARD_PETAL_SAVED.getLocal());
                case 3 -> lines.add(CrazyAETooltip.MEMORYCARD_PUREDAISY_SAVED.getLocal());
                case 4 -> lines.add(CrazyAETooltip.MEMORYCARD_RUNEALTAR_SAVED.getLocal());
                case 5 -> lines.add(CrazyAETooltip.MEMORYCARD_ELVENTRADE_SAVED.getLocal());
            }
            if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                lines.add(CrazyAETooltip.SHIFT_FOR_DETAILS.getLocal());
                return;
            }

            NBTTagList tagList = nbt.getTagList("saveItems", 10);

            lines.add(CrazyAETooltip.MEMORYCARD_ITEMS_SAVED.getLocal());
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                lines.add(ItemStackHelper.stackFromNBT(itemTags).getDisplayName());
            }
            return;
        }

        lines.add(GuiText.Blank.getLocal());
    }

    @Override
    @NotNull
    public EnumActionResult onItemUse(@NotNull EntityPlayer player, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        final TileEntity te = worldIn.getTileEntity(pos);
        ItemStack card = player.getHeldItem(hand);
        NBTTagCompound nbt = new NBTTagCompound();

//        if (te instanceof final TileMechanicalManapool pool) {
//            nbt.setInteger("saveType", 1);
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (te instanceof final TileMechanicalPetal petal) {
//            nbt.setInteger("saveType", 2);
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (te instanceof final TileMechanicalPuredaisy puredaisy) {
//            nbt.setInteger("saveType", 3);
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (te instanceof final TileMechanicalRunealtar runealtar) {
//            nbt.setInteger("saveType", 4);
//            return EnumActionResult.SUCCESS;
//        }

        if (te instanceof final TileMechanicalElventrade elventrade) {
            nbt.setInteger("saveType", 5);
            return EnumActionResult.SUCCESS;
        }

        card.setTagCompound(nbt);
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
