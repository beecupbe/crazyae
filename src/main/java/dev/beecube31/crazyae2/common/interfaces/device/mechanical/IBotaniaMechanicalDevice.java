package dev.beecube31.crazyae2.common.interfaces.device.mechanical;

import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;

import java.util.List;

public interface IBotaniaMechanicalDevice {
    BotaniaMechanicalDeviceType getType();

    TileBotaniaMechanicalMachineBase getMechanicalTile();

    List<TileBotaniaMechanicalMachineBase.CraftingTask> getQueueMap();

    int getTasksQueued();

    int getTasksMaxAmt();

    int getProgressPerTick();

    int getItemsPerTick();
}
