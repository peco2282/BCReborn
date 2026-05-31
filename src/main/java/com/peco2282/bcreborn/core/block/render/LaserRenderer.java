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
package com.peco2282.bcreborn.core.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LaserRenderer {
  public static final ResourceLocation LASER_RED = BCRebornCore.location("textures/laser_beams/red.png");
  public static final ResourceLocation LASER_BLUE = BCRebornCore.location("textures/laser_beams/blue.png");
  public static final ResourceLocation LASER_GREEN = BCRebornCore.location("textures/laser_beams/green.png");
  public static final ResourceLocation LASER_YELLOW = BCRebornCore.location("textures/laser_beams/yellow.png");
  public static final ResourceLocation LASER_STRIPES = BCRebornCore.location("textures/laser_beams/stripes.png");

  public static void renderLaser(PoseStack poseStack, MultiBufferSource buffer, LaserData laser, float partialTicks) {
    if (!laser.isVisible) return;

    ResourceLocation texture = switch (laser.kind) {
      case Red -> LASER_RED;
      case Blue -> LASER_BLUE;
      case Stripes -> LASER_STRIPES;
      case Yellow -> LASER_YELLOW;
      case Green -> LASER_GREEN;
    };

    int alpha = laser.isGlowing ? 255 : 128;
    int r = 255;
    int g = 255;
    int b = 255;

    // If the texture itself looks purple, we may need to adjust the tint.
    // Pure blue should have 0 red and 0 green if we want to force it,
    // but the texture usually provides the base color.
    if (laser.kind == LaserKind.Blue) {
      r = 60;  // Reduce red component to fix purple tint
      g = 60;  // Reduce green slightly
      b = 255; // Keep blue full
    }

    poseStack.pushPose();
    
    // Translation to tail
    poseStack.translate(laser.tail.x, laser.tail.y, laser.tail.z);
    
    // Rotate to match angles
    // order: Y rotation then Z rotation
    poseStack.mulPose(Axis.YP.rotationDegrees((float)laser.angleZ));
    poseStack.mulPose(Axis.ZP.rotationDegrees((float)laser.angleY));

    VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
    
    float v1 = laser.laserTexAnimation / 40.0f;
    float v2 = v1 + (float)laser.renderSize; // Texture tiling
    float size = 1.0f / 16.0f;
    float length = (float)laser.renderSize;

    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();

    // Render 4 sides of the laser beam (rectangle along X axis)
    // 1 (Top)
    renderSide(matrix4f, matrix3f, consumer, 0, length, -size, -size, size, -size, 0, 1, v1, v2, 0, 1, 0, r, g, b, alpha);
    // 2 (Bottom)
    renderSide(matrix4f, matrix3f, consumer, 0, length, size, size, -size, size, 0, 1, v1, v2, 0, -1, 0, r, g, b, alpha);
    // 3 (Front)
    renderSide(matrix4f, matrix3f, consumer, 0, length, -size, size, -size, -size, 0, 1, v1, v2, 0, 0, 1, r, g, b, alpha);
    // 4 (Back)
    renderSide(matrix4f, matrix3f, consumer, 0, length, size, -size, size, size, 0, 1, v1, v2, 0, 0, -1, r, g, b, alpha);

    poseStack.popPose();
  }

  private static void renderSide(Matrix4f m4, Matrix3f m3, VertexConsumer consumer, float x1, float x2, float y1, float y2, float z1, float z2, float u1, float u2, float v1, float v2, float nx, float ny, float nz, int r, int g, int b, int alpha) {
    consumer.vertex(m4, x1, y1, z1).color(r, g, b, alpha).uv(u1, v1).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x2, y1, z1).color(r, g, b, alpha).uv(u2, v1).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x2, y2, z2).color(r, g, b, alpha).uv(u2, v2).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x1, y2, z2).color(r, g, b, alpha).uv(u1, v2).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
  }
}
