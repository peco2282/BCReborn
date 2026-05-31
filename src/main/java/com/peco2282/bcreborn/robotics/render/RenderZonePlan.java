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

public class RenderZonePlan implements BlockEntityRenderer<ZonePlanBlockEntity> {
  private static final float Z_OFFSET = 1.0005F;
  private final Map<ZonePlanBlockEntity, DynamicTexture> textures = new HashMap<>();
  private final Map<ZonePlanBlockEntity, ResourceLocation> resourceLocations = new HashMap<>();

  public RenderZonePlan(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(ZonePlanBlockEntity zonePlan, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    boolean firstRender = !textures.containsKey(zonePlan);
    if (firstRender) {
      DynamicTexture texture = new DynamicTexture(16, 16, true);
      textures.put(zonePlan, texture);
      resourceLocations.put(zonePlan, Minecraft.getInstance().getTextureManager().register("zone_plan_preview_" + zonePlan.getBlockPos().asLong(), texture));
    }

    DynamicTexture dynamicTexture = textures.get(zonePlan);
    byte[] previewColors = zonePlan.getPreviewTexture(firstRender);

    if (previewColors != null) {
      for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 10; x++) {
          int col = MapColor.byId(previewColors[y * 10 + x] & 0xFF).col;
          if (((x & 1) != (y & 1))) {
            int r = (col >> 16) & 0xFF;
            int g = (col >> 8) & 0xFF;
            int b = col & 0xFF;
            r = r * 15 / 16;
            g = g * 15 / 16;
            b = b * 15 / 16;
            col = (r << 16) | (g << 8) | b;
          }
          dynamicTexture.getPixels().setPixelRGBA(x + 3, y + 3, 0xFF000000 | (col & 0x00FFFFFF)); // ARGB vs ABGR might need check
        }
      }
      dynamicTexture.upload();
    }

    poseStack.pushPose();
    poseStack.translate(0.5D, 0.5D, 0.5D);
    poseStack.scale(Z_OFFSET, Z_OFFSET, Z_OFFSET);
    poseStack.translate(-0.5D, -0.5D, -0.5D);

    ResourceLocation textureLoc = resourceLocations.get(zonePlan);
    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(textureLoc));

    // Draw the front face with the preview texture
    // Assuming North for simplicity, but should follow block orientation
    renderFace(poseStack, vertexConsumer, packedLight);

    poseStack.popPose();
  }

  private void renderFace(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();

    // Assuming front face (Z=0 or Z=1 depending on orientation)
    // For now let's just draw a quad at Z=1.0001
    float z = 1.0F;
    consumer.vertex(matrix4f, 0, 0, z).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, 1).endVertex();
    consumer.vertex(matrix4f, 1, 0, z).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, 1).endVertex();
    consumer.vertex(matrix4f, 1, 1, z).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, 1).endVertex();
    consumer.vertex(matrix4f, 0, 1, z).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0, 0, 1).endVertex();
  }
}
