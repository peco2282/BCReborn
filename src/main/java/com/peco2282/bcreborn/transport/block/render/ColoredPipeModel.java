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
 * Shows a Lapis / Daizuli pipe in its colour (original {@code PipeItemsLapis.getIconIndex()}: one texture per
 * colour). The colour is runtime state of the block entity, so it is not in the block state; it reaches the model
 * as {@link PipeBlockEntity#PIPE_COLOR} model data, and the quads that use the model's built-in colour texture are
 * moved onto the texture of that colour.
 */
@OnlyIn(Dist.CLIENT)
public class ColoredPipeModel extends BakedModelWrapper<BakedModel> {
  /** Ints per vertex in {@code DefaultVertexFormat.BLOCK}, and where the texture coordinates are. */
  private static final int VERTEX_SIZE = 8;
  private static final int U_OFFSET = 4;
  private static final int V_OFFSET = 5;

  /** Texture path without the colour, e.g. {@code block/pipes/pipe_items_lapis_}. */
  private final String texturePrefix;
  /** The colour texture the model files were generated with. */
  private final ResourceLocation builtIn;

  public ColoredPipeModel(BakedModel original, String texturePrefix, DyeColor builtInColor) {
    super(original);
    this.texturePrefix = texturePrefix;
    this.builtIn = texture(texturePrefix, builtInColor);
  }

  /** The texture files use the original colour names, without underscores ({@code lightblue}). */
  public static ResourceLocation texture(String prefix, DyeColor color) {
    return BCRebornTransport.location(prefix + color.getName().replace("_", ""));
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand,
                                           @NotNull ModelData data, @Nullable RenderType renderType) {
    List<BakedQuad> quads = originalModel.getQuads(state, side, rand, data, renderType);
    DyeColor color = data.get(PipeBlockEntity.PIPE_COLOR);
    if (color == null || quads.isEmpty()) return quads;
    ResourceLocation wanted = texture(texturePrefix, color);
    if (wanted.equals(builtIn)) return quads;
    TextureAtlasSprite target = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(wanted);

    List<BakedQuad> result = new ArrayList<>(quads.size());
    for (BakedQuad quad : quads) {
      // Other textures (the Daizuli solid arms) stay as they are.
      result.add(quad.getSprite().contents().name().equals(builtIn) ? retexture(quad, target) : quad);
    }
    return result;
  }

  private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite target) {
    TextureAtlasSprite source = quad.getSprite();
    int[] vertices = quad.getVertices().clone();
    for (int i = 0; i + VERTEX_SIZE <= vertices.length; i += VERTEX_SIZE) {
      float u = Float.intBitsToFloat(vertices[i + U_OFFSET]);
      float v = Float.intBitsToFloat(vertices[i + V_OFFSET]);
      vertices[i + U_OFFSET] = Float.floatToRawIntBits(target.getU(source.getUOffset(u)));
      vertices[i + V_OFFSET] = Float.floatToRawIntBits(target.getV(source.getVOffset(v)));
    }
    return new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), target, quad.isShade(), quad.hasAmbientOcclusion());
  }
}
