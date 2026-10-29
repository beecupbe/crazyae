package dev.beecube31.crazyae2.common.parts;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import dev.beecube31.crazyae2.common.definitions.CrazyItems;

public final class CrazyBusSpeed {
    public static int operations(IUpgradeInventory upgrades) {
        int ops = 256;
        switch (upgrades.getInstalledUpgrades(CrazyItems.STACKS_CARD.get())) {
            case 1 -> ops += 8 * 96;
            case 2 -> ops += 8 * 192;
            case 3 -> ops += 8 * 384;
            case 4 -> ops += 8 * 512;
            default -> {}
        }

        if (upgrades.getInstalledUpgrades(CrazyItems.IMPROVED_SPEED_CARD.get()) > 0) {
            ops += 512;
        }

        if (upgrades.getInstalledUpgrades(CrazyItems.ADVANCED_SPEED_CARD.get()) > 0) {
            ops += 4096;
        }

        return ops;
    }
}
