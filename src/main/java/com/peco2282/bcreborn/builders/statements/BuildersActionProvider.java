/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.builders.statements;

import com.peco2282.bcreborn.api.filler.FillerManager;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IActionProvider;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.builder.patterns.FillerPattern;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class BuildersActionProvider implements IActionProvider {
	private final HashMap<String, ActionFiller> actionMap = new HashMap<String, ActionFiller>();

	@Override
	public Collection<IActionInternal> getInternalActions(IStatementContainer container) {
		return null;
	}

	@Override
	public Collection<IActionExternal> getExternalActions(Direction side, BlockEntity tile) {
		LinkedList<IActionExternal> actions = new LinkedList<IActionExternal>();
		if (tile instanceof FillerBlockEntity) {
			for (IFillerPattern p : FillerManager.registry.getPatterns()) {
				if (p instanceof FillerPattern) {
					if (!actionMap.containsKey(p.getUniqueTag())) {
						actionMap.put(p.getUniqueTag(), new ActionFiller((FillerPattern) p));
					}
					actions.add(actionMap.get(p.getUniqueTag()));
				}
			}
		}
		return actions;
	}
}
