package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.helpers.IPriorityHost;
import appeng.menu.ISubMenu;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.world.entity.player.Inventory;

public class ContainerCrazyPriority extends CrazyAEBaseContainer implements ISubMenu {

    private static final String ACTION_SET_PRIORITY = "setPriority";

    private final IPriorityHost host;

    private int priorityValue;

    public ContainerCrazyPriority(int id, Inventory ip, IPriorityHost host) {
        super(CrazyContainers.PRIORITY.get(), id, ip, host);
        this.host = host;
        this.priorityValue = host.getPriority();

        registerClientAction(ACTION_SET_PRIORITY, Integer.class, this::setPriority);
    }

    public void setPriority(int newValue) {
        if (newValue != priorityValue) {
            if (isClientSide()) {
                // If for whatever reason the client enters the value first, do not update based
                // on incoming server data
                this.priorityValue = newValue;
                sendClientAction(ACTION_SET_PRIORITY, newValue);
            } else {
                this.host.setPriority(newValue);
                this.priorityValue = newValue;
            }
        }
    }

    public void forcePriority(int newValue) {
        this.priorityValue = newValue;
    }

    public int getPriorityValue() {
        return priorityValue;
    }

    @Override
    public IPriorityHost getHost() {
        return this.host;
    }

}
