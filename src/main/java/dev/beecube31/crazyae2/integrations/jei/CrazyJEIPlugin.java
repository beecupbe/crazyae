package dev.beecube31.crazyae2.integrations.jei;

import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.CrazyStackWithBounds;
import dev.beecube31.crazyae2.core.CrazyAE;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@JeiPlugin
public class CrazyJEIPlugin implements IModPlugin {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(CrazyAE.MODID, "core");

    @Nullable
    private static IJeiRuntime jeiRuntime;

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(CrazyAEBaseGui.class,
                new IGuiContainerHandler<CrazyAEBaseGui<?>>() {
                    @Override
                    public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull CrazyAEBaseGui<?> screen) {
                        // Upgrades panel, toolbox, vertical toolbar, side tabs: everything outside the background
                        return screen.getExclusionZones();
                    }

                    @Override
                    public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(
                            @NotNull CrazyAEBaseGui<?> screen, double mouseX, double mouseY) {
                        // Lets R/U work on fluids and other generic stacks shown in config slots
                        var stackWithBounds = screen.getStackUnderMouse(mouseX, mouseY);
                        if (stackWithBounds != null) {
                            return makeClickableIngredient(stackWithBounds);
                        }
                        return Optional.empty();
                    }
                });

        registration.addGhostIngredientHandler(CrazyAEBaseGui.class, new CrazyGhostIngredientHandler());
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    @Override
    public void onRuntimeUnavailable() {
        jeiRuntime = null;
    }

    private static Optional<IClickableIngredient<?>> makeClickableIngredient(CrazyStackWithBounds stackWithBounds) {
        if (jeiRuntime == null) {
            return Optional.empty();
        }
        var ingredient = GenericEntryStackHelper.stackToIngredient(jeiRuntime.getIngredientManager(),
                stackWithBounds.stack());
        if (ingredient == null) {
            return Optional.empty();
        }
        return Optional.of(new ClickableIngredient<>(ingredient, stackWithBounds.bounds()));
    }

    private record ClickableIngredient<T>(ITypedIngredient<T> ingredient, Rect2i area)
            implements IClickableIngredient<T> {
        @Override
        public @NotNull ITypedIngredient<T> getTypedIngredient() {
            return ingredient;
        }

        @Override
        public @NotNull Rect2i getArea() {
            return area;
        }
    }
}
