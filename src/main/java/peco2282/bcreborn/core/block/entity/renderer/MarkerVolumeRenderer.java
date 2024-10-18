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

  static void renderBeam(
      PoseStack stack,
      MultiBufferSource source,
      float partialTick,
      float scale,
      long gameTime,
      int yStart,
      int height,
      int color,
      float radius
  ) {
    int i = yStart + height;
    stack.pushPose();
    stack.translate(0.5, 0.0, 0.5);
    float f = Math.floorMod(gameTime, 40) + yStart;
    stack.pushPose();
    renderPart(
        stack,
        source.getBuffer(RenderType.beaconBeam(BEAM, false)),
        color,
        yStart,
        height,
        0F,
        0F
    );
    stack.popPose();
  }

  private static void renderPart(PoseStack stack, VertexConsumer buffer, int color, int yStart, int height, float x, float z) {
    PoseStack.Pose pose = stack.last();
    buffer.addVertex(pose, 0.45F, 0.5F, 0.45F)
        .setColor(color)
        .setLight(15728880)
        .setUv(1, 1)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setNormal(pose, 1F, 0F, 1F);
    buffer.addVertex(pose, 0.45F, 0.5F, 0.55F)
        .setColor(color)
        .setLight(15728880)
        .setUv(1, 1)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setNormal(pose, 1F, 0F, 1F);
    buffer.addVertex(pose, 0.55F, 0.5F, 0.55F)
        .setColor(color)
        .setLight(15728880)
        .setUv(1, 1)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setNormal(pose, 1F, 0F, 1F);
    buffer.addVertex(pose, 0.55F, 0.5F, 0.45F)
        .setColor(color)
        .setLight(15728880)
        .setUv(1, 1)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setNormal(pose, 1F, 0F, 1F);
//    buffer.addVertex(pose, 0.25F, 0.25F, 0.25F).setColor(color);
  }

  @Override
  public void render(MarkerVolumeBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
//    BeaconRenderer.renderBeaconBeam(
//        p_112309_,
//        p_112310_,
//        BEAM,
//        p_112308_,
//        1F,
//        p_112307_.getLevel().getGameTime(),
//        0,
//        1024,
//        Color.BLUE.getRGB(),
//        0.2F,
//        0.25F
//    );
//    renderBeam(p_112309_, p_112310_, p_112308_, 0.25F, p_112307_.getLevel().getGameTime(), 0, 1024, BLUE, 0.25F);
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
}
