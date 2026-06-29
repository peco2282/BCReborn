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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.core.block.render.LaserRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class QuarryRenderer implements BlockEntityRenderer<QuarryBlockEntity> {
  private static final ResourceLocation DRILL_TEXTURE = ResourceBuilder.builder("quarry_block/drill.png").build(ResourceBuilder.ResourceType.BLOCK);
  private static final ResourceLocation DRILL_HEAD_TEXTURE = ResourceBuilder.builder("quarry_block/drill_head.png").build(ResourceBuilder.ResourceType.BLOCK);
  private static final ResourceLocation DRILL_XZ_TEXTURE = ResourceBuilder.builder("quarry_block/drill_xz.png").build(ResourceBuilder.ResourceType.BLOCK);

  public QuarryRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(QuarryBlockEntity quarry, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    // Render the box (lasers)
    Box box = quarry.getBox();
    if (box.isInitialized() && box.lasersData != null) {
      poseStack.pushPose();
      poseStack.translate(-quarry.getBlockPos().getX(), -quarry.getBlockPos().getY(), -quarry.getBlockPos().getZ());
      for (LaserData laser : box.lasersData) {
        if (laser == null) continue;
        laser.iterateTexture();
        laser.update();
        LaserRenderer.renderLaser(poseStack, buffer, laser, partialTicks);
      }
      poseStack.popPose();
    }

    // Render the drill
    if (quarry.getStage() != QuarryBlockEntity.Stage.BUILDING && quarry.getStage() != QuarryBlockEntity.Stage.DONE) {
      double headX = quarry.getHeadPosX(partialTicks) - quarry.getBlockPos().getX();
      double headY = quarry.getHeadPosY(partialTicks) - quarry.getBlockPos().getY();
      double headZ = quarry.getHeadPosZ(partialTicks) - quarry.getBlockPos().getZ();

      poseStack.pushPose();
      poseStack.translate(headX, headY, headZ);

      // BuildCraft's drill head rendering logic
      // For now, let's render a simple box for the drill head
      renderDrillHead(poseStack, buffer, combinedLight, combinedOverlay);

      poseStack.popPose();

      // Render the frame that connects to the quarry block
      renderDrillFrame(quarry, headX, headY, headZ, poseStack, buffer, combinedLight, combinedOverlay);
    }
  }

  private void renderDrillHead(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
    VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(DRILL_HEAD_TEXTURE));

    poseStack.pushPose();
    poseStack.translate(-0.125, -0.25, -0.125);
    poseStack.scale(0.25f, 0.25f, 0.25f);

    // Simple cube for drill head
    addBox(poseStack, builder, 0, 0, 0, 1, 1, 1, light, overlay);

    poseStack.popPose();
  }

  private void renderDrillFrame(QuarryBlockEntity quarry, double headX, double headY, double headZ, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
    Box box = quarry.getBox();
    if (!box.isInitialized()) return;

    double xMin = box.xMin - quarry.getBlockPos().getX() + 0.5;
    double xMax = box.xMax - quarry.getBlockPos().getX() + 0.5;
    double yMax = box.yMax - quarry.getBlockPos().getY() + 0.5;
    double zMin = box.zMin - quarry.getBlockPos().getZ() + 0.5;
    double zMax = box.zMax - quarry.getBlockPos().getZ() + 0.5;

    VertexConsumer drillX = buffer.getBuffer(RenderType.entityCutout(DRILL_XZ_TEXTURE));
    VertexConsumer drillY = buffer.getBuffer(RenderType.entityCutout(DRILL_TEXTURE));
    VertexConsumer drillZ = buffer.getBuffer(RenderType.entityCutout(DRILL_XZ_TEXTURE));

    // Y Frame (Vertical)
    poseStack.pushPose();
    poseStack.translate(headX, headY, headZ);
    addBox(poseStack, drillY, -0.0625f, 0, -0.0625f, 0.0625f, (float) (yMax - headY), 0.0625f, light, overlay);
    poseStack.popPose();

    // X Frame (Horizontal)
    poseStack.pushPose();
    poseStack.translate(xMin, yMax, headZ);
    addBox(poseStack, drillX, 0, -0.0625f, -0.0625f, (float) (xMax - xMin), 0.0625f, 0.0625f, light, overlay);
    poseStack.popPose();

    // Z Frame (Horizontal)
    poseStack.pushPose();
    poseStack.translate(headX, yMax, zMin);
    addBox(poseStack, drillZ, -0.0625f, -0.0625f, 0, 0.0625f, 0.0625f, (float) (zMax - zMin), light, overlay);
    poseStack.popPose();
  }

  private void addBox(PoseStack poseStack, VertexConsumer builder, float x0, float y0, float z0, float x1, float y1, float z1, int light, int overlay) {
    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();

    // Bottom
    addVertex(builder, matrix4f, matrix3f, x0, y0, z0, 0, 0, 0, -1, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y0, z0, 1, 0, 0, -1, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y0, z1, 1, 1, 0, -1, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y0, z1, 0, 1, 0, -1, 0, light, overlay);

    // Top
    addVertex(builder, matrix4f, matrix3f, x0, y1, z1, 0, 0, 0, 1, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y1, z1, 1, 0, 0, 1, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y1, z0, 1, 1, 0, 1, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y1, z0, 0, 1, 0, 1, 0, light, overlay);

    // North
    addVertex(builder, matrix4f, matrix3f, x0, y0, z0, 0, 1, 0, 0, -1, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y1, z0, 0, 0, 0, 0, -1, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y1, z0, 1, 0, 0, 0, -1, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y0, z0, 1, 1, 0, 0, -1, light, overlay);

    // South
    addVertex(builder, matrix4f, matrix3f, x1, y0, z1, 0, 1, 0, 0, 1, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y1, z1, 0, 0, 0, 0, 1, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y1, z1, 1, 0, 0, 0, 1, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y0, z1, 1, 1, 0, 0, 1, light, overlay);

    // West
    addVertex(builder, matrix4f, matrix3f, x0, y0, z1, 0, 1, -1, 0, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y1, z1, 0, 0, -1, 0, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y1, z0, 1, 0, -1, 0, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x0, y0, z0, 1, 1, -1, 0, 0, light, overlay);

    // East
    addVertex(builder, matrix4f, matrix3f, x1, y0, z0, 0, 1, 1, 0, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y1, z0, 0, 0, 1, 0, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y1, z1, 1, 0, 1, 0, 0, light, overlay);
    addVertex(builder, matrix4f, matrix3f, x1, y0, z1, 1, 1, 1, 0, 0, light, overlay);
  }

  private void addVertex(VertexConsumer builder, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, float u, float v, float nx, float ny, float nz, int light, int overlay) {
    builder.vertex(matrix4f, x, y, z)
      .color(255, 255, 255, 255)
      .uv(u, v)
      .overlayCoords(overlay)
      .uv2(light)
      .normal(matrix3f, nx, ny, nz)
      .endVertex();
  }

  @Override
  public boolean shouldRenderOffScreen(QuarryBlockEntity quarry) {
    return true;
  }

  @Override
  public int getViewDistance() {
    return 128;
  }
}
