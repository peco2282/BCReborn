package com.peco2282.bcreborn.builders.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RenderBuilderTile implements BlockEntityRenderer<BuilderBlockEntity> {

	public RenderBuilderTile(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(BuilderBlockEntity builder, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
	}
}
