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

  @Override
  public void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    if (!(pipePluggable instanceof FacadePluggable facade)) return;

    BlockState state = facade.getState();
    if (state.isAir()) return;

    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

    poseStack.pushPose();

    // 側面に薄い板として配置するためのトランスフォーム
    float thickness = 1.0f / 16.0f;
    switch (side) {
      case DOWN:
        poseStack.translate(0, 0, 0);
        poseStack.scale(1, thickness, 1);
        break;
      case UP:
        poseStack.translate(0, 1.0f - thickness, 0);
        poseStack.scale(1, thickness, 1);
        break;
      case NORTH:
        poseStack.translate(0, 0, 0);
        poseStack.scale(1, 1, thickness);
        break;
      case SOUTH:
        poseStack.translate(0, 0, 1.0f - thickness);
        poseStack.scale(1, 1, thickness);
        break;
      case WEST:
        poseStack.translate(0, 0, 0);
        poseStack.scale(thickness, 1, 1);
        break;
      case EAST:
        poseStack.translate(1.0f - thickness, 0, 0);
        poseStack.scale(thickness, 1, 1);
        break;
    }

    // ブロックモデルを描画
    // 注意: 本来はパイプの接続部分を隠すために、テクスチャだけを貼り付けたカスタムモデルを使うのがBC流だが、
    // ここでは単純化のためにブロック全体をスケーリングして描画する。
    dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, RenderType.cutout());

    poseStack.popPose();
  }
}
