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
package com.peco2282.bcreborn.builders.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** Original {@code RenderBuilder}: the Builder's building items in flight. */
public class BuilderRenderer implements BlockEntityRenderer<BuilderBlockEntity> {
  public BuilderRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(BuilderBlockEntity builder, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                     int combinedLight, int combinedOverlay) {
    BuildingItemRenderer.render(builder, builder.getBlockPos(), builder.getLevel(), poseStack, buffer);
  }

  @Override
  public boolean shouldRenderOffScreen(BuilderBlockEntity builder) {
    return true;
  }
}
