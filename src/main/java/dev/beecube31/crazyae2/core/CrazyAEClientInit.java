package dev.beecube31.crazyae2.core;

import dev.beecube31.crazyae2.client.tooltip.CrazyTooltipHandler;
import dev.beecube31.crazyae2.core.registries.client.ModelsFactory;
import dev.beecube31.crazyae2.core.registries.client.RegisterFactoryClient;
import dev.beecube31.updchecker.UpdateChecker;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.common.network.CrazyPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class CrazyAEClientInit {
    private CrazyAEClientInit() {}

    public static void init(IEventBus bus) {
        bus.addListener(ModelsFactory::registerBlockStateModels);
        bus.addListener(ModelsFactory::registerRenderers);
        bus.addListener(ModelsFactory::registerPartRenderers);
        bus.addListener(RegisterFactoryClient::onRegisterScreens);
        bus.addListener(CrazyAEClientInit::registerPayloadHandlers);

        NeoForge.EVENT_BUS.register(new CrazyTooltipHandler());
        NeoForge.EVENT_BUS.register(new UpdateChecker(CrazyAE.MODID, "CrazyAE", "https://raw.githubusercontent.com/beecupbe/crazyae/refs/heads/master/updates.json"));
    }

    private static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(CrazyPayload.TYPE, CrazyNetworkHandler::handleClient);
    }
}
