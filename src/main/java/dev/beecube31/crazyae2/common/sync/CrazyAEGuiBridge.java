package dev.beecube31.crazyae2.common.sync;

import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.exceptions.AppEngException;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.IPartHost;
import appeng.api.storage.ITerminalHost;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.core.sync.GuiHostType;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.IPriorityHost;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.Platform;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.common.containers.*;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEGuiItem;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.items.ColorizerObj;
import dev.beecube31.crazyae2.common.items.PatternsUSBStickObj;
import dev.beecube31.crazyae2.common.parts.implementations.PartDrive;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.CrazyAEPartSharedFluidBus;
import dev.beecube31.crazyae2.common.tile.botania.*;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.common.tile.misc.TileImprovedCondenser;
import dev.beecube31.crazyae2.common.tile.networking.TileBigCrystalCharger;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import dev.beecube31.crazyae2.common.tile.networking.TileImprovedIOPort;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanEnergy;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanFluids;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanItems;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanMana;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

public enum CrazyAEGuiBridge {
	STUB(),

	GUI_PRIORITY(IPriorityHost.class, ContainerPriority.class, GuiHostType.WORLD, SecurityPermissions.BUILD),

	GUI_CRAFTING_BLOCKS_LIST(ICrazyCraftHost.class, ContainerCraftingBlockList.class, GuiHostType.WORLD, SecurityPermissions.BUILD),

