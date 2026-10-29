package dev.beecube31.crazyae2.core.registries;

import com.mojang.serialization.Codec;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CrazyDataComponents {
    private CrazyDataComponents() {}

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CrazyAE.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_PICKUP =
            COMPONENTS.registerComponentType("auto_pickup", builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}
