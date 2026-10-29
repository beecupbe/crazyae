package dev.beecube31.crazyae2.common.item;

import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.AEBlocks;
import appeng.util.InteractionUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class CrazyCraftingBlockItem extends AEBaseBlockItem {
    protected final Supplier<ItemLike> disassemblyExtra;

    public CrazyCraftingBlockItem(Block id, Item.Properties props, Supplier<ItemLike> disassemblyExtra) {
        super(id, props);
        this.disassemblyExtra = disassemblyExtra;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            int itemCount = player.getItemInHand(hand).getCount();
            player.setItemInHand(hand, ItemStack.EMPTY);

            player.getInventory().placeItemBackInInventory(AEBlocks.CRAFTING_UNIT.stack(itemCount));
            player.getInventory().placeItemBackInInventory(new ItemStack(disassemblyExtra.get(), itemCount));

            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        }
        return super.use(level, player, hand);
    }
}
