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
package com.peco2282.bcreborn.factory.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.factory.block.entity.PumpBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PumpTubeRenderer implements BlockEntityRenderer<PumpBlockEntity> {
  private static final ResourceLocation TEXTURE = BCRebornFactory.location("textures/block/pump_block/tube.png");

  public PumpTubeRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public boolean shouldRenderOffScreen(PumpBlockEntity blockEntity) {
    return true;
  }

  @Override
  public void render(PumpBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    double tubeHeight = blockEntity.getTubeHeight();
    if (Double.isNaN(tubeHeight) || tubeHeight <= 0) {
      return;
    }

    int light = net.minecraft.client.renderer.LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().below());
    if ((light & 0xFFFF) == 0 && (light >> 16 & 0xFFFF) == 0) light = packedLight;
    VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
    poseStack.pushPose();

    // 管はブロックの下から始まる。Pumpの座標系では(0,0,0)がブロックの北西下端。
    // 管を下に伸ばすので、1ブロック分下にずらしてから描画を開始する。
    poseStack.translate(0, -1, 0);

    int fullBlocks = (int) Math.floor(tubeHeight);
    double partialBlock = tubeHeight - fullBlocks;

    for (int i = 0; i < fullBlocks; i++) {
      renderTubeSegment(poseStack, consumer, 1.0, light, packedOverlay);
      poseStack.translate(0, -1, 0);
    }

    if (partialBlock > 0) {
      renderTubeSegment(poseStack, consumer, partialBlock, light, packedOverlay);
    }

    poseStack.popPose();
  }

  private void renderTubeSegment(PoseStack poseStack, VertexConsumer consumer, double height, int packedLight, int packedOverlay) {
    Matrix4f matrix = poseStack.last().pose();
    float h = (float) height;

    float minX = 4.0f / 16.0f;
    float maxX = 12.0f / 16.0f;
    float minZ = 4.0f / 16.0f;
    float maxZ = 12.0f / 16.0f;
    float minY = 1.0f - h;
    float maxY = 1.0f;

    // 外側の4面をレンダリング（BuildCraftの管は中空の立方体のような見た目）
    // North Face
    renderQuad(matrix, consumer, minX, minY, minZ, maxX, maxY, minZ, 0, 0, 8, 16 * h, packedLight, packedOverlay, 0, 0, -1);
    // South Face
    renderQuad(matrix, consumer, maxX, minY, maxZ, minX, maxY, maxZ, 0, 0, 8, 16 * h, packedLight, packedOverlay, 0, 0, 1);
    // West Face
    renderQuad(matrix, consumer, minX, minY, maxZ, minX, maxY, minZ, 0, 0, 8, 16 * h, packedLight, packedOverlay, -1, 0, 0);
    // East Face
    renderQuad(matrix, consumer, maxX, minY, minZ, maxX, maxY, maxZ, 0, 0, 8, 16 * h, packedLight, packedOverlay, 1, 0, 0);

    // 底面 (Bottom Face)
    renderQuad(matrix, consumer, minX, minY, minZ, maxX, minY, maxZ, 0, 0, 8, 8, packedLight, packedOverlay, 0, -1, 0);

    // 内側の4面をレンダリング（必要であれば）
    // North Face (Inner)
    renderQuad(matrix, consumer, maxX, minY, minZ + 0.001f, minX, maxY, minZ + 0.001f, 0, 0, 8, 16 * h, packedLight, packedOverlay, 0, 0, 1);
    // South Face (Inner)
    renderQuad(matrix, consumer, minX, minY, maxZ - 0.001f, maxX, maxY, maxZ - 0.001f, 0, 0, 8, 16 * h, packedLight, packedOverlay, 0, 0, -1);
    // West Face (Inner)
    renderQuad(matrix, consumer, minX + 0.001f, minY, minZ, minX + 0.001f, maxY, maxZ, 0, 0, 8, 16 * h, packedLight, packedOverlay, 1, 0, 0);
    // East Face (Inner)
    renderQuad(matrix, consumer, maxX - 0.001f, minY, maxZ, maxX - 0.001f, maxY, minZ, 0, 0, 8, 16 * h, packedLight, packedOverlay, -1, 0, 0);

    // 上面 (Top Face) - 内側から見えるように
    renderQuad(matrix, consumer, maxX, maxY, minZ, minX, maxY, maxZ, 0, 0, 8, 8, packedLight, packedOverlay, 0, 1, 0);
  }

  private void renderQuad(Matrix4f matrix, VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float u1, float v1, float u2, float v2, int packedLight, int packedOverlay, float nx, float ny, float nz) {
    u1 /= 16f;
    v1 /= 16f;
    u2 /= 16f;
    v2 /= 16f;
    // 簡易的な四角形描画。
    if (ny != 0) { // Y面（水平面）
      consumer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
      consumer.vertex(matrix, x1, y1, z2).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
      consumer.vertex(matrix, x2, y1, z2).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
      consumer.vertex(matrix, x2, y1, z1).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
    } else { // 垂直面
      consumer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
      consumer.vertex(matrix, x2, y1, z2).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
      consumer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
      consumer.vertex(matrix, x1, y2, z1).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
    }
  }
}
