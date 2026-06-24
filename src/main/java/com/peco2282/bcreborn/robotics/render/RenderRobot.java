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

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.robots.IRobotOverlayItem;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class RenderRobot extends EntityRenderer<RobotEntity> {
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(BCRebornRobotics.location("robot"), "main");
  private static final ResourceLocation OVERLAY_RED = BCRebornRobotics.location("textures/entity/overlay_side.png");
  private static final ResourceLocation OVERLAY_CYAN = BCRebornRobotics.location("textures/entity/overlay_bottom.png");
  private static final ResourceLocation LASER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/laser.png");
  private final ModelPart box;
  private final ModelPart skullOverlayBox;
  private final ItemRenderer itemRenderer;

  private final Map<String, GameProfile> gameProfileCache = new HashMap<>();

  public RenderRobot(EntityRendererProvider.Context context) {
    super(context);
    this.itemRenderer = context.getItemRenderer();

    ModelPart root = context.bakeLayer(RobotModelLayers.ROBOT);
    this.box = root.getChild("box");
    ModelPart helmetBox = root.getChild("helmetBox");
    this.skullOverlayBox = root.getChild("skullOverlayBox");
  }

  public static LayerDefinition createLayer() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();
    root.addOrReplaceChild("box", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -4F, -4F, 8, 8, 8), PartPose.ZERO);
    root.addOrReplaceChild("helmetBox", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -4F, 8, 8, 8), PartPose.ZERO);
    root.addOrReplaceChild("skullOverlayBox", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, new CubeDeformation(0.5F)), PartPose.ZERO);
    return LayerDefinition.create(mesh, 64, 32);
  }

  @Override
  public void render(RobotEntity robot, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    poseStack.pushPose();

    float robotYaw = Mth.lerp(partialTicks, robot.yRotO, robot.getYRot());
    poseStack.mulPose(Axis.YP.rotationDegrees(-robotYaw));

    // Items in slots
    for (int i = 0; i < 4; i++) {
      ItemStack stack = robot.getItem(i);
      if (!stack.isEmpty()) {
        poseStack.pushPose();
        float tx = (i == 1 || i == 2) ? 0.125F : -0.125F;
        float tz = (i == 2 || i == 3) ? 0.125F : -0.125F;
        poseStack.translate(tx, 0, tz);
        doRenderItem(stack, poseStack, bufferSource, packedLight);
        poseStack.popPose();
      }
    }

    // Item in use
    ItemStack itemInUse = robot.itemInUse;
    if (itemInUse != null && !itemInUse.isEmpty()) {
      poseStack.pushPose();
      poseStack.mulPose(Axis.ZP.rotationDegrees(robot.itemAngle2));
      if (robot.itemActive) {
        float stage = robot.itemActiveStage; // Updated in RobotEntity.tick
        poseStack.mulPose(Axis.ZP.rotationDegrees(stage));
      }
      poseStack.translate(-0.4F, 0, 0);
      poseStack.mulPose(Axis.YP.rotationDegrees(-45F + 180F));
      poseStack.scale(0.8F, 0.8F, 0.8F);

      itemRenderer.renderStatic(itemInUse, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, robot.level(), robot.getId());
      poseStack.popPose();
    }

    // Laser
    LaserData laser = robot.laser;
    if (laser != null && laser.isVisible) {
      poseStack.pushPose();
      // Laser is rendered relative to robot position
      // RobotEntity sets laser head to its own pos, so we render at 0,0,0 relative to robot
      laser.update();
      poseStack.mulPose(Axis.YP.rotationDegrees((float) laser.angleZ));
      poseStack.mulPose(Axis.ZP.rotationDegrees((float) laser.angleY));
      VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(LASER_TEXTURE));
      renderLaserLine(poseStack, vertexConsumer, laser.renderSize, laser.laserTexAnimation, packedLight);
      poseStack.popPose();
    }

    // Robot body
    ResourceLocation texture = robot.getTexture();
    poseStack.pushPose();
    float storagePercent = (float) robot.getBattery().getEnergyStored() / (float) robot.getBattery().getMaxEnergyStored();
    if (robot.hurtTime > 0) {
      poseStack.mulPose(Axis.ZP.rotationDegrees(robot.hurtTime * 0.01f));
    }

    VertexConsumer bodyConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
    int bodyRed = 255;
    int bodyGreen = robot.hurtTime > 0 ? 153 : 255;
    int bodyBlue = robot.hurtTime > 0 ? 153 : 255;
    box.render(poseStack, bodyConsumer, packedLight, OverlayTexture.NO_OVERLAY, bodyRed, bodyGreen, bodyBlue, 255);

    if (robot.isActive()) {
      // Overlay
      VertexConsumer redConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(OVERLAY_RED));
      int alpha = (int) (storagePercent * 255);
      box.render(poseStack, redConsumer, packedLight, OverlayTexture.NO_OVERLAY, 255, 255, 255, alpha);

      VertexConsumer cyanConsumer = bufferSource.getBuffer(RenderType.entityCutout(OVERLAY_CYAN));
      box.render(poseStack, cyanConsumer, packedLight, OverlayTexture.NO_OVERLAY, 255, 255, 255, 255);
    }
    poseStack.popPose();

    // Wearables
    for (ItemStack wearable : robot.getWearables()) {
      doRenderWearable(robot, wearable, poseStack, bufferSource, packedLight);
    }

    poseStack.popPose();
    super.render(robot, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
  }

  private void doRenderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    poseStack.pushPose();
    poseStack.translate(0, 0.28F, 0);
    poseStack.scale(0.5f, 0.5f, 0.5f);
    itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, null, 0);
    poseStack.popPose();
  }

  private void doRenderWearable(RobotEntity entity, ItemStack wearable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    Item item = wearable.getItem();
    if (item instanceof IRobotOverlayItem) {
      // IRobotOverlayItem still uses TextureManager, this is a limitation of not updating API
      ((IRobotOverlayItem) item).renderRobotOverlay(wearable, Minecraft.getInstance().getTextureManager());
    } else if (item instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.HEAD) {
      poseStack.pushPose();
      poseStack.scale(1.0125F, 1.0125F, 1.0125F);
      poseStack.translate(0.0f, -0.25f, 0.0f);
      poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

      @SuppressWarnings({"unchecked", "UnstableApiUsage"}) HumanoidModel<RobotEntity> armorModel = (HumanoidModel<RobotEntity>) ForgeHooksClient.getArmorModel(entity, wearable, EquipmentSlot.HEAD, new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(RobotModelLayers.ARMOR_HELMET)));
      @SuppressWarnings("UnstableApiUsage") ResourceLocation armorTexture = ResourceLocation.parse(ForgeHooksClient.getArmorTexture(entity, wearable, "layer1", EquipmentSlot.HEAD, null));

      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(armorTexture));
      armorModel.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

      poseStack.popPose();
    } else if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SkullBlock) {
      doRenderSkull(wearable, poseStack, bufferSource, packedLight);
    }
  }

  private void doRenderSkull(ItemStack wearable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    poseStack.pushPose();
    poseStack.scale(1.0125F, 1.0125F, 1.0125F);
    GameProfile gameProfile = null;
    CompoundTag nbt = wearable.getTag();
    if (nbt != null) {
      if (nbt.contains("SkullOwner", 10)) {
        gameProfile = NbtUtils.readGameProfile(nbt.getCompound("SkullOwner"));
      }
    }

    poseStack.translate(-0.5F, -0.25F, -0.5F);
    SkullBlock.Type skullType = ((SkullBlock) ((BlockItem) wearable.getItem()).getBlock()).getType();
    SkullBlockRenderer.renderSkull(null, 180.0F, 0.0F, poseStack, bufferSource, packedLight, SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels()).get(skullType), SkullBlockRenderer.getRenderType(skullType, gameProfile));

    if (gameProfile != null) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 0.25F, 0.5F);
      poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
      poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(SkullBlockRenderer.getRenderType(skullType, gameProfile).toString().contains("overlay") ? OVERLAY_RED : OVERLAY_CYAN)); // Dummy logic for skull overlay
      skullOverlayBox.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
    }
    poseStack.popPose();
  }

  private void renderLaserLine(PoseStack poseStack, VertexConsumer consumer, double length, int texIndex, int packedLight) {
    Matrix4f matrix4f = poseStack.last().pose();
    Matrix3f matrix3f = poseStack.last().normal();

    float size = 1.0f / 16.0f;
    float v0 = texIndex / 40.0f;
    float v1 = v0 + (1.0f / 40.0f);

    drawQuad(matrix4f, matrix3f, consumer, (float) length, size, v0, v1, packedLight);
  }

  private void drawQuad(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float length, float size, float v0, float v1, int packedLight) {
    vertex(matrix4f, matrix3f, consumer, 0, -size, -size, 0, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, -size, -size, 1, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, size, -size, 1, v1, packedLight);
    vertex(matrix4f, matrix3f, consumer, 0, size, -size, 0, v1, packedLight);

    vertex(matrix4f, matrix3f, consumer, 0, size, size, 0, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, size, size, 1, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, -size, size, 1, v1, packedLight);
    vertex(matrix4f, matrix3f, consumer, 0, -size, size, 0, v1, packedLight);

    vertex(matrix4f, matrix3f, consumer, 0, size, -size, 0, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, size, -size, 1, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, size, size, 1, v1, packedLight);
    vertex(matrix4f, matrix3f, consumer, 0, size, size, 0, v1, packedLight);

    vertex(matrix4f, matrix3f, consumer, 0, -size, size, 0, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, -size, size, 1, v0, packedLight);
    vertex(matrix4f, matrix3f, consumer, length, -size, -size, 1, v1, packedLight);
    vertex(matrix4f, matrix3f, consumer, 0, -size, -size, 0, v1, packedLight);
  }

  private void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, int packedLight) {
    consumer.vertex(matrix4f, x, y, z)
      .color(255, 255, 255, 255)
      .uv(u, v)
      .overlayCoords(OverlayTexture.NO_OVERLAY)
      .uv2(packedLight)
      .normal(matrix3f, 0, 1, 0)
      .endVertex();
  }

  @Override
  public ResourceLocation getTextureLocation(RobotEntity robot) {
    return robot.getTexture();
  }
}
