package dev.beecube31.crazyae2.common.interfaces.mana;

public interface IManaLinkableDevice {
    void link(int x, int y, int z);

    int getLinkedPoolPosX();

    int getLinkedPoolPosY();

    int getLinkedPoolPosZ();

    boolean hasLinkedPool();
}
