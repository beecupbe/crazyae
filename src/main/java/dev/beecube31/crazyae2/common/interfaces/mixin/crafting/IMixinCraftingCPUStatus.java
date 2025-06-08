package dev.beecube31.crazyae2.common.interfaces.mixin.crafting;

public interface IMixinCraftingCPUStatus {
    long crazyae$whenJobStarted();

    void crazyae$setWhenJobStarted(long when);

    String crazyae$jobInitiator();

    void crazyae$setJobInitiator(String player);
}
