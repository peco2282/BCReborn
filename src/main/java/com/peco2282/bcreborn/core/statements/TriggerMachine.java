/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class TriggerMachine extends BCStatement implements ITriggerExternal {

	boolean active;

	public TriggerMachine(boolean active) {
		super("buildcraft:work." + (active ? "scheduled" : "done"), "buildcraft.work." + (active ? "scheduled" : "done"));

		this.active = active;
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.trigger.machine." + (active ? "scheduled" : "done"));
	}

	@Override
	public boolean isTriggerActive(BlockEntity tile, Direction side, IStatementContainer container, IStatementParameter[] parameters) {
		if (tile instanceof IHasWork) {
			IHasWork machine = (IHasWork) tile;

			if (active) {
				return machine.hasWork();
			} else {
				return !machine.hasWork();
			}
		}

		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_machine_" + (active ? "active" : "inactive")));
	}
}
