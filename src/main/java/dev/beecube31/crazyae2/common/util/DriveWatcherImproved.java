package dev.beecube31.crazyae2.common.util;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.data.IAEStack;
import appeng.core.features.registries.cell.CreativeCellHandler;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.me.storage.MEInventoryHandler;
import dev.beecube31.crazyae2.common.tile.storage.TileImprovedDrive;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class DriveWatcherImproved<T extends IAEStack<T>> extends MEInventoryHandler<T> {

    private int oldStatus = 0;
    private final ItemStack is;
    private final ICellHandler handler;
    private final TileImprovedDrive drive;
    private final IActionSource source;

    public DriveWatcherImproved(final ICellInventoryHandler<T> i, final ItemStack is, final ICellHandler han, final TileImprovedDrive drive) {
        super(i, i.getChannel());
        this.is = is;
        this.handler = han;
        this.drive = drive;
        this.source = new MachineSource(drive);
    }

    public int getStatus() {
        return this.handler.getStatusForCell(this.is, (ICellInventoryHandler) this.getInternal());
    }

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
            if (this.drive.getProxy().isActive() && !(handler instanceof CreativeCellHandler)) {
                try {
                    this.drive.getProxy().getStorage().postAlterationOfStoredItems(this.getChannel(), Collections.singletonList(input.copy().setStackSize(input.getStackSize() - (remainder == null ? 0 : remainder.getStackSize()))), this.source);
                } catch (GridAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return remainder;
    }

    @Override
    public T extractItems(final T request, final Actionable type, final IActionSource src) {
        final T extractable = super.extractItems(request, type, src);

        if (type == Actionable.MODULATE && extractable != null) {
            final int newStatus = this.getStatus();

            if (newStatus != this.oldStatus) {
                this.drive.blinkCell(this.getSlot());
                this.oldStatus = newStatus;
            }
            if (this.drive.getProxy().isActive() && !(handler instanceof CreativeCellHandler)) {
                try {
                    this.drive.getProxy().getStorage().postAlterationOfStoredItems(this.getChannel(), Collections.singletonList(request.copy().setStackSize(-extractable.getStackSize())), this.source);
                } catch (GridAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return extractable;
    }
}
