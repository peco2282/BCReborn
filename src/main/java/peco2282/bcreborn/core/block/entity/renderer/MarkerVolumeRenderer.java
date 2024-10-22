package peco2282.bcreborn.core.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.entity.MarkerVolumeBlockEntity;

public class MarkerVolumeRenderer implements BlockEntityRenderer<MarkerVolumeBlockEntity> {
  private static final ResourceLocation BEAM = BCReborn.location("textures/block/marker/marker_volume_signal.png");
  private static final int BLUE = (0xFF << 24) | 0xFF;

  public MarkerVolumeRenderer(BlockEntityRendererProvider.Context context) {
  }

  private static void renderBeamToX(
      PoseStack stack,
      MultiBufferSource source
  ) {
    stack.pushPose();

    RangeMap map = new RangeMap(
       0.45F,
       0.55F,
        0.45F,
       0.55F,
        0.55F,
        5.0F
    );

    renderPart(
        stack,
        source,
        map
    );
    stack.popPose();
  }

  private static void renderPart(PoseStack stack, MultiBufferSource source, RangeMap map) {
    final float width = 1F;

    float f = map.minZ();
    float fMax = map.minZ() + width;

    while (fMax < map.maxZ()) {
      stack.pushPose();
      PoseStack.Pose pose = stack.last();
      VertexConsumer buffer = source.getBuffer(RenderType.beaconBeam(BEAM, false));
      /*
               $4
            \------|
         $2 |      | $1
            |------|
               $3
       */
      // $1(right)
      vertex(buffer, pose, map.minX(), map.minY(), f, 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), f, 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.minX(), map.maxY(), f, 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), f, 0, 0).setNormal(pose, 0, 0, -1);

      // $2(left)
      vertex(buffer, pose, map.maxX(), map.minY(), f, 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.maxY(), f, 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.maxX(), map.maxY(), f, 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.minY(), f, 0, 0).setNormal(pose, 0, 0, -1);


      // $3 (bottom)
      vertex(buffer, pose, map.minX(), map.minY(), f, 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.minY(), f, 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.maxX(), map.minY(), f, 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), f, 0, 0).setNormal(pose, 0, 0, -1);


      // $4(top)
      vertex(buffer, pose, map.maxX(), map.maxY(), f, 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), f, 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.minX(), map.maxY(), f, 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.maxY(), f, 0, 0).setNormal(pose, 0, 0, -1);

      f += width;
      fMax += width;
      stack.popPose();
    }
  }

  private static VertexConsumer vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
    return buffer.addVertex(pose, x, y, z)
        .setUv(u, v)
        .setColor(BLUE)
        .setLight(15728880)
        .setOverlay(OverlayTexture.NO_OVERLAY);
  }

  @Override
  public void render(MarkerVolumeBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
    renderBeamToX(p_112309_, p_112310_);
  }

  @Override
  public boolean shouldRender(MarkerVolumeBlockEntity p_173568_, Vec3 p_173569_) {
    return Vec3.atCenterOf(p_173568_.getBlockPos())
        .multiply(1.0, 0.0, 1.0)
        .closerThan(
            p_173569_.multiply(1.0, 0.0, 1.0),
            getViewDistance()
        );
  }
  private record RangeMap(
      float minX,
      float maxX,
      float minY,
      float maxY,
      float minZ,
      float maxZ
  ) {
  }
}
