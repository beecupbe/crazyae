package dev.beecube31.crazyae2.common.networking.events;

import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkEvent;
import dev.beecube31.crazyae2.common.interfaces.craftsystem.ICrazyCraftHost;

public class MECraftHostPatternsChangedEv extends MENetworkEvent {
    public final ICrazyCraftHost provider;
    public final IGridNode node;

    public MECraftHostPatternsChangedEv(ICrazyCraftHost p, IGridNode n) {
        this.provider = p;
        this.node = n;
    }
}
