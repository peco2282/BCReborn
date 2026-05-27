package com.peco2282.bcreborn.robotics.statements;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IActionProvider;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.robotics.RobotUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RobotsActionProvider implements IActionProvider {

	@Override
	public Collection<IActionInternal> getInternalActions(IStatementContainer container) {
		LinkedList<IActionInternal> result = new LinkedList<IActionInternal>();
		BlockEntity tile = container.getTile();

		if (!(tile instanceof IPipeTile)) {
			return result;
		}

		IPipeTile pipeTile = (IPipeTile) tile;

		List<DockingStation> stations = RobotUtils.getStations(pipeTile);

		if (stations.size() == 0) {
			return result;
		}

		result.add(RoboticsStatements.actionRobotGotoStation);
		result.add(RoboticsStatements.actionRobotWorkInArea);
		result.add(RoboticsStatements.actionRobotLoadUnloadArea);
		result.add(RoboticsStatements.actionRobotWakeUp);
		result.add(RoboticsStatements.actionRobotFilter);
		result.add(RoboticsStatements.actionRobotFilterTool);
		result.add(RoboticsStatements.actionStationForbidRobot);
		result.add(RoboticsStatements.actionStationForceRobot);

		if (pipeTile.getPipeType() == IPipeTile.PipeType.ITEM) {
			result.add(RoboticsStatements.actionStationRequestItems);
			result.add(RoboticsStatements.actionStationAcceptItems);
		}

		if (pipeTile.getPipeType() == IPipeTile.PipeType.FLUID) {
			result.add(RoboticsStatements.actionStationAcceptFluids);
		}

		for (DockingStation station : stations) {
			if (station.getItemInput() != null) {
				result.add(RoboticsStatements.actionStationProvideItems);
			}

			if (station.getFluidInput() != null) {
				result.add(RoboticsStatements.actionStationProvideFluids);
			}

			if (station.getRequestProvider() != null) {
				result.add(RoboticsStatements.actionStationMachineRequestItems);
			}
		}

		return result;
	}

	@Override
	public Collection<IActionExternal> getExternalActions(Direction side, BlockEntity tile) {
		return null;
	}

}
