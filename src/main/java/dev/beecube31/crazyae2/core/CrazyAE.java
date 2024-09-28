package dev.beecube31.crazyae2.core;

import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.networking.CrazyAENetworkHandler;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.registration.Registration;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
		modid = Tags.MODID,
		version = Tags.VERSION,
		name = Tags.MODNAME,
		acceptedMinecraftVersions = "[1.12.2]",
		dependencies = "required-after:appliedenergistics2;required-after:mixinbooter@[8.3,);after:codechickenlib;after:lumenized"
)
public class CrazyAE {
	public static CrazyAE instance;
	private static FeatureManager featureManager;
	private final Logger logger = LogManager.getLogger(Tags.MODID.toUpperCase());
	private final CrazyAENetworkHandler network = new CrazyAENetworkHandler();
	private Registration registration;
	private CrazyAEGuiHandler crazyAEGuiHandler;
	@SideOnly(Side.CLIENT)
	private ItemStack icon;

    public static void setupFeatureConfig() {
		if (featureManager == null) {
			featureManager = new FeatureManager();
		}
	}

	public static SimpleNetworkWrapper net() {
		return instance.network.getChannel();
	}

	public static CrazyAEGuiHandler gui() {
		return instance.crazyAEGuiHandler;
	}

	public static Registration definitions() {
		return instance.registration;
	}

	@SideOnly(Side.CLIENT)
	public static ItemStack icon() {
		return instance.icon;
	}

	public static Logger logger() {
		return instance.logger;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.registration = new Registration();

		CrazyAE.instance = this;
		NetworkRegistry.INSTANCE.registerGuiHandler(this, this.crazyAEGuiHandler = new CrazyAEGuiHandler());

		this.registration.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		this.registration.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			this.icon = CrazyAE.definitions().items().storageCell1MB().maybeStack(1).orElse(ItemStack.EMPTY);
		}

		NetworkHandler.init("CrazyAE");
		this.registration.postInit(event);
	}

	public static class FeatureManager {

		public FeatureManager() {
			var file = new File("config/crazyae-features.cfg");
			var config = new Configuration(file);

			for (var feature : Features.values()) {
				var lowerCase = feature.name().toLowerCase();

				var featureCategory = config.getCategory(lowerCase);
				var entry = featureCategory.computeIfAbsent("enabled", x -> new Property("enabled", "true",
					Property.Type.BOOLEAN));

				feature.setEnabled(entry.getBoolean(true));

				var subFeatures = feature.getSubFeatures();
				if (subFeatures != null) {
					for (var subFeature : subFeatures) {
						var subFeatureLowerCase = subFeature.name().toLowerCase();
						var subFeatureEntry = featureCategory.computeIfAbsent(subFeatureLowerCase,
							x -> new Property(subFeatureLowerCase, "true", Property.Type.BOOLEAN));

						subFeature.setEnabled(feature.isEnabled() && subFeatureEntry.getBoolean(true));
						subFeatureEntry.setComment(subFeature.getDescription());
					}
				}
			}

			config.save();
		}
	}


}