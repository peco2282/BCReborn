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
    poseStack.translate(-blockEntity.getBlockPos().getX() + 0.001, -blockEntity.getBlockPos().getY() + 0.001, -blockEntity.getBlockPos().getZ() + 0.001);
    System.out.println("Lasers");
    for (LaserData laser : blockEntity.lasers) {
      System.out.println(laser);
      laser.iterateTexture();
      laser.update();
      LaserRenderer.renderLaser(poseStack, buffer, laser, partialTicks);
    }

    System.out.println("Signals");
    for (LaserData laser : blockEntity.signals) {
      System.out.println(laser);
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
