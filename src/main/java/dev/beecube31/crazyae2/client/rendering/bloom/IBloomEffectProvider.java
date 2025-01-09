package dev.beecube31.crazyae2.client.rendering.bloom;

import gregtech.client.utils.IBloomEffect;

public interface IBloomEffectProvider {
    IBloomEffect getEffect();

    boolean canRenderBloom();
}
