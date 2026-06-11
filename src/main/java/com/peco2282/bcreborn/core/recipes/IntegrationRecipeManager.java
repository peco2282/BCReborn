package com.peco2282.bcreborn.core.recipes;

import com.peco2282.bcreborn.api.recipes.IIntegrationRecipe;
import com.peco2282.bcreborn.api.recipes.IIntegrationRecipeManager;

import java.util.LinkedList;
import java.util.List;

public class IntegrationRecipeManager implements IIntegrationRecipeManager {
	public static final IntegrationRecipeManager INSTANCE = new IntegrationRecipeManager();
	private List<IIntegrationRecipe> integrationRecipes = new LinkedList<IIntegrationRecipe>();

	@Override
	public void addRecipe(IIntegrationRecipe recipe) {
		integrationRecipes.add(recipe);
	}

	@Override
	public List<? extends IIntegrationRecipe> getRecipes() {
		return integrationRecipes;
	}
}
