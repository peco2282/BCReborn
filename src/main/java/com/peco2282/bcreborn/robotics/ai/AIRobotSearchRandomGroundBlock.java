/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.util.MathHelper;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.lib.utils.IBlockFilter;

public class AIRobotSearchRandomGroundBlock extends AIRobot {

	private static final int MAX_ATTEMPTS = 4096;

	public BlockIndex blockFound;

	private int range;
	private IBlockFilter filter;
	private IZone zone;
	private int attempts = 0;

	public AIRobotSearchRandomGroundBlock(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotSearchRandomGroundBlock(EntityRobotBase iRobot, int iRange, IBlockFilter iFilter, IZone iZone) {
		this(iRobot);

		range = iRange;
		filter = iFilter;
		zone = iZone;
	}

	@Override
	public void update() {
		if (filter == null) {
			terminate();
		}

		attempts++;

		if (attempts > MAX_ATTEMPTS) {
			terminate();
		}

		int x, z;

		if (zone == null) {
			double r = robot.level().rand.nextFloat() * range;
			float a = robot.level().rand.nextFloat() * 2.0F * (float) Math.PI;

			x = (int) (MathHelper.cos(a) * r + Math.floor(robot.getX()));
			z = (int) (MathHelper.sin(a) * r + Math.floor(robot.getZ()));
		} else {
			BlockIndex b = zone.getRandomBlockIndex(robot.level().rand);
			x = b.x;
			z = b.z;
		}

		for (int y = robot.level().getHeight(); y >= 0; --y) {
			if (filter.matches(robot.level(), x, y, z)) {
				blockFound = new BlockIndex(x, y, z);
				terminate();
				return;
			} else if (!robot.level().isAirBlock(x, y, z)) {
				return;
			}
		}
	}

	@Override
	public boolean success() {
		return blockFound != null;
	}

	@Override
	public int getEnergyCost() {
		return 2;
	}
}
