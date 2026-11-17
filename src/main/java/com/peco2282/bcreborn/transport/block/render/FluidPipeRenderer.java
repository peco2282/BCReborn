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
package com.peco2282.bcreborn.transport.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule.TransferState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Draws the fluid inside a fluid pipe, after the original {@code PipeTransportFluidsRenderer}: horizontal parts
 * fill up from the bottom, vertical parts are a column that widens with the amount.
 * <p>
 * The original moved the fluid through seven sections per pipe. This pipe has one tank, so the amount comes from
 * {@link FluidTransportModule#getRenderFill(float)} (eased on the client) and the movement from the fluid's
 * flowing texture, laid along each arm in the direction the fluid travels there (towards the centre on an input
 * side, away from it on an output side).
 */
public class FluidPipeRenderer implements BlockEntityRenderer<PipeBlockEntity> {

  /** Inside of the pipe (4/16..12/16), slightly inset so the fluid does not z-fight with the pipe walls. */
  private static final float MIN = 0.25f + 0.01f;
  private static final float MAX = 0.75f - 0.01f;
  /** A vanilla flowing sprite is twice as large as a still one and is sampled at half scale. */
  private static final float FLOWING_UV_SCALE = 8.0f;
  private static final float STILL_UV_SCALE = 16.0f;

  public FluidPipeRenderer(BlockEntityRendererProvider.Context context) {
  }

  @Override
  public void render(PipeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                     MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    renderPluggables(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    PipeWireRenderer.render(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);

    FluidTransportModule module = blockEntity.getFluidTransportModule();
    if (module == null) return;
    FluidStack fluid = module.getRenderFluid();
    float fill = module.getRenderFill(partialTick);
    if (fluid.isEmpty() || fill <= 0.0f) return;

    IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
    ResourceLocation stillTexture = fluidExt.getStillTexture(fluid);
    if (stillTexture == null) return;
    Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
    TextureAtlasSprite still = atlas.apply(stillTexture);
    ResourceLocation flowingTexture = fluidExt.getFlowingTexture(fluid);
    TextureAtlasSprite flowing = flowingTexture != null ? atlas.apply(flowingTexture) : null;

    // Water takes the biome colour, like the block.
    int color = blockEntity.getLevel() != null
      ? fluidExt.getTintColor(fluid.getFluid().defaultFluidState(), blockEntity.getLevel(), blockEntity.getBlockPos())
      : fluidExt.getTintColor(fluid);
    float r = ((color >> 16) & 0xFF) / 255.0f;
    float g = ((color >> 8) & 0xFF) / 255.0f;
    float b = (color & 0xFF) / 255.0f;
    float a = ((color >> 24) & 0xFF) / 255.0f;
    if (a == 0.0f) a = 1.0f; // アルファが0の場合は不透明扱い

    // Glowing fluids (lava) light themselves.
    int glow = fluid.getFluid().getFluidType().getLightLevel(fluid);
    int light = LightTexture.pack(Math.max(LightTexture.block(packedLight), glow), LightTexture.sky(packedLight));

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
    PoseStack.Pose pose = poseStack.last();
    BlockState state = blockEntity.getBlockState();

    boolean horizontal = false;
    for (Direction dir : Direction.Plane.HORIZONTAL) {
      horizontal |= PipeBlock.isConnected(state, dir);
    }
    boolean up = PipeBlock.isConnected(state, Direction.UP);
    boolean down = PipeBlock.isConnected(state, Direction.DOWN);
    // Original: horizontal sections fill up to a level, vertical ones are a centred column.
    float surface = MIN + (MAX - MIN) * fill;
    float half = (MAX - MIN) * 0.5f * fill;
    float colMin = 0.5f - half;
    float colMax = 0.5f + half;
    int lateral = BoxQuads.ALL_FACES & ~BoxQuads.mask(Direction.UP) & ~BoxQuads.mask(Direction.DOWN);

    // Centre. The faces towards a horizontal arm are left out: the arm continues there at the same level.
    float columnFrom;
    if (!horizontal && (up || down)) {
      // A purely vertical pipe: the column runs straight through.
      BoxQuads.box(pose, consumer, still, colMin, MIN, colMin, colMax, MAX, colMax, r, g, b, a, light,
        lateral | (up ? 0 : BoxQuads.mask(Direction.UP)) | (down ? 0 : BoxQuads.mask(Direction.DOWN)));
      columnFrom = MAX;
    } else {
      int faces = BoxQuads.ALL_FACES;
      for (Direction dir : Direction.Plane.HORIZONTAL) {
        if (PipeBlock.isConnected(state, dir)) faces &= ~BoxQuads.mask(dir);
      }
      BoxQuads.box(pose, consumer, still, MIN, MIN, MIN, MAX, surface, MAX, r, g, b, a, light, faces);
      // The column to the upper arm starts at the surface, so the two do not overlap.
      columnFrom = surface;
    }

    for (Direction dir : Direction.values()) {
      if (!PipeBlock.isConnected(state, dir)) continue;
      Direction flow = flowDirection(module, dir);
      TextureAtlasSprite sprite = flow != null && flowing != null ? flowing : still;
      if (sprite == still) flow = null;
      float scale = flow != null ? FLOWING_UV_SCALE : STILL_UV_SCALE;
      // The two ends of an arm are never drawn: one touches the centre, the other the neighbour.
      int faces = BoxQuads.ALL_FACES & ~BoxQuads.mask(dir) & ~BoxQuads.mask(dir.getOpposite());
      switch (dir) {
        case UP -> BoxQuads.box(pose, consumer, sprite, colMin, columnFrom, colMin, colMax, 1.0f, colMax, r, g, b, a, light, faces, flow, scale);
        case DOWN -> BoxQuads.box(pose, consumer, sprite, colMin, 0.0f, colMin, colMax, MIN, colMax, r, g, b, a, light, faces, flow, scale);
        case WEST -> BoxQuads.box(pose, consumer, sprite, 0.0f, MIN, MIN, MIN, surface, MAX, r, g, b, a, light, faces, flow, scale);
        case EAST -> BoxQuads.box(pose, consumer, sprite, MAX, MIN, MIN, 1.0f, surface, MAX, r, g, b, a, light, faces, flow, scale);
        case NORTH -> BoxQuads.box(pose, consumer, sprite, MIN, MIN, 0.0f, MAX, surface, MIN, r, g, b, a, light, faces, flow, scale);
        case SOUTH -> BoxQuads.box(pose, consumer, sprite, MIN, MIN, MAX, MAX, surface, 1.0f, r, g, b, a, light, faces, flow, scale);
      }
    }
  }

  /**
   * The way the fluid moves in the arm on {@code side}: into the pipe on an input side, out of it on an output
   * side, or null if it stands still there.
   */
  @Nullable
  private static Direction flowDirection(FluidTransportModule module, Direction side) {
    TransferState state = module.getTransferState(side);
    if (state == TransferState.Output && !module.getFlowFluid().isEmpty()) return side;
    if (state == TransferState.Input) return side.getOpposite();
    return null;
  }

  @Override
  public boolean shouldRenderOffScreen(PipeBlockEntity blockEntity) {
    return false;
  }

  private void renderPluggables(PipeBlockEntity pipe, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    for (Direction side : Direction.values()) {
      PipePluggable pluggable = pipe.sideProperties.pluggables[side.ordinal()];
      if (pluggable != null) {
        IPipePluggableRenderer renderer = pluggable.getRenderer();
        if (renderer != null) {
          renderer.renderPluggable(pipe.getPipe(), side, pluggable, 0, poseStack, buffer, packedLight, packedOverlay);
        }
      }
    }
  }
}
