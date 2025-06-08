package dev.beecube31.crazyae2.craftsystem;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;
import dev.beecube31.crazyae2.core.cache.impl.CrazyAutocraftingSystem;

public class CrazyCraftingLinkNexus {

    private final String craftID;
    private boolean canceled = false;
    private boolean done = false;
    private int tickOfDeath = 0;
    private CrazyCraftingLink req;

    public CrazyCraftingLinkNexus(final String craftID) {
        this.craftID = craftID;
    }

    public boolean isDead(final IGrid g, final CrazyAutocraftingSystem sys) {
        if (this.canceled || this.done) {
            return true;
        }

        CrazyCraftingLink req = this.getRequest();
        if (req == null) {
            this.tickOfDeath++;
        } else {
            final boolean valid = isRequesterValid(this.req);
            final ICrazyCraftHost requester = req.getRequester();
            final IGridNode actionableNode = requester != null ? requester.getNode() : null;
            final IGrid grid = actionableNode != null ? actionableNode.getGrid() : null;
            final boolean hasMachine = grid != null && grid == g;

            if (valid && hasMachine) {
                this.tickOfDeath = 0;
            } else {
                this.tickOfDeath += 60;
            }
        }

        if (this.tickOfDeath > 60) {
            this.cancel();
            return true;
        }

        return false;
    }

    void cancel() {
        this.canceled = true;

        if (this.getRequest() != null) {
            this.getRequest().setCanceled(true);
            if (this.getRequest().getRequester() != null) {
                this.getRequest().getRequester().jobStateChange(this.getRequest());
            }
        }

        if (this.req != null) {
            this.req.setCanceled(true);
        }
    }

    void remove(final CrazyCraftingLink craftingLink) {
        if (this.getRequest() == craftingLink) {
            this.setRequest(null);
        } else if (this.req == craftingLink) {
            this.req = null;
        }
    }

    void add(final CrazyCraftingLink craftingLink) {
        if (isRequesterValid(craftingLink)) {
            this.req = craftingLink;
        } else if (craftingLink.getRequester() != null) {
            this.setRequest(craftingLink);
        }
    }

    boolean isCanceled() {
        return this.canceled;
    }

    boolean isDone() {
        return this.done;
    }

    void markDone() {
        this.done = true;

        if (this.getRequest() != null) {
            this.getRequest().setDone(true);
            if (this.getRequest().getRequester() != null) {
                this.getRequest().getRequester().jobStateChange(this.getRequest());
            }
        }

        if (this.req != null) {
            this.req.setDone(true);
        }
    }

    public static boolean isRequesterValid(CrazyCraftingLink req) {
        return req.getRequester().getStorageCount() > 0;
    }

    public void removeNode() {
        if (this.getRequest() != null) {
            this.getRequest().setNexus(null);
        }

        this.setRequest(null);
        this.tickOfDeath = 0;
    }

    public CrazyCraftingLink getRequest() {
        return this.req;
    }

    public void setRequest(final CrazyCraftingLink req) {
        this.req = req;
    }
}
