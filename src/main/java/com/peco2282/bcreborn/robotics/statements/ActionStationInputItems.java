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
