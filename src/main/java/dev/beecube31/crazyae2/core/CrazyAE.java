package dev.beecube31.crazyae2.core;

import appeng.api.storage.StorageCells;
import com.mojang.logging.LogUtils;
import dev.beecube31.crazyae2.client.tooltip.CrazyTooltipHandler;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;
import dev.beecube31.crazyae2.common.definitions.CrazyParts;
import dev.beecube31.crazyae2.common.item.cell.CrazyPortableCellItem;
import dev.beecube31.crazyae2.common.item.cell.inv.CrazyCellHandler;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.core.config.CrazyAEClientConfig;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.config.CrazyAEMachinesConfig;
import dev.beecube31.crazyae2.core.registries.CrazyRegistries;
import dev.beecube31.crazyae2.core.registries.client.ModelsFactory;
import dev.beecube31.crazyae2.core.registries.client.RegisterFactoryClient;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import dev.beecube31.crazyae2.core.registries.server.CrazyTileEntities;
import dev.beecube31.crazyae2.core.registries.server.RegisterFactoryServer;
import dev.beecube31.updchecker.UpdateChecker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(CrazyAE.MODID)
public class CrazyAE {

    public static final String MODID = "crazyae";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static CrazyAE INSTANCE;

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static Logger logger() {
        return LOGGER;
    }

    public CrazyAE() {
        CrazyAE.LOGGER.info("Launching CrazyAE {} for Minecraft {}", ModVersion.get(), FMLLoader.versionInfo().mcVersion());
        INSTANCE = this;
        StorageCells.addCellHandler(CrazyCellHandler.INSTANCE);

        ModsChecker.init();
        CrazyItems.init();
        CrazyBlocks.init();
        CrazyParts.init();
        CrazyTileEntities.init();
        CrazyContainers.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CrazyRegistries.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CrazyAEClientConfig.CLIENT_SPEC);
            ModelsFactory.registerBuiltInModels();
            bus.addListener(ModelsFactory::onClientSetup);

            bus.addListener(RegisterFactoryClient::onClientSetup);

            new UpdateChecker(MODID, "CrazyAE", "https://raw.githubusercontent.com/beecupbe/crazyae/refs/heads/master/updates.json");

            MinecraftForge.EVENT_BUS.register(new CrazyTooltipHandler());
        });

        MinecraftForge.EVENT_BUS.addListener(CrazyPortableCellItem::onItemPickup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrazyAEConfig.COMMON_SPEC, CrazyAEConfig.CONFIG_FILENAME);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrazyAEMachinesConfig.COMMON_SPEC, CrazyAEMachinesConfig.CONFIG_FILENAME);

        bus.addListener(this::commonSetup);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                RegisterFactoryServer.INSTANCE.registerTab(e.getVanillaRegistry());
            }
        });
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        RegisterFactoryServer.INSTANCE.onInit();
        RegisterFactoryServer.onCommonSetup(event);
        CrazyNetworkHandler.init(new ResourceLocation(MODID, "main"));
    }
}
