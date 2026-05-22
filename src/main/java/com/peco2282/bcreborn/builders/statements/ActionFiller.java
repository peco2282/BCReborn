/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.builders.statements;

import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.builder.patterns.FillerPattern;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionFiller implements IActionExternal {

	public final FillerPattern pattern;

	public ActionFiller(FillerPattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public String getDescription() {
		return "Pattern: " + pattern.getDescription();
	}

	@Override
	public String getUniqueTag() {
		return "filler:" + pattern.getUniqueTag();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getIcon() {
		return pattern.getIcon();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		pattern.registerIcons(textureGetter);
	}

	@Override
	public int minParameters() {
		return pattern.minParameters();
	}

	@Override
	public int maxParameters() {
		return pattern.maxParameters();
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return pattern.createParameter(index);
	}

	@Override
	public IStatement rotateLeft() {
		return this;
	}

	@Override
	public void actionActivate(BlockEntity target, Direction side,
							   IStatementContainer source, IStatementParameter[] parameters) {
		if (target instanceof FillerBlockEntity) {
			// ((FillerBlockEntity) target).setPattern(pattern);
			// ((FillerBlockEntity) target).patternParameters = parameters;
		}
	}
}
