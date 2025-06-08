package dev.beecube31.crazyae2.common.interfaces.craftsystem;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

public interface ICrazyCraftingProviderHelper {
    void addCraftingOption(ICrazyCraftingMethod var1, ICraftingPatternDetails var2);

    void setEmitable(IAEItemStack var1);
}
