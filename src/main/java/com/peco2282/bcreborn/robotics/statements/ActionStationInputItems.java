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

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.core.statements.BCStatement;

public abstract class ActionStationInputItems extends BCStatement implements IActionInternal {
	public ActionStationInputItems(String name) {
		super(name);
	}

	@Override
	public void actionActivate(IStatementContainer source,
							   IStatementParameter[] parameters) {

	}
}
