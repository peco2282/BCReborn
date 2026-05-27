package com.peco2282.bcreborn.robotics.statements;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.ITriggerProvider;
import com.peco2282.bcreborn.robotics.RobotUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RobotsTriggerProvider implements ITriggerProvider {
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		LinkedList<ITriggerInternal> result = new LinkedList<ITriggerInternal>();
		List<DockingStation> stations = RobotUtils.getStations(container.getTile());

		if (stations.size() > 0) {
			result.add(RoboticsStatements.triggerRobotSleep);
			result.add(RoboticsStatements.triggerRobotInStation);
			result.add(RoboticsStatements.triggerRobotLinked);
			result.add(RoboticsStatements.triggerRobotReserved);
		}

		return result;
	}

	@Override
	public Collection<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity tile) {
		return null;
	}
}
