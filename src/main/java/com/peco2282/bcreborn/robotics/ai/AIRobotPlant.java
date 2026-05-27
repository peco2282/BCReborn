package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.crops.CropManager;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.lib.utils.BlockUtils;
import com.peco2282.bcreborn.common.proxy.CoreProxy;

public class AIRobotPlant extends AIRobot {
	private BlockIndex blockFound;
	private int delay = 0;

	public AIRobotPlant(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotPlant(EntityRobotBase iRobot, BlockIndex iBlockFound) {
		this(iRobot);

		blockFound = iBlockFound;
	}

	@Override
	public void start() {
		robot.aimItemAt(blockFound.x, blockFound.y, blockFound.z);
		robot.setItemActive(true);
	}

	@Override
	public void update() {
		if (blockFound == null) {
			setSuccess(false);
			terminate();
		}

		if (delay++ > 40) {
			EntityPlayer player = CoreProxy.proxy.getBuildCraftPlayer((WorldServer) robot.level())
					.get();
			if (CropManager.plantCrop(robot.level(), player, robot.getHeldItem(), blockFound.x,
					blockFound.y, blockFound.z)) {
			} else {
				setSuccess(false);
			}
			if (robot.getHeldItem().getCount() > 0) {
				BlockUtils.dropItem((WorldServer) robot.level(),
						MathHelper.floor_double(robot.getX()), MathHelper.floor_double(robot.getY()),
						MathHelper.floor_double(robot.getZ()), 6000, robot.getHeldItem());
			}
			robot.setItemInUse(null);
			terminate();
		}
	}

	@Override
	public void end() {
		robot.setItemActive(false);
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
			nbt.setTag("blockFound", sub);
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
