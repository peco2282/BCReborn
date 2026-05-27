/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import com.peco2282.bcreborn.api.statements.containers.ISidedStatementContainer;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class TriggerRedstoneInput extends BCStatement implements ITriggerInternal {

	boolean active;

	public TriggerRedstoneInput(boolean active) {
		super("buildcraft:redstone.input." + (active ? "active" : "inactive"), active ? "buildcraft.redtone.input.active" : "buildcraft.redtone.input.inactive");
		this.active = active;
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.trigger.redstone.input." + (active ? "active" : "inactive"));
	}

	@Override
	public IStatementParameter createParameter(int index) {
		IStatementParameter param = null;

		if (index == 0) {
			param = new StatementParameterRedstoneGateSideOnly();
		}

		return param;
	}

	@Override
	public int maxParameters() {
		return 1;
	}

	@Override
	public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
		if (container instanceof IRedstoneStatementContainer) {
			int level = ((IRedstoneStatementContainer) container).getRedstoneInput(null);
			if (parameters.length > 0 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly &&
					((StatementParameterRedstoneGateSideOnly) parameters[0]).isOn &&
					container instanceof ISidedStatementContainer) {
				level = ((IRedstoneStatementContainer) container).getRedstoneInput(((ISidedStatementContainer) container).getSide());
			}

			return active ? level > 0 : level == 0;
		} else {
			return false;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_redstoneinput_" + (active ? "active" : "inactive")));
	}
}
