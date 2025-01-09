package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.interfaces.device.mechanical.IBotaniaMechanicalDevice;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ContainerBotaniaDevicePatternsInv extends ContainerCrazyAEUpgradeable {

    protected final TileBotaniaMechanicalMachineBase tile;
    protected BotaniaMechanicalDeviceType type;

    public ContainerBotaniaDevicePatternsInv(final InventoryPlayer ip, final TileBotaniaMechanicalMachineBase te) {
        super(ip, te);
        this.tile = te;

        final IUpgradesInfoProvider tile = this.getUpgradeable();
        if (tile instanceof IBotaniaMechanicalDevice s) {
            this.type = s.getType();
        }

        final IItemHandler patternsHandler = this.getUpgradeable().getInventoryByName("patternsInternal");
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new RestrictedSlot(
                        this.type == BotaniaMechanicalDeviceType.ELVENTRADE ? RestrictedSlot.PlaceableItemType.ELVENTRADE_ENCODED_PATTERN
                                : this.type == BotaniaMechanicalDeviceType.MANAPOOL ? RestrictedSlot.PlaceableItemType.MANAPOOL_ENCODED_PATTERN
                                : this.type == BotaniaMechanicalDeviceType.PETAL ? RestrictedSlot.PlaceableItemType.PETAL_ENCODED_PATTERN
                                : this.type == BotaniaMechanicalDeviceType.PUREDAISY ? RestrictedSlot.PlaceableItemType.PUREDAISY_ENCODED_PATTERN
                                : this.type == BotaniaMechanicalDeviceType.RUNEALTAR ? RestrictedSlot.PlaceableItemType.RUNEALTAR_ENCODED_PATTERN
                                : RestrictedSlot.PlaceableItemType.TRASH,
                        patternsHandler,
                        x + y * 9,
                        8 + x * 18,
                        18 + y * 18,
                        this.getInventoryPlayer()).setStackLimit(1)
                );
            }
        }

        for (int u = 0; u < this.availableUpgrades(); u++) {
            this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlaceableItemType.UPGRADES, upgrades, u, 187, 8 + u * 18, this.getInventoryPlayer())).setStackLimit(1).setNotDraggable());
        }
    }

    @Override
    protected int getHeight() {
        return 197;
    }

    @Override
    protected void setupConfig() {}

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    public int availableUpgrades() {
        return 5;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            this.setRedStoneMode((RedstoneMode) this.getUpgradeable().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
        }

        this.standardDetectAndSendChanges();
    }
}
