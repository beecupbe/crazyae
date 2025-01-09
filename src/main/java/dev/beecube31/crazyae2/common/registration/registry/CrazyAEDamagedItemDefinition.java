package dev.beecube31.crazyae2.common.registration.registry;

import appeng.api.definitions.IItemDefinition;
import appeng.core.features.IStackSrc;
import com.google.common.base.Preconditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CrazyAEDamagedItemDefinition implements IItemDefinition {
    private final String identifier;
    private final Optional<IStackSrc> source;

    public CrazyAEDamagedItemDefinition(@Nonnull final String identifier, @Nonnull final IStackSrc source) {
        this.identifier = Preconditions.checkNotNull(identifier);
        Preconditions.checkNotNull(source);

        if (source.isEnabled()) {
            this.source = Optional.of(source);
        } else {
            this.source = Optional.empty();
        }
    }

    @Nonnull
    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public Optional<Item> maybeItem() {
        return this.source.map(IStackSrc::getItem);
    }

    @Override
    public Optional<ItemStack> maybeStack(final int stackSize) {
        return this.source.map(input -> input.stack(stackSize));
    }

    @Override
    public boolean isEnabled() {
        return this.source.isPresent();
    }

    @Override
    public boolean isSameAs(final ItemStack comparableStack) {
        if (comparableStack.isEmpty()) {
            return false;
        }

        return this.isEnabled() && comparableStack.getItem() == this.source.get().getItem() && comparableStack.getItemDamage() == this.source.get().getDamage();
    }
}

