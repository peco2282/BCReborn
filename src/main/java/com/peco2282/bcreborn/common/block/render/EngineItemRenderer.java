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
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.common.block.entity.EngineTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class EngineItemRenderer extends BlockEntityWithoutLevelRenderer {
  public static final EngineItemRenderer INSTANCE = new EngineItemRenderer();
  private static final ResourceLocation MISSING = ResourceLocation.withDefaultNamespace("missingno");
  private final EngineModel model;

  public EngineItemRenderer() {
    super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    model = new EngineModel(Minecraft.getInstance().getEntityModels().bakeLayer(EngineBlockRenderer.LAYER_LOCATION));
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    Block block = ((BlockItem) stack.getItem()).getBlock();
    if (!(block instanceof EngineBlock)) return;

    ResourceLocation baseTex = MISSING;
    ResourceLocation chamberTex = MISSING;
    ResourceLocation trunkTex = EngineTextures.TRUNK_BLUE_TEXTURE;

    // アイテムからエンジンタイプを判別してテクスチャを決定
    String path = block.builtInRegistryHolder().key().location().getPath();
    if (path.contains("wood")) {
      baseTex = getTex("wood_engine", "base");
      chamberTex = getTex("wood_engine", "chamber");
    } else if (path.contains("stone")) {
      baseTex = getTex("stone_engine", "base");
      chamberTex = getTex("stone_engine", "chamber");
    } else if (path.contains("iron")) {
      baseTex = getTex("iron_engine", "base");
      chamberTex = getTex("iron_engine", "chamber");
    } else if (path.contains("creative")) {
      baseTex = getTex("creative_engine", "base");
      chamberTex = getTex("creative_engine", "chamber");
    }

    poseStack.pushPose();
    poseStack.scale(1.2F, 1.2F, 1.2F);

    poseStack.translate(0.62, 0.2, 0.2);

    poseStack.mulPose(Axis.YP.rotationDegrees(65F));

    poseStack.translate(-0.5, -0.5, -0.5);

    model.setMovingOffset(0);

    model.renderBase(poseStack, bufferSource.getBuffer(RenderType.entityCutout(baseTex)), packedLight, packedOverlay);
    model.renderMoving(poseStack, bufferSource.getBuffer(RenderType.entityCutout(baseTex)), packedLight, packedOverlay);
    model.renderChamber(poseStack, bufferSource.getBuffer(RenderType.entityCutout(chamberTex)), packedLight, packedOverlay);
    model.renderTrunk(poseStack, bufferSource.getBuffer(RenderType.entityCutout(trunkTex)), packedLight, packedOverlay);

    poseStack.popPose();
  }

  private ResourceLocation getTex(String folder, String name) {
    String modid = folder.contains("wood") ? "bcreborncore" : "bcrebornenergy";
    return ResourceLocation.fromNamespaceAndPath(modid, "textures/block/" + folder + "/" + name + ".png");
  }
}
