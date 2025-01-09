package dev.beecube31.crazyae2.common.interfaces.upgrades;

import appeng.api.definitions.IItemDefinition;
import appeng.api.implementations.IUpgradeableHost;

public interface IUpgradesInfoProvider extends IUpgradeableHost {
    IItemDefinition getBlock();
}
