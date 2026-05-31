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
import com.mojang.math.Axis;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemPipeRenderer implements BlockEntityRenderer<PipeBlockEntity> {

  private static final int MAX_ITEMS_TO_RENDER = 10;
  private static final float ITEM_SCALE = 0.4f;

  private final ItemRenderer itemRenderer;

  public ItemPipeRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(PipeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                     MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    List<TravelingItem> items = blockEntity.getTravelingItems();
    if (items.isEmpty()) return;

    int count = Math.min(items.size(), MAX_ITEMS_TO_RENDER);
    for (int i = 0; i < count; i++) {
      TravelingItem travelingItem = items.get(i);
      renderTravelingItem(travelingItem, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
  }

  private void renderTravelingItem(TravelingItem travelingItem, float partialTick,
                                   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    ItemStack stack = travelingItem.getStack();
    if (stack.isEmpty()) return;

    // 現tickのprogressから次tickの予測位置へ外挿補間（速度×partialTickを加算）
    float progress = travelingItem.getProgress() + travelingItem.getSpeed() * partialTick;
    System.out.println("progress: " + progress + ", speed: " + travelingItem.getSpeed() + ", partialTick: " + partialTick);

    // アイテムの3D座標を計算（パイプ中心 = 0.5, 0.5, 0.5）
    float x = 0.5f;
    float y = 0.5f;
    float z = 0.5f;

    Direction entryDir = travelingItem.getEntryDirection();
    Direction nextDir = travelingItem.getNextDirection();

    if (progress < 0.5f) {
      // エントリー方向から中心へ向かっている
      // entryDirの逆方向の端（progress=0）から中心（progress=0.5）へ
      Direction fromEdge = entryDir.getOpposite();
      float t = progress * 2.0f; // 0.0 -> 1.0 (端から中心へ)
      x = 0.5f + fromEdge.getStepX() * 0.5f * (1.0f - t);
      y = 0.5f + fromEdge.getStepY() * 0.5f * (1.0f - t);
      z = 0.5f + fromEdge.getStepZ() * 0.5f * (1.0f - t);
    } else if (nextDir != null) {
      // 中心からnextDirection方向の端へ向かっている
      float t = (progress - 0.5f) * 2.0f; // 0.0 -> 1.0 (中心から端へ)
      x = 0.5f + nextDir.getStepX() * 0.5f * t;
      y = 0.5f + nextDir.getStepY() * 0.5f * t;
      z = 0.5f + nextDir.getStepZ() * 0.5f * t;
    }
    // nextDirがnullの場合は中心に留まる

    poseStack.pushPose();
    poseStack.translate(x, y, z);
    poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

    // アイテムモデルが3Dブロック系の場合は少し回転させて見栄えを良くする
    BakedModel model = itemRenderer.getModel(stack, null, null, 0);
    if (model.isGui3d()) {
      poseStack.mulPose(Axis.YP.rotationDegrees((float) (System.currentTimeMillis() % 36000L) / 100.0f));
    }

    itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay,
      poseStack, bufferSource, null, 0);

    poseStack.popPose();
  }
}
