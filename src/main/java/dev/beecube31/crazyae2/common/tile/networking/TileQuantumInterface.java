package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IMaterials;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.GuiBridge;
import appeng.items.misc.ItemEncodedPattern;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.misc.TileInterface;
import appeng.util.Platform;
import appeng.util.SettingsFrom;
import dev.beecube31.crazyae2.common.duality.QuantumInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftingProviderHelper;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyInterfaceHost;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostExtender;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostGuiOverrider;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class TileQuantumInterface extends TileInterface implements IPriHostGuiOverrider, IPriHostExtender, IUpgradesInfoProvider, ICrazyInterfaceHost {

    private final QuantumInterfaceDuality myDuality;

    public TileQuantumInterface() {
        ObfuscationReflectionHelper.setPrivateValue(
                TileInterface.class,
                this,
                new QuantumInterfaceDuality(this.getProxy(), this),
                "duality"
        );

        this.myDuality = ObfuscationReflectionHelper.getPrivateValue(
                TileInterface.class,
                this,
                "duality"
        );
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.GUI_Handler; //stub, use getOverrideGui();
    }

    @Override
    public CrazyAEGuiBridge getOverrideGui() {
        return CrazyAEGuiBridge.QUANTUM_INTERFACE;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return CrazyAE.definitions().blocks().patternsInterface().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public void onReady() {
        super.onReady();
        this.myDuality.onReady();
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().quantumInterface();
    }

    @Override
    public int getConfigSlots() {
        return 0;
    }

    @Override
    public int getStorageSlots() {
        return 9;
    }

    @Override
    public int getPatternsSlots() {
        return QuantumInterfaceDuality.NUMBER_OF_PATTERN_SLOTS;
    }

    @Override
    public boolean pushDetails(ICraftingPatternDetails details, long batchSize, ICrazyCraftHost who) {
        return this.myDuality.pushDetails(details, batchSize, who);
    }

    @Override
    public void cancelCraftingForPattern(ICraftingPatternDetails details, ICrazyCraftHost requestingCpu) {
        this.myDuality.cancelCraftingForPattern(details, requestingCpu);
    }

    @Override
    public void tickInterfaceHost(IGrid grid, CrazyAutocraftingSystem cache) {
        this.myDuality.tickInterfaceHost(grid, cache);
    }

    @Override
    public boolean canAcceptPattern(ICraftingPatternDetails details) {
        return this.myDuality.canAcceptPattern(details);
    }

    @Override
    public IGridNode getNode() {
        return this.getProxy().getNode();
    }

    @Override
    public long estimatePushableBatchSize(ICraftingPatternDetails details, long desiredBatchSize, ICrazyCraftHost requestingCpu, World world) {
        return this.myDuality.estimatePushableBatchSize(details, desiredBatchSize, requestingCpu, world);
    }

    @Override
    public void provideCrafting(ICrazyCraftingProviderHelper var1) {
        this.myDuality.provideCrafting(var1);
    }

    @Override
    public void uploadSettings(SettingsFrom from, NBTTagCompound compound, EntityPlayer player) {
        super.uploadSettings(from, compound, player);
        final IItemHandler inv = this.getInventoryByName("patterns");
        if (inv instanceof AppEngInternalInventory target) {
            AppEngInternalInventory tmp = new AppEngInternalInventory(null, target.getSlots());
            tmp.readFromNBT(compound, "patterns");
            PlayerMainInvWrapper playerInv = new PlayerMainInvWrapper(player.inventory);
            final IMaterials materials = AEApi.instance().definitions().materials();
            int missingPatternsToEncode = 0;

            for (int i = 0; i < inv.getSlots(); i++) {
                if (target.getStackInSlot(i).getItem() instanceof ItemEncodedPattern) {
                    ItemStack blank = materials.blankPattern().maybeStack(target.getStackInSlot(i).getCount()).get();
                    if (!player.addItemStackToInventory(blank)) {
                        player.dropItem(blank, true);
                    }
                    target.setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            for (int x = 0; x < inv.getSlots(); x++) {
                if (!tmp.getStackInSlot(x).isEmpty()) {
                    boolean found = false;
                    for (int i = 0; i < playerInv.getSlots(); i++) {
                        if (materials.blankPattern().isSameAs(playerInv.getStackInSlot(i))) {
                            target.setStackInSlot(x, tmp.getStackInSlot(x));
                            playerInv.getStackInSlot(i).shrink(1);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missingPatternsToEncode++;
                    }
                }
            }

            if (Platform.isServer() && missingPatternsToEncode > 0) {
                player.sendMessage(PlayerMessages.MissingPatternsToEncode.get());
            }
        }
    }
}
