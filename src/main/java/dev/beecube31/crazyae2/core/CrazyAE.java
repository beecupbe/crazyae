package dev.beecube31.crazyae2.core;

import appeng.api.definitions.IItemDefinition;
import appeng.api.features.IRegistryContainer;
import appeng.api.networking.IGridCacheRegistry;
import appeng.container.implementations.ContainerMEPortableCell;
import appeng.core.Api;
import appeng.items.AEBaseItem;
import appeng.util.Platform;
import dev.beecube31.crazyae2.Tags;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.sprites.ISpriteProvider;
import dev.beecube31.crazyae2.common.features.Features;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipIconsObj;
import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipSpritesObj;
import dev.beecube31.crazyae2.common.items.cells.handlers.CreativeEnergyCellHandler;
import dev.beecube31.crazyae2.common.items.cells.handlers.CreativeManaCellHandler;
import dev.beecube31.crazyae2.common.items.cells.storage.ImprovedPortableCell;
import dev.beecube31.crazyae2.common.networking.CrazyAENetworkHandler;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.registration.Registration;
import dev.beecube31.crazyae2.common.registration.definitions.Upgrades;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiHandler;
import dev.beecube31.crazyae2.common.util.Utils;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import dev.beecube31.crazyae2.core.cache.IGridChannelBoostersCache;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import dev.beecube31.crazyae2.core.cache.impl.GridChannelBoostersCache;
import dev.beecube31.crazyae2.core.client.CrazyAEClientConfig;
import dev.beecube31.crazyae2.core.client.CrazyAEClientHandler;
import dev.beecube31.crazyae2.core.config.CrazyAEConfig;
import dev.beecube31.crazyae2.core.helpers.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(
		modid = Tags.MODID,
		version = Tags.VERSION,
		name = Tags.MODNAME,
		acceptedMinecraftVersions = "[1.12.2]",
		dependencies = "required-after:appliedenergistics2;required-after:mixinbooter@[8.3,);after:codechickenlib;after:lumenized;after:botania"
)
public class CrazyAE {
	public static CrazyAE instance;

	@SidedProxy(clientSide = "dev.beecube31.crazyae2.core.helpers.ClientHelper", serverSide = "dev.beecube31.crazyae2.core.helpers.ServerHelper", modId = Tags.MODID)
	public static Helper proxy;

	private static FeatureManager featureManager;
	private final Logger logger = LogManager.getLogger(Tags.MODID.toUpperCase());
	private final CrazyAENetworkHandler network = new CrazyAENetworkHandler();
	private Registration registration;
	private CrazyAEGuiHandler crazyAEGuiHandler;
	@SideOnly(Side.CLIENT)
	private static ItemStack icon;

