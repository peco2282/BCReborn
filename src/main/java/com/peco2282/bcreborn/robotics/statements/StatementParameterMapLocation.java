/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.items.IMapLocation;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import net.minecraft.world.item.ItemStack;

public class StatementParameterMapLocation extends StatementParameterItemStack {

	public StatementParameterMapLocation() {
		super(ItemStack.EMPTY);
	}

	@Override
	public String getUniqueTag() {
		return "maplocation";
	}

	@Override
	public void onClick(IStatementContainer source, IStatement stmt, ItemStack stackIn, StatementMouseClick mouse) {
		ItemStack stack = stackIn;
		if (stack != null && !stack.isEmpty() && !(stack.getItem() instanceof IMapLocation)) {
			stack = ItemStack.EMPTY;
		}
		super.onClick(source, stmt, stack, mouse);
	}
}
