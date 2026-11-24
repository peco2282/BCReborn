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

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a stained pipe (original {@code PipeRendererWorld}: the {@code PipeStainedOverlay} icon tinted with the
 * glass colour over the pipe). The colour reaches the model as {@link PipeBlockEntity#GLASS_COLOR} model data; every
 * quad of the pipe gets a copy on the overlay texture, coloured in the vertices and lifted a little off the face so
 * the two do not fight.
 */
@OnlyIn(Dist.CLIENT)
public class StainedPipeModel extends BakedModelWrapper<BakedModel> {
  public static final ResourceLocation OVERLAY = BCRebornTransport.location("block/pipes/pipe_stained_overlay");

  /** Ints per vertex in {@code DefaultVertexFormat.BLOCK}, and where the position, colour and texture are. */
  private static final int VERTEX_SIZE = 8;
  private static final int COLOR_OFFSET = 3;
  private static final int U_OFFSET = 4;
  private static final int V_OFFSET = 5;
  private static final float LIFT = 0.001f;

  public StainedPipeModel(BakedModel original) {
    super(original);
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand,
                                           @NotNull ModelData data, @Nullable RenderType renderType) {
    List<BakedQuad> quads = originalModel.getQuads(state, side, rand, data, renderType);
    DyeColor color = data.get(PipeBlockEntity.GLASS_COLOR);
    if (color == null || quads.isEmpty()) return quads;
    TextureAtlasSprite overlay = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(OVERLAY);
    int abgr = abgr(color);

    List<BakedQuad> result = new ArrayList<>(quads.size() * 2);
    result.addAll(quads);
    for (BakedQuad quad : quads) {
      result.add(overlay(quad, overlay, abgr));
    }
    return result;
  }

  /** The dye's colour as the vertex format stores it (bytes R, G, B, A in memory). */
  private static int abgr(DyeColor color) {
    float[] rgb = color.getTextureDiffuseColors();
    int r = Math.round(rgb[0] * 255.0f);
    int g = Math.round(rgb[1] * 255.0f);
    int b = Math.round(rgb[2] * 255.0f);
    return 0xFF000000 | (b << 16) | (g << 8) | r;
  }

  private static BakedQuad overlay(BakedQuad quad, TextureAtlasSprite target, int abgr) {
    TextureAtlasSprite source = quad.getSprite();
    Direction face = quad.getDirection();
    int[] vertices = quad.getVertices().clone();
    for (int i = 0; i + VERTEX_SIZE <= vertices.length; i += VERTEX_SIZE) {
      vertices[i] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i]) + face.getStepX() * LIFT);
      vertices[i + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i + 1]) + face.getStepY() * LIFT);
      vertices[i + 2] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i + 2]) + face.getStepZ() * LIFT);
      vertices[i + COLOR_OFFSET] = abgr;
      float u = Float.intBitsToFloat(vertices[i + U_OFFSET]);
      float v = Float.intBitsToFloat(vertices[i + V_OFFSET]);
      vertices[i + U_OFFSET] = Float.floatToRawIntBits(target.getU(source.getUOffset(u)));
      vertices[i + V_OFFSET] = Float.floatToRawIntBits(target.getV(source.getVOffset(v)));
    }
    // No tint index: the colour is in the vertices.
    return new BakedQuad(vertices, -1, face, target, quad.isShade(), quad.hasAmbientOcclusion());
  }
}
