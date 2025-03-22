package dev.beecube31.crazyae2.common.recipes.botania;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import quaternary.botaniatweaks.modules.botania.recipe.AgglomerationRecipe;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecipeTerraplate {

    public final ImmutableList<ItemStack> recipeStacks;
    public final ImmutableList<String> recipeOreKeys;
    public final ItemStack recipeOutput;
    public final int manaCost;

    final int totalInputs;

    private void verifyInputs(ImmutableList<Object> inputs) {
        if(inputs.isEmpty()) throw new IllegalArgumentException("Can't make empty agglomeration recipe");

        for(Object o : inputs) {
            if(o instanceof ItemStack || o instanceof String) continue;
            throw new IllegalArgumentException("illegal recipe input " + o);
        }
    }

    public RecipeTerraplate(ImmutableList<Object> recipeInputs, ItemStack recipeOutput, int manaCost) {
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
    }

    public RecipeTerraplate(AgglomerationRecipe s) {
        ImmutableList.Builder<ItemStack> stackInputBuilder = new ImmutableList.Builder<>();
        ImmutableList.Builder<String> keyInputBuilder = new ImmutableList.Builder<>();

        for(Object o : s.recipeStacks) {
            if(o instanceof ItemStack) stackInputBuilder.add((ItemStack) o);
            else keyInputBuilder.add((String) o);
        }

        this.recipeStacks = stackInputBuilder.build();
        this.recipeOreKeys = keyInputBuilder.build();
        this.totalInputs = recipeStacks.size() + recipeOreKeys.size();

        this.recipeOutput = s.recipeOutput;
        this.manaCost = s.manaCost;
    }

    public boolean matches(IItemHandler inv) {
        List<Object> inputsMissing = new ArrayList<>(this.recipeStacks);

        for(int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(stack.isEmpty())
                break;

            int stackIndex = -1, oredictIndex = -1;

            for(int j = 0; j < inputsMissing.size(); j++) {
                Object input = inputsMissing.get(j);
                if(input instanceof String) {
                    boolean found = false;
                    for(ItemStack ostack : OreDictionary.getOres((String) input, false)) {
                        if(OreDictionary.itemMatches(ostack, stack, false)) {
                            oredictIndex = j;
                            found = true;
                            break;
                        }
                    }


                    if(found) break;
                } else if(input instanceof ItemStack && compareStacks((ItemStack) input, stack)) {
                    stackIndex = j;
                    break;
                }
            }

            if(stackIndex != -1)
                inputsMissing.remove(stackIndex);
            else if(oredictIndex != -1)
                inputsMissing.remove(oredictIndex);
            else return false;
        }

        return inputsMissing.isEmpty();
    }

    private boolean compareStacks(ItemStack recipe, ItemStack supplied) {
        return recipe.getItem() == supplied.getItem() && recipe.getItemDamage() == supplied.getItemDamage() && ItemNBTHelper.matchTag(recipe.getTagCompound(), supplied.getTagCompound());
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
