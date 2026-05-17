package com.peco2282.bcreborn.transport.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
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
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class FluidPipeRenderer implements BlockEntityRenderer<PipeBlockEntity> {

  // パイプ腕の断面サイズ（中心からの半幅）
  private static final float ARM_HALF = 0.125f;
  // 中心部のサイズ
  private static final float CENTER_MIN = 0.5f - ARM_HALF;
  private static final float CENTER_MAX = 0.5f + ARM_HALF;

  public FluidPipeRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(PipeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    FluidTank tank = blockEntity.getFluidTank();
    if (tank == null) return;

    FluidStack fluidStack = tank.getFluid();
    if (fluidStack.isEmpty()) return;

    int capacity = tank.getCapacity();
    int amount = fluidStack.getAmount();
    float fillRatio = Math.min(1.0f, (float) amount / capacity);

    // 液体テクスチャを取得
    IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluidStack.getFluid());
    ResourceLocation stillTexture = fluidExt.getStillTexture(fluidStack);
    if (stillTexture == null) return;

    TextureAtlasSprite sprite = Minecraft.getInstance()
        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
        .apply(stillTexture);

    int color = fluidExt.getTintColor(fluidStack);
    float r = ((color >> 16) & 0xFF) / 255.0f;
    float g = ((color >> 8) & 0xFF) / 255.0f;
    float b = (color & 0xFF) / 255.0f;
    float a = ((color >> 24) & 0xFF) / 255.0f;
    if (a == 0.0f) a = 1.0f; // アルファが0の場合は不透明扱い

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
    Matrix4f matrix = poseStack.last().pose();

    BlockState state = blockEntity.getBlockState();

    // 中心部を描画
    renderCube(matrix, consumer, sprite,
        CENTER_MIN, CENTER_MIN, CENTER_MIN,
        CENTER_MAX, CENTER_MAX, CENTER_MAX,
        r, g, b, a, packedLight);

    // 各方向の腕を描画（接続されている方向のみ）
    for (Direction dir : Direction.values()) {
      if (!isConnected(state, dir)) continue;
      renderArm(matrix, consumer, sprite, dir, fillRatio, r, g, b, a, packedLight);
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
      Direction dir, float fillRatio, float r, float g, float b, float a, int packedLight) {
    float minX, minY, minZ, maxX, maxY, maxZ;

    // 充填率に応じて液体の高さを調整（水平方向は高さ、垂直方向は全体）
    float liquidHeight = CENTER_MIN + (CENTER_MAX - CENTER_MIN) * fillRatio;

    switch (dir) {
      case EAST -> {
        minX = CENTER_MAX; maxX = 1.0f;
        minY = CENTER_MIN; maxY = liquidHeight;
        minZ = CENTER_MIN; maxZ = CENTER_MAX;
      }
      case WEST -> {
        minX = 0.0f; maxX = CENTER_MIN;
        minY = CENTER_MIN; maxY = liquidHeight;
        minZ = CENTER_MIN; maxZ = CENTER_MAX;
      }
      case NORTH -> {
        minX = CENTER_MIN; maxX = CENTER_MAX;
        minY = CENTER_MIN; maxY = liquidHeight;
        minZ = 0.0f; maxZ = CENTER_MIN;
      }
      case SOUTH -> {
        minX = CENTER_MIN; maxX = CENTER_MAX;
        minY = CENTER_MIN; maxY = liquidHeight;
        minZ = CENTER_MAX; maxZ = 1.0f;
      }
      case UP -> {
        minX = CENTER_MIN; maxX = CENTER_MAX;
        minY = CENTER_MAX; maxY = 1.0f;
        minZ = CENTER_MIN; maxZ = CENTER_MAX;
      }
      case DOWN -> {
        minX = CENTER_MIN; maxX = CENTER_MAX;
        minY = 0.0f; maxY = CENTER_MIN;
        minZ = CENTER_MIN; maxZ = CENTER_MAX;
      }
      default -> { return; }
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
