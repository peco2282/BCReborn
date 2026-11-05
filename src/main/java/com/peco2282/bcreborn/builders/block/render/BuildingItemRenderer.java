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
import com.peco2282.bcreborn.common.StackAtPosition;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.builder.IBuildingItemsProvider;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;

/**
 * Port of the original {@code RenderBuildingItems}: draws the stacks of every building item in flight along its arc.
 * The pose stack must be at the block entity's position.
 */
public final class BuildingItemRenderer {
  /** Original {@code renderScale}. */
  private static final float SCALE = 0.7F;

  private BuildingItemRenderer() {
  }

  public static void render(IBuildingItemsProvider provider, BlockPos origin, Level level, PoseStack poseStack,
                            MultiBufferSource buffer) {
    if (provider.getBuilders() == null || provider.getBuilders().isEmpty()) return;
    var itemRenderer = Minecraft.getInstance().getItemRenderer();
    for (BuildingItem item : List.copyOf(provider.getBuilders())) {
      item.displayUpdate();
      for (StackAtPosition s : item.getStacks()) {
        if (!s.display || s.pos == null || s.stack == null || s.stack.isEmpty()) continue;
        BlockPos at = BlockPos.containing(s.pos.x, s.pos.y, s.pos.z);
        poseStack.pushPose();
        poseStack.translate(s.pos.x - origin.getX(), s.pos.y - origin.getY() + 0.25F, s.pos.z - origin.getZ());
        poseStack.scale(SCALE, SCALE, SCALE);
        itemRenderer.renderStatic(s.stack, ItemDisplayContext.GROUND, LevelRenderer.getLightColor(level, at),
          OverlayTexture.NO_OVERLAY, poseStack, buffer, level, 0);
        poseStack.popPose();
      }
    }
  }
}
