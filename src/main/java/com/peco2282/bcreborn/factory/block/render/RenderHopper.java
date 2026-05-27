/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.factory.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.factory.event.BCRebornFactoryEvent;
import com.peco2282.bcreborn.factory.block.entity.HopperBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderHopper implements BlockEntityRenderer<HopperBlockEntity> {

	private static final ResourceLocation HOPPER_TEXTURE = BCRebornFactory.location("textures/blocks/hopperBlock/top.png");
	private static final ResourceLocation HOPPER_MIDDLE_TEXTURE = BCRebornFactory.location("textures/blocks/hopperBlock/middle.png");

	private final ModelPart top;
	private final ModelPart bottom;

	public RenderHopper(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(BCRebornFactoryEvent.HOPPER_LAYER);
		this.top = root.getChild("top");
		this.bottom = root.getChild("bottom");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("top",
				CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 1.0F, -8.0F, 16.0F, 7.0F, 16.0F),
				PartPose.offset(8.0F, 8.0F, 8.0F));

		root.addOrReplaceChild("bottom",
				CubeListBuilder.create().texOffs(0, 23).addBox(-3.0F, -8.0F, -3.0F, 6.0F, 3.0F, 6.0F),
				PartPose.offset(8.0F, 8.0F, 8.0F));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void render(HopperBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		// ブロックの座標系に合わせる
		poseStack.translate(0, 0, 0);

		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(HOPPER_TEXTURE));
		top.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay);

		// Middle part (ModelFrustum) の代わり
		// 1.7.10では middle = new ModelFrustum(top, 32, 0, 0, 3, 0, 8, 8, 16, 16, 7, 1F / 16F);
		// bottomWidth=8, bottomDepth=8, topWidth=16, topDepth=16, height=7
		// これは台形（錐台）状の形状。
		renderMiddle(poseStack, bufferSource.getBuffer(RenderType.entitySolid(HOPPER_MIDDLE_TEXTURE)), packedLight, packedOverlay);

		poseStack.popPose();
	}

	@SuppressWarnings("PointlessArithmeticExpression")
    private void renderMiddle(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		// 1.7.10のコードを参考:
		// GL11.glTranslated(0.005, 0, 0.005);
		// GL11.glScaled(0.99, 1, 0.99);
		poseStack.translate(0.005, 0, 0.005);
		poseStack.scale(0.99F, 1.0F, 0.99F);

		Matrix4f matrix = poseStack.last().pose();

		// ModelFrustumの描画ロジックを簡略化して実装
		// bottomWidth=8, bottomDepth=8 (at y=0)
		// topWidth=16, topDepth=16 (at y=7)
		// 原点は (8,8,8) からのオフセット
		// 1.7.10 ModelFrustum originX=0, originY=3, originZ=0

		float x1 = 4.0F / 16.0F, x2 = 12.0F / 16.0F; // bottom
		float tx1 = 0.0F, tx2 = 1.0F; // top
		float y1 = 9.0F / 16.0F, y2 = 16.0F / 16.0F;
		float z1 = 4.0F / 16.0F, z2 = 12.0F / 16.0F; // bottom
		float tz1 = 0.0F, tz2 = 1.0F; // top

		// 4つの側面を描画
		// North
		renderQuad(matrix, consumer, tx1, y2, tz1, tx2, y2, tz1, x2, y1, z1, x1, y1, z1, packedLight);
		// South
		renderQuad(matrix, consumer, tx2, y2, tz2, tx1, y2, tz2, x1, y1, z2, x2, y1, z2, packedLight);
		// West
		renderQuad(matrix, consumer, tx1, y2, tz2, tx1, y2, tz1, x1, y1, z1, x1, y1, z2, packedLight);
		// East
		renderQuad(matrix, consumer, tx2, y2, tz1, tx2, y2, tz2, x2, y1, z2, x2, y1, z1, packedLight);

		poseStack.popPose();
	}

	private void renderQuad(Matrix4f matrix, VertexConsumer consumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int packedLight) {
		consumer.vertex(matrix, x1, y1, z1).color(255, 255, 255, 255).uv(0, 0).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
		consumer.vertex(matrix, x2, y2, z2).color(255, 255, 255, 255).uv(1, 0).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
		consumer.vertex(matrix, x3, y3, z3).color(255, 255, 255, 255).uv(1, 1).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
		consumer.vertex(matrix, x4, y4, z4).color(255, 255, 255, 255).uv(0, 1).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
	}
}
