package dev.beecube31.crazyae2.core;

import dev.beecube31.crazyae2.client.tooltip.CrazyTooltipHandler;
import dev.beecube31.crazyae2.core.registries.client.ModelsFactory;
import dev.beecube31.crazyae2.core.registries.client.RegisterFactoryClient;
import dev.beecube31.updchecker.UpdateChecker;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class CrazyAEClientInit {
    private CrazyAEClientInit() {}

    public static void init(IEventBus bus) {
        ModelsFactory.registerBuiltInModels();
        bus.addListener(ModelsFactory::onClientSetup);
        bus.addListener(ModelsFactory::registerRenderers);
        bus.addListener(RegisterFactoryClient::onRegisterScreens);

        NeoForge.EVENT_BUS.register(new CrazyTooltipHandler());
        NeoForge.EVENT_BUS.register(new UpdateChecker(CrazyAE.MODID, "CrazyAE", "https://raw.githubusercontent.com/beecupbe/crazyae/refs/heads/master/updates.json"));
    }
}
