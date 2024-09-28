package dev.beecube31.crazyae2.common.containers;


import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.container.slot.SlotRestrictedInput;
import appeng.items.misc.ItemEncodedPattern;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.containers.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.tile.crafting.TileImprovedMAC;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;


public class ContainerFastMAC extends ContainerCrazyAEUpgradeable {

    public ContainerFastMAC(final InventoryPlayer ip, final TileImprovedMAC te) {
        super(ip, te);
    }

    public boolean isValidItemForSlot(final int slotIndex, final ItemStack i) {
        final IItemHandler mac = this.getUpgradeable().getInventoryByName("mac");

        final ItemStack is = mac.getStackInSlot(10);
        if (is.isEmpty()) {
            return false;
        }

        if (is.getItem() instanceof final ItemEncodedPattern iep) {
            final World w = this.getTileEntity().getWorld();
            final ICraftingPatternDetails ph = iep.getPatternForItem(is, w);
            if (ph.isCraftable()) {
                return ph.isValidItemForSlot(slotIndex, i, w);
            }
        }

        return false;
    }

    @Override
    protected int getHeight() {
        return 197;
    }

    @Override
    protected void setupConfig() {
        final IItemHandler patternsHandler = this.getUpgradeable().getInventoryByName("patterns");
        final IItemHandler upgrades = this.getUpgradeable().getInventoryByName("upgrades");

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.ENCODED_CRAFTING_PATTERN, patternsHandler, x + y * 9, 8 + x * 18, 18 + y * 18, this.getInventoryPlayer()).setStackLimit(1));
            }
        }

        for (int u = 0; u < this.availableUpgrades(); u++) {
            this.addSlotToContainer((new RestrictedSlot(RestrictedSlot.PlacableItemType.UPGRADES, upgrades, u, 187, 8 + u * 18, this.getInventoryPlayer())).setNotDraggable());
        }
    }

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
