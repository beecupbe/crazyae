package dev.beecube31.crazyae2.client.gui;

import appeng.client.gui.style.StyleManager;
import appeng.menu.SlotSemantic;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class CrazyAESubGui<C extends CrazyAEBaseContainer, P extends CrazyAEBaseGui<C>> extends CrazyAEBaseGui<C> {
    private final P parent;
    /**
     * Keeps track of any client-side slots added by this sub-screen to clean them up when we return to the parent.
     */
    private final List<Slot> clientSideSlots = new ArrayList<>();

    public CrazyAESubGui(P parent, String stylePath) {
        super(parent.getMenu(),
                parent.getMenu().getPlayerInventory(),
                parent.getTitle(),
                StyleManager.loadStyleDoc(stylePath));
        this.parent = parent;
    }

    public P getParent() {
        return parent;
    }

    protected final void returnToParent() {
        // Remove any client-side slots added by this screen
        for (var clientSideSlot : clientSideSlots) {
            menu.removeClientSideSlot(clientSideSlot);
        }
        clientSideSlots.clear();

        onReturnToParent();
        var parent = getParent();
        switchToScreen(parent);
        parent.onReturnFromSubScreen(this);
    }

    protected void onReturnToParent() {}

    protected final Slot addClientSideSlot(Slot slot, SlotSemantic semantic) {
        clientSideSlots.add(slot);
        return menu.addClientSideSlot(slot, semantic);
    }
}
