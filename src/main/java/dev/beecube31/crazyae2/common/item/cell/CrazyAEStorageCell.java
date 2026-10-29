package dev.beecube31.crazyae2.common.item.cell;

import appeng.api.ids.AEComponents;
import appeng.api.storage.cells.CellState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.PlayerMessages;
import appeng.hooks.AEToolItem;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import dev.beecube31.crazyae2.common.interfaces.cells.ICrazyCellItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CrazyAEStorageCell extends AEBaseItem implements ICrazyCellItem, AEToolItem {

    protected final ItemLike coreItem;
    protected final ItemLike housingItem;
    protected final double idleDrain;
    protected final long totalBytes;
    protected final int bytesPerType;
    protected final int totalTypes;
    private final AEKeyType keyType;

    public CrazyAEStorageCell(Properties properties, ItemLike coreItem, ItemLike housingItem, double idleDrain, long kilobytes, int bytesPerType, int totalTypes, AEKeyType keyType) {
        super(properties);
        this.idleDrain = idleDrain;
        this.totalBytes = kilobytes * 1024;
        this.coreItem = coreItem;
        this.housingItem = housingItem;
        this.bytesPerType = bytesPerType;
        this.totalTypes = totalTypes;
        this.keyType = keyType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext level, TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> lines, @NotNull TooltipFlag advancedTooltips) {
        addCellInformationToTooltip(stack, lines);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return getCellTooltipImage(stack);
    }

    @Override
    public AEKeyType getKeyType() {
        return this.keyType;
    }

    @Override
    public long getBytes(ItemStack cellItem) {
        return this.totalBytes;
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return totalTypes;
    }

    @Override
    public double getIdleDrain() {
        return idleDrain;
    }

    @Override
    public long getBytesPerType(ItemStack cellItem) {
        return bytesPerType;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, keyType == AEKeyType.items() ? 4 : 3);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(java.util.Set.of(keyType), is);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return is.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
        is.set(AEComponents.STORAGE_CELL_FUZZY_MODE, fzMode);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        this.disassembleDrive(player.getItemInHand(hand), level, player);
        return InteractionResult.SUCCESS;
    }

    private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            if (level.isClientSide()) {
                return false;
            }

            final Inventory playerInventory = player.getInventory();
            var inv = StorageCells.getCellInventory(stack, null);
            if (inv != null && playerInventory.getSelectedItem() == stack) {
                var list = inv.getAvailableStacks();
                if (list.isEmpty()) {
                    playerInventory.setItem(playerInventory.getSelectedSlot(), ItemStack.EMPTY);

                    playerInventory.placeItemBackInInventory(new ItemStack(coreItem));

                    for (var upgrade : this.getUpgrades(stack)) {
                        playerInventory.placeItemBackInInventory(upgrade);
                    }

                    playerInventory.placeItemBackInInventory(new ItemStack(housingItem));

                    return true;
                } else {
                    player.sendOverlayMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text());
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            var cellInv = StorageCells.getCellInventory(stack, null);
            var cellStatus = cellInv != null ? cellInv.getStatus() : CellState.EMPTY;
            return cellStatus.getStateColor();
        } else {
            return 0xFFFFFF;
        }
    }
}
