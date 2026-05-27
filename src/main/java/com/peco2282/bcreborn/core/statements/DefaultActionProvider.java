/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.api.IControllable;
import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IActionProvider;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultActionProvider implements IActionProvider {

	// These should be initialized in a central place like BuildCraftCore
	public static IActionInternal actionRedstone = new ActionRedstoneOutput();
	public static IActionExternal[] actionControl;

	static {
		actionControl = new IActionExternal[IControllable.Mode.values().length];
		for (IControllable.Mode mode : IControllable.Mode.values()) {
			if (mode != IControllable.Mode.Unknown) {
				actionControl[mode.ordinal()] = new ActionMachineControl(mode);
			}
		}
	}

	@Override
	public Collection<IActionInternal> getInternalActions(IStatementContainer container) {
		LinkedList<IActionInternal> res = new LinkedList<IActionInternal>();

		if (container instanceof IRedstoneStatementContainer) {
			res.add(actionRedstone);
		}

		return res;
	}

	@Override
	public Collection<IActionExternal> getExternalActions(Direction side, BlockEntity tile) {
		LinkedList<IActionExternal> res = new LinkedList<IActionExternal>();

		if (tile instanceof IControllable) {
			for (IControllable.Mode mode : IControllable.Mode.values()) {
				if (mode != IControllable.Mode.Unknown &&
						((IControllable) tile).acceptsControlMode(mode)) {
					res.add(actionControl[mode.ordinal()]);
				}
			}
		}

		return res;
	}
}
