package dev.beecube31.crazyae2.client.gui;

import net.minecraft.inventory.Container;

public class GuiNull extends CrazyAEBaseGui {

    public GuiNull(final Container container) {
        super(container);
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {}

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {}
}
