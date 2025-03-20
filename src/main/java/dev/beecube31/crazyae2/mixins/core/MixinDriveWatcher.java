package dev.beecube31.crazyae2.mixins.core;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.me.GridAccessException;
import appeng.me.storage.DriveWatcher;
import appeng.me.storage.MEInventoryHandler;
import appeng.tile.storage.TileDrive;
import dev.beecube31.crazyae2.common.items.cells.handlers.CreativeEnergyCellHandler;
import dev.beecube31.crazyae2.common.items.cells.handlers.CreativeManaCellHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collections;

@Mixin(value = DriveWatcher.class, remap = false, priority = 990)
public abstract class MixinDriveWatcher<T extends IAEStack<T>> extends MEInventoryHandler<T> {
    @Shadow public abstract int getStatus();

    @Shadow private int oldStatus;

    @Shadow @Final private TileDrive drive;

    @Shadow @Final private ICellHandler handler;

    @Shadow @Final private IActionSource source;

    public MixinDriveWatcher(IMEInventory<T> i, IStorageChannel<T> channel) {
        super(i, channel);
    }

    /**
     * @author Beecube31
     * @reason Support Creative Mana && Energy cells
     */
    @Overwrite
    @Override
    public T injectItems(final T input, final Actionable type, final IActionSource src) {
        final long size = input.getStackSize();

        final T remainder = super.injectItems(input, type, src);

        if (type == Actionable.MODULATE && (remainder == null || remainder.getStackSize() != size)) {
            final int newStatus = this.getStatus();

            if (newStatus != this.oldStatus) {
                this.drive.blinkCell(this.getSlot());
                this.oldStatus = newStatus;
            }
            if (this.drive.getProxy().isActive() && !(handler instanceof CreativeEnergyCellHandler || handler instanceof CreativeManaCellHandler)) {
                try {
                    this.drive.getProxy().getStorage().postAlterationOfStoredItems(this.getChannel(), Collections.singletonList(input.copy().setStackSize(input.getStackSize() - (remainder == null ? 0 : remainder.getStackSize()))), this.source);
                } catch (GridAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return remainder;
    }
}
