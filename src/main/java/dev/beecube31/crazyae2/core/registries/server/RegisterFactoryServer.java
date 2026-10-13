package dev.beecube31.crazyae2.core.registries.server;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.menu.locator.MenuLocators;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.definitions.CrazyParts;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.util.locator.SimpleLocator;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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
            Upgrades.add(AEItems.FUZZY_CARD, iface, 1, "group.interface.name");
            Upgrades.add(AEItems.CRAFTING_CARD, iface, 1, "group.interface.name");
        }

        for (ItemLike bus : new ItemLike[]{CrazyParts.IMPROVED_IMPORT_BUS.get(), CrazyParts.IMPROVED_EXPORT_BUS.get()}) {
            Upgrades.add(AEItems.FUZZY_CARD, bus, 1, ioBuses);
            Upgrades.add(AEItems.REDSTONE_CARD, bus, 1, ioBuses);
            Upgrades.add(AEItems.CAPACITY_CARD, bus, 2, ioBuses);
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

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(CrazyItems.ITEM_CELL_1MB.get()))
                .title(Component.translatable("itemGroup.crazyae"))
                .displayItems((params, output) ->
                        CrazyRegistries.ITEMS.getEntries().forEach(itemRegistryObject -> output.accept(itemRegistryObject.get())))
                .build();
        Registry.register(registry, CrazyAE.makeId("tab_main"), tab);
    }

    public void onInit() {}
}
