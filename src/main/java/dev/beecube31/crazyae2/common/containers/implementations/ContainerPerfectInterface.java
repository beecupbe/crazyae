package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.config.Settings;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.SetStockAmountMenu;
import dev.beecube31.crazyae2.common.containers.slots.CrazyAESlot;
import dev.beecube31.crazyae2.common.containers.slots.FakeSlot;
import dev.beecube31.crazyae2.core.builder.ContainerTypeBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import static appeng.menu.implementations.InterfaceMenu.ACTION_OPEN_SET_AMOUNT;


public class ContainerPerfectInterface extends ContainerCrazyUpgradeable<InterfaceLogicHost> {

    public static final String ACTION_ADJUST_AMOUNT = "crazyaeAdjustAmount";

    public static final int OP_INCREASE = 0;
    public static final int OP_DECREASE = 1;
    public static final int OP_DOUBLE = 2;
    public static final int OP_HALVE = 3;

    public static final MenuType<ContainerPerfectInterface> TYPE = ContainerTypeBuilder
            .create(ContainerPerfectInterface::new, InterfaceLogicHost.class)
            .build("interface");

    public ContainerPerfectInterface(MenuType<? extends ContainerPerfectInterface> menuType, int id, Inventory ip, InterfaceLogicHost host) {
        super(menuType, id, ip, host);

        registerClientAction(ACTION_OPEN_SET_AMOUNT, Integer.class, this::openSetAmountMenu);
        registerClientAction(ACTION_ADJUST_AMOUNT, Integer.class, this::adjustAmount);

        var logic = host.getInterfaceLogic();

        var config = logic.getConfig().createMenuWrapper();
        for (int x = 0; x < config.size(); x++) {
            this.addSlot(new FakeSlot(config, x), SlotSemantics.CONFIG);
        }

        var storage = logic.getStorage().createMenuWrapper();
        for (int x = 0; x < storage.size(); x++) {
            this.addSlot(new CrazyAESlot(storage, x), SlotSemantics.STORAGE);
        }
    }

    public void adjustAmount(int configSlot, int op) {
        adjustAmount(configSlot * 4 + (op & 3));
    }

    private void adjustAmount(Integer packed) {
        if (isClientSide()) {
            sendClientAction(ACTION_ADJUST_AMOUNT, packed);
            return;
        }

        int configSlot = packed >> 2;
        int op = packed & 3;
        var config = getHost().getConfig();
        if (configSlot < 0 || configSlot >= config.size()) {
            return;
        }

        var stack = config.getStack(configSlot);
        if (stack == null) {
            return;
        }

        var what = stack.what();
        long amount = stack.amount();
        long max = config.getMaxAmount(what);

        long step = Math.max(1, what.getAmountPerUnit());

        long result = switch (op) {
            case OP_INCREASE -> Math.min(max, amount + step);
            case OP_DECREASE -> amount - step;
            case OP_DOUBLE -> Math.min(max, amount > max / 2 ? max : amount * 2);
            default -> amount / 2;
        };

        if (result <= 0 || result == amount) {
            return;
        }

        config.setStack(configSlot, new GenericStack(what, result));
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
    }

    public void openSetAmountMenu(int configSlot) {
        if (isClientSide()) {
            sendClientAction(ACTION_OPEN_SET_AMOUNT, configSlot);
        } else {
            var stack = getHost().getConfig().getStack(configSlot);
            if (stack != null) {
                SetStockAmountMenu.open((ServerPlayer) getPlayer(), getLocator(), configSlot,
                        stack.what(), (int) stack.amount());
            }
        }
    }
}
