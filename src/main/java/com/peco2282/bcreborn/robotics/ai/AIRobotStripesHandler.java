/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.util.ForgeDirection;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import com.peco2282.bcreborn.api.transport.IStripesHandler.StripesHandlerType;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.common.lib.inventory.InvUtils;
import com.peco2282.bcreborn.common.proxy.CoreProxy;

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
			ItemStack stack = robot.getHeldItem();

			ForgeDirection direction = ForgeDirection.NORTH;

			Position p = new Position(useToBlock.x, useToBlock.y, useToBlock.z);

			EntityPlayer player = CoreProxy.proxy.getBuildCraftPlayer(
					(WorldServer) robot.level(), (int) p.x, (int) p.y,
					(int) p.z).get();
			player.rotationPitch = 0;
			player.rotationYaw = 180;

			for (IStripesHandler handler : PipeManager.stripesHandlers) {
				if (handler.getType() == StripesHandlerType.ITEM_USE
						&& handler.shouldHandle(stack)) {
					if (handler.handle(robot.level(), (int) p.x, (int) p.y,
							(int) p.z, direction, stack, player, this)) {
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
	public void sendItem(ItemStack stack, ForgeDirection direction) {
		InvUtils.dropItems(robot.level(), stack, (int) Math.floor(robot.getX()),
				(int) Math.floor(robot.getY()), (int) Math.floor(robot.getZ()));
	}

	@Override
	public void dropItem(ItemStack stack, ForgeDirection direction) {
		sendItem(stack, direction);
	}
}
