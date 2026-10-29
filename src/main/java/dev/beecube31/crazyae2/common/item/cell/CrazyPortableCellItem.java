package dev.beecube31.crazyae2.common.item.cell;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.StorageCells;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.CrazyDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.items.contents.CellConfig;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.util.ConfigInventory;
import dev.beecube31.crazyae2.common.enums.CrazyStorageTier;
import dev.beecube31.crazyae2.common.interfaces.cells.ICrazyCellItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CrazyPortableCellItem extends AbstractPortableCell implements ICrazyCellItem {
    private static final String AUTO_PICKUP_TAG = "autoPickup";

    private final double maxCapacity;
    private final CrazyStorageTier tier;
    private final AEKeyType keyType;
    private final int totalTypes;

    public CrazyPortableCellItem(AEKeyType keyType, int totalTypes, MenuType<?> menuType, CrazyStorageTier tier,
                                 Properties props, int defaultColor, double power) {
        super(menuType, props, defaultColor);

        this.maxCapacity = power;
        this.tier = tier;
        this.keyType = keyType;
        this.totalTypes = totalTypes;
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 80d + 80d * Upgrades.getEnergyCardMultiplier(getUpgrades(stack));
    }

    @Override
    public Identifier getRecipeId() {
        return Identifier.fromNamespaceAndPath(CrazyAE.MODID, "cells/portable/" + Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return stack.getOrDefault(AEComponents.ENERGY_CAPACITY, maxCapacity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext level,
                                TooltipDisplay tooltipDisplay, Consumer<Component> lines,
                                TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, level, tooltipDisplay, lines, advancedTooltips);
        boolean enabled = isAutoPickupEnabled(stack);
        lines.accept(CrazyAETooltip.AUTO_PICKUP.getComponentWithSpaceAtEnd()
                .append(autoPickupState(enabled)).withStyle(ChatFormatting.GRAY));
        lines.accept(CrazyAETooltip.AUTO_PICKUP_HOW_TO_ENABLE.getComponent()
                .withStyle(ChatFormatting.GRAY));
        lines.accept(CrazyAETooltip.AUTO_PICKUP_TIP.getComponent()
                .withStyle(ChatFormatting.GRAY));
        addCellInformationToTooltip(stack, lines);
    }

    public static boolean isAutoPickupEnabled(ItemStack cell) {
        return cell.getOrDefault(CrazyDataComponents.AUTO_PICKUP, false);
    }

    private static Component autoPickupState(boolean enabled) {
        return enabled
                ? CrazyAETooltip.ENABLED_LOWERCASE.getComponent().withStyle(ChatFormatting.GREEN)
                : CrazyAETooltip.DISABLED_LOWERCASE.getComponent().withStyle(ChatFormatting.RED);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && this.keyType == AEKeyType.items()) {
            if (!level.isClientSide()) {
                boolean enabled = !isAutoPickupEnabled(stack);
                stack.set(CrazyDataComponents.AUTO_PICKUP, enabled);
                player.sendOverlayMessage(CrazyAETooltip.AUTO_PICKUP
                        .getComponentWithSpaceAtEnd().append(autoPickupState(enabled)));
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }

    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        var player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }

        var picked = event.getItemEntity().getItem();
        var what = appeng.api.stacks.AEItemKey.of(picked);
        if (what == null) {
            return;
        }

        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize() && !picked.isEmpty(); i++) {
            var cell = inv.getItem(i);
            if (!(cell.getItem() instanceof CrazyPortableCellItem item) || item.keyType != AEKeyType.items()
                    || !isAutoPickupEnabled(cell)) {
                continue;
            }
            var storage = StorageCells.getCellInventory(cell, null);
            if (storage == null) {
                continue;
            }

            long inserted = storage.insert(what, picked.getCount(), Actionable.MODULATE,
                    IActionSource.ofPlayer(player));
            if (inserted > 0) {
                picked.shrink((int) inserted);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.08F,
                        (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F);
            }
        }
        if (picked.isEmpty()) {
            event.setCanPickup(TriState.TRUE);
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return getCellTooltipImage(stack);
    }

    @Override
    public long getBytes(ItemStack cellItem) {
        return this.tier.bytes() / 2;
    }

    @Override
    public long getBytesPerType(ItemStack cellItem) {
        return this.tier.bytes() / 128;
    }

    @Override
    public int getTotalTypes(ItemStack cellItem) {
        return this.totalTypes;
    }

    @Override
    public double getIdleDrain() {
        return 0.5;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, this.keyType == AEKeyType.items() ? 4 : 3, super::onUpgradesChanged);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(Set.of(keyType), is);
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
    public AEKeyType getKeyType() {
        return keyType;
    }

    public CrazyStorageTier getTier() {
        return tier;
    }
}
