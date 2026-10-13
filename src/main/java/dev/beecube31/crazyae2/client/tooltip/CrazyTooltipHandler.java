package dev.beecube31.crazyae2.client.tooltip;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.util.CrazyTooltips;
import dev.beecube31.crazyae2.common.util.CrazyTooltips.MachineAttributes;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


public final class CrazyTooltipHandler {
    private static Map<Block, MachineAttributes> attributes;

    private static Map<Block, MachineAttributes> attributes() {
        if (attributes == null) {
            var map = new IdentityHashMap<Block, MachineAttributes>();
            map.put(CrazyBlocks.IMPROVED_MAC.get(), MachineAttributes.perTick(32, false));
            map.put(CrazyBlocks.IMPROVED_CRYSTAL_CHARGER.get(), MachineAttributes.perTick(16, true));
            map.put(CrazyBlocks.CRAFTING_UNITS_COMBINER.get(), MachineAttributes.perTick(1024, true));
            map.put(CrazyBlocks.IMPROVED_IO_PORT.get(), MachineAttributes.perTick(64, true));
            map.put(CrazyBlocks.PERFECT_INTERFACE.get(), MachineAttributes.channel());
            map.put(CrazyBlocks.PATTERNS_INTERFACE.get(), MachineAttributes.channel());
            map.put(CrazyBlocks.IMPROVED_DRIVE.get(), MachineAttributes.channel());
            map.put(CrazyBlocks.TRASHCAN_ITEMS.get(), MachineAttributes.perTick(4, true));
            map.put(CrazyBlocks.TRASHCAN_FLUIDS.get(), MachineAttributes.perTick(4, true));
            attributes = map;
        }

        return attributes;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof BlockItem bi)) {
            return;
        }
        var block = bi.getBlock();
        var id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null || !CrazyAE.MODID.equals(id.getNamespace())) {
            return;
        }

        MachineAttributes attrib = attributes().get(block);
        if (attrib == null && block instanceof AbstractCraftingUnitBlock<?>) {
            attrib = MachineAttributes.channel();
        }

        if (attrib == null) {
            return;
        }

        List<Component> extra = new ArrayList<>();
        CrazyTooltips.addMachineAttributes(extra, attrib);
        var lines = event.getToolTip();
        lines.addAll(Math.min(1, lines.size()), extra);
    }
}
