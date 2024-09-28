package peco2282.bcreborn.core.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import peco2282.bcreborn.core.block.entity.EngineBlockEntity;

public class EngineRenderer implements BlockEntityRenderer<EngineBlockEntity> {
  private final BlockRenderDispatcher dispatcher;

  public EngineRenderer(BlockEntityRendererProvider.Context context) {
    this.dispatcher = context.getBlockRenderDispatcher();
  }

  public void render(EngineBlockEntity entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
//    System.out.println(dispatcher.getBlockModel(entity.getBlockState()).isCustomRenderer());
    // エンジンが動作中の場合はアニメーションを描画
    if (entity.isActive(entity.getLevel(), entity.getBlockPos(), entity.getLevel().getBlockState(entity.getBlockPos()))) {
      // エンジンの動きや回転をここで描画
      matrixStack.pushPose();
      // エンジンの回転や移動などのアニメーション処理
      matrixStack.popPose();
    }
  }
}
