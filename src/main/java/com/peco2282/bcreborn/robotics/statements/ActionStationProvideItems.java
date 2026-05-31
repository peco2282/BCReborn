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
package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.*;
import com.peco2282.bcreborn.common.inventory.filters.StatementParameterStackFilter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionStationProvideItems extends BCStatement implements IActionInternal {

	public ActionStationProvideItems() {
		super("station.provide_items");
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.station.provide_items");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornRobotics.location("triggers/action_station_provide_items"));
	}

	@Override
	public int maxParameters() {
		return 3;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}

	@Override
	public void actionActivate(IStatementContainer source,
							   IStatementParameter[] parameters) {

	}

	public static boolean canExtractItem(DockingStation station, ItemStack stack) {
		boolean hasFilter = false;

		for (StatementSlot s : station.getActiveActions()) {
			if (s.statement instanceof ActionStationProvideItems) {
				StatementParameterStackFilter param = new StatementParameterStackFilter(s.parameters);

				if (param.hasFilter()) {
					hasFilter = true;

					if (param.matches(stack)) {
						return true;
					}
				}
			}
		}

		return !hasFilter;
	}
}