	IMPROVED_MOLECULAR_ASSEMBLER(TileImprovedMAC.class, ContainerFastMAC.class, GuiHostType.WORLD, null),
	QUANTUM_CPU_HOST(TileQuantumCPU.class, ContainerQuantumCPU.class, GuiHostType.WORLD, null),
	IMPROVED_DRIVE(TileImprovedDrive.class, ContainerDriveImproved.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	IMPROVED_IO_PORT(TileImprovedIOPort.class, ContainerIOPortImproved.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	CRAFTING_UNITS_COMBINER(TileCraftingUnitsCombiner.class, ContainerCraftingUnitsCombiner.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	BIG_CRYSTAL_CHARGER(TileBigCrystalCharger.class, ContainerBigCrystalCharger.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	IMPROVED_BUS(IUpgradesInfoProvider.class, ContainerCrazyAEUpgradeable.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	ENERGY_BUS(IUpgradesInfoProvider.class, ContainerEnergyBus.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	IMPROVED_FLUID_BUSES(CrazyAEPartSharedFluidBus.class, ContainerImprovedFluidBuses.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	IMPROVED_CONDENSER(TileImprovedCondenser.class, ContainerImprovedCondenser.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	PATTERN_INTERFACE(IInterfaceHost.class, ContainerPatternsInterface.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	QUANTUM_INTERFACE(IInterfaceHost.class, ContainerQuantumInterface.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	PERFECT_INTERFACE(IInterfaceHost.class, ContainerPerfectInterface.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	//PERFECT_INSCRIBER(TilePerfectInscriber.class, ContainerPerfectInscriber.class, GuiHostType.WORLD, null),

	GUI_MANA_TERMINAL(ITerminalHost.class, ContainerManaTerminal.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_ENERGY_TERMINAL(ITerminalHost.class, ContainerEnergyTerminal.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_DRIVE_PART(PartDrive.class, ContainerPartDrive.class, GuiHostType.WORLD, SecurityPermissions.BUILD),


	GUI_ELVENTRADE_MECHANICAL(TileMechanicalElventrade.class, ContainerElventradeMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_MANAPOOL_MECHANICAL(TileMechanicalManapool.class, ContainerManapoolMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_PETAL_MECHANICAL(TileMechanicalPetal.class, ContainerPetalMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_PUREDAISY_MECHANICAL(TileMechanicalPuredaisy.class, ContainerPuredaisyMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_RUNEALTAR_MECHANICAL(TileMechanicalRunealtar.class, ContainerRunealtarMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_TERAPLATE_MECHANICAL(TileMechanicalTerraplate.class, ContainerTeraplateMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_BREWERY_MECHANICAL(TileMechanicalBrewery.class, ContainerBreweryMechanical.class, GuiHostType.WORLD, SecurityPermissions.BUILD),

	GUI_TRASHCAN_ITEMS(TileTrashcanItems.class, ContainerTrashcan.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_TRASHCAN_FLUID(TileTrashcanFluids.class, ContainerTrashcanFluid.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_TRASHCAN_MANA(TileTrashcanMana.class, ContainerTrashcanMana.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_TRASHCAN_ENERGY(TileTrashcanEnergy.class, ContainerTrashcanEnergy.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_TRASHCAN_EXP(TileTrashcanItems.class, ContainerTrashcan.class, GuiHostType.WORLD, SecurityPermissions.BUILD),


	GUI_MECHANICAL_DEVICE_PATTERN_INV(TileBotaniaMechanicalMachineBase.class, ContainerBotaniaDevicePatternsInv.class, GuiHostType.WORLD, SecurityPermissions.BUILD),
	GUI_ENERGY_BUS_SETTINGS(IUpgradesInfoProvider.class, ContainerEnergyBusSettings.class, GuiHostType.WORLD, SecurityPermissions.BUILD),

	//Item GUIs
	GUI_DENSE_PORTABLE_CELL(IPortableCell.class, ContainerMEPortableCellColorizeable.class, GuiHostType.ITEM, null),

	GUI_ITEM_COLORIZER_GUI(ColorizerObj.class, ContainerColorizerGui.class, GuiHostType.ITEM, SecurityPermissions.BUILD),
	GUI_ITEM_COLORIZER_TEXT(ColorizerObj.class, ContainerColorizerText.class, GuiHostType.ITEM, SecurityPermissions.BUILD),

	GUI_USB_PATTERNS_STICK(PatternsUSBStickObj.class, ContainerColorizerGui.class, GuiHostType.ITEM, SecurityPermissions.BUILD);

	private static CrazyAEGuiBridge[] cachedValues;
	private final Class<?> clazz;
	private final Class<? extends CrazyAEBaseContainer> containerClass;
	private final GuiHostType hostType;
	private final SecurityPermissions securityPermissions;

	@SideOnly(Side.CLIENT)
	private Class<? super CrazyAEBaseGui> clientGuiClass;

	CrazyAEGuiBridge(
			Class<?> clazz,
			Class<? extends CrazyAEBaseContainer> containerClass,
			GuiHostType hostType,
			SecurityPermissions securityPermissions
	) {
		this.hostType = hostType;
		this.securityPermissions = securityPermissions;
		this.clazz = clazz;
		this.containerClass = containerClass;

		this.getGui();
	}

	CrazyAEGuiBridge() {
		this.clazz = null;
		this.containerClass = null;
		this.hostType = null;
		this.securityPermissions = null;
	}

	private static CrazyAEGuiBridge[] cachedValues() {
		if (cachedValues == null) {
			return cachedValues = values();
		}
		return cachedValues;
	}

	@Nullable
	public static CrazyAEGuiBridge getByID(int id) {
		if (id < 0 || id > cachedValues().length) return null;

		return cachedValues[id];
	}

	static Object getGuiObject(final ItemStack it, final EntityPlayer player, final World w, final int x, final int y,
							   final int z, AEPartLocation side) {
		if (!it.isEmpty()) {
			if (it.getItem() instanceof ICrazyAEGuiItem<?> ngi) {
				return ngi.getGuiObject(it, w, new BlockPos(x, y, z), side);
			}

			final var wh = AEApi.instance().registries().wireless().getWirelessTerminalHandler(it);
			if (wh != null) {
				return new WirelessTerminalGuiObject(wh, it, player, w, x, y, z);
			}
		}

		return null;
	}

	static Object getGuiObject(final ItemStack it, final World w, BlockPos pos) {
		if (!it.isEmpty()) {
			if (it.getItem() instanceof ICrazyAEGuiItem<?> ngi) {
				return ngi.getGuiObject(it, w, pos);
			}
		}

		return null;
	}

	public boolean CorrectTileOrPart(final Object tE) {
		if (this.clazz == null) {
			throw new IllegalArgumentException("This Gui Cannot use the standard Handler.");
		}

		return this.clazz.isInstance(tE);
	}

	@SuppressWarnings("deprecation")
	private void getGui() {
		if (Platform.isClientInstall()) {
			//noinspection ResultOfMethodCallIgnored
			CrazyAEBaseGui.class.getName();
			var start = this.containerClass.getName();
			var guiClass = start.replaceFirst("common.containers.Container", "client.gui.implementations.Gui");
			if (start.equals(guiClass)) {
				throw new IllegalStateException("Unable to find gui class: " + start + "/" + guiClass);
			}

			this.clientGuiClass = ReflectionHelper.getClass(this.getClass().getClassLoader(), guiClass);
		}
	}

	private Constructor<?> findConstructor(Constructor<?>[] constructors, InventoryPlayer inventory, Object tE) {
		for (var con : constructors) {
			var types = con.getParameterTypes();
			if (types.length == 2 && types[0].isAssignableFrom(inventory.getClass()) && types[1].isAssignableFrom(tE.getClass())) {
				return con;
			}
		}

		return null;
	}

	private String typeName(Object inventory) {
		return inventory == null ? "NULL" : inventory.getClass().getName();
	}

	public Object ConstructContainer(final InventoryPlayer inventory, final Object tE) {
		try {
			final var c = this.containerClass.getConstructors();
			if (c.length == 0) {
				throw new AppEngException("Invalid Gui Class");
			}

			final var target = this.findConstructor(c, inventory, tE);

			if (target == null) {
				throw new IllegalStateException("Cannot find " + this.containerClass.getName() + "( " + this.typeName(inventory) + ", " + this.typeName(tE) + " )");
			}

			return target.newInstance(inventory, tE);
		} catch (final Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	public GuiHostType getHostType() {
		return this.hostType;
	}

	public Object ConstructGui(final InventoryPlayer inventory, final Object tE) {
		try {
			final var c = this.clientGuiClass.getConstructors();
			if (c.length == 0) {
				throw new AppEngException("Invalid Gui Class");
			}

			final var target = this.findConstructor(c, inventory, tE);

			if (target == null) {
				throw new IllegalStateException("Cannot find " + this.containerClass.getName() + "( " + this.typeName(
					inventory) + ", " + this
					.typeName(tE) + " )");
			}

			return target.newInstance(inventory, tE);
		} catch (final Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	public boolean hasPermissions(final TileEntity te, final int x, final int y, final int z, final AEPartLocation side, final EntityPlayer player) {
		final var w = player.getEntityWorld();
		final var pos = new BlockPos(x, y, z);

		if (Platform.hasPermissions(te != null ? new DimensionalCoord(te) : new DimensionalCoord(player.world, pos), player)) {
			if (this.hostType.isItem()) {
				final var it = player.inventory.getCurrentItem();
				if (!it.isEmpty() && it.getItem() instanceof ICrazyAEGuiItem) {
					final Object myItem = ((ICrazyAEGuiItem<?>) it.getItem()).getGuiObject(it, w, pos, side);
					if (this.CorrectTileOrPart(myItem)) {
						return true;
					}
				}
			}

			if (!this.hostType.isItem()) {
				final var TE = w.getTileEntity(pos);
				if (TE instanceof IPartHost) {
					((IPartHost) TE).getPart(side);
					final var part = ((IPartHost) TE).getPart(side);
					if (this.CorrectTileOrPart(part)) {
						return this.securityCheck(part, player);
					}
				} else {
					if (this.CorrectTileOrPart(TE)) {
						return this.securityCheck(TE, player);
					}
				}
			}
		}
		return false;
	}

	private boolean securityCheck(final Object te, final EntityPlayer player) {
		if (te instanceof IActionHost && this.securityPermissions != null) {

			final var gn = ((IActionHost) te).getActionableNode();
			final var g = gn.getGrid();

			final ISecurityGrid sg = g.getCache(ISecurityGrid.class);
			return sg.hasPermission(player, this.securityPermissions);

		}
		return true;
	}

}