	public static int iconTicks = 0;
	public static int oneToSixteenTicks = 0;

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
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public static void runClientLoops() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new TicksLoop(), 0, 1000, TimeUnit.MILLISECONDS);
	}

	public static Logger logger() {
		return instance.logger;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.registration = new Registration();

		CrazyAE.instance = this;

		NetworkRegistry.INSTANCE.registerGuiHandler(this, this.crazyAEGuiHandler = new CrazyAEGuiHandler());

		if (Platform.isClient()) {
			CrazyAE.proxy.preinit();
			CrazyAEClientConfig.init(
					new Configuration(
							new File(
									event.getModConfigurationDirectory().getPath(),
									"crazyae-client.cfg"
							)
					)
			);
		}


		MinecraftForge.EVENT_BUS.register(instance);

		this.registration.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		this.registration.init(event);

		CrazyAE.proxy.init();
		MinecraftForge.EVENT_BUS.register(Ticker.INSTANCE);

		if (!CrazyAEConfig.disableUpdatesCheck) {
			MinecraftForge.EVENT_BUS.register(new UpdateChecker());
		}

		final Api api = Api.INSTANCE;
		final IRegistryContainer registries = api.registries();
		final IGridCacheRegistry gcr = registries.gridCache();

		if (Features.MANA_CELLS.isEnabled() || Features.MANA_DENSE_CELLS.isEnabled() || Features.MEGA_MANA_DENSE_CELLS.isEnabled()) {
			registries.cell().addCellHandler(new CreativeManaCellHandler());
		}

		if (Features.ENERGY_CELLS.isEnabled() || Features.ENERGY_DENSE_CELLS.isEnabled() || Features.MEGA_ENERGY_DENSE_CELLS.isEnabled()) {
			registries.cell().addCellHandler(new CreativeEnergyCellHandler());
		}

		if (Features.FAST_AUTOCRAFTING_SYSTEM.isEnabled()) {
			gcr.registerGridCache(ICrazyAutocraftingSystem.class, CrazyAutocraftingSystem.class);
		}

		if (Features.QUANTUM_CHANNELS_MULTIPLIER.isEnabled()) {
			gcr.registerGridCache(IGridChannelBoostersCache.class, GridChannelBoostersCache.class);
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			CrazyAE.runClientLoops();
		}

		CrazyAE.proxy.postinit();

		NetworkHandler.init("CrazyAE");
		this.registration.postInit(event);
	}

	public static class FeatureManager {
		public FeatureManager() {
			var file = new File("config/crazyae-features.cfg");
			var config = new Configuration(file);

			for (var feature : Features.values()) {
				if (feature.name().equals("STUB")) {
					feature.setEnabled(true);
					continue;
				}

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

	@SubscribeEvent
	public void handleEntityItemPickup(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();

		if (player.openContainer instanceof ContainerMEPortableCell) {
			return;
		}

		InventoryPlayer inventory = event.getEntityPlayer().inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ImprovedPortableCell cell && cell.isAutoPickupEnabled(stack)) {
				if (cell.onItemPickup(event, stack)) {
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTooltipPreRenderingEv(RenderTooltipEvent.Pre event) {
		if (!CrazyAEClientConfig.isAdvancedTooltipsEnabled()) return;

		List<String> originalLines = event.getLines();
		if (originalLines.isEmpty()) {
			return;
		}

		List<String> lines = new ArrayList<>();
		Map<Integer, Integer> originalToProcessed = new HashMap<>();
		Map<Integer, Integer> processedToOriginal = new HashMap<>();

		for (int i = 0; i < originalLines.size(); i++) {
			String line = originalLines.get(i);
			if (line == null) line = "";
			String[] subLines = line.split("\n", -1);
			originalToProcessed.put(i, lines.size());
			for (String subLine : subLines) {
				processedToOriginal.put(lines.size(), i);
				lines.add(subLine);
			}
		}

		boolean needDraw = false;
		List<String> flagProcessedLines = new ArrayList<>(lines);
		Map<Integer, String> itemIconDefinitions = new HashMap<>();
		Map<Integer, String> spriteIconDefinitions = new HashMap<>();

		for (int i = 0; i < flagProcessedLines.size(); i++) {
			String line = flagProcessedLines.get(i);
			Integer originalIndex = processedToOriginal.get(i);

			if (originalIndex != null && originalToProcessed.get(originalIndex) == i) {
				boolean canRender = false;
				String flagContent = null;

				if (line.startsWith(Utils.RENDER_FLAG_BASE_TOOLTIPED)) {
					canRender = true;
					flagContent = line.substring(Utils.RENDER_FLAG_BASE_TOOLTIPED.length());
				} else if (line.startsWith(Utils.RENDER_FLAG_BASE)) {
					canRender = true;
					flagContent = line.substring(Utils.RENDER_FLAG_BASE.length());
				} else if (line.length() >= 2) {
					String potentialFlagPart = line.substring(2);
					if (potentialFlagPart.startsWith(Utils.RENDER_FLAG_BASE)) {
						canRender = true;
						flagContent = potentialFlagPart.substring(Utils.RENDER_FLAG_BASE.length());
					} else if (potentialFlagPart.startsWith(Utils.RENDER_FLAG_BASE_TOOLTIPED)) {
						canRender = true;
						flagContent = potentialFlagPart.substring(Utils.RENDER_FLAG_BASE_TOOLTIPED.length());
					}
				}

				if (canRender) {
					String[] baseSplit = flagContent.split("~", 2);
					if (baseSplit.length > 1) {
						String typeMarker = baseSplit[0] + "~";
						String dataAndText = baseSplit[1];
						String[] typeAndTextSplit = dataAndText.split(";", 2);

						if (typeAndTextSplit.length > 1) {
							String definition = typeAndTextSplit[0];
							String lineText = typeAndTextSplit[1];

							switch (typeMarker) {
								case Utils.ITEMSTACK_FLAG:
									needDraw = true;
									itemIconDefinitions.put(i, definition);
									flagProcessedLines.set(i, CrazyAEClientHandler.ICON_INDENTATION + lineText);
									break;
								case Utils.SPRITE_FLAG:
									needDraw = true;
									spriteIconDefinitions.put(i, definition);
									flagProcessedLines.set(i, CrazyAEClientHandler.ICON_INDENTATION + lineText);
									break;
							}
						}
					}
				}
			}
		}

		if (needDraw) {
			event.setCanceled(true);
			final FontRenderer font = event.getFontRenderer();
			final int screenWidth = event.getScreenWidth();
			final int screenHeight = event.getScreenHeight();

			CrazyAEClientHandler.TooltipLayout layout = CrazyAEClientHandler.drawTooltipAndGetComponents(
					event.getStack(),
					flagProcessedLines,
					event.getX(),
					event.getY(),
					screenWidth,
					screenHeight,
					-1,
					font
			);

			if (layout.posArray != null && !layout.effectiveLines.isEmpty()) {
				final int finalTooltipX = layout.posArray[0];
				final int finalTooltipY = layout.posArray[1];
				final int titleLinesCount = layout.posArray[4];
				final int iconOffsetX = 2;
				final int iconBaseYOffset = 0;

				for (Map.Entry<Integer, String> entry : itemIconDefinitions.entrySet()) {
					int logicalLineIndex = entry.getKey();
					Integer firstEffectiveLineIndex = layout.logicalToEffectiveLineMap.get(logicalLineIndex);

					if (firstEffectiveLineIndex != null) {
						int lineTextStartY = CrazyAEClientHandler.getCorrectTextLineY(
								firstEffectiveLineIndex,
								finalTooltipY,
								titleLinesCount,
								layout.effectiveLines.size()
						);
						CrazyAEClientHandler.drawItemIntoTooltip(
								Utils.decodeItemStack(entry.getValue()),
								finalTooltipX + iconOffsetX,
								lineTextStartY + iconBaseYOffset
						);
					}
				}

				for (Map.Entry<Integer, String> entry : spriteIconDefinitions.entrySet()) {
					int logicalLineIndex = entry.getKey();
					Integer firstEffectiveLineIndex = layout.logicalToEffectiveLineMap.get(logicalLineIndex);

					if (firstEffectiveLineIndex != null) {
						int lineTextStartY = CrazyAEClientHandler.getCorrectTextLineY(
								firstEffectiveLineIndex,
								finalTooltipY,
								titleLinesCount,
								layout.effectiveLines.size()
						);
						CrazyAEClientHandler.drawSpriteIntoTooltip(
								Utils.decodeSpriteFlag(entry.getValue()),
								finalTooltipX + iconOffsetX,
								lineTextStartY + iconBaseYOffset
						);
					}
				}
			}
			return;
		}

		if (Minecraft.getMinecraft().currentScreen instanceof CrazyAEBaseGui g && g.hoveredClientSlot != null) {
			return;
		}

		Upgrades.UpgradeType contain = null;
		if (CrazyAE.definitions().upgrades().stackUpgrade().isSameAs(event.getStack())) {
			contain = Upgrades.UpgradeType.STACKS;
		} else if (CrazyAE.definitions().upgrades().improvedSpeedUpgrade().isSameAs(event.getStack())) {
			contain = Upgrades.UpgradeType.IMPROVED_SPEED;
		} else if (CrazyAE.definitions().upgrades().advancedSpeedUpgrade().isSameAs(event.getStack())) {
			contain = Upgrades.UpgradeType.ADVANCED_SPEED;
		}

		if (contain != null) {
			event.setCanceled(true);
			final FontRenderer font = event.getFontRenderer();
			final int screenWidth = event.getScreenWidth();
			final int screenHeight = event.getScreenHeight();

			List<String> upgradeProcessedLines = new ArrayList<>(lines.size());
			Map<Integer, Integer> upgradeIconTargetMap = new HashMap<>();
			int entryIndex = 0;

			for (int i = 0; i < lines.size(); i++) {
				String lineContent = lines.get(i);
				Integer originalIndex = processedToOriginal.get(i);

				if (originalIndex != null &&
						originalIndex >= 2 &&
						originalIndex < originalLines.size() - (Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? 2 : 0) &&
						originalToProcessed.get(originalIndex) == i
				) {

					if (!lineContent.startsWith("crazyae:") && !lineContent.contains("CrazyAE")) {
						lineContent = CrazyAEClientHandler.ICON_INDENTATION + lineContent;
					}

					if (originalIndex - 2 == entryIndex) {
						upgradeIconTargetMap.put(entryIndex, i);
						entryIndex++;
					}
				}
				upgradeProcessedLines.add(lineContent.replaceAll("§+.", ""));
			}

			Map<ItemStack, Integer> supportedDevices = contain.getSupported();
			List<Map.Entry<ItemStack, Integer>> sortedSupportedDeviceEntries =
					CrazyAEClientHandler.getSortedItemEntries(supportedDevices, supportedDevices.keySet());


			CrazyAEClientHandler.TooltipLayout layout = CrazyAEClientHandler.drawTooltipAndGetComponents(
					event.getStack(),
					upgradeProcessedLines,
					event.getX(),
					event.getY(),
					screenWidth,
					screenHeight,
					-1,
					font
			);

			if (layout.posArray != null && !layout.effectiveLines.isEmpty()) {
				final int finalTooltipX = layout.posArray[0];
				final int finalTooltipY = layout.posArray[1];
				final int titleActualLinesCount = layout.posArray[4];
				final int iconOffsetX = 2;
				final int iconBaseYOffset = 0;

				for (int deviceIdx = 0; deviceIdx < sortedSupportedDeviceEntries.size(); deviceIdx++) {
					Integer logicalLineIndex = upgradeIconTargetMap.get(deviceIdx);
					if (logicalLineIndex != null && logicalLineIndex < upgradeProcessedLines.size()) {
						Integer firstEffectiveLineIndex = layout.logicalToEffectiveLineMap.get(logicalLineIndex);
						if (firstEffectiveLineIndex != null) {
							ItemStack itemToDraw = sortedSupportedDeviceEntries.get(deviceIdx).getKey();
							int lineTextStartY = CrazyAEClientHandler.getCorrectTextLineY(
									firstEffectiveLineIndex,
									finalTooltipY,
									titleActualLinesCount,
									layout.effectiveLines.size()
							);
							CrazyAEClientHandler.drawItemIntoTooltip(
									itemToDraw,
									finalTooltipX + iconOffsetX,
									lineTextStartY + iconBaseYOffset
							);
						}
					}
				}
			}
			return;
		}

		Item eventItem = event.getStack().getItem();
		if (eventItem instanceof AEBaseItem && (eventItem instanceof ITooltipSpritesObj || eventItem instanceof ITooltipIconsObj)) {
			event.setCanceled(true);
			final FontRenderer font = event.getFontRenderer();
			final int screenWidth = event.getScreenWidth();
			final int screenHeight = event.getScreenHeight();

			List<String> itemTooltipProcessedLines = new ArrayList<>(lines.size());
			for (int i = 0; i < lines.size(); i++) {
				String lineContent = lines.get(i);
				Integer originalIndex = processedToOriginal.get(i);

				if (originalIndex != null &&
						originalIndex >= 2 && originalIndex < originalLines.size() - (Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? 2 : 0) &&
						originalToProcessed.get(originalIndex) == i) {
					if (!lineContent.startsWith("crazyae:") && !lineContent.contains("CrazyAE")) {
						lineContent = CrazyAEClientHandler.ICON_INDENTATION + lineContent;
					}
				}
				itemTooltipProcessedLines.add(lineContent.replaceAll("§+.", ""));
			}

			CrazyAEClientHandler.TooltipLayout layout = CrazyAEClientHandler.drawTooltipAndGetComponents(
					event.getStack(),
					itemTooltipProcessedLines,
					event.getX(),
					event.getY(),
					screenWidth,
					screenHeight,
					-1,
					font
			);

			if (layout.posArray != null && !layout.effectiveLines.isEmpty()) {
				final int finalTooltipX = layout.posArray[0];
				final int finalTooltipY = layout.posArray[1];
				final int titleActualLinesCount = layout.posArray[4];
				final int iconOffsetX = 3;
				final int iconBaseYOffset = 0;

				if (eventItem instanceof ITooltipSpritesObj tSprites) {
					for (Map.Entry<ISpriteProvider, Integer> e : tSprites.getTooltipIcons().entrySet()) {
						int originalLineIndexForIcon = e.getValue();
						Integer logicalLineIndex = originalToProcessed.get(originalLineIndexForIcon);

						if (logicalLineIndex != null) {
							Integer firstEffectiveLineIndex = layout.logicalToEffectiveLineMap.get(logicalLineIndex);
							if (firstEffectiveLineIndex != null && firstEffectiveLineIndex < layout.effectiveLines.size()) {
								int lineTextStartY = CrazyAEClientHandler.getCorrectTextLineY(
										firstEffectiveLineIndex,
										finalTooltipY,
										titleActualLinesCount,
										layout.effectiveLines.size()
								);
								CrazyAEClientHandler.drawSpriteIntoTooltip(
										e.getKey(),
										finalTooltipX + iconOffsetX,
										lineTextStartY + iconBaseYOffset
								);
							}
						}
					}
				} else {
					ITooltipIconsObj tIcons = (ITooltipIconsObj) eventItem;
					for (Map.Entry<ItemStack, Integer> e : tIcons.getTooltipIcons().entrySet()) {
						int originalLineIndexForIcon = e.getValue();
						Integer logicalLineIndex = originalToProcessed.get(originalLineIndexForIcon);

						if (logicalLineIndex != null) {
							Integer firstEffectiveLineIndex = layout.logicalToEffectiveLineMap.get(logicalLineIndex);
							if (firstEffectiveLineIndex != null && firstEffectiveLineIndex < layout.effectiveLines.size()) {
								int lineTextStartY = CrazyAEClientHandler.getCorrectTextLineY(
										firstEffectiveLineIndex,
										finalTooltipY,
										titleActualLinesCount,
										layout.effectiveLines.size()
								);
								CrazyAEClientHandler.drawItemIntoTooltip(
										e.getKey(),
										finalTooltipX + iconOffsetX,
										lineTextStartY + iconBaseYOffset
								);
							}
						}
					}
				}
			}
		}
	}

	public static class TicksLoop implements Runnable {
		public void run() {
			List<IItemDefinition> list = CrazyAE.definitions().items().getCreativeTabIcons();

			if (CrazyAE.iconTicks >= list.size()) {
				CrazyAE.iconTicks = 0;
			}

			if (CrazyAE.oneToSixteenTicks >= 16) {
				CrazyAE.oneToSixteenTicks = 0;
			} else {
				CrazyAE.oneToSixteenTicks++;
			}

			list.get(CrazyAE.iconTicks).maybeStack(1).ifPresent(is -> icon = is);

			CrazyAE.iconTicks++;
		}
	}
}