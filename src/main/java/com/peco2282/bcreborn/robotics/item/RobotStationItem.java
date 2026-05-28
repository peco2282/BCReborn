/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.item;

import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.robotics.render.RobotStationItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class RobotStationItem extends BuildCraftItem {

	public RobotStationItem() {
		super(new Properties());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return RobotStationItemRenderer.INSTANCE;
			}
		});
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
		return true;
	}

	// TODO: Implement IPipePluggableItem when the modern Pipe system API is ready
}
