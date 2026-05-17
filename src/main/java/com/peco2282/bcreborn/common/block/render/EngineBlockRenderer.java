package com.peco2282.bcreborn.common.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.common.block.entity.EngineTextures;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class EngineBlockRenderer<E extends EngineBlockEntity<E>> implements BlockEntityRenderer<E> {
  public static final ModelLayerLocation LAYER_LOCATION = ResourceBuilder.core("engine").asModel("main");

  private final EngineModel model;

  public EngineBlockRenderer(
      BlockEntityRendererProvider.Context p_173962_1_
  ) {
    EntityModelSet modelSet = p_173962_1_.getModelSet();
    this.model = new EngineModel(modelSet.bakeLayer(LAYER_LOCATION));
  }

  public static LayerDefinition createLayer() {
    return EngineModel.createLayer();
  }

  @Override
  public void render(E engine, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

    poseStack.pushPose();

    // --- 向きに応じた回転 ---
    Direction facing = engine.orientation;
    applyFacingRotation(poseStack, facing);

    // --- ピストンアニメーション（partialTick補間） ---
    // 本家BuildCraft準拠: progress 0→0.5でstep 0→7.99、0.5→1でstep 7.99→0
    float progress = engine.getPistonProgress(partialTick);
    float step;
    if (progress > .5f) {
      step = 7.99f - (progress - .5f) * 2f * 7.99f;
    } else {
      step = progress * 2f * 7.99f;
    }
    float offset = step;// 16f; // movingBoxのみ動く（trunkは固定）
    model.setMovingOffset(offset); // Y軸負方向（UP向き基準）

    // --- テクスチャ取得 ---
    ResourceLocation baseTex = EngineTextures.getBaseTexture(engine);
    ResourceLocation chamberTex = EngineTextures.getChamberTexture(engine);
    ResourceLocation trunkTex = EngineTextures.getTrunkTexture(engine);

    // --- 各パーツを描画（getBuffer直後に描画してバッファフラッシュを防ぐ） ---
    // base と moving は同じbaseTexture（本家準拠）
    model.renderBase(poseStack, bufferSource.getBuffer(RenderType.entityCutout(baseTex)), LightTexture.FULL_BRIGHT, packedOverlay);
    model.renderMoving(poseStack, bufferSource.getBuffer(RenderType.entityCutout(baseTex)), LightTexture.FULL_BRIGHT, packedOverlay);
    model.renderChamber(poseStack, bufferSource.getBuffer(RenderType.entityCutout(chamberTex)), LightTexture.FULL_BRIGHT, packedOverlay);
    model.renderTrunk(poseStack, bufferSource.getBuffer(RenderType.entityCutout(trunkTex)), LightTexture.FULL_BRIGHT, packedOverlay);

    poseStack.popPose();
  }

  private void applyFacingRotation(PoseStack poseStack, Direction facing) {
    // モデルの原点をブロック中心に移動
    poseStack.translate(0.5, 0.5, 0.5);
    switch (facing) {
      case UP -> {
        // デフォルト（回転なし）
      }
      case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
      case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
      case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
      case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
      case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
    }
    poseStack.translate(-0.5, -0.5, -0.5);
  }

  @Override
  public boolean shouldRenderOffScreen(E p_112306_) {
    return true;
  }
}
