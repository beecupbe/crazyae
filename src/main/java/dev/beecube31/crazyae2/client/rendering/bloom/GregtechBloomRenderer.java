package dev.beecube31.crazyae2.client.rendering.bloom;

import co.neeve.nae2.common.interfaces.IBeamFormer;
import dev.beecube31.crazyae2.client.rendering.bloom.setup.GregtechBloomSetup;
import gregtech.client.renderer.IRenderSetup;
import gregtech.client.shader.postprocessing.BloomType;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.IBloomEffect;

import javax.annotation.Nullable;

public class GregtechBloomRenderer implements IBloomRenderer {
    private final Object setup = new GregtechBloomSetup();

    @Nullable
    public static IBloomRenderer create() {
        try {
            Class<?> clazz = Class.forName("gregtech.client.utils.IBloomEffect");
            if (clazz.isInterface())
                return new GregtechBloomRenderer();
        } catch (Exception ignored) {}

        return null;
    }

    public boolean shouldRenderDynamic(IBeamFormer partBeamFormer) {
        return false;
    }

    public void init(IBloomEffectProvider block) {
        IBloomEffect effect = block.getEffect();
        BloomEffectUtil.registerBloomRender((IRenderSetup)this.setup, BloomType.UNITY, effect, bloomRenderTicket -> block.canRenderBloom());
    }
}
