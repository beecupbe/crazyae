package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.*;
import appeng.api.implementations.guiobjects.IGuiItem;
import appeng.api.parts.IPart;
import appeng.api.util.IConfigManager;
import appeng.container.slot.IOptionalSlotHost;
import appeng.items.contents.NetworkToolViewer;
import appeng.items.tools.ToolNetworkTool;
import appeng.parts.automation.PartExportBus;
import appeng.util.Platform;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.base.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.base.slot.OptionalSlotFake;
import dev.beecube31.crazyae2.common.containers.base.slot.OptionalSlotFakeTypeOnly;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFakeTypeOnly;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.interfaces.IEnergyBus;
import dev.beecube31.crazyae2.common.interfaces.mana.IManaLinkableDevice;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class ContainerCrazyAEUpgradeable extends CrazyAEBaseContainer implements IOptionalSlotHost {

    private final IUpgradesInfoProvider upgradeable;
    @GuiSync(0)
    public RedstoneMode rsMode = RedstoneMode.IGNORE;
    @GuiSync(1)
    public FuzzyMode fzMode = FuzzyMode.IGNORE_ALL;
    @GuiSync(5)
    public YesNo cMode = YesNo.NO;
    @GuiSync(6)
    public SchedulingMode schedulingMode = SchedulingMode.DEFAULT;

    public IManaLinkableDevice manaDevice;

    @GuiSync(9)
    public boolean isThisManaDevice = false;
    @GuiSync(10)
    public int manaDevicePosX;
    @GuiSync(11)
    public int manaDevicePosY;
    @GuiSync(12)
    public int manaDevicePosZ;
    @GuiSync(13)
    public boolean manaDeviceLinked;

    @GuiSync(14)
    public boolean isThisEnergyDevice = false;

    private int tbSlot;
    private NetworkToolViewer tbInventory;

    protected int myOffsetX = 0;

    public ContainerCrazyAEUpgradeable(final InventoryPlayer ip, final IUpgradesInfoProvider te) {
        super(ip, (TileEntity) (te instanceof TileEntity ? te : null), (IPart) (te instanceof IPart ? te : null));
        Preconditions.checkNotNull(te);

        this.upgradeable = te;

        if (te instanceof IManaLinkableDevice) {
            this.manaDevice = (IManaLinkableDevice) te;
            this.isThisManaDevice = true;
        }

        if (te instanceof IEnergyBus) {
            this.isThisEnergyDevice = true;
        }

        World w = null;
        int xCoord = 0;
        int yCoord = 0;
        int zCoord = 0;

        if (te instanceof final TileEntity myTile) {
            w = myTile.getWorld();
            xCoord = myTile.getPos().getX();
            yCoord = myTile.getPos().getY();
            zCoord = myTile.getPos().getZ();
        }

        if (te instanceof IPart) {
            final TileEntity mk = te.getTile();
            w = mk.getWorld();
            xCoord = mk.getPos().getX();
            yCoord = mk.getPos().getY();
            zCoord = mk.getPos().getZ();
        }

        final IInventory pi = this.getPlayerInv();
        for (int x = 0; x < pi.getSizeInventory(); x++) {
            final ItemStack pii = pi.getStackInSlot(x);
            if (!pii.isEmpty() && pii.getItem() instanceof ToolNetworkTool) {
                this.lockPlayerInventorySlot(x);
                this.tbSlot = x;
                this.tbInventory = (NetworkToolViewer) ((IGuiItem) pii.getItem()).getGuiObject(pii, w, new BlockPos(xCoord, yCoord, zCoord));
                break;
            }
        }

        if (this.hasToolbox()) {
            for (int v = 0; v < 3; v++) {
                for (int u = 0; u < 3; u++) {
                    this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, this.tbInventory
                            .getInternalInventory(), u + v * 3, 187 + u * 18, this.getHeight() - this.getToolboxYOffset() + v * 18, this.getInventoryPlayer())).setPlayerSide());
                }
            }
        }

        this.setupConfig();

        this.bindPlayerInventory(ip, 0, this.getHeight() - /* height of player inventory */82);
    }

    public void setMyOffsetX(int v) {
        this.myOffsetX = v;
    }

    public void addMyOffsetX(int v) {
        this.myOffsetX += v;
    }

    public int getMyOffsetX() {
        return this.myOffsetX;
    }

    public boolean hasToolbox() {
        return this.tbInventory != null;
    }

    protected int getHeight() {
        return 184;
    }

    protected int getToolboxYOffset() {
        return 82;
    }

    protected void setupConfig() {
        this.setupUpgrades();

        if (this.isThisManaDevice ||
                this.isThisEnergyDevice) {
            return;
        }

        final IItemHandler inv = this.getUpgradeable().getInventoryByName("config");
        final int y = 40;
        final int x = 80;
        this.addSlotToContainer(new SlotFakeTypeOnly(inv, 0, x, y));

        if (this.supportCapacity()) {
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 1, x, y, -1, 0, 1));
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 2, x, y, 1, 0, 1));
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 3, x, y, 0, -1, 1));
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 4, x, y, 0, 1, 1));

            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 5, x, y, -1, -1, 2));
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 6, x, y, 1, -1, 2));
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 7, x, y, -1, 1, 2));
            this.addSlotToContainer(new OptionalSlotFakeTypeOnly(inv, this, 8, x, y, 1, 1, 2));
        }
    }

    protected void setupUpgrades() {
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");
        if (this.availableUpgrades() > 0) {
            this.addSlotToContainer(
                    (new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, 0, this.hasOptionSideButton() ? 223 : 187 + this.myOffsetX, 8, this.getInventoryPlayer()))
                            .setStackLimit(1).setNotDraggable());
        }
        if (this.availableUpgrades() > 1) {
            this.addSlotToContainer(
                    (new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, 1, this.hasOptionSideButton() ? 223 : 187 + this.myOffsetX, 8 + 18, this.getInventoryPlayer()))
                            .setStackLimit(1).setNotDraggable());
        }
        if (this.availableUpgrades() > 2) {
            this.addSlotToContainer(
                    (new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, 2, this.hasOptionSideButton() ? 223 : 187 + this.myOffsetX, 8 + 18 * 2, this.getInventoryPlayer()))
                            .setStackLimit(1).setNotDraggable());
        }
        if (this.availableUpgrades() > 3) {
            this.addSlotToContainer(
                    (new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, 3, this.hasOptionSideButton() ? 223 : 187 + this.myOffsetX, 8 + 18 * 3, this.getInventoryPlayer()))
                            .setStackLimit(1).setNotDraggable());
        }
    }

    public boolean hasOptionSideButton() {
        return false;
    }

    protected boolean supportCapacity() {
        return true;
    }

    public int availableUpgrades() {
        return 4;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            final IConfigManager cm = this.getUpgradeable().getConfigManager();
            this.loadSettingsFromHost(cm);

            if (this.manaDevice != null) {
                this.manaDevicePosX = this.manaDevice.getLinkedPoolPosX();
                this.manaDevicePosY = this.manaDevice.getLinkedPoolPosY();
                this.manaDevicePosZ = this.manaDevice.getLinkedPoolPosZ();
                this.manaDeviceLinked = this.manaDevice.hasLinkedPool();
            }
        }

        this.checkToolbox();

        for (final Object o : this.inventorySlots) {
            if (o instanceof final OptionalSlotFake fs) {
                if (!fs.isSlotEnabled() && !fs.getDisplayStack().isEmpty()) {
                    fs.clearStack();
                }
            }
        }

        this.standardDetectAndSendChanges();
    }

    protected void loadSettingsFromHost(final IConfigManager cm) {
        this.setFuzzyMode((FuzzyMode) cm.getSetting(Settings.FUZZY_MODE));
        this.setRedStoneMode((RedstoneMode) cm.getSetting(Settings.REDSTONE_CONTROLLED));
        if (this.getUpgradeable() instanceof PartExportBus) {
            this.setCraftingMode((YesNo) cm.getSetting(Settings.CRAFT_ONLY));
            this.setSchedulingMode((SchedulingMode) cm.getSetting(Settings.SCHEDULING_MODE));
        }
    }

    protected void checkToolbox() {
        if (this.hasToolbox()) {
            final ItemStack currentItem = this.getPlayerInv().getStackInSlot(this.tbSlot);

            if (currentItem != this.tbInventory.getItemStack()) {
                if (!currentItem.isEmpty()) {
                    if (ItemStack.areItemsEqual(this.tbInventory.getItemStack(), currentItem)) {
                        this.getPlayerInv().setInventorySlotContents(this.tbSlot, this.tbInventory.getItemStack());
                    } else {
                        this.setValidContainer(false);
                    }
                } else {
                    this.setValidContainer(false);
                }
            }
        }
    }

    protected void standardDetectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        final int upgrades = this.getUpgradeable().getInstalledUpgrades(Upgrades.CAPACITY);

        if (idx == 1 && upgrades > 0) {
            return true;
        }
        return idx == 2 && upgrades > 1;
    }

    public FuzzyMode getFuzzyMode() {
        return this.fzMode;
    }

    public void setFuzzyMode(final FuzzyMode fzMode) {
        this.fzMode = fzMode;
    }

    public YesNo getCraftingMode() {
        return this.cMode;
    }

    public void setCraftingMode(final YesNo cMode) {
        this.cMode = cMode;
    }

    public RedstoneMode getRedStoneMode() {
        return this.rsMode;
    }

    public void setRedStoneMode(final RedstoneMode rsMode) {
        this.rsMode = rsMode;
    }

    public SchedulingMode getSchedulingMode() {
        return this.schedulingMode;
    }

    public void setSchedulingMode(final SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    public IUpgradesInfoProvider getUpgradeable() {
        return this.upgradeable;
    }

    public IManaLinkableDevice getManaDevice() {
        return this.manaDevice;
    }

    public boolean isThisManaDevice() {
        return this.isThisManaDevice;
    }

    public int getManaDevicePoolPosX() {
        return this.manaDevicePosX;
    }

    public int getManaDevicePoolPosY() {
        return this.manaDevicePosY;
    }

    public int getManaDevicePoolPosZ() {
        return this.manaDevicePosZ;
    }

    public boolean manaDeviceHasPool() {
        return this.manaDeviceLinked;
    }
}
