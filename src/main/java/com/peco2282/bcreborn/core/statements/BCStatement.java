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
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public abstract class BCStatement implements IStatement {

	protected final String uniqueTag;

	@OnlyIn(Dist.CLIENT)
	protected TextureAtlasSprite icon;

	/**
	 * UniqueTag accepts multiple possible tags, use this feature to migrate to
	 * more standardized tags if needed, otherwise just pass a single string.
	 * The first passed string will be the one used when saved to disk.
	 *
	 * @param uniqueTag
	 */
	public BCStatement(String... uniqueTag) {
		this.uniqueTag = uniqueTag[0];
		for (String tag : uniqueTag) {
			StatementManager.statements.put(String.format("%s:%s", BCRebornRobotics.MODID, tag), this);
		}
	}

	@Override
	public String getUniqueTag() {
		return uniqueTag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getIcon() {
		return icon;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
	}

	@Override
	public int maxParameters() {
		return 0;
	}

	@Override
	public int minParameters() {
		return 0;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public IStatement rotateLeft() {
		return this;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return null;
	}
}
