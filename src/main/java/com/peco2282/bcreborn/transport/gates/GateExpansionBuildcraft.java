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
package com.peco2282.bcreborn.transport.gates;

import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class GateExpansionBuildcraft implements IGateExpansion {

	private final String tag;
	private TextureAtlasSprite iconBlock;
	private TextureAtlasSprite iconItem;

	public GateExpansionBuildcraft(String tag) {
		this.tag = tag;
	}

	@Override
	public String getUniqueIdentifier() {
		return "bcreborntransport:" + tag;
	}

	@Override
	public String getDisplayName() {
		return StringUtils.localize("gate.expansion." + tag);
	}

	@Override
	public void registerBlockOverlay(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		iconBlock = textureGetter.apply(new ResourceLocation("bcreborntransport", "gates/gate_expansion_" + tag));
	}

	@Override
	public void registerItemOverlay(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		iconItem = textureGetter.apply(new ResourceLocation("bcreborntransport", "gates/gate_expansion_" + tag));
	}

	@Override
	public TextureAtlasSprite getOverlayBlock() {
		return iconBlock;
	}

	@Override
	public TextureAtlasSprite getOverlayItem() {
		return iconItem;
	}
}
