/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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
