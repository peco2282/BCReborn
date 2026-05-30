package com.peco2282.bcreborn.core.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MarkerBlockEntityRenderer<T extends MarkerBlockEntity> implements BlockEntityRenderer<T> {
  public MarkerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
    poseStack.pushPose();
    // BlockEntityの座標基準(0,0,0)から世界座標の差分を吸収するため、
    // LaserDataが世界座標系で保持されている場合は、blockEntity.getBlockPos()分を引く必要がある。
    // 今回、LaserDataにはマーカー座標(add(0.5, 0.5, 0.5))が設定されている。
    poseStack.translate(-blockEntity.getBlockPos().getX(), -blockEntity.getBlockPos().getY(), -blockEntity.getBlockPos().getZ());
    for (LaserData laser : blockEntity.lasers) {
      laser.iterateTexture();
      laser.update();
      LaserRenderer.renderLaser(poseStack, buffer, laser, partialTicks);
    }

    for (LaserData laser : blockEntity.signals) {
      laser.iterateTexture();
      laser.update();
      LaserRenderer.renderLaser(poseStack, buffer, laser, partialTicks);
    }

    poseStack.popPose();
  }

  @Override
  public boolean shouldRenderOffScreen(T blockEntity) {
    return true;
  }

  @Override
  public int getViewDistance() {
    return 64;
  }
}
