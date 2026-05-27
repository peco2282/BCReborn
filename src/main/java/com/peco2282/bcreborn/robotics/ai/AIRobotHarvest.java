package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import net.minecraft.nbt.CompoundTag;

public class AIRobotHarvest extends AIRobot {

	private BlockIndex blockFound;
	private int delay = 0;

	public AIRobotHarvest(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotHarvest(EntityRobotBase iRobot, BlockIndex iBlockFound) {
		super(iRobot);
		blockFound = iBlockFound;
	}

	@Override
	public void update() {
		if (blockFound == null) {
			setSuccess(false);
			terminate();
			return;
		}
		// Implementation simplified for now
		terminate();
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);

		if (blockFound != null) {
			CompoundTag sub = new CompoundTag();
			blockFound.writeTo(sub);
			nbt.put("blockFound", sub);
		}
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		super.loadSelfFromNBT(nbt);

		if (nbt.contains("blockFound")) {
			blockFound = new BlockIndex(nbt.getCompound("blockFound"));
		}
	}
}
