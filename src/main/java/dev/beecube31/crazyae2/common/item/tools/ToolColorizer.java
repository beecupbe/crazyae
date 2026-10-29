package dev.beecube31.crazyae2.common.item.tools;

import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import appeng.api.implementations.menuobjects.IMenuItem;
import net.minecraft.world.InteractionResult;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.AEBaseItem;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import net.minecraft.world.phys.BlockHitResult;
import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToolColorizer extends AEBaseItem implements IMenuItem {
    public ToolColorizer(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
        tooltip.accept(CrazyAETooltip.COLORIZER_DESC.getComponent().withStyle(ChatFormatting.GRAY));
    }

    @Override
    @NotNull
    public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide()) {
            ContainerOpener.open(CrazyContainers.COLORIZER.get(), player, MenuLocators.forHand(player, hand));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new ItemMenuHost<>(this, player, locator);
    }
}
