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
package com.peco2282.bcreborn.robotics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRegistry;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IDockingStationProvider;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;


public final class RobotUtils {
	private RobotUtils() {

	}

	public static List<DockingStation> getStations(Object tile) {
		ArrayList<DockingStation> stations = new ArrayList<DockingStation>();

		if (tile instanceof IDockingStationProvider) {
			DockingStation station = ((IDockingStationProvider) tile).getStation();
			if (station != null) {
				stations.add(station);
			}
		}

		if (tile instanceof IPipeTile) {
			IPipeTile pipeTile = (IPipeTile) tile;
			for (Direction d : Direction.values()) {
				if (pipeTile.getPipePluggable(d) instanceof IDockingStationProvider pluggable) {
                    DockingStation station = pluggable.getStation();

					if (station != null) {
						stations.add(station);
					}
				}
			}
		}

		return stations;
	}


	public static RedstoneBoardRobotNBT getNextBoard(ItemStack stack, boolean reverse) {
		Collection<RedstoneBoardNBT<?>> boards = RedstoneBoardRegistry.instance.getAllBoardNBTs();
		if (stack.isEmpty() || !(stack.getItem() instanceof RobotItem)) {
			if (!reverse) {
				return (RedstoneBoardRobotNBT) Iterables.getFirst(boards, null);
			} else {
				return (RedstoneBoardRobotNBT) Iterables.getLast(boards, null);
			}
		} else {
			if (reverse) {
				boards = Lists.reverse(new ArrayList<>(boards));
			}
			boolean found = false;
			for (RedstoneBoardNBT<?> boardNBT : boards) {
				if (found) {
					return (RedstoneBoardRobotNBT) boardNBT;
				} else if (RobotItem.getRobotNBT(stack) == boardNBT) {
					found = true;
				}
			}
			return null;
		}
	}
}
