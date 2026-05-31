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
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class RenderTank implements BlockEntityRenderer<TankBlockEntity> {

  public RenderTank(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(TankBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    FluidStack liquid = blockEntity.tank.getFluid();
    if (liquid.isEmpty() || liquid.getAmount() <= 0) {
      return;
    }

    float fillRatio = (float) liquid.getAmount() / (float) blockEntity.tank.getCapacity();

    IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(liquid.getFluid());
    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidExt.getStillTexture(liquid));
    int color = fluidExt.getTintColor(liquid);
    float r = ((color >> 16) & 0xFF) / 255.0f;
    float g = ((color >> 8) & 0xFF) / 255.0f;
    float b = (color & 0xFF) / 255.0f;
    float a = ((color >> 24) & 0xFF) / 255.0f;
    if (a == 0) a = 1.0f;

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
    Matrix4f matrix = poseStack.last().pose();

    poseStack.pushPose();
    // 1.7.10: GL11.glTranslatef((float) x + 0.125F, (float) y + 0.5F, (float) z + 0.125F);
    // 1.7.10: GL11.glScalef(0.75F, 0.999F, 0.75F);
    // 1.7.10: GL11.glTranslatef(0, -0.5F, 0);
    poseStack.translate(0.125F, 0.001F, 0.125F);
    poseStack.scale(0.75F, 0.998F * fillRatio, 0.75F);

    renderCube(matrix, consumer, sprite, 0, 0, 0, 1, 1, 1, r, g, b, a, packedLight);

    poseStack.popPose();
  }

  private void renderCube(Matrix4f matrix, VertexConsumer consumer, TextureAtlasSprite sprite, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a, int packedLight) {
    float u0 = sprite.getU0();
    float u1 = sprite.getU1();
    float v0 = sprite.getV0();
    float v1 = sprite.getV1();

    // Top
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();

    // Bottom
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();

    // Sides
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();

    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();

    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();

    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
  }
}
