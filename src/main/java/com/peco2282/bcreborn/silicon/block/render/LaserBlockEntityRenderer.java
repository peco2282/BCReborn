package com.peco2282.bcreborn.silicon.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.silicon.block.entity.LaserBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LaserBlockEntityRenderer implements BlockEntityRenderer<LaserBlockEntity> {
    private static final ResourceLocation LASER_TEXTURE = ResourceLocation.fromNamespaceAndPath("bcrebornsilicon", "textures/entity/laser.png");

    public LaserBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LaserBlockEntity laserBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        LaserData laser = laserBlockEntity.laser;

        if (laser == null || !laser.isVisible) {
            return;
        }

        poseStack.pushPose();

        // 始点位置へ移動
        // LaserData.head はワールド座標なので、BlockEntityの相対座標に変換
        poseStack.translate(laser.head.x - laserBlockEntity.getBlockPos().getX(),
                          laser.head.y - laserBlockEntity.getBlockPos().getY(),
                          laser.head.z - laserBlockEntity.getBlockPos().getZ());

        laser.update();

        poseStack.mulPose(Axis.YP.rotationDegrees((float) laser.angleZ));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) laser.angleY));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(LASER_TEXTURE));
        renderLaserLine(poseStack, vertexConsumer, laser.renderSize, laser.laserTexAnimation, packedLight);

        poseStack.popPose();
    }

    private void renderLaserLine(PoseStack poseStack, VertexConsumer consumer, double length, int texIndex, int packedLight) {
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();

        float size = 1.0f / 16.0f;
        float v0 = texIndex / 40.0f;
        float v1 = v0 + (1.0f / 40.0f);

        // 四角柱としてレーザーを描画 (簡易版)
        drawQuad(matrix4f, matrix3f, consumer, (float) length, size, v0, v1, packedLight);
    }

    private void drawQuad(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float length, float size, float v0, float v1, int packedLight) {
        // 前面
        vertex(matrix4f, matrix3f, consumer, 0, -size, -size, 0, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, -size, -size, 1, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, size, -size, 1, v1, packedLight);
        vertex(matrix4f, matrix3f, consumer, 0, size, -size, 0, v1, packedLight);

        // 背面
        vertex(matrix4f, matrix3f, consumer, 0, size, size, 0, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, size, size, 1, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, -size, size, 1, v1, packedLight);
        vertex(matrix4f, matrix3f, consumer, 0, -size, size, 0, v1, packedLight);
        
        // 上面
        vertex(matrix4f, matrix3f, consumer, 0, size, -size, 0, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, size, -size, 1, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, size, size, 1, v1, packedLight);
        vertex(matrix4f, matrix3f, consumer, 0, size, size, 0, v1, packedLight);
        
        // 下面
        vertex(matrix4f, matrix3f, consumer, 0, -size, size, 0, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, -size, size, 1, v0, packedLight);
        vertex(matrix4f, matrix3f, consumer, length, -size, -size, 1, v1, packedLight);
        vertex(matrix4f, matrix3f, consumer, 0, -size, -size, 0, v1, packedLight);
    }

    private void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(0)
                .uv2(packedLight)
                .normal(matrix3f, 0, 1, 0)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(LaserBlockEntity laserBlockEntity) {
        return true;
    }
}
