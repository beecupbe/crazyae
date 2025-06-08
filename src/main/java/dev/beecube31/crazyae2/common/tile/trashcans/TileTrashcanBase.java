package dev.beecube31.crazyae2.common.tile.trashcans;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.tile.base.CrazyAENetworkInvOCTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public abstract class TileTrashcanBase extends CrazyAENetworkInvOCTile implements IUpgradesInfoProvider, IConfigManagerHost, IGridTickable {

    protected final IActionSource src;
    protected final ConfigManager manager;
    protected boolean isActive = false;

    protected int itemsToSendPerTick = 32;

    public TileTrashcanBase() {
        this.src = new MachineSource(this);
        this.manager = new ConfigManager(this);

        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setIdlePowerUsage(4D);

        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
    }

    @Override
    public abstract void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removed, ItemStack added);

    @Override
    protected boolean readFromStream(ByteBuf data) throws IOException {
        boolean c = super.readFromStream(data);

        final boolean oldIsActive = this.isActive;
        this.isActive = data.readBoolean();
        return oldIsActive != this.isActive || c;
    }

    @Override
    protected void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeBoolean(this.isActive());
    }

    public boolean isActive() {
        if (Platform.isServer()) {
            try {
                return this.getProxy().getEnergy().isNetworkPowered();
            } catch (GridAccessException e) {
                return false;
            }
        }
        return this.isActive;
    }

    @Override
    public @NotNull AECableType getCableConnectionType(final @NotNull AEPartLocation dir) {
        return AECableType.SMART;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.manager;
    }

    @Override public abstract IItemDefinition getBlock();

    @Override
    public abstract IItemHandler getInventoryByName(final String name);

    @Override
    public abstract @NotNull IItemHandler getInternalInventory();

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {

    }

    @Override
    public void getDrops(final World w, final BlockPos pos, final List<ItemStack> drops) {

    }


    @Override
    public @NotNull TickingRequest getTickingRequest(final @NotNull IGridNode node) {
        return new TickingRequest(1, 5, false, false);
    }

    @Override
    public @NotNull abstract TickRateModulation tickingRequest(final @NotNull IGridNode node, final int ticksSinceLastCall);
}
