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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Port of the original {@code PowerAdapterPluggableRenderer}: a wide base plate and a smaller block on top. */
@OnlyIn(Dist.CLIENT)
public class PowerAdapterPluggableRenderer implements IPipePluggableRenderer {
  public static final PowerAdapterPluggableRenderer INSTANCE = new PowerAdapterPluggableRenderer();
  private static final ResourceLocation SIDE = BCRebornTransport.location("block/pipes/pipe_power_adapter_side");
  private static final ResourceLocation TOP = BCRebornTransport.location("block/pipes/pipe_power_adapter_top");
  private static final float Z_FIGHT_OFFSET = 1.0f / 4096.0f;

  private PowerAdapterPluggableRenderer() {
  }

  @Override
  public void renderPluggable(IPipe pipe, Direction side, PipePluggable<?> pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
    TextureAtlasSprite sideSprite = atlas.apply(SIDE);
    TextureAtlasSprite topSprite = atlas.apply(TOP);
    VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
    PoseStack.Pose pose = poseStack.last();
    PlugPluggableRenderer.draw(pose, consumer, sideSprite, PluggableBoxes.box(side, 0.1875f, 0.8125f, 0.0f, 0.1251f),
      1, 1, 1, 1, packedLight, BoxQuads.ALL_FACES);
    PlugPluggableRenderer.draw(pose, consumer, topSprite,
      PluggableBoxes.box(side, 0.25f + Z_FIGHT_OFFSET, 0.75f - Z_FIGHT_OFFSET, 0.125f, 0.25f + Z_FIGHT_OFFSET),
      1, 1, 1, 1, packedLight, BoxQuads.ALL_FACES);
  }
}
