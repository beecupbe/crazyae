package dev.beecube31.crazyae2.core;

import appeng.api.storage.StorageCells;
import com.mojang.logging.LogUtils;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.definitions.CrazyParts;
import dev.beecube31.crazyae2.common.item.cell.inv.CrazyCellHandler;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.core.config.CrazyAEClientConfig;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.config.CrazyAEMachinesConfig;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import dev.beecube31.crazyae2.core.registries.server.CrazyTileEntities;
import dev.beecube31.crazyae2.core.registries.server.RegisterFactoryServer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(CrazyAE.MODID)
public class CrazyAE {

    public static final String MODID = "crazyae";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static CrazyAE INSTANCE;

    public static Identifier makeId(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }

    public static Logger logger() {
        return LOGGER;
    }

    public CrazyAE(IEventBus bus, ModContainer container) {
        CrazyAE.LOGGER.info("Launching CrazyAE {} for Minecraft {}", ModVersion.get(), FMLLoader.getCurrent().getVersionInfo().mcVersion());
        INSTANCE = this;
        StorageCells.addCellHandler(CrazyCellHandler.INSTANCE);

        ModsChecker.init();
        CrazyItems.init();
        CrazyBlocks.init();
        CrazyParts.init();
        CrazyTileEntities.init();
        CrazyContainers.init();
        RegisterFactoryServer.registerTab();

        CrazyRegistries.register(bus);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            container.registerConfig(ModConfig.Type.CLIENT, CrazyAEClientConfig.CLIENT_SPEC);
            CrazyAEClientInit.init(bus);
        }
        NeoForge.EVENT_BUS.addListener(dev.beecube31.crazyae2.common.item.cell.CrazyPortableCellItem::onItemPickup);

        container.registerConfig(ModConfig.Type.COMMON, CrazyAEConfig.COMMON_SPEC, CrazyAEConfig.CONFIG_FILENAME);
        container.registerConfig(ModConfig.Type.COMMON, CrazyAEMachinesConfig.COMMON_SPEC, CrazyAEMachinesConfig.CONFIG_FILENAME);

        bus.addListener(this::commonSetup);
        bus.addListener(CrazyNetworkHandler::register);
        bus.addListener(RegisterFactoryServer::registerCapabilities);
        bus.addListener(RegisterFactoryServer::registerPartCapabilities);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        RegisterFactoryServer.INSTANCE.onInit();
        RegisterFactoryServer.onCommonSetup(event);
    }
}
