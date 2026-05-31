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
package com.peco2282.bcreborn.robotics.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.robots.IRequestProvider;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.robotics.IStationFilter;
import com.peco2282.bcreborn.robotics.StackRequest;
import net.minecraft.world.item.ItemStack;

public class AIRobotSearchStackRequest extends AIRobot {

	public StackRequest request = null;
	public DockingStation station = null;

	private Collection<ItemStack> blackList;

	private IStackFilter filter;

	public AIRobotSearchStackRequest(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotSearchStackRequest(EntityRobotBase iRobot, IStackFilter iFilter, Collection<ItemStack> iBlackList) {
		this(iRobot);

		blackList = iBlackList;
		filter = iFilter;
	}

	@Override
	public void start() {
		startDelegateAI(new AIRobotSearchStation(robot, new StationProviderFilter(), robot.getZoneToWork()));
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotSearchStation) {
			if (ai.success()) {
				request = getOrderFromRequestingStation(((AIRobotSearchStation) ai).targetStation, true);
			}

			terminate();
		}
	}

	@Override
	public boolean success() {
		return request != null;
	}

	private boolean isBlacklisted(ItemStack stack) {
		for (ItemStack black : blackList) {
			if (StackHelper.isMatchingItem(stack, black)) {
				return true;
			}
		}

		return false;
	}

	private StackRequest getOrderFromRequestingStation(DockingStation station, boolean take) {

		for (StackRequest req : getAvailableRequests(station)) {
			if (!isBlacklisted(req.getStack()) && filter.matches(req.getStack())) {
				req.setStation(station);
				if (take) {
					if (robot.getRegistry().take(req.getResourceId(robot.level()), robot)) {
						return req;
					}
				} else {
					return req;
				}
			}
		}

		return null;
	}

	private Collection<StackRequest> getAvailableRequests(DockingStation station) {
		List<StackRequest> result = new ArrayList<StackRequest>();

		IRequestProvider provider = station.getRequestProvider();
		if (provider == null) {
			return result;
		}

		for (int i = 0; i < provider.getRequestsCount(); i++) {
			if (provider.getRequest(i) == null) {
				continue;
			}
			StackRequest req = new StackRequest(provider, i, provider.getRequest(i));
			req.setStation(station);
			if (!robot.getRegistry().isTaken(req.getResourceId(robot.level()))) {
				result.add(req);
			}
		}
		return result;
	}

	private class StationProviderFilter implements IStationFilter {

		@Override
		public boolean matches(DockingStation station) {
			return getOrderFromRequestingStation(station, false) != null;
		}
	}

}
