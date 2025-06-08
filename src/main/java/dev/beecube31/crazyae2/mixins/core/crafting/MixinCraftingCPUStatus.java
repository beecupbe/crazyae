package dev.beecube31.crazyae2.mixins.core.crafting;

import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.CraftingCPUStatus;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinCraftingCPUStatus;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingCPUStatus.class, remap = false)
public abstract class MixinCraftingCPUStatus implements IMixinCraftingCPUStatus {
    @Shadow public abstract long getStorage();

    @Shadow @Final private String name;

    @Shadow @Final private IAEItemStack crafting;

    @Shadow @Final private int serial;

    @Mutable @Shadow @Final private long storage;

    @Shadow @Final private long totalItems;

    @Shadow @Final private long remainingItems;

    @Unique private long crazyae$accelerators;

    @Unique private long crazyae$millisJobStarted;

    @Unique private String crazyae$jobInitiator;


    @Override
    public long crazyae$whenJobStarted() {
        return this.crazyae$millisJobStarted;
    }

    @Override
    public void crazyae$setWhenJobStarted(long when) {
        this.crazyae$millisJobStarted = when;
    }

    @Override
    public String crazyae$jobInitiator() {
        return this.crazyae$jobInitiator;
    }

    @Override
    public void crazyae$setJobInitiator(String player) {
        this.crazyae$jobInitiator = player;
    }


    @Inject(
            method = "<init>(Lappeng/api/networking/crafting/ICraftingCPU;I)V",
            at = @At("RETURN"),
            remap = false
    )
    private void crazyae$setOwnCount(ICraftingCPU cluster, int serial, CallbackInfo ci) {
        if (cluster instanceof ICrazyCraftHost r) {
            this.storage = r.getAvailableStorage();
            this.crazyae$accelerators = r.getAcceleratorCount();
        } else if (cluster instanceof TileCraftingUnitsCombiner r) {
            this.storage = r.getStorageAmt();
            this.crazyae$accelerators = r.getAcceleratorAmt();
        } else {
            this.storage = cluster.getAvailableStorage();
            this.crazyae$accelerators = cluster.getCoProcessors();
        }
    }

    @Inject(
            method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void crazyae$setOwnCountFromNBT(NBTTagCompound i, CallbackInfo ci) {
        this.crazyae$accelerators = i.getLong("coprocessors");
        this.crazyae$jobInitiator = i.getString("jobInitiator");
        this.crazyae$millisJobStarted = i.getLong("millisJobStarted");
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    public void writeToNBT(NBTTagCompound i) {
        if (name != null && !name.isEmpty()) {
            i.setString("name", name);
        }
        i.setInteger("serial", serial);
        i.setLong("storage", storage);
        i.setLong("coprocessors", this.crazyae$accelerators);
        i.setLong("totalItems", totalItems);
        i.setLong("remainingItems", remainingItems);
        if (this.crazyae$jobInitiator != null && !this.crazyae$jobInitiator.isEmpty()) {
            i.setString("jobInitiator", this.crazyae$jobInitiator);
        }
        if (this.crazyae$millisJobStarted > 0) {
            i.setLong("millisJobStarted", this.crazyae$millisJobStarted);
        }
        if (crafting != null) {
            NBTTagCompound stack = new NBTTagCompound();
            crafting.writeToNBT(stack);
            i.setTag("crafting", stack);
        }
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    public long getCoprocessors() {
        return this.crazyae$accelerators;
    }
}
