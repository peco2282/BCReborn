package com.peco2282.bcreborn.core.client.render;

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
  public static final ResourceLocation LASER_RED = BCRebornCore.location("textures/entity/laser_red.png");
  public static final ResourceLocation LASER_BLUE = BCRebornCore.location("textures/entity/laser_blue.png");
  public static final ResourceLocation LASER_STRIPES = BCRebornCore.location("textures/entity/laser_stripes.png");

  public static void renderLaser(PoseStack poseStack, MultiBufferSource buffer, LaserData laser, float partialTicks) {
    if (!laser.isVisible) return;

    ResourceLocation texture = switch (laser.kind) {
      case Red -> LASER_RED;
      case Blue -> LASER_BLUE;
      case Stripes -> LASER_STRIPES;
    };

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
    renderSide(matrix4f, matrix3f, consumer, 0, length, -size, -size, size, -size, 0, 1, v1, v2, 0, 1, 0);
    // 2 (Bottom)
    renderSide(matrix4f, matrix3f, consumer, 0, length, size, size, -size, size, 0, 1, v1, v2, 0, -1, 0);
    // 3 (Front)
    renderSide(matrix4f, matrix3f, consumer, 0, length, -size, size, -size, -size, 0, 1, v1, v2, 0, 0, 1);
    // 4 (Back)
    renderSide(matrix4f, matrix3f, consumer, 0, length, size, -size, size, size, 0, 1, v1, v2, 0, 0, -1);

    poseStack.popPose();
  }

  private static void renderSide(Matrix4f m4, Matrix3f m3, VertexConsumer consumer, float x1, float x2, float y1, float y2, float z1, float z2, float u1, float u2, float v1, float v2, float nx, float ny, float nz) {
    consumer.vertex(m4, x1, y1, z1).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x2, y1, z1).color(255, 255, 255, 255).uv(u2, v1).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x2, y2, z2).color(255, 255, 255, 255).uv(u2, v2).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
    consumer.vertex(m4, x1, y2, z2).color(255, 255, 255, 255).uv(u1, v2).overlayCoords(0).uv2(15728880).normal(m3, nx, ny, nz).endVertex();
  }
}
