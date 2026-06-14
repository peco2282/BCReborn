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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class FacadePluggableRenderer implements IPipePluggableRenderer {

  public static final FacadePluggableRenderer INSTANCE = new FacadePluggableRenderer();

  private FacadePluggableRenderer() {
  }

  private static final float Z_FIGHT_OFFSET = 1.0f / 4096.0f;

  @Override
  public void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    if (!(pipePluggable instanceof FacadePluggable facade)) return;

    BlockState state = facade.getState();
    if (state.isAir()) return;

    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

    poseStack.pushPose();

    float thickness = 1.0f / 16.0f;
    float offset = Z_FIGHT_OFFSET;

    // 側面に配置するためのトランスフォーム
    // 1.0.1では、各面に対して適切な移動とスケーリングを行う
    switch (side) {
      case DOWN:
        poseStack.translate(offset, 0, offset);
        poseStack.scale(1.0f - offset * 2, thickness, 1.0f - offset * 2);
        break;
      case UP:
        poseStack.translate(offset, 1.0f - thickness, offset);
        poseStack.scale(1.0f - offset * 2, thickness, 1.0f - offset * 2);
        break;
      case NORTH:
        poseStack.translate(offset, offset, 0);
        poseStack.scale(1.0f - offset * 2, 1.0f - offset * 2, thickness);
        break;
      case SOUTH:
        poseStack.translate(offset, offset, 1.0f - thickness);
        poseStack.scale(1.0f - offset * 2, 1.0f - offset * 2, thickness);
        break;
      case WEST:
        poseStack.translate(0, offset, offset);
        poseStack.scale(thickness, 1.0f - offset * 2, 1.0f - offset * 2);
        break;
      case EAST:
        poseStack.translate(1.0f - thickness, offset, offset);
        poseStack.scale(thickness, 1.0f - offset * 2, 1.0f - offset * 2);
        break;
    }

    // ブロックモデルを描画
    if (facade.isHollow()) {
      renderHollow(state, side, thickness, offset, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    } else {
      dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());
    }

    poseStack.popPose();
  }

  private void renderHollow(BlockState state, Direction side, float thickness, float offset, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockRenderDispatcher dispatcher) {
    float holeMin = 4.0f / 16.0f;
    float holeMax = 12.0f / 16.0f;

    // 4つの板を描画して中央を空ける
    // 1. 上
    poseStack.pushPose();
    renderSection(state, 0, holeMax, 1.0f, 1.0f, thickness, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();

    // 2. 下
    poseStack.pushPose();
    renderSection(state, 0, 0, 1.0f, holeMin, thickness, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();

    // 3. 左
    poseStack.pushPose();
    renderSection(state, 0, holeMin, holeMin, holeMax, thickness, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();

    // 4. 右
    poseStack.pushPose();
    renderSection(state, holeMax, holeMin, 1.0f, holeMax, thickness, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();
  }

  private void renderSection(BlockState state, float xMin, float yMin, float xMax, float yMax, float thickness, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockRenderDispatcher dispatcher) {
    poseStack.translate(xMin, yMin, 0);
    poseStack.scale(xMax - xMin, yMax - yMin, 1.0f);
    // すでに親のトランスフォームで厚みと向きが調整されている前提
    dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());
  }
}
