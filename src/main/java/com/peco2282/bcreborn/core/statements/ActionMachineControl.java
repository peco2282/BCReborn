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
import com.peco2282.bcreborn.api.IControllable;
import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;
import java.util.function.Function;



public class ActionMachineControl extends BCStatement implements IActionExternal {
	public final IControllable.Mode mode;

	public ActionMachineControl(IControllable.Mode mode) {
		super("buildcraft:machine." + mode.name().toLowerCase(Locale.ENGLISH), "buildcraft.machine." + mode.name().toLowerCase(Locale.ENGLISH));

		this.mode = mode;
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.machine." + mode.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public void actionActivate(BlockEntity target, Direction side,
	                           IStatementContainer source, IStatementParameter[] parameters) {
		if (target instanceof IControllable) {
			((IControllable) target).setControlMode(mode);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornCore.location("triggers/action_machinecontrol_" + mode.name().toLowerCase(Locale.ENGLISH)));
	}
}
