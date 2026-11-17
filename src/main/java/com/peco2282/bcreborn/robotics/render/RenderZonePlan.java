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

import java.util.Iterator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

/**
 * Original {@code RenderZonePlan}: the 10 x 8 map preview on the planner's front, in the screen of its texture
 * (pixels 3..12 x 3..10).
 */
public class RenderZonePlan implements BlockEntityRenderer<ZonePlanBlockEntity> {
  /** Just in front of the face, so the preview does not z-fight with it. */
  private static final float FACE_OFFSET = -0.0005F;
  private final Map<ZonePlanBlockEntity, DynamicTexture> textures = new HashMap<>();
  private final Map<ZonePlanBlockEntity, ResourceLocation> resourceLocations = new HashMap<>();

  public RenderZonePlan(BlockEntityRendererProvider.Context context) {
  }

  /** Frees the textures of planners that are gone. */
  private void releaseRemoved() {
    Iterator<Map.Entry<ZonePlanBlockEntity, DynamicTexture>> it = textures.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ZonePlanBlockEntity, DynamicTexture> entry = it.next();
      if (entry.getKey().isRemoved()) {
        ResourceLocation location = resourceLocations.remove(entry.getKey());
        if (location != null) Minecraft.getInstance().getTextureManager().release(location);
        it.remove();
      }
    }
  }

  @Override
  public void render(ZonePlanBlockEntity zonePlan, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    releaseRemoved();
    boolean firstRender = !textures.containsKey(zonePlan);
    if (firstRender) {
      DynamicTexture texture = new DynamicTexture(16, 16, true);
      textures.put(zonePlan, texture);
      resourceLocations.put(zonePlan, Minecraft.getInstance().getTextureManager().register("zone_plan_preview_" + zonePlan.getBlockPos().asLong(), texture));
    }

    DynamicTexture dynamicTexture = textures.get(zonePlan);
    byte[] previewColors = zonePlan.getPreviewTexture(firstRender);

    if (previewColors != null && dynamicTexture.getPixels() != null) {
      for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 10; x++) {
          int col = MapColor.byId(previewColors[y * 10 + x] & 0xFF).col;
          int r = (col >> 16) & 0xFF;
          int g = (col >> 8) & 0xFF;
          int b = col & 0xFF;
          // Original: a checkerboard, every other pixel slightly darker.
          if ((x & 1) != (y & 1)) {
            r = r * 15 / 16;
            g = g * 15 / 16;
            b = b * 15 / 16;
          }
          // NativeImage pixels are ABGR.
          dynamicTexture.getPixels().setPixelRGBA(x + 3, y + 3, 0xFF000000 | (b << 16) | (g << 8) | r);
        }
      }
      dynamicTexture.upload();
    }

    BlockState state = zonePlan.getBlockState();
    Direction facing = state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
      ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
    int light = zonePlan.getLevel() != null
      ? LevelRenderer.getLightColor(zonePlan.getLevel(), zonePlan.getBlockPos().relative(facing)) : packedLight;

    poseStack.pushPose();
    // The model's front is its north face; the block model is turned the same way.
    poseStack.translate(0.5D, 0.5D, 0.5D);
    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
    poseStack.translate(-0.5D, -0.5D, -0.5D);

    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(resourceLocations.get(zonePlan)));
    renderFace(poseStack, vertexConsumer, light);

    poseStack.popPose();
  }

  /** The north face, seen from outside: texture left is the block's east edge. */
  private void renderFace(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();
    float z = FACE_OFFSET;
    consumer.vertex(matrix4f, 1, 1, z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, -1).endVertex();
    consumer.vertex(matrix4f, 1, 0, z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, -1).endVertex();
    consumer.vertex(matrix4f, 0, 0, z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, -1).endVertex();
    consumer.vertex(matrix4f, 0, 1, z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, -1).endVertex();
  }
}
