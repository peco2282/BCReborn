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
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * Port of the wire part of the original {@code PipeRendererTESR} ({@code renderGatesWires} / {@code pipeWireRender}).
 * <p>
 * Every colour runs along its own edge of the pipe core (red: -x +y -z, blue: +x +y +z, green: +x -y -z,
 * yellow: -x -y +z), reaches out to the block edge towards each pipe its wire is connected to, and is drawn with the
 * "lit" texture at full brightness while it carries a signal (original {@code WireIconProvider} / {@code renderLitBox}).
 */
@OnlyIn(Dist.CLIENT)
public final class PipeWireRenderer {
  private static final float MIN = 0.25F;
  private static final float MAX = 0.75F;
  private static final float THICKNESS = 0.05F;

  private PipeWireRenderer() {
  }

  public static void render(PipeBlockEntity pipe, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    if (pipe.getLevel() == null) return;
    for (PipeWire wire : PipeWire.VALUES) {
      if (pipe.isWired(wire)) {
        renderWire(pipe, wire, poseStack, buffer, packedLight, packedOverlay);
      }
    }
  }

  private static float cornerX(PipeWire wire) {
    return wire == PipeWire.BLUE || wire == PipeWire.GREEN ? MAX : MIN;
  }

  private static float cornerY(PipeWire wire) {
    return wire == PipeWire.RED || wire == PipeWire.BLUE ? MAX : MIN;
  }

  private static float cornerZ(PipeWire wire) {
    return wire == PipeWire.BLUE || wire == PipeWire.YELLOW ? MAX : MIN;
  }

  private static void renderWire(PipeBlockEntity pipe, PipeWire wire, PoseStack poseStack, MultiBufferSource buffer,
                                 int packedLight, int packedOverlay) {
    float cx = cornerX(wire);
    float cy = cornerY(wire);
    float cz = cornerZ(wire);

    float minX = MIN, minY = MIN, minZ = MIN;
    float maxX = MAX, maxY = MAX, maxZ = MAX;
    boolean foundX = false, foundY = false, foundZ = false;

    if (pipe.isWireConnected(wire, Direction.WEST)) { minX = 0; foundX = true; }
    if (pipe.isWireConnected(wire, Direction.EAST)) { maxX = 1; foundX = true; }
    if (pipe.isWireConnected(wire, Direction.DOWN)) { minY = 0; foundY = true; }
    if (pipe.isWireConnected(wire, Direction.UP)) { maxY = 1; foundY = true; }
    if (pipe.isWireConnected(wire, Direction.NORTH)) { minZ = 0; foundZ = true; }
    if (pipe.isWireConnected(wire, Direction.SOUTH)) { maxZ = 1; foundZ = true; }

    // Original: an arm that only goes one way stops at the wire's own corner, otherwise a centre joint is needed.
    boolean center = false;
    if (minX == 0 && maxX != 1 && (foundY || foundZ)) {
      if (cx == MIN) maxX = MIN; else center = true;
    }
    if (minX != 0 && maxX == 1 && (foundY || foundZ)) {
      if (cx == MAX) minX = MAX; else center = true;
    }
    if (minY == 0 && maxY != 1 && (foundX || foundZ)) {
      if (cy == MIN) maxY = MIN; else center = true;
    }
    if (minY != 0 && maxY == 1 && (foundX || foundZ)) {
      if (cy == MAX) minY = MAX; else center = true;
    }
    if (minZ == 0 && maxZ != 1 && (foundX || foundY)) {
      if (cz == MIN) maxZ = MIN; else center = true;
    }
    if (minZ != 0 && maxZ == 1 && (foundX || foundY)) {
      if (cz == MAX) minZ = MAX; else center = true;
    }
    boolean found = foundX || foundY || foundZ;

    // The wire sits just outside the core along its edge.
    float x0 = cx == MIN ? cx - THICKNESS : cx, x1 = cx == MIN ? cx : cx + THICKNESS;
    float y0 = cy == MIN ? cy - THICKNESS : cy, y1 = cy == MIN ? cy : cy + THICKNESS;
    float z0 = cz == MIN ? cz - THICKNESS : cz, z1 = cz == MIN ? cz : cz + THICKNESS;

    boolean lit = pipe.isWireActive(wire);
    TextureAtlasSprite sprite = sprite(wire, lit);
    int light = lit ? LightTexture.FULL_BRIGHT : packedLight;
    VertexConsumer consumer = buffer.getBuffer(Sheets.cutoutBlockSheet());

    poseStack.pushPose();
    // Original: scale 1.001 around the centre so the wire never z-fights with the pipe.
    poseStack.translate(0.5, 0.5, 0.5);
    poseStack.scale(1.001F, 1.001F, 1.001F);
    poseStack.translate(-0.5, -0.5, -0.5);

    if (minZ != MIN || maxZ != MAX || !found) {
      box(poseStack, consumer, sprite, x0, y0, minZ, x1, y1, maxZ, light, packedOverlay);
    }
    if (minX != MIN || maxX != MAX || !found) {
      box(poseStack, consumer, sprite, minX, y0, z0, maxX, y1, z1, light, packedOverlay);
    }
    if (minY != MIN || maxY != MAX || !found) {
      box(poseStack, consumer, sprite, x0, minY, z0, x1, maxY, z1, light, packedOverlay);
    }
    if (center || !found) {
      box(poseStack, consumer, sprite, x0, y0, z0, x1, y1, z1, light, packedOverlay);
    }
    poseStack.popPose();
  }

  private static TextureAtlasSprite sprite(PipeWire wire, boolean lit) {
    ResourceLocation location = BCRebornCore.location("block/misc/texture_" + wire.name().toLowerCase(Locale.ENGLISH)
      + (lit ? "_lit" : "_dark"));
    return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(location);
  }

  private static void box(PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite,
                          float x0, float y0, float z0, float x1, float y1, float z1, int light, int overlay) {
    Matrix4f m = poseStack.last().pose();
    var n = poseStack.last().normal();
    float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
    // DOWN / UP
    quad(consumer, m, n, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, u0, v0, u1, v1, light, overlay, 0, -1, 0);
    quad(consumer, m, n, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, u0, v0, u1, v1, light, overlay, 0, 1, 0);
    // NORTH / SOUTH
    quad(consumer, m, n, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, u0, v0, u1, v1, light, overlay, 0, 0, -1);
    quad(consumer, m, n, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, u0, v0, u1, v1, light, overlay, 0, 0, 1);
    // WEST / EAST
    quad(consumer, m, n, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, u0, v0, u1, v1, light, overlay, -1, 0, 0);
    quad(consumer, m, n, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, u0, v0, u1, v1, light, overlay, 1, 0, 0);
  }

  @SuppressWarnings("SameParameterValue")
  private static void quad(VertexConsumer c, Matrix4f m, org.joml.Matrix3f n,
                           float ax, float ay, float az, float bx, float by, float bz,
                           float cx, float cy, float cz, float dx, float dy, float dz,
                           float u0, float v0, float u1, float v1, int light, int overlay, float nx, float ny, float nz) {
    c.vertex(m, ax, ay, az).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
    c.vertex(m, bx, by, bz).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
    c.vertex(m, cx, cy, cz).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
    c.vertex(m, dx, dy, dz).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, nx, ny, nz).endVertex();
  }
}
