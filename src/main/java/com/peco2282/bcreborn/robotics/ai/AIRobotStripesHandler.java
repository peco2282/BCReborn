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


import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import com.peco2282.bcreborn.api.transport.IStripesHandler.StripesHandlerType;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.common.inventory.InvUtils;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;;

public class AIRobotStripesHandler extends AIRobot implements IStripesActivator {
	private BlockIndex useToBlock;
	private int useCycles = 0;

	public AIRobotStripesHandler(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotStripesHandler(EntityRobotBase iRobot, BlockIndex index) {
		this(iRobot);

		useToBlock = index;
	}

	@Override
	public void start() {
		robot.aimItemAt(useToBlock.x, useToBlock.y, useToBlock.z);
		robot.setItemActive(true);
	}

	@Override
	public void update() {
		if (useToBlock == null) {
			setSuccess(false);
			terminate();
			return;
		}

		useCycles++;

		if (useCycles > 60) {
			ItemStack stack = robot.getMainHandItem();

			Direction direction = Direction.NORTH;

			Position p = new Position(useToBlock.x, useToBlock.y, useToBlock.z);

			Player player = BCFakePlayer.getBuildCraftPlayer(
					(ServerLevel) robot.level(), (int) p.x, (int) p.y,
					(int) p.z).get();
			player.setXRot(0);
			player.setYRot(180);

			for (IStripesHandler handler : PipeManager.stripesHandlers) {
				if (handler.getType() == StripesHandlerType.ITEM_USE
						&& handler.shouldHandle(stack)) {
					if (handler.handle(robot.level(), new BlockPos((int) p.x, (int) p.y,
							(int) p.z), direction, stack, player, this)) {
						robot.setItemInUse(null);
						terminate();
						return;
					}
				}
			}
			terminate();
		}
	}

	@Override
	public void end() {
		robot.setItemActive(false);
	}

	@Override
	public int getEnergyCost() {
		return 15;
	}

	@Override
	public void sendItem(ItemStack stack, Direction direction) {
		InvUtils.dropItems(robot.level(), stack, (int) Math.floor(robot.getX()),
				(int) Math.floor(robot.getY()), (int) Math.floor(robot.getZ()));
	}

	@Override
	public void dropItem(ItemStack stack, Direction direction) {
		sendItem(stack, direction);
	}
}
