package dev.beecube31.crazyae2.core.registries;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CrazyRegistries {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CrazyAE.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CrazyAE.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrazyAE.MODID);

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, CrazyAE.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrazyAE.MODID);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        MENU_TYPES.register(eventBus);
        CREATIVE_TABS.register(eventBus);
        CrazyDataComponents.register(eventBus);
    }
}
