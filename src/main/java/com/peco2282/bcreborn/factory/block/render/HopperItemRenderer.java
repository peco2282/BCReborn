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
package com.peco2282.bcreborn.factory.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HopperItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final HopperItemRenderer INSTANCE = new HopperItemRenderer();

    private final RenderHopper renderer;

    public HopperItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        renderer = new RenderHopper(Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        // アイテムとしての微調整
      poseStack.scale(1.2F, 1.2F, 1.2F);

      poseStack.translate(0.62, 0.2, 0.2);

      poseStack.mulPose(Axis.YP.rotationDegrees(65F));

      poseStack.translate(-0.5, -0.5, -0.5);

        renderer.renderInternal(poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
