package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.tile.inventory.AppEngInternalInventory;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;

public class ClientDCInternalInv implements Comparable<ClientDCInternalInv> {

    private final String unlocalizedName;
    private final AppEngInternalInventory inventory;

    private final long id;
    private final long sortBy;

    public ClientDCInternalInv(final int size, final long id, final long sortBy, final String unlocalizedName) {
        this.inventory = new AppEngInternalInventory(null, size, 1);
        this.unlocalizedName = unlocalizedName;
        this.id = id;
        this.sortBy = sortBy;
    }

    public ClientDCInternalInv(final int size, final long id, final long sortBy, final String unlocalizedName, int stackSize) {
        this.inventory = new AppEngInternalInventory(null, size, stackSize);
        this.unlocalizedName = unlocalizedName;
        this.id = id;
        this.sortBy = sortBy;
    }

    public String getName() {
        final String s = I18n.translateToLocal(this.unlocalizedName + ".name");
        if (s.equals(this.unlocalizedName + ".name")) {
            return I18n.translateToLocal(this.unlocalizedName);
        }
        return s;
    }

    @Override
    public int compareTo(@Nonnull final ClientDCInternalInv o) {
        return Long.compare(this.sortBy, o.sortBy);
    }

    public AppEngInternalInventory getInventory() {
        return this.inventory;
    }

    public long getId() {
        return this.id;
    }
}
