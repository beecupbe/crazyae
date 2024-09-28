package dev.beecube31.crazyae2.common.registration.registry.components;

import appeng.bootstrap.components.IPreInitComponent;
import appeng.core.AppEng;
import com.google.common.collect.Sets;
import dev.beecube31.crazyae2.Tags;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class CrazyAEModelOverrideComponent implements IPreInitComponent {
	private static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
	private final Map<String, BiFunction<ModelResourceLocation, IBakedModel, IBakedModel>> customizer =
			new HashMap<>();

	public CrazyAEModelOverrideComponent() {}

	public void addOverride(String resourcePath,
							BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> customizer) {
		this.customizer.put(resourcePath, customizer);
	}

	public void preInitialize(Side side) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		var modelRegistry = event.getModelRegistry();
		Set<ModelResourceLocation> keys = Sets.newHashSet(modelRegistry.getKeys());
		var missingModel = ModelLoaderRegistry.getMissingModel();

		for (var location : keys) {
			if (location.getNamespace().equals(Tags.MODID)) {
				var orgModel = modelRegistry.getObject(location);
				if (orgModel != missingModel) {
					var customizer = this.customizer.get(location.getPath());
					if (customizer != null) {
						var newModel = customizer.apply(location, orgModel);
						if (newModel != orgModel) {
							modelRegistry.putObject(location, newModel);
						}
					}
				}
			}
		}

	}
}
