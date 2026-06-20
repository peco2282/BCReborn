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
import com.mojang.math.Axis;
import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.factory.block.RefineryBlock;
import com.peco2282.bcreborn.factory.block.entity.RefineryBlockEntity;
import com.peco2282.bcreborn.factory.event.BCRebornFactoryEvent;
import net.minecraft.client.Minecraft;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class RefineryRenderer implements BlockEntityRenderer<RefineryBlockEntity> {

  private static final ResourceLocation TEXTURE = BCRebornFactory.location("textures/block/refinery_block/refinery.png");
  private final ModelPart tank;
  private final ModelPart[] magnet = new ModelPart[4];

  public RefineryRenderer(BlockEntityRendererProvider.Context context) {
    ModelPart root = context.bakeLayer(BCRebornFactoryEvent.REFINERY_LAYER);
    this.tank = root.getChild("tank");
    for (int i = 0; i < 4; i++) {
      this.magnet[i] = root.getChild("magnet_" + i);
    }
  }

  public static LayerDefinition createLayer() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();

    root.addOrReplaceChild("tank",
      CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 16.0F, 8.0F),
      PartPose.ZERO);

    for (int i = 0; i < 4; i++) {
      root.addOrReplaceChild("magnet_" + i,
        CubeListBuilder.create().texOffs(32, i * 8).addBox(0, -8.0F, -8.0F, 8.0F, 4.0F, 4.0F),
        PartPose.ZERO);
    }

    return LayerDefinition.create(mesh, 64, 32);
  }

  @Override
  public void render(RefineryBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    poseStack.pushPose();
    poseStack.translate(0.5, 0.5, 0.5);
    poseStack.scale(0.99F, 0.99F, 0.99F);

    Direction facing = blockEntity.getBlockState().getValue(RefineryBlock.HORIZONTAL_FACING);
    float angle = facing.toYRot();
    poseStack.mulPose(Axis.YP.rotationDegrees(-angle));

    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));

    // Render 3 tanks
    poseStack.pushPose();
    poseStack.translate(-0.25F, 0, -0.25F);
    tank.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    poseStack.popPose();

    poseStack.pushPose();
    poseStack.translate(-0.25F, 0, 0.25F);
    tank.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    poseStack.popPose();

    poseStack.pushPose();
    poseStack.translate(0.25F, 0, 0);
    tank.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    poseStack.popPose();

    float anim = blockEntity.animationStage;
    ModelPart theMagnet;
    float speed = blockEntity.animationSpeed;
    if (speed <= 1) theMagnet = magnet[0];
    else if (speed <= 2.5) theMagnet = magnet[1];
    else if (speed <= 4.5) theMagnet = magnet[2];
    else theMagnet = magnet[3];

    float trans1, trans2;
    if (anim <= 100) {
      trans1 = 0.75F * anim / 100F;
      trans2 = 0;
    } else if (anim <= 200) {
      trans1 = 0.75F - (0.75F * (anim - 100F) / 100F);
      trans2 = 0.75F * (anim - 100F) / 100F;
    } else {
      trans1 = 0.75F * (anim - 200F) / 100F;
      trans2 = 0.75F - (0.75F * (anim - 200F) / 100F);
    }

    poseStack.pushPose();
//    poseStack.translate(-0.51F, trans1 - 0.5F, -0.5F);
    theMagnet.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    poseStack.popPose();

    poseStack.pushPose();
    poseStack.translate(0F, 0, 0.75);
    theMagnet.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    poseStack.popPose();

    // Render fluids
    renderFluid(blockEntity.tanks[0], poseStack, bufferSource, packedLight, -0.5f, -0.5f, -0.5f, 0.0f, 0.5f, 0.0f);
    renderFluid(blockEntity.tanks[1], poseStack, bufferSource, packedLight, -0.5f, -0.5f, 0.0f, 0.0f, 0.5f, 0.5f);
    renderFluid(blockEntity.result, poseStack, bufferSource, packedLight, 0.0f, -0.5f, -0.25f, 0.5f, 0.5f, 0.25f);

    poseStack.popPose();
  }

  private void renderFluid(FluidTank tank, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
    FluidStack fluidStack = tank.getFluid();
    if (fluidStack.isEmpty()) return;

    float fillRatio = (float) fluidStack.getAmount() / tank.getCapacity();
    if (fillRatio <= 0) return;

    IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluidStack.getFluid());
    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidExt.getStillTexture(fluidStack));
    int color = fluidExt.getTintColor(fluidStack);
    float r = ((color >> 16) & 0xFF) / 255.0f;
    float g = ((color >> 8) & 0xFF) / 255.0f;
    float b = (color & 0xFF) / 255.0f;
    float a = ((color >> 24) & 0xFF) / 255.0f;
    if (a == 0) a = 1.0f;

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
    Matrix4f matrix = poseStack.last().pose();

    float currentMaxY = minY + (maxY - minY) * fillRatio;

    renderCube(matrix, consumer, sprite, minX, minY, minZ, maxX, currentMaxY, maxZ, r, g, b, a, packedLight);
  }

  private void renderCube(Matrix4f matrix, VertexConsumer consumer, TextureAtlasSprite sprite, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a, int packedLight) {
    float u0 = sprite.getU0();
    float u1 = sprite.getU1();
    float v0 = sprite.getV0();
    float v1 = sprite.getV1();

    // Top
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, 1, 0).endVertex();

    // Bottom
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, -1, 0).endVertex();

    // Sides
    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();
    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(-1, 0, 0).endVertex();

    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(1, 0, 0).endVertex();

    consumer.vertex(matrix, minX, minY, minZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();
    consumer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, -1).endVertex();

    consumer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).uv(u0, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).uv(u0, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).uv(u1, v0).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
    consumer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).uv(u1, v1).overlayCoords(0).uv2(packedLight).normal(0, 0, 1).endVertex();
  }
}
