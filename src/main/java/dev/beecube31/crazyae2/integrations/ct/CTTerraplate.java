package dev.beecube31.crazyae2.integrations.ct;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.oredict.IOreDictEntry;
import dev.beecube31.crazyae2.common.recipes.botania.RecipeRepo;
import dev.beecube31.crazyae2.common.recipes.botania.RecipeTerraplate;
import dev.beecube31.crazyae2.common.util.Utils;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.crazyae.Agglomeration")
@ZenRegister
public class CTTerraplate {
	public static final String NAME = "CrazyAE Agglomeration";

	@ZenMethod
	public static void addRecipe(
			IItemStack output,
			IIngredient input1,
			IIngredient input2,
			IIngredient input3,
			int manaCost
	) {
		CraftTweakerPlugin.ACTIONS.add(new AddAction(buildAgglomerationRecipe(output, new IIngredient[]{input1, input2, input3}, manaCost)));
	}
	
	@ZenMethod
	public static void removeRecipe(
			IItemStack output,
			IIngredient input1,
			IIngredient input2,
			IIngredient input3,
			int manaCost
	) {
		CraftTweakerPlugin.ACTIONS.add(new RemoveAction(buildAgglomerationRecipe(output, new IIngredient[]{input1, input2, input3}, manaCost)));
	}

	@ZenClass("mods.crazyae.AgglomerationRecipe")
	@ZenRegister
	public static class CTAgglomerationRecipe {
		@ZenProperty
		public IItemStack output;
		@ZenProperty
		public IIngredient[] inputs;
		@ZenProperty
		public int manaCost;

		@ZenMethod
		public static CTAgglomerationRecipe create() {
			return new CTAgglomerationRecipe();
		}

		@ZenMethod
		public CTAgglomerationRecipe output(IItemStack output) {
			this.output = output;
			return this;
		}

		@ZenMethod
		public CTAgglomerationRecipe inputs(IIngredient input1, IIngredient input2, IIngredient input3) {
			this.inputs = new IIngredient[]{input1, input2, input3};
			return this;
		}

		@ZenMethod
		public CTAgglomerationRecipe manaCost(int manaCost) {
			this.manaCost = manaCost;
			return this;
		}

		RecipeTerraplate toAgglomerationRecipe() {
			return buildAgglomerationRecipe(output, inputs, manaCost);
		}
	}
	
	public static class AddAction implements IAction {
		RecipeTerraplate recipe;
		
		public AddAction(RecipeTerraplate recipe) {
			this.recipe = recipe;
		}
		
		@Override
		public void apply() {
			RecipeRepo.add(recipe);
		}
		
		@Override
		public String describe() {
			return "Adding an agglomeration recipe: " + recipe.toString();
		}
	}
	
	public static class RemoveAction implements IAction {
		RecipeTerraplate recipe;
		
		public RemoveAction(RecipeTerraplate recipe) {
			this.recipe = recipe;
		}
		
		@Override
		public void apply() {
			RecipeRepo.add(recipe);
		}
		
		@Override
		public String describe() {
			return "Removing an agglomeration recipe: " + recipe.toString();
		}
	}
	
	private static RecipeTerraplate buildAgglomerationRecipe(
					IItemStack output,
					IIngredient[] inputs,
					int manaCostIn
	) {
		Preconditions.checkNotNull(output, "Recipe output must be defined!");
		Preconditions.checkNotNull(inputs, "Recipe inputs must be defined!");

		ImmutableList<Object> ins = ImmutableList.copyOf(mtlibToObjects(inputs));
		ItemStack out = Utils.getItemStack(output);
		
		return new RecipeTerraplate(ins, out, manaCostIn);
	}

	public static Object[] mtlibToObjects(IIngredient[] ingredient) {
		if(ingredient == null)
			return null;
		else {
			Object[] output = new Object[ingredient.length];
			for(int i = 0; i < ingredient.length; i++) {
				if(ingredient[i] != null) {
					output[i] = mtlibToObject(ingredient[i]);
				} else
					output[i] = "";
			}
			
			return output;
		}
	}

	public static Object mtlibToObject(IIngredient iStack) {
		if(iStack == null)
			return null;
		else {
			if(iStack instanceof IOreDictEntry) {
				return ((IOreDictEntry) iStack).getName();
			} else if(iStack instanceof IItemStack) {
				return Utils.getItemStack((IItemStack) iStack);
			} else if(iStack instanceof IngredientStack) {
				return iStack.getItems();
			} else
				return null;
		}
	}
}
