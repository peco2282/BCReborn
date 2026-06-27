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
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.internal.IBoxProvider;
import com.peco2282.bcreborn.core.block.render.LaserRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BoxBlockEntityRenderer<T extends BlockEntity & IBoxProvider> implements BlockEntityRenderer<T> {
  public BoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    Box box = blockEntity.getBox();
    if (!box.isInitialized() || box.lasersData == null) {
      return;
    }

    poseStack.pushPose();
    // Translate from world coordinates to local coordinates
    poseStack.translate(-blockEntity.getBlockPos().getX(), -blockEntity.getBlockPos().getY(), -blockEntity.getBlockPos().getZ());

    for (LaserData laser : box.lasersData) {
      if (laser == null) continue;
      laser.iterateTexture();
      laser.update();
      LaserRenderer.renderLaser(poseStack, buffer, laser, partialTicks);
    }

    poseStack.popPose();
  }

  @Override
  public boolean shouldRenderOffScreen(T blockEntity) {
    return true;
  }
}
