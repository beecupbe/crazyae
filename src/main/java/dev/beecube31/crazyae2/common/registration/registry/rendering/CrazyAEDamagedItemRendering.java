package dev.beecube31.crazyae2.common.registration.registry.rendering;

import appeng.bootstrap.IItemRendering;
import appeng.bootstrap.ItemRenderingCustomizer;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.DamagedDefinitions;
import dev.beecube31.crazyae2.common.registration.registry.interfaces.IDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Collectors;

public class CrazyAEDamagedItemRendering<T extends DamagedDefinitions<?, ?>> extends ItemRenderingCustomizer {
	public static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
	private final T registry;

	public CrazyAEDamagedItemRendering(T registry) {
		this.registry = registry;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void customize(IItemRendering rendering) {
		rendering.variants(this.registry.getEntries().stream()
			.filter(IDefinition::isEnabled)
			.map(CrazyAEIModelProvider::getModel)
			.collect(Collectors.toList()));

		rendering.meshDefinition(is -> {
			var type = this.registry.getType(is);

			if (type == null) {
				return MODEL_MISSING;
			}

			return type.getModel();
		});
	}
}
