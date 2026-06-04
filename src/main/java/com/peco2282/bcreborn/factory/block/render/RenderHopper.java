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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.factory.block.entity.HopperBlockEntity;
import com.peco2282.bcreborn.factory.event.BCRebornFactoryEvent;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RenderHopper implements BlockEntityRenderer<HopperBlockEntity> {

  private static final ResourceLocation HOPPER_TEXTURE = BCRebornFactory.location("textures/block/hopper_block/top.png");
  private static final ResourceLocation HOPPER_MIDDLE_TEXTURE = BCRebornFactory.location("textures/block/hopper_block/middle.png");

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
      CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -7.0F, -8.0F, 16.0F, 7.0F, 16.0F),
      PartPose.offset(8.0F, 16.0F, 8.0F));

    root.addOrReplaceChild("bottom",
      CubeListBuilder.create().texOffs(0, 23).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 3.0F, 6.0F),
      PartPose.offset(8.0F, 0.0F, 8.0F));

    return LayerDefinition.create(mesh, 64, 32);
  }

  @Override
  public void render(HopperBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    poseStack.pushPose();
    int light = LevelRenderer.getLightColor(
      blockEntity.getLevel(),
      blockEntity.getBlockPos().above()
    );

    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(HOPPER_TEXTURE));
    top.render(poseStack, vertexConsumer, light, packedOverlay);
    bottom.render(poseStack, vertexConsumer, light, packedOverlay);

    // Middle part (ModelFrustum)
    renderMiddle(poseStack, bufferSource.getBuffer(RenderType.entityCutout(HOPPER_MIDDLE_TEXTURE)), light, packedOverlay);

    poseStack.popPose();
  }

  private void renderMiddle(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
    poseStack.pushPose();
    // 1.7.10: GL11.glTranslated(0.005, 0, 0.005); GL11.glScaled(0.99, 1, 0.99);
    poseStack.translate(0.005, 0, 0.005);
    poseStack.scale(0.99F, 1.0F, 0.99F);

    Matrix4f matrix = poseStack.last().pose();

    // 1.7.10 ModelFrustum middle = new ModelFrustum(top, 32, 0, 0, 3, 0, 8, 8, 16, 16, 7, 1F / 16F);
    // textureOffsetX = 32, textureOffsetY = 0
    // bottomWidth = 8, bottomDepth = 8, topWidth = 16, topDepth = 16, height = 7

    // Middle starts at Y=3/16 and ends at Y=10/16 (height 7/16)
    float yMin = 3.0F / 16.0F;
    float yMax = 10.0F / 16.0F;

    float bDelta = 4.0F / 16.0F; // (16-8)/2 / 16
    float tDelta = 0.0F / 16.0F; // (16-16)/2 / 16

    float xMin = 0.0F;
    float xMax = 1.0F;
    float zMin = 0.0F;
    float zMax = 1.0F;

    // Vertices (0-7)
    float[] v0 = {xMin + bDelta, yMin, zMin + bDelta};
    float[] v1 = {xMax - bDelta, yMin, zMin + bDelta};
    float[] v2 = {xMax - tDelta, yMax, zMin + tDelta};
    float[] v3 = {xMin + tDelta, yMax, zMin + tDelta};
    float[] v4 = {xMin + bDelta, yMin, zMax - bDelta};
    float[] v5 = {xMax - bDelta, yMin, zMax - bDelta};
    float[] v6 = {xMax - tDelta, yMax, zMax - tDelta};
    float[] v7 = {xMin + tDelta, yMax, zMax - tDelta};

    // Texture mapping (64x32)
    float tw = 64.0F;
    float th = 32.0F;
    int offX = 32;
    int offY = 0;
    int d = 16;
    int w = 16;
    int h = 7;

    // Quad 0 (East/Right): v1, v5, v6, v2 -> original [v19, v15, v16, v20] where 19=v5, 15=v1, 16=v2, 20=v6
    renderQuad(matrix, consumer, v5, v1, v2, v6,
      (offX + d + w) / tw, (offY + d) / th,
      (offX + d + w + d) / tw, (offY + d + h) / th, packedLight, packedOverlay, 1, 0, 0);

    // Quad 1 (West/Left): v23, v18, v21, v17 where 23=v0, 18=v4, 21=v7, 17=v3
    renderQuad(matrix, consumer, v0, v4, v7, v3,
      offX / tw, (offY + d) / th,
      (offX + d) / tw, (offY + d + h) / th, packedLight, packedOverlay, -1, 0, 0);

    // Quad 2 (Bottom): v19, v18, v23, v15 where 19=v5, 18=v4, 23=v0, 15=v1
    renderQuad(matrix, consumer, v5, v4, v0, v1,
      (offX + d) / tw, offY / th,
      (offX + d + w) / tw, (offY + d) / th, packedLight, packedOverlay, 0, -1, 0);

    // Quad 3 (Top): v16, v17, v21, v20 where 16=v2, 17=v3, 21=v7, 20=v6
    renderQuad(matrix, consumer, v2, v3, v7, v6,
      (offX + d + w) / tw, (offY + d) / th,
      (offX + d + w + w) / tw, offY / th, packedLight, packedOverlay, 0, 1, 0);

    // Quad 4 (Front/North): v15, v23, v17, v16 where 15=v1, 23=v0, 17=v3, 16=v2
    renderQuad(matrix, consumer, v1, v0, v3, v2,
      (offX + d) / tw, (offY + d) / th,
      (offX + d + w) / tw, (offY + d + h) / th, packedLight, packedOverlay, 0, 0, -1);

    // Quad 5 (Back/South): v18, v19, v20, v21 where 18=v4, 19=v5, 20=v6, 21=v7
    renderQuad(matrix, consumer, v4, v5, v6, v7,
      (offX + d + w + d) / tw, (offY + d) / th,
      (offX + d + w + d + w) / tw, (offY + d + h) / th, packedLight, packedOverlay, 0, 0, 1);

    poseStack.popPose();
  }

  private void renderQuad(Matrix4f matrix, VertexConsumer consumer, float[] v1, float[] v2, float[] v3, float[] v4, float u1, float v1u, float u2, float v2u, int packedLight, int packedOverlay, float nx, float ny, float nz) {
    consumer.vertex(matrix, v1[0], v1[1], v1[2]).color(255, 255, 255, 255).uv(u1, v1u).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
    consumer.vertex(matrix, v2[0], v2[1], v2[2]).color(255, 255, 255, 255).uv(u2, v1u).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
    consumer.vertex(matrix, v3[0], v3[1], v3[2]).color(255, 255, 255, 255).uv(u2, v2u).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
    consumer.vertex(matrix, v4[0], v4[1], v4[2]).color(255, 255, 255, 255).uv(u1, v2u).overlayCoords(packedOverlay).uv2(packedLight).normal(nx, ny, nz).endVertex();
  }
}
