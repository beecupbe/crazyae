package dev.beecube31.crazyae2.common.recipes.botania;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipe;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecipeTerraplate {

    public enum PlateType {
        TERRA_PLATE,
        GAIA_PLATE,
        GOD_AGGLOMERATION_PLATE
    }

    public final ImmutableList<ItemStack> recipeStacks;
    public final ImmutableList<String> recipeOreKeys;
    public final ItemStack recipeOutput;
    public final int manaCost;
    public final PlateType plateType;

    final int totalInputs;

    private void verifyInputs(ImmutableList<Object> inputs) {
        if(inputs.isEmpty()) throw new IllegalArgumentException("Can't make empty agglomeration recipe");

        for(Object o : inputs) {
            if(o instanceof ItemStack || o instanceof String) continue;
            throw new IllegalArgumentException("illegal recipe input " + o);
        }
    }

    public RecipeTerraplate(ImmutableList<Object> recipeInputs, ItemStack recipeOutput, int manaCost) {
        this(recipeInputs, recipeOutput, manaCost, PlateType.TERRA_PLATE);
    }

    public RecipeTerraplate(ImmutableList<Object> recipeInputs, ItemStack recipeOutput, int manaCost, PlateType plateType) {
        verifyInputs(recipeInputs);

        ImmutableList.Builder<ItemStack> stackInputBuilder = new ImmutableList.Builder<>();
        ImmutableList.Builder<String> keyInputBuilder = new ImmutableList.Builder<>();

        for(Object o : recipeInputs) {
            if(o instanceof ItemStack) stackInputBuilder.add((ItemStack) o);
            else keyInputBuilder.add((String) o);
        }

        this.recipeStacks = stackInputBuilder.build();
        this.recipeOreKeys = keyInputBuilder.build();
        this.totalInputs = recipeStacks.size() + recipeOreKeys.size();

        this.recipeOutput = recipeOutput;
        this.manaCost = manaCost;
        this.plateType = plateType;
    }

    public RecipeTerraplate(AgglomerationRecipe s) {
        this.recipeStacks = ImmutableList.copyOf(s.recipeStacks);
        this.recipeOreKeys = ImmutableList.copyOf(s.recipeOreKeys);
        this.totalInputs = recipeStacks.size() + recipeOreKeys.size();

        this.recipeOutput = s.getRecipeOutputCopy();
        this.manaCost = s.manaCost;
        this.plateType = PlateType.TERRA_PLATE;
    }

    public RecipeTerraplate(ItemStack recipeOutput, int manaCost) {
        this(recipeOutput, manaCost, PlateType.TERRA_PLATE);
    }

    public RecipeTerraplate(ItemStack recipeOutput, int manaCost, PlateType plateType) {
        this.recipeStacks = ImmutableList.of();
        this.recipeOreKeys = ImmutableList.of();
        this.totalInputs = 0;

        this.recipeOutput = recipeOutput;
        this.manaCost = manaCost;
        this.plateType = plateType;
    }

    public boolean matches(IItemHandler inv) {
        final List<ItemStack> userInputs = new ArrayList<>();
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                userInputs.add(stack);
            }
        }

        return itemsMatch(userInputs);
    }

    private boolean compareStacks(ItemStack recipe, ItemStack supplied) {
        if(recipe.isEmpty() || supplied.isEmpty()) return false;
        if(recipe.getItem() != supplied.getItem()) return false;
        if(recipe.getItemDamage() != supplied.getItemDamage()) return false;

        return isTagSubset(recipe.getTagCompound(), supplied.getTagCompound());
    }

    private static boolean isTagSubset(@Nullable NBTTagCompound recipeTag, @Nullable NBTTagCompound suppliedTag) {
        if (recipeTag == null || recipeTag.isEmpty()) return true;
        if (suppliedTag == null || suppliedTag.isEmpty()) return false;
        if (recipeTag.getKeySet().size() > suppliedTag.getKeySet().size()) return false;

        for (String key : suppliedTag.getKeySet()) {
            if (!recipeTag.hasKey(key)) continue;

            NBTBase suppliedEntry = suppliedTag.getTag(key);
            NBTBase recipeEntry = recipeTag.getTag(key);

            if (suppliedEntry instanceof NBTTagCompound && recipeEntry instanceof NBTTagCompound) {
                if (!isTagSubset((NBTTagCompound) recipeEntry, (NBTTagCompound) suppliedEntry)) return false;
            } else {
                if (!suppliedEntry.equals(recipeEntry)) return false;
            }
        }

        return true;
    }

    public boolean itemsMatch(List<ItemStack> userInputs) {
        if(userInputs.isEmpty() || userInputs.size() != totalInputs) return false;

        int usedRecipeStackCount = 0;
        int usedOreKeyCount = 0;
        boolean[] usedUserInputs = new boolean[userInputs.size()];

        for(ItemStack recipeStack : recipeStacks) {
            for(int i = 0; i < userInputs.size(); i++) {
                if(usedUserInputs[i]) continue;

                ItemStack userInputStack = userInputs.get(i);
                if(compareStacks(recipeStack, userInputStack) && recipeStack.getCount() == userInputStack.getCount()) {
                    usedRecipeStackCount++;
                    usedUserInputs[i] = true;
                }
            }
        }

        if(usedRecipeStackCount != recipeStacks.size()) return false;

        for(String key : recipeOreKeys) {
            List<ItemStack> matchingOres = OreDictionary.getOres(key);
            for(ItemStack oreStack : matchingOres) {
                for(int i = 0; i < userInputs.size(); i++) {
                    if(usedUserInputs[i]) continue;

                    ItemStack userInputStack = userInputs.get(i);
                    if(compareStacks(oreStack, userInputStack) && userInputStack.getCount() == 1) {
                        usedOreKeyCount++;
                        usedUserInputs[i] = true;
                    }
                }
            }
        }

        return usedOreKeyCount == recipeOreKeys.size();
    }

    public ImmutableList<ItemStack> getRecipeStacks() {
        return recipeStacks;
    }

    public ImmutableList<String> getRecipeOreKeys() {
        return recipeOreKeys;
    }

    public int getManaCost() {
        return manaCost;
    }

    public ItemStack getOutput() {
        return recipeOutput;
    }

    public PlateType getPlateType() {
        return this.plateType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RecipeTerraplate other)) return false;

        if (other.manaCost != manaCost) return false;
        if (!ItemStack.areItemStacksEqual(other.recipeOutput, recipeOutput)) return false;
        if (!new HashSet<>(other.recipeOreKeys).equals(new HashSet<>(recipeOreKeys))) return false;

        List<ItemStack> myStackCopy = new ArrayList<>(recipeStacks);
        for (ItemStack otherStack : other.recipeStacks) {
            myStackCopy.removeIf(stack -> ItemStack.areItemStacksEqual(stack, otherStack));
        }

        return myStackCopy.isEmpty();
    }
}
