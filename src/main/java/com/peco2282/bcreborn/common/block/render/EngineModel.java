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
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class EngineModel {
  final ModelPart base;
  final ModelPart trunk;
  final ModelPart moving;
  final ModelPart chamber;
  private final float movingInitialY;

  public EngineModel(ModelPart root) {
    this.base = root.getChild("base");
    this.trunk = root.getChild("trunk");
    this.moving = root.getChild("moving");
    this.chamber = root.getChild("chamber");
    this.movingInitialY = this.moving.y;
  }

  public static LayerDefinition createLayer() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();

    // base部分（固定）
    root.addOrReplaceChild("base",
        CubeListBuilder.create()
            .texOffs(0, 1)
            .addBox(-8.0F, -8.0F, -8.0F, 16, 4, 16),
        PartPose.offset(8.0F, 8.0F, 8.0F)
    );

    // trunk部分（上下に動くピストン）
    root.addOrReplaceChild("trunk",
        CubeListBuilder.create()
            .texOffs(1, 1)
            .addBox(-4, -4, -4, 8, 12, 8),
        PartPose.offset(8.0F, 8.0F, 8.0F));


    root.addOrReplaceChild("moving",
        CubeListBuilder.create()
            .texOffs(0, 1)
            .addBox(-8F, -4, -8F, 16, 4, 16),
        PartPose.offset(8.0F, 8.0F, 8.0F));

    // chamber部分（固定・中間層）
    root.addOrReplaceChild("chamber",
        CubeListBuilder.create()
            .texOffs(1, 1)
            .addBox(-5F, -4, -5F, 10, 2, 10),
        PartPose.offset(8.0F, 8.0F, 8.0F));

    return LayerDefinition.create(mesh, 64, 32);
  }

  public void renderBase(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
    base.render(poseStack, buffer, light, overlay);
  }

  public void renderChamber(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
    chamber.render(poseStack, buffer, light, overlay);
  }

  public void renderTrunk(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
    trunk.render(poseStack, buffer, light, overlay);
  }

  public void renderMoving(PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
    moving.render(poseStack, buffer, light, overlay);
  }

  public void setMovingOffset(float offset) {
    moving.setPos(moving.x, movingInitialY + offset, moving.z);
  }
}
