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
package com.peco2282.bcreborn.transport.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class EnergyPipeRenderer implements BlockEntityRenderer<PipeBlockEntity> {

  // 通常時の色: 黄色
  private static final float R_NORMAL = 1.0f;
  private static final float G_NORMAL = 0.8f;
  private static final float B_NORMAL = 0.0f;

  // オーバーロード時の色: 赤
  private static final float R_OVERLOAD = 1.0f;
  private static final float G_OVERLOAD = 0.1f;
  private static final float B_OVERLOAD = 0.0f;

  // パイプ腕の最小断面半幅（エネルギー0時）
  private static final float MIN_HALF = 0.05f;
  // パイプ腕の最大断面半幅（エネルギー最大時）
  private static final float MAX_HALF = 0.125f;

  // 中心部の固定サイズ
  private static final float CENTER_MIN = 0.5f - MAX_HALF;
  private static final float CENTER_MAX = 0.5f + MAX_HALF;

  // エネルギーテクスチャ（白いプレーンテクスチャを流用）
  private static final ResourceLocation ENERGY_TEXTURE =
    ResourceLocation.withDefaultNamespace("block/white_concrete");

  public EnergyPipeRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(PipeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                     MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    EnergyTransportModule module = blockEntity.getEnergyTransportModule();
    if (module == null) return;

    double[] internalPower = module.getInternalPower();
    int maxPower = module.getMaxPower();
    boolean overloaded = module.isOverloaded();

    // いずれかの方向にエネルギーが流れているか確認
    double totalPower = 0;
    for (double p : internalPower) totalPower += p;
    if (totalPower <= 0) return;

    float r = overloaded ? R_OVERLOAD : R_NORMAL;
    float g = overloaded ? G_OVERLOAD : G_NORMAL;
    float b = overloaded ? B_OVERLOAD : B_NORMAL;
    float a = 1.0f;

    TextureAtlasSprite sprite = Minecraft.getInstance()
      .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
      .apply(ENERGY_TEXTURE);

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
    Matrix4f matrix = poseStack.last().pose();

    BlockState state = blockEntity.getBlockState();

    // 中心部を描画（最大サイズ固定）
    renderCube(matrix, consumer, sprite,
      CENTER_MIN, CENTER_MIN, CENTER_MIN,
      CENTER_MAX, CENTER_MAX, CENTER_MAX,
      r, g, b, a, packedLight);

    // 各方向の腕を描画（接続されている方向のみ）
    for (Direction dir : Direction.values()) {
      if (!isConnected(state, dir)) continue;

      int side = dir.get3DDataValue();
      float ratio = (maxPower > 0) ? Math.min(1.0f, (float) internalPower[side] / maxPower) : 0.0f;
      // 0.7乗でBuildCraft同様の非線形スケール感
      float scaledRatio = (float) Math.pow(ratio, 0.7);
      float half = MIN_HALF + (MAX_HALF - MIN_HALF) * scaledRatio;

      renderArm(matrix, consumer, sprite, dir, half, r, g, b, a, packedLight);
    }
  }

  private boolean isConnected(BlockState state, Direction dir) {
    return switch (dir) {
      case NORTH -> state.getValue(PipeBlock.NORTH);
      case SOUTH -> state.getValue(PipeBlock.SOUTH);
      case EAST -> state.getValue(PipeBlock.EAST);
      case WEST -> state.getValue(PipeBlock.WEST);
      case UP -> state.getValue(PipeBlock.UP);
      case DOWN -> state.getValue(PipeBlock.DOWN);
    };
  }

  private void renderArm(Matrix4f matrix, VertexConsumer consumer, TextureAtlasSprite sprite,
                         Direction dir, float half, float r, float g, float b, float a, int packedLight) {
    float minX, minY, minZ, maxX, maxY, maxZ;
    float lo = 0.5f - half;
    float hi = 0.5f + half;

    switch (dir) {
      case EAST -> {
        minX = CENTER_MAX;
        maxX = 1.0f;
        minY = lo;
        maxY = hi;
        minZ = lo;
        maxZ = hi;
      }
      case WEST -> {
        minX = 0.0f;
        maxX = CENTER_MIN;
        minY = lo;
        maxY = hi;
        minZ = lo;
        maxZ = hi;
      }
      case NORTH -> {
        minX = lo;
        maxX = hi;
        minY = lo;
        maxY = hi;
        minZ = 0.0f;
        maxZ = CENTER_MIN;
      }
      case SOUTH -> {
        minX = lo;
        maxX = hi;
        minY = lo;
        maxY = hi;
        minZ = CENTER_MAX;
        maxZ = 1.0f;
      }
      case UP -> {
        minX = lo;
        maxX = hi;
        minY = CENTER_MAX;
        maxY = 1.0f;
        minZ = lo;
        maxZ = hi;
      }
      case DOWN -> {
        minX = lo;
        maxX = hi;
        minY = 0.0f;
        maxY = CENTER_MIN;
        minZ = lo;
        maxZ = hi;
      }
      default -> {
        return;
      }
    }

    renderCube(matrix, consumer, sprite, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a, packedLight);
  }

  private void renderCube(Matrix4f matrix, VertexConsumer consumer, TextureAtlasSprite sprite,
                          float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                          float r, float g, float b, float a, int packedLight) {
    float u0 = sprite.getU0();
    float u1 = sprite.getU1();
    float v0 = sprite.getV0();
    float v1 = sprite.getV1();

    // DOWN face (Y-)
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v0).uv2(packedLight).normal(0, -1, 0).endVertex();

    // UP face (Y+)
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(packedLight).normal(0, 1, 0).endVertex();

    // NORTH face (Z-)
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u0, v0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u1, v0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u1, v1).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u0, v1).uv2(packedLight).normal(0, 0, -1).endVertex();

    // SOUTH face (Z+)
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(packedLight).normal(0, 0, 1).endVertex();

    // WEST face (X-)
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u1, v1).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v1).uv2(packedLight).normal(-1, 0, 0).endVertex();

    // EAST face (X+)
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u0, v0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v1).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u0, v1).uv2(packedLight).normal(1, 0, 0).endVertex();
  }

  @Override
  public boolean shouldRenderOffScreen(PipeBlockEntity blockEntity) {
    return false;
  }
}
