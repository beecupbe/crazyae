package dev.beecube31.crazyae2.craftsystem;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.storage.data.IAEItemStack;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import net.minecraft.nbt.NBTTagCompound;

public class CrazyCraftingLink implements ICraftingLink {

    private final ICrazyCraftHost req;
    private final String CraftID;
    private final boolean standalone;
    private boolean canceled = false;
    private boolean done = false;
    private CrazyCraftingLinkNexus tie;

    public CrazyCraftingLink(final NBTTagCompound data, final ICrazyCraftHost req) {
        this.CraftID = data.getString("CraftID");
        this.setCanceled(data.getBoolean("canceled"));
        this.setDone(data.getBoolean("done"));
        this.standalone = data.getBoolean("standalone");

        this.req = req;
    }

    @Override
    public boolean isCanceled() {
        if (this.canceled) {
            return true;
        }

        if (this.done) {
            return false;
        }

        if (this.tie == null) {
            return false;
        }

        return this.tie.isCanceled();
    }

    @Override
    public boolean isDone() {
        if (this.done) {
            return true;
        }

        if (this.canceled) {
            return false;
        }

        if (this.tie == null) {
            return false;
        }

        return this.tie.isDone();
    }

    @Override
    public void cancel() {
        if (this.done) {
            return;
        }

        this.setCanceled(true);

        if (this.tie != null) {
            this.tie.cancel();
        }

        this.tie = null;
    }

    @Override
    public boolean isStandalone() {
        return this.standalone;
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag) {
        tag.setString("CraftID", this.CraftID);
        tag.setBoolean("canceled", this.isCanceled());
        tag.setBoolean("done", this.isDone());
        tag.setBoolean("standalone", this.standalone);
        tag.setBoolean("req", this.getRequester() != null);
    }

    @Override
    public String getCraftingID() {
        return this.CraftID;
    }

    public void setNexus(final CrazyCraftingLinkNexus n) {
        if (this.tie != null) {
            this.tie.remove(this);
        }

        if (this.isCanceled() && n != null) {
            n.cancel();
            this.tie = null;
            return;
        }

        this.tie = n;

        if (n != null) {
            n.add(this);
        }
    }

    public IAEItemStack injectItems(final IAEItemStack input, final Actionable mode) {
        if (this.tie == null || this.tie.getRequest() == null || this.tie.getRequest().getRequester() == null) {
            return input;
        }

        return this.tie.getRequest().getRequester().injectCraftedItems(this.tie.getRequest(), input, mode);
    }

    public void markDone() {
        if (this.tie != null) {
            this.tie.markDone();
        }
    }

    void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

    ICrazyCraftHost getRequester() {
        return this.req;
    }

    void setDone(final boolean done) {
        this.done = done;
    }
}
