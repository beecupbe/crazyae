package dev.beecube31.crazyae2.common.networking.events;

import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkEvent;

public class MECraftHostStateUpdateEv extends MENetworkEvent {
    public final IGridNode node;

    public MECraftHostStateUpdateEv(IGridNode n) {
        this.node = n;
    }
}

