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
package com.peco2282.bcreborn.robotics.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.BCRebornRobotics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RobotStationItemRenderer extends BlockEntityWithoutLevelRenderer {
  public static final RobotStationItemRenderer INSTANCE = new RobotStationItemRenderer();
  private static final ResourceLocation TEXTURE = BCRebornRobotics.location("textures/block/pipes/pipe_robot_station.png");

  public RobotStationItemRenderer() {
    super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
    renderPlug(poseStack, vertexConsumer, packedLight);
  }

  private void renderPlug(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
    // Equivalent to the original code's two boxes
    // Box 1: 0.25, 0.1875, 0.25 to 0.75, 0.25, 0.75
    renderBox(poseStack, consumer, 0.25F, 0.1875F, 0.25F, 0.75F, 0.25F, 0.75F, packedLight);
    // Box 2: 0.4325, 0.25, 0.4325 to 0.5675, 0.4375, 0.5675
    renderBox(poseStack, consumer, 0.4325F, 0.25F, 0.4325F, 0.5675F, 0.4375F, 0.5675F, packedLight);
  }

  private void renderBox(PoseStack poseStack, VertexConsumer consumer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int packedLight) {
    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();

    // Draw 6 faces
    // Down
    vertex(matrix4f, matrix3f, consumer, minX, minY, minZ, 0, 0, 0, -1, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, minY, minZ, 1, 0, 0, -1, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, minY, maxZ, 1, 1, 0, -1, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, minY, maxZ, 0, 1, 0, -1, 0, packedLight);

    // Up
    vertex(matrix4f, matrix3f, consumer, minX, maxY, maxZ, 0, 0, 0, 1, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, maxY, maxZ, 1, 0, 0, 1, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, maxY, minZ, 1, 1, 0, 1, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, maxY, minZ, 0, 1, 0, 1, 0, packedLight);

    // North
    vertex(matrix4f, matrix3f, consumer, minX, minY, minZ, 0, 0, 0, 0, -1, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, maxY, minZ, 0, 1, 0, 0, -1, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, maxY, minZ, 1, 1, 0, 0, -1, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, minY, minZ, 1, 0, 0, 0, -1, packedLight);

    // South
    vertex(matrix4f, matrix3f, consumer, maxX, minY, maxZ, 0, 0, 0, 0, 1, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, maxY, maxZ, 0, 1, 0, 0, 1, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, maxY, maxZ, 1, 1, 0, 0, 1, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, minY, maxZ, 1, 0, 0, 0, 1, packedLight);

    // West
    vertex(matrix4f, matrix3f, consumer, minX, minY, maxZ, 0, 0, -1, 0, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, maxY, maxZ, 0, 1, -1, 0, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, maxY, minZ, 1, 1, -1, 0, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, minX, minY, minZ, 1, 0, -1, 0, 0, packedLight);

    // East
    vertex(matrix4f, matrix3f, consumer, maxX, minY, minZ, 0, 0, 1, 0, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, maxY, minZ, 0, 1, 1, 0, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, maxY, maxZ, 1, 1, 1, 0, 0, packedLight);
    vertex(matrix4f, matrix3f, consumer, maxX, minY, maxZ, 1, 0, 1, 0, 0, packedLight);
  }

  private void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, float nx, float ny, float nz, int packedLight) {
    consumer.vertex(matrix4f, x, y, z)
      .color(255, 255, 255, 255)
      .uv(u, v)
      .overlayCoords(OverlayTexture.NO_OVERLAY)
      .uv2(packedLight)
      .normal(matrix3f, nx, ny, nz)
      .endVertex();
  }
}
