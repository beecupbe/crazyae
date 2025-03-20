package dev.beecube31.crazyae2.client.gui.slot;

import dev.beecube31.crazyae2.common.interfaces.gui.ITooltipIconsObj;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class SlotTooltip extends CustomSlot implements ITooltipIconsObj {
    private String text;
    private Map<ItemStack, Integer> tooltipIcons;

    public SlotTooltip(int id, int x, int y, String text, Map<ItemStack, Integer> tooltipIcons) {
        super(id, x, y);
        this.text = text;
        this.tooltipIcons = tooltipIcons;
    }

    public SlotTooltip setText(String text) {
        this.text = text;
        return this;
    }

    public SlotTooltip setTooltipIcons(Map<ItemStack, Integer> tooltipIcons) {
        this.tooltipIcons = tooltipIcons;
        return this;
    }


    @Override
    public void drawContent(Minecraft var1, int var2, int var3, float var4) {
        //display only tooltip
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public String getTooltipMsg() {
        return this.text;
    }

    @Override
    public Map<ItemStack, Integer> getTooltipIcons() {
        return this.tooltipIcons;
    }
}
