package com.peco2282.bcreborn.core.recipes;
import com.peco2282.bcreborn.api.recipes.IFlexibleRecipe;
import com.peco2282.bcreborn.api.recipes.IRecipeManager;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collection;
import java.util.Collections;

public class RecipeManager<T> implements IRecipeManager<T> {
	private BiMap<String, IFlexibleRecipe<T>> recipes = HashBiMap.create();

	@Override
	public void addRecipe(String id, int energyCost, T output, Object... input) {
		addRecipe(id, energyCost, 0, output, input);
	}

	@Override
	public void addRecipe(String id, int energyCost, int craftingDelay, T output, Object... input) {
		recipes.put(id, new FlexibleRecipe<T>(id, output, energyCost, craftingDelay, input));
	}

	@Override
	public void addRecipe(IFlexibleRecipe<T> recipe) {
		recipes.put(recipe.getId(), recipe);
	}

	@Override
	public void removeRecipe(String id) {
		recipes.remove(id);
	}

	@Override
	public void removeRecipe(IFlexibleRecipe<T> recipe) {
		recipes.remove(recipes.inverse().get(recipe));
	}

	@Override
	public Collection<IFlexibleRecipe<T>> getRecipes() {
		return Collections.unmodifiableCollection(recipes.values());
	}

	@Override
	public IFlexibleRecipe<T> getRecipe(String id) {
		return recipes.get(id);
	}
}
