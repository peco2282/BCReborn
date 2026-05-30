package com.peco2282.bcreborn.core.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.LaserData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LaserRenderer {
  public static final ResourceLocation LASER_RED = BCRebornCore.location("textures/laser_beams/red.png");
  public static final ResourceLocation LASER_BLUE = BCRebornCore.location("textures/laser_beams/blue.png");
  public static final ResourceLocation LASER_STRIPES = BCRebornCore.location("textures/laser_beams/stripes.png");

  public static void renderLaser(PoseStack poseStack, MultiBufferSource buffer, LaserData laser, float partialTicks) {
    if (!laser.isVisible) return;

    ResourceLocation texture = switch (laser.kind) {
      case Red -> LASER_RED;
      case Blue -> LASER_BLUE;
      case Stripes -> LASER_STRIPES;
      case Yellow -> LASER_STRIPES; // Stripe texture is used for connection
      case Green -> LASER_RED; // Fallback
    };

    int alpha = laser.isGlowing ? 255 : 128;
    int color = 255; // White tint

    poseStack.pushPose();
    
    // Translation to tail
    poseStack.translate(laser.tail.x, laser.tail.y, laser.tail.z);
    
    // Rotate to match angles
    poseStack.mulPose(Axis.YP.rotationDegrees((float)laser.angleZ));
    poseStack.mulPose(Axis.ZP.rotationDegrees((float)laser.angleY));

    VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
    
    float v1 = laser.laserTexAnimation / 40.0f;
    float v2 = v1 + 1.0f;
    float size = 1.0f / 16.0f;
    float length = (float)laser.renderSize;

    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();

    // Render 4 sides of the laser beam (rectangle along X axis)
    // 1 (Top)
    renderSide(matrix4f, matrix3f, consumer, 0, length, -size, -size, size, -size, 0, 1, v1, v2, 0, 1, 0, color, alpha);
    // 2 (Bottom)
    renderSide(matrix4f, matrix3f, consumer, 0, length, size, size, -size, size, 0, 1, v1, v2, 0, -1, 0, color, alpha);
    // 3 (Front)
    renderSide(matrix4f, matrix3f, consumer, 0, length, -size, size, -size, -size, 0, 1, v1, v2, 0, 0, 1, color, alpha);
    // 4 (Back)
    renderSide(matrix4f, matrix3f, consumer, 0, length, size, -size, size, size, 0, 1, v1, v2, 0, 0, -1, color, alpha);

    poseStack.popPose();
  }

  private static void renderSide(Matrix4f m4, Matrix3f m3, VertexConsumer consumer, float x1, float x2, float y1, float y2, float z1, float z2, float u1, float u2, float v1, float v2, float nx, float ny, float nz, int color, int alpha) {
    consumer.vertex(m4, x1, y1, z1).color(color, color, color, alpha).uv(u1, v1).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x2, y1, z1).color(color, color, color, alpha).uv(u2, v1).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x2, y2, z2).color(color, color, color, alpha).uv(u2, v2).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x1, y2, z2).color(color, color, color, alpha).uv(u1, v2).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
  }
}
