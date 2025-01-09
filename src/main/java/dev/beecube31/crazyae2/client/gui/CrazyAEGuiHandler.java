package dev.beecube31.crazyae2.client.gui;

import dev.beecube31.crazyae2.client.gui.implementations.GuiCrazyAEUpgradeable;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.interfaces.jei.IJEIGhostIngredients;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CrazyAEGuiHandler implements IAdvancedGuiHandler<CrazyAEBaseGui>, IGhostIngredientHandler<CrazyAEBaseGui> {
    @Override
    @Nonnull
    public Class<CrazyAEBaseGui> getGuiContainerClass() {
        return CrazyAEBaseGui.class;
    }

    @Nullable
    @Override
    public List<Rectangle> getGuiExtraAreas(@Nonnull CrazyAEBaseGui guiContainer) {
        return guiContainer.getJEIExclusionArea();
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(@Nonnull CrazyAEBaseGui guiContainer, int mouseX, int mouseY) {
        return null;
    }

    @Override
    @Nonnull
    public <I> List<Target<I>> getTargets(@Nonnull CrazyAEBaseGui gui, @Nonnull I ingredient, boolean doStart) {
        ArrayList<Target<I>> targets = new ArrayList<>();
        if (gui instanceof IJEIGhostIngredients g) {
            List<Target<?>> phantomTargets = g.getPhantomTargets(ingredient);
            targets.addAll((List<Target<I>>) (Object) phantomTargets);
        }
        if (doStart && GuiScreen.isShiftKeyDown() && Mouse.isButtonDown(0)) {
            if (gui instanceof GuiCrazyAEUpgradeable ghostGui) {
                for (Target<I> target : targets) {
                    if (ghostGui.getFakeSlotTargetMap().get(target) instanceof SlotFake) {
                        if (((SlotFake) ghostGui.getFakeSlotTargetMap().get(target)).getStack().isEmpty()) {
                            target.accept(ingredient);
                            break;
                        }
                    }
                }
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }

    @Override
    public boolean shouldHighlightTargets() {
        return true;
    }

}
