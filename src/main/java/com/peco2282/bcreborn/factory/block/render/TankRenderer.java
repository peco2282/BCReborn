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

public class TankRenderer implements BlockEntityRenderer<TankBlockEntity> {

  public TankRenderer(BlockEntityRendererProvider.Context context) {
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
    // 0.75F, 0.998F, 0.75F は元の比率を維持するための値
    // 液体の高さだけを調整し、テクスチャの引き伸ばしを防ぐ
    float height = 0.998F * fillRatio;

    renderCube(matrix, consumer, sprite, 0.125F, 0, 0.125F, 0.875F, height, 0.875F, r, g, b, a, packedLight);

    poseStack.popPose();
  }

  private void renderCube(Matrix4f matrix, VertexConsumer consumer, TextureAtlasSprite sprite, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a, int packedLight) {
    float u0 = sprite.getU0();
    float u1 = sprite.getU1();
    float v0 = sprite.getV0();
    float v1 = sprite.getV1();

    // 縦方向のUVを調整 (maxYに合わせてテクスチャの下側を使う)
    // MinecraftのV座標は上が0、下が1。
    // 液体の高さが半分(0.5)なら、テクスチャのVは 1.0(底) から 0.5(中間) になるべき。
    // ※ 1ブロック分の高さを 0.998F と想定
    float vHeight = (v1 - v0) * (maxY - minY) / 0.998F;
    float vBottom = v1;
    float vTop = v1 - vHeight;

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
    // 横方向のUV (u0からu1) も、テクスチャの 0.5倍 (0.5/1.0) だけ使うように調整
    float uWidth = (u1 - u0) * 0.75F;
    float uStart = u0;
    float uEnd = u0 + uWidth;

    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(uStart, vBottom).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(uStart, vTop).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(uEnd, vTop).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(uEnd, vBottom).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();

    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(uStart, vBottom).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(uEnd, vBottom).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(uEnd, vTop).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(uStart, vTop).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();

    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(uStart, vBottom).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(uEnd, vBottom).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(uEnd, vTop).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(uStart, vTop).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();

    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(uStart, vBottom).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(uStart, vTop).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(uEnd, vTop).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(uEnd, vBottom).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
  }
}
