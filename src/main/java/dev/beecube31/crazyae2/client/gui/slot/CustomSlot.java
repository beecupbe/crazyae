package dev.beecube31.crazyae2.client.gui.slot;

import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipObj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class CustomSlot extends Gui implements ITooltipObj {
    protected final int x;
    protected final int y;
    protected final int id;

    public CustomSlot(int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean canClick(EntityPlayer player) {
        return true;
    }

    public void slotClicked(ItemStack clickStack, int mouseButton) {}

    public abstract void drawContent(Minecraft var1, int var2, int var3, float var4);

    public void drawBackground(int guileft, int guitop) {}

    public String getTooltipMsg() {
        return null;
    }

    public int xPos() {
        return this.x;
    }

    public int yPos() {
        return this.y;
    }

    public int getWidth() {
        return 16;
    }

    public int getHeight() {
        return 16;
    }

    public boolean isVisible() {
        return false;
    }

    public boolean isSlotEnabled() {
        return true;
    }
}