package peco2282.bcreborn.core.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import peco2282.bcreborn.BCConfiguration;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.MarkerPlaceHolder;
import peco2282.bcreborn.core.block.entity.MarkerVolumeBlockEntity;

import java.util.EnumMap;
import java.util.function.Consumer;

public class MarkerVolumeRenderer implements BlockEntityRenderer<MarkerVolumeBlockEntity> {
  private static final ResourceLocation BEAM = BCReborn.location("textures/block/marker/marker_volume_signal.png");
  private static final int BLUE = (0xFF << 24) | 0xFF;

  private static final EnumMap<Direction, Consumer<PoseStack>> DIRECTION_MAP = new EnumMap<>(Direction.class);

  static {
    DIRECTION_MAP.put(Direction.SOUTH, stack -> {
      stack.translate(0F, 0F, 0F);
      stack.mulPose(Axis.YP.rotationDegrees(0));
    });
    DIRECTION_MAP.put(Direction.EAST, stack -> {
      stack.translate(0F, 0F, 1F);
      stack.mulPose(Axis.YP.rotationDegrees(90));
    });
    DIRECTION_MAP.put(Direction.NORTH, stack -> {
      stack.translate(1F, 0F, 1F);
      stack.mulPose(Axis.YP.rotationDegrees(180));
    });
    DIRECTION_MAP.put(Direction.WEST, stack -> {
      stack.translate(1F, 0F, 0F);
      stack.mulPose(Axis.YP.rotationDegrees(270));
    });
    DIRECTION_MAP.put(Direction.UP, stack -> {
      stack.translate(0F, 1F, 0F);
      stack.mulPose(Axis.XP.rotationDegrees(90));
    });
    DIRECTION_MAP.put(Direction.DOWN, stack -> {
      stack.translate(0F, 1F, 0F);
      stack.mulPose(Axis.XP.rotationDegrees(90));
    });
  }

  public MarkerVolumeRenderer(BlockEntityRendererProvider.Context context) {
  }

  private static void renderBeam(
      PoseStack stack,
      MultiBufferSource source,
      Direction direction,
      int size
  ) {
    stack.pushPose();

    RangeMap map = new RangeMap(
        0.45F,
        0.55F,
        0.45F,
        0.55F,
        0.55F,
        size + 1.0F
    );

    renderPart(
        stack,
        source,
        map,
        direction
    );
    stack.popPose();
  }

  private static void renderPart(PoseStack stack, MultiBufferSource source, RangeMap map, Direction direction) {
    final float width = 1F;

    float f = map.minZ();
    float fMax = map.minZ() + width;

    while (fMax <= map.maxZ()) {
      stack.pushPose();

      Consumer<PoseStack> consumer = DIRECTION_MAP.get(direction);
      consumer.accept(stack);

      PoseStack.Pose pose = stack.last();
      VertexConsumer buffer = source.getBuffer(RenderType.beaconBeam(BEAM, false));
      // @formatter:off
      /*
               $4
            \------|
         $2 |      | $1
            |------|
               $3
       */
      // $1(right)
      vertex(buffer, pose, map.minX(), map.minY(), f   , 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), f   , 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.minX(), map.maxY(), f   , 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), f   , 0, 0).setNormal(pose, 0, 0, -1);

      // $2(left)
      vertex(buffer, pose, map.maxX(), map.minY(), f   , 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.maxY(), f   , 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.maxX(), map.maxY(), f   , 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.minY(), f   , 0, 0).setNormal(pose, 0, 0, -1);

      // $3 (bottom)
      vertex(buffer, pose, map.minX(), map.minY(), f   , 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.maxX(), map.minY(), f   , 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.maxX(), map.minY(), f   , 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.maxX(), map.minY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.minX(), map.minY(), f   , 0, 0).setNormal(pose, 0, 0, -1);

      // $4(top)
      vertex(buffer, pose, map.maxX(), map.maxY(), f   , 0, 0).setNormal(pose, 0, 0, 1);  // Z軸正方向の法線
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 0, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, 1);
      vertex(buffer, pose, map.minX(), map.maxY(), f   , 1, 0).setNormal(pose, 0, 0, 1);

      vertex(buffer, pose, map.minX(), map.maxY(), f   , 1, 0).setNormal(pose, 0, 0, -1);  // Z軸負方向の法線
      vertex(buffer, pose, map.minX(), map.maxY(), fMax, 1, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.maxY(), fMax, 0, 1).setNormal(pose, 0, 0, -1);
      vertex(buffer, pose, map.maxX(), map.maxY(), f   , 0, 0).setNormal(pose, 0, 0, -1);
      // @formatter:on

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
//    if (!p_112307_.isActive()) return;
    if (p_112307_.norender()) return;
    MarkerPlaceHolder holder = p_112307_.renderer();
    if (!holder.canRender()) return;
    if (p_112307_.isSignal()) {
      int max = BCConfiguration.maxVolumeLength;
      renderBeam(p_112309_, p_112310_, Direction.NORTH, max);
      renderBeam(p_112309_, p_112310_, Direction.SOUTH, max);
      renderBeam(p_112309_, p_112310_, Direction.EAST, max);
      renderBeam(p_112309_, p_112310_, Direction.WEST, max);
      renderBeam(p_112309_, p_112310_, Direction.UP, max);
      renderBeam(p_112309_, p_112310_, Direction.DOWN, max);
      return;
    }

    MarkerPlaceHolder.XYZ edges = holder.getEdges();

    if (!edges.isXEmpty()) {
      edges.x().rendering((stack, edge) -> renderBeam(stack, p_112310_, holder.directionX(), edge.end().getX() - edge.start().getX()), p_112309_);
    }
    if (!edges.isYEmpty()) {
      edges.y().rendering((stack, edge) -> renderBeam(stack, p_112310_, holder.directionX(), edge.end().getX() - edge.start().getX()), p_112309_);
    }
    if (!edges.isZEmpty()) {
      edges.z().rendering((stack, edge) -> renderBeam(stack, p_112310_, holder.directionX(), edge.end().getX() - edge.start().getX()), p_112309_);
    }

/*
    if (holder.rangeX() != 0) {
      renderBeam(p_112309_, p_112310_, holder.directionX(), holder.rangeX());
      p_112309_.pushPose();
      p_112309_.translate(0, 0, (holder.distanceZ() > 0 ? 1 : -1) * holder.rangeZ());
      renderBeam(p_112309_, p_112310_, holder.directionX(), holder.rangeX());
      p_112309_.popPose();
    }
    if (holder.rangeY() != 0) {
      renderBeam(p_112309_, p_112310_, holder.directionY(), holder.rangeY());
      p_112309_.pushPose();
      p_112309_.translate(0, (holder.distanceY() > 0 ? 1 : -1) * holder.rangeY(), 0);
      renderBeam(p_112309_, p_112310_, holder.directionY(), holder.rangeY());
      p_112309_.popPose();
    }
    if (holder.rangeZ() != 0) {
      renderBeam(p_112309_, p_112310_, holder.directionZ(), holder.rangeZ());
      p_112309_.pushPose();
      p_112309_.translate((holder.distanceX() > 0 ? 1 : -1) * holder.rangeX(), 0, 0);
      renderBeam(p_112309_, p_112310_, holder.directionZ(), holder.rangeZ());
      p_112309_.popPose();
    }*/
  }

  @Override
  public int getViewDistance() {
    return BlockEntityRenderer.super.getViewDistance() << 1;
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
