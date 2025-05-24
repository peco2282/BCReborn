/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;

public class PipeRenderer implements BlockEntityRenderer<BasePipeBlockEntity> {
  public PipeRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      BasePipeBlockEntity p_112307_,
      float p_112308_,
      PoseStack p_112309_,
      MultiBufferSource p_112310_,
      int p_112311_,
      int p_112312_) {}
}
