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
package com.peco2282.bcreborn.common.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
    // EngineBlockEntity.getPistonProgress は既に 0.0 -> 1.0 -> 0.0 の往復値を返す
    float progress = engine.getPistonProgress(partialTick);
    float offset = progress * 7.99f;
    model.setMovingOffset(offset); // Y軸負方向（UP向き基準）
    System.out.println("offset: " + offset + " Progresses: " + progress);

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
    for (int i = 0; i < 8; i += 2) {
      model.setMovingOffset(offset + i);
      model.renderChamber(poseStack, bufferSource.getBuffer(RenderType.entityCutout(chamberTex)), LightTexture.FULL_BRIGHT, packedOverlay);
    }

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
