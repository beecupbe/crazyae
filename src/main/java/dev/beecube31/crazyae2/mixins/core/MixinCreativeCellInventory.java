package dev.beecube31.crazyae2.mixins.core;

import appeng.api.AEApi;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.items.contents.CellConfig;
import appeng.me.storage.CreativeCellInventory;
import appeng.util.item.AEItemStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreativeCellInventory.class, remap = false)
public abstract class MixinCreativeCellInventory implements IMEInventoryHandler<IAEItemStack> {

    @Mutable @Shadow @Final private IItemList<IAEItemStack> itemListCache;

    @Inject(
            method = "<init>",
            at = @At("RETURN"),
            remap = false
    )
    private void patchCreativeCell(ItemStack o, CallbackInfo ci) {
        final CellConfig cc = new CellConfig(o);
        this.itemListCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        for (final ItemStack is : cc) {
            if (!is.isEmpty()) {
                final IAEItemStack i = AEItemStack.fromItemStack(is);
                i.setStackSize(Long.MAX_VALUE);
                this.itemListCache.add(i);
            }
        }
    }
}
