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
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Port of the original {@code PlugPluggable.PlugPluggableRenderer}: a plate over the pipe opening and a smaller
 * block behind it.
 */
@OnlyIn(Dist.CLIENT)
public class PlugPluggableRenderer implements IPipePluggableRenderer {
  public static final PlugPluggableRenderer INSTANCE = new PlugPluggableRenderer();
  private static final ResourceLocation TEXTURE = BCRebornTransport.location("block/pipes/pipe_plug");
  private static final float Z_FIGHT_OFFSET = 1.0f / 4096.0f;

  private PlugPluggableRenderer() {
  }

  static void draw(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite sprite, AABB box,
                   float r, float g, float b, float a, int light, int faces) {
    BoxQuads.box(pose, consumer, sprite,
      (float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ,
      r, g, b, a, light, faces);
  }

  @Override
  public void renderPluggable(IPipe pipe, Direction side, PipePluggable<?> pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TEXTURE);
    VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
    PoseStack.Pose pose = poseStack.last();
    draw(pose, consumer, sprite,
      PluggableBoxes.box(side, 0.25f + Z_FIGHT_OFFSET, 0.75f - Z_FIGHT_OFFSET, 0.125f, 0.251f),
      1, 1, 1, 1, packedLight, BoxQuads.ALL_FACES);
    draw(pose, consumer, sprite,
      PluggableBoxes.box(side, 0.3125f + Z_FIGHT_OFFSET, 0.6875f - Z_FIGHT_OFFSET, 0.25f, 0.375f),
      1, 1, 1, 1, packedLight, BoxQuads.ALL_FACES);
  }
}
