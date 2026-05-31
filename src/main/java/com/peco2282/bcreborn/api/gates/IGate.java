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
package com.peco2282.bcreborn.api.gates;

import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.statements.containers.ISidedStatementContainer;
import com.peco2282.bcreborn.api.transport.IPipe;

import java.util.List;

public interface IGate extends ISidedStatementContainer {
    @Deprecated
    void setPulsing(boolean pulse);

    IPipe getPipe();

    List<IStatement> getTriggers();

    List<IStatement> getActions();

    List<StatementSlot> getActiveActions();

    List<IStatementParameter> getTriggerParameters(int index);

    List<IStatementParameter> getActionParameters(int index);
}
