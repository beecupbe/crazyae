package dev.beecube31.crazyae2.core.registries.server;

import appeng.api.AECapabilities;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.upgrades.Upgrades;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import appeng.menu.locator.MenuLocators;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.definitions.CrazyParts;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.item.cell.CrazyPortableCellItem;
import dev.beecube31.crazyae2.common.parts.PartPatternsInterface;
import dev.beecube31.crazyae2.common.parts.PartPerfectInterface;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import dev.beecube31.crazyae2.common.util.locator.SimpleLocator;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class RegisterFactoryServer {

    public static final RegisterFactoryServer INSTANCE = new RegisterFactoryServer();

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerUpgrades();
            MenuLocators.register(SimpleLocator.class, SimpleLocator::writeToPacket, SimpleLocator::readFromPacket);
        });
    }

    private static void registerUpgrades() {
        var mac = CrazyAEGuiText.IMPROVED_MAC_GUI.getUnlocalizedString().toLowerCase();
        var ioBuses = GuiText.IOBuses.getTranslationKey();
        var ifaces = GuiText.Interfaces.getTranslationKey();

        Upgrades.add(CrazyItems.STACKS_CARD.get(), CrazyBlocks.IMPROVED_MAC.get(), 5, mac);
        Upgrades.add(CrazyItems.ADVANCED_SPEED_CARD.get(), CrazyBlocks.IMPROVED_MAC.get(), 5, mac);
        Upgrades.add(CrazyItems.IMPROVED_SPEED_CARD.get(), CrazyBlocks.IMPROVED_MAC.get(), 5, mac);

        ItemLike ioPort = CrazyBlocks.IMPROVED_IO_PORT.get();
        Upgrades.add(AEItems.REDSTONE_CARD, ioPort, 1, "group.io_devices.name");
        Upgrades.add(CrazyItems.STACKS_CARD.get(), ioPort, 3, "group.io_devices.name");
        Upgrades.add(CrazyItems.IMPROVED_SPEED_CARD.get(), ioPort, 3, "group.io_devices.name");
        Upgrades.add(CrazyItems.ADVANCED_SPEED_CARD.get(), ioPort, 1, "group.io_devices.name");

        ItemLike charger = CrazyBlocks.IMPROVED_CRYSTAL_CHARGER.get();
        Upgrades.add(CrazyItems.STACKS_CARD.get(), charger, 5);
        Upgrades.add(CrazyItems.IMPROVED_SPEED_CARD.get(), charger, 5);
        Upgrades.add(CrazyItems.ADVANCED_SPEED_CARD.get(), charger, 1);

        for (ItemLike iface : new ItemLike[]{CrazyBlocks.PERFECT_INTERFACE.get(), CrazyParts.PERFECT_INTERFACE.get()}) {
            Upgrades.add(AEItems.FUZZY_CARD, iface, 1, ifaces);
            Upgrades.add(AEItems.CRAFTING_CARD, iface, 1, ifaces);
        }

        for (ItemLike bus : new ItemLike[]{CrazyParts.IMPROVED_IMPORT_BUS.get(), CrazyParts.IMPROVED_EXPORT_BUS.get()}) {
            Upgrades.add(AEItems.FUZZY_CARD, bus, 1, ioBuses);
            Upgrades.add(AEItems.REDSTONE_CARD, bus, 1, ioBuses);
            Upgrades.add(CrazyItems.STACKS_CARD.get(), bus, 4, ioBuses);
            Upgrades.add(CrazyItems.ADVANCED_SPEED_CARD.get(), bus, 1, ioBuses);
            Upgrades.add(CrazyItems.IMPROVED_SPEED_CARD.get(), bus, 4, ioBuses);
        }
        Upgrades.add(AEItems.CRAFTING_CARD, CrazyParts.IMPROVED_EXPORT_BUS.get(), 1, ioBuses);
        Upgrades.add(AEItems.INVERTER_CARD, CrazyParts.IMPROVED_IMPORT_BUS.get(), 1, ioBuses);

        for (ItemLike trashcan : new ItemLike[]{CrazyBlocks.TRASHCAN_ITEMS.get(), CrazyBlocks.TRASHCAN_FLUIDS.get()}) {
            Upgrades.add(CrazyItems.STACKS_CARD.get(), trashcan, 4);
        }
    }

    public static void registerTab() {
        CrazyRegistries.CREATIVE_TABS.register("tab_main", () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(CrazyItems.ITEM_CELL_1MB.get()))
                .title(Component.translatable("itemGroup.crazyae"))
                .displayItems((params, output) -> {
                    for (var entry : CrazyRegistries.ITEMS.getEntries()) {
                        var item = entry.get();
                        output.accept(item);
                        if ((item instanceof EnergyCellBlockItem || item instanceof CrazyPortableCellItem)) {
                            IAEItemPowerStorage powered = (IAEItemPowerStorage) item;
                            var charged = new ItemStack(item);
                            double capacity = powered.getAEMaxPower(charged);
                            if (capacity > 0) {
                                charged.set(AEComponents.STORED_ENERGY, capacity);
                                output.accept(charged);
                            }
                        }
                    }
                })
                .build());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        for (var entry : CrazyTileEntities.getTypeClasses().entrySet()) {
            BlockEntityType type = entry.getKey().get();
            Class<?> cls = entry.getValue();

            if (IInWorldGridNodeHost.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, type,
                        (object, context) -> (IInWorldGridNodeHost) object);
            }
            if (AEBasePoweredBlockEntity.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
                        (object, context) -> ((AEBasePoweredBlockEntity) object).getEnergyStorage((Direction) context));
            }
            if (ICraftingMachine.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(AECapabilities.CRAFTING_MACHINE, type,
                        (object, context) -> (ICraftingMachine) object);
            }
            if (InterfaceBlockEntity.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(AECapabilities.GENERIC_INTERNAL_INV, type,
                        (object, context) -> ((InterfaceBlockEntity) object).getInterfaceLogic().getStorage());
                event.registerBlockEntity(AECapabilities.ME_STORAGE, type,
                        (object, context) -> ((InterfaceBlockEntity) object).getInterfaceLogic().getInventory());
            }
            if (PatternProviderBlockEntity.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(AECapabilities.GENERIC_INTERNAL_INV, type,
                        (object, context) -> ((PatternProviderBlockEntity) object).getLogic().getReturnInv());
            }
            if (TileImprovedCondenser.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(Capabilities.Item.BLOCK, type,
                        (object, context) -> ((TileImprovedCondenser) object).getExternalItemHandler());
                event.registerBlockEntity(Capabilities.Fluid.BLOCK, type,
                        (object, context) -> ((TileImprovedCondenser) object).getFluidHandler());
                event.registerBlockEntity(AECapabilities.ME_STORAGE, type,
                        (object, context) -> ((TileImprovedCondenser) object).getMEStorage());
            } else if (AEBaseInvBlockEntity.class.isAssignableFrom(cls)) {
                event.registerBlockEntity(Capabilities.Item.BLOCK, type,
                        (object, context) -> ((AEBaseInvBlockEntity) object).getExposedItemHandler((Direction) context));
            }
        }

        for (var cell : CrazyItems.getPortableCells()) {
            event.registerItem(Capabilities.Energy.ITEM,
                    (stack, context) -> new PoweredItemCapabilities(context, cell.get(), (IAEItemPowerStorage) cell.get()),
                    cell.get());
        }
    }

    public static void registerPartCapabilities(final RegisterPartCapabilitiesEvent event) {
        event.register(AECapabilities.GENERIC_INTERNAL_INV,
                (part, context) -> part.getInterfaceLogic().getStorage(), PartPerfectInterface.class);
        event.register(AECapabilities.ME_STORAGE,
                (part, context) -> part.getInterfaceLogic().getInventory(), PartPerfectInterface.class);
        event.register(AECapabilities.GENERIC_INTERNAL_INV,
                (part, context) -> part.getLogic().getReturnInv(), PartPatternsInterface.class);
    }

    public void onInit() {}
}
