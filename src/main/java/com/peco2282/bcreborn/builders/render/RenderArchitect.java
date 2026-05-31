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
package com.peco2282.bcreborn.builders.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RenderArchitect implements BlockEntityRenderer<ArchitectBlockEntity> {

	public RenderArchitect(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(ArchitectBlockEntity architect, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
	}
}
