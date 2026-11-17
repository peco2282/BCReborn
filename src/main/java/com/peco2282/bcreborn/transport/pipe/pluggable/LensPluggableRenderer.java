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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.block.render.BoxQuads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

/**
 * Port of the original {@code LensPluggable.LensPluggableRenderer}: a frame around the end of the arm (lens or
 * filter texture) and, on the two faces across the pipe, the glass in the lens colour.
 */
@OnlyIn(Dist.CLIENT)
public class LensPluggableRenderer implements IPipePluggableRenderer {
  public static final LensPluggableRenderer INSTANCE = new LensPluggableRenderer();
  private static final ResourceLocation LENS = BCRebornTransport.location("block/pipes/pipe_lens");
  private static final ResourceLocation FILTER = BCRebornTransport.location("block/pipes/pipe_filter");
  private static final ResourceLocation OVERLAY = BCRebornTransport.location("block/pipes/pipe_lens_overlay");
  private static final ResourceLocation CLEAR_OVERLAY = BCRebornTransport.location("block/pipes/pipe_lens_clear_overlay");
  private static final float Z_FIGHT_OFFSET = 1.0f / 4096.0f;

  private LensPluggableRenderer() {
  }

  @Override
  public void renderPluggable(IPipe pipe, Direction side, PipePluggable<?> pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    if (!(pipePluggable instanceof LensPluggable lens)) return;
    Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
    PoseStack.Pose pose = poseStack.last();

    PlugPluggableRenderer.draw(pose, buffer.getBuffer(RenderType.cutout()), atlas.apply(lens.isFilter() ? FILTER : LENS),
      PluggableBoxes.box(side, 0.1875f, 0.8125f, 0.0f, 0.125f),
      1, 1, 1, 1, packedLight, BoxQuads.ALL_FACES);

    // Original render pass 1: only the faces across the pipe, slightly inside the frame.
    DyeColor color = lens.getColor();
    float r = 1;
    float g = 1;
    float b = 1;
    if (color != null) {
      float[] rgb = color.getTextureDiffuseColors();
      r = rgb[0];
      g = rgb[1];
      b = rgb[2];
    }
    PlugPluggableRenderer.draw(pose, buffer.getBuffer(RenderType.translucent()), atlas.apply(color == null ? CLEAR_OVERLAY : OVERLAY),
      PluggableBoxes.box(side, 0.1875f + Z_FIGHT_OFFSET, 0.8125f - Z_FIGHT_OFFSET, Z_FIGHT_OFFSET, 0.125f - Z_FIGHT_OFFSET),
      r, g, b, 1, packedLight, BoxQuads.mask(side) | BoxQuads.mask(side.getOpposite()));
  }
}
