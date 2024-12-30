package peco2282.bcreborn.builder.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import peco2282.bcreborn.builder.block.entity.TankBlockEntity;

public class TankRenderer implements BlockEntityRenderer<TankBlockEntity> {
  private static final int BLACK = 0xFF << 24;
  private static final int BLUE = 0xFF << 24 | 0xFF;
  private static final int GOLD = 0xFF << 24 | 0xFF << 16 | 0xFF << 8;
  public TankRenderer(BlockEntityRendererProvider.Context context) {}
  @Override
  public void render(TankBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
    int fluids = p_112307_.getFluids();
  }
}
