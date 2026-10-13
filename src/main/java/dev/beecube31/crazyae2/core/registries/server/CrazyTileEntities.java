package dev.beecube31.crazyae2.core.registries.server;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.misc.CrankBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.enums.TrashcanType;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.common.tile.grindstone.ImprovedCrankBlockEntity;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import dev.beecube31.crazyae2.common.tile.networking.*;
import dev.beecube31.crazyae2.common.tile.solars.TileSolarPanel;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcan;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class CrazyTileEntities {
    private CrazyTileEntities() {
    }

    @FunctionalInterface
    public interface Factory<T extends net.minecraft.world.level.block.entity.BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <T extends AEBaseBlockEntity> RegistryObject<BlockEntityType<T>> create(
            String id, Class<T> beClass, Factory<? extends T> factory, Supplier<? extends AEBaseEntityBlock<?>>... blocks) {
        return CrazyRegistries.BLOCK_ENTITIES.register(id, () -> {
            var blockList = Arrays.stream(blocks).map(Supplier::get).toList();
            var holder = new AtomicReference<BlockEntityType<T>>();
            BlockEntityType<T> type = BlockEntityType.Builder
                    .of((pos, state) -> ((Factory<T>) factory).create(holder.get(), pos, state), blockList.toArray(new Block[0]))
                    .build(null);
            holder.set(type);

            BlockEntityTicker<T> serverTicker = null;
            if (ServerTickingBlockEntity.class.isAssignableFrom(beClass)) {
                serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
            }
            BlockEntityTicker<T> clientTicker = null;
            if (ClientTickingBlockEntity.class.isAssignableFrom(beClass)) {
                clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
            }
            for (var block : blockList) {
                ((AEBaseEntityBlock<T>) block).setBlockEntity(beClass, type, clientTicker, serverTicker);
            }

            AEBaseBlockEntity.registerBlockEntityItem(type, blocks[0].get().asItem());
            return type;
        });
    }

    public static final RegistryObject<BlockEntityType<TileImprovedMAC>> IMPROVED_MAC =
            create("improved_mac", TileImprovedMAC.class, TileImprovedMAC::new, CrazyBlocks.IMPROVED_MAC);
    public static final RegistryObject<BlockEntityType<TileImprovedDrive>> IMPROVED_DRIVE =
            create("improved_drive", TileImprovedDrive.class, TileImprovedDrive::new, CrazyBlocks.IMPROVED_DRIVE);
    public static final RegistryObject<BlockEntityType<TileBigCrystalCharger>> IMPROVED_CRYSTAL_CHARGER =
            create("improved_crystal_charger", TileBigCrystalCharger.class, TileBigCrystalCharger::new, CrazyBlocks.IMPROVED_CRYSTAL_CHARGER);
    public static final RegistryObject<BlockEntityType<TileImprovedIOPort>> IMPROVED_IO_PORT =
            create("improved_io_port", TileImprovedIOPort.class, TileImprovedIOPort::new, CrazyBlocks.IMPROVED_IO_PORT);

    public static final RegistryObject<BlockEntityType<InterfaceBlockEntity>> PERFECT_INTERFACE =
            create("perfect_interface", InterfaceBlockEntity.class, TilePerfectInterface::new, CrazyBlocks.PERFECT_INTERFACE);
    public static final RegistryObject<BlockEntityType<PatternProviderBlockEntity>> PATTERNS_INTERFACE =
            create("patterns_interface", PatternProviderBlockEntity.class, TilePatternsInterface::new, CrazyBlocks.PATTERNS_INTERFACE);

    public static final RegistryObject<BlockEntityType<TileTrashcan>> TRASHCAN_ITEMS =
            create("trashcan_items", TileTrashcan.class, (t, p, s) -> new TileTrashcan(t, p, s, TrashcanType.ITEMS), CrazyBlocks.TRASHCAN_ITEMS);
    public static final RegistryObject<BlockEntityType<TileTrashcan>> TRASHCAN_FLUIDS =
            create("trashcan_fluids", TileTrashcan.class, (t, p, s) -> new TileTrashcan(t, p, s, TrashcanType.FLUIDS), CrazyBlocks.TRASHCAN_FLUIDS);

    public static final RegistryObject<BlockEntityType<TileImprovedCondenser>> IMPROVED_CONDENSER =
            create("improved_condenser", TileImprovedCondenser.class, TileImprovedCondenser::new, CrazyBlocks.IMPROVED_CONDENSER);

    public static final RegistryObject<BlockEntityType<CrankBlockEntity>> IMPROVED_CRANK =
            create("improved_crank", CrankBlockEntity.class, ImprovedCrankBlockEntity::new, CrazyBlocks.IMPROVED_CRANK);

    public static final RegistryObject<BlockEntityType<EnergyCellBlockEntity>> ENERGY_CELLS =
            create("energy_cell", EnergyCellBlockEntity.class, EnergyCellBlockEntity::new,
                    CrazyBlocks.IMPROVED_ENERGY_CELL, CrazyBlocks.ADVANCED_ENERGY_CELL,
                    CrazyBlocks.PERFECT_ENERGY_CELL, CrazyBlocks.QUANTUM_ENERGY_CELL);

    public static final RegistryObject<BlockEntityType<TileSolarPanel>> SOLAR_PANELS =
            create("solar_panel", TileSolarPanel.class, TileSolarPanel::new,
                    CrazyBlocks.BASIC_SOLAR_PANEL, CrazyBlocks.IMPROVED_SOLAR_PANEL, CrazyBlocks.ADVANCED_SOLAR_PANEL,
                    CrazyBlocks.PERFECT_SOLAR_PANEL, CrazyBlocks.QUANTUM_SOLAR_PANEL);

    @SuppressWarnings("unchecked")
    public static final RegistryObject<BlockEntityType<CraftingBlockEntity>> CRAFTING_UNITS =
            create("crafting_unit", CraftingBlockEntity.class, CraftingBlockEntity::new,
                    Arrays.stream(CrazyAECraftingUnitType.values())
                            .filter(t -> t != CrazyAECraftingUnitType.COMBINER)
                            .map(t -> (Supplier<AEBaseEntityBlock<?>>) () -> CrazyBlocks.getCraftingBlock(t))
                            .toArray(Supplier[]::new));

    public static final RegistryObject<BlockEntityType<CraftingBlockEntity>> CRAFTING_UNITS_COMBINER =
            create("crafting_units_combiner", CraftingBlockEntity.class, TileCraftingUnitsCombiner::new, CrazyBlocks.CRAFTING_UNITS_COMBINER);

    public static void init() {}
}
