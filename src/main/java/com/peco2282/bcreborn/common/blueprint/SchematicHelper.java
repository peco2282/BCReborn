package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.ISchematicHelper;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import net.minecraft.world.item.ItemStack;

public final class SchematicHelper implements ISchematicHelper {
	public static final SchematicHelper INSTANCE = new SchematicHelper();

	private SchematicHelper() {

	}

	@Override
	public boolean isEqualItem(ItemStack a, ItemStack b) {
		return StackHelper.isEqualItem(a, b);
	}
}
