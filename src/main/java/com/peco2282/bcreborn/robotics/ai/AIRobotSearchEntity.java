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

import net.minecraft.world.entity.Entity;

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.utils.IEntityFilter;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AIRobotSearchEntity extends AIRobot {

	public Entity target;

	private float maxRange;
	private IZone zone;
	private IEntityFilter filter;

	public AIRobotSearchEntity(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotSearchEntity(EntityRobotBase iRobot, IEntityFilter iFilter, float iMaxRange, IZone iZone) {
		this(iRobot);

		maxRange = iMaxRange;
		zone = iZone;
		filter = iFilter;
	}

	@Override
	public void start() {
		double previousDistance = Double.MAX_VALUE;

		AABB area = robot.getBoundingBox().inflate(maxRange);
		List<Entity> entities = robot.level().getEntities(robot, area);

		for (Entity e : entities) {
			if (!e.isRemoved()
					&& filter.matches(e)
					&& (zone == null || zone.contains(e.getX(), e.getY(), e.getZ()))
					&& (!robot.isKnownUnreachable(e))) {
				double dx = e.getX() - robot.getX();
				double dy = e.getY() - robot.getY();
				double dz = e.getZ() - robot.getZ();

				double sqrDistance = dx * dx + dy * dy + dz * dz;
				double maxDistance = maxRange * maxRange;

				if (sqrDistance >= maxDistance) {
					continue;
				} else {
					if (target == null) {
						previousDistance = sqrDistance;
						target = e;
					} else {
						if (sqrDistance < previousDistance) {
							previousDistance = sqrDistance;
							target = e;
						}
					}
				}
			}
		}

		terminate();
	}

	@Override
	public boolean success() {
		return target != null;
	}

	@Override
	public int getEnergyCost() {
		return 2;
	}
}
