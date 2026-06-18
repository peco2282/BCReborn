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
package com.peco2282.bcreborn.robotics.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RobotItemRenderer extends BlockEntityWithoutLevelRenderer {
  public static final RobotItemRenderer INSTANCE = new RobotItemRenderer();
  private final ModelPart box;

  public RobotItemRenderer() {
    super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    this.box = Minecraft.getInstance().getEntityModels().bakeLayer(RobotModelLayers.ROBOT).getChild("box");
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    ResourceLocation texture = RobotItem.getRobotNBT(stack).getRobotTexture();

    poseStack.pushPose();
    poseStack.translate(0.5, 0.5, 0.5);

    poseStack.mulPose(Axis.XP.rotationDegrees(22.5F));
    poseStack.mulPose(Axis.YP.rotationDegrees(135F));

    // モデルを描画
    this.box.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(texture)), packedLight, packedOverlay);

    poseStack.popPose();
  }
}
