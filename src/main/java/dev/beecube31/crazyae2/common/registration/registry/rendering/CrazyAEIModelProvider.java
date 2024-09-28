package dev.beecube31.crazyae2.common.registration.registry.rendering;

import dev.beecube31.crazyae2.common.registration.registry.interfaces.IDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface CrazyAEIModelProvider extends IDefinition {
	ModelResourceLocation getModel();
}
