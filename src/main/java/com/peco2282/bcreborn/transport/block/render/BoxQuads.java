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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Draws axis-aligned boxes with a block atlas sprite into a block render type (cutout / translucent).
 * <p>
 * The texture is laid out by position inside the block, like a block model without explicit UVs, so neighbouring
 * boxes line up. Block render types do not shade by normal, so the vanilla face shade is baked into the colour.
 */
@OnlyIn(Dist.CLIENT)
public final class BoxQuads {
  /** Every face. */
  public static final int ALL_FACES = 0x3F;

  private BoxQuads() {
  }

  public static int mask(Direction face) {
    return 1 << face.get3DDataValue();
  }

  /**
   * @param faces   bit mask of the faces to draw ({@link #mask(Direction)})
   * @param flow    if not null, the faces along this direction use it as the texture's "down" (the way a flowing
   *                fluid texture moves) instead of the layout by position
   * @param uvScale texture units per block: 16 for a normal sprite, 8 for a vanilla flowing fluid sprite
   */
  public static void box(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite sprite,
                         float x0, float y0, float z0, float x1, float y1, float z1,
                         float r, float g, float b, float a, int light, int faces,
                         @Nullable Direction flow, float uvScale) {
    if (x1 <= x0 || y1 <= y0 || z1 <= z0) return;
    Quad q = new Quad(pose.pose(), pose.normal(), consumer, sprite, r, g, b, a, light, flow, uvScale);
    // Vertex order as in vanilla FaceInfo (counter-clockwise seen from outside).
    if ((faces & mask(Direction.DOWN)) != 0) {
      q.face(Direction.DOWN, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1);
    }
    if ((faces & mask(Direction.UP)) != 0) {
      q.face(Direction.UP, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
    }
    if ((faces & mask(Direction.NORTH)) != 0) {
      q.face(Direction.NORTH, x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0);
    }
    if ((faces & mask(Direction.SOUTH)) != 0) {
      q.face(Direction.SOUTH, x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1);
    }
    if ((faces & mask(Direction.WEST)) != 0) {
      q.face(Direction.WEST, x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1);
    }
    if ((faces & mask(Direction.EAST)) != 0) {
      q.face(Direction.EAST, x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0);
    }
  }

  public static void box(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite sprite,
                         float x0, float y0, float z0, float x1, float y1, float z1,
                         float r, float g, float b, float a, int light, int faces) {
    box(pose, consumer, sprite, x0, y0, z0, x1, y1, z1, r, g, b, a, light, faces, null, 16.0f);
  }

  /** Vanilla block face shade (the overworld values of {@code ClientLevel.getShade()}). */
  private static float shade(Direction face) {
    return switch (face) {
      case DOWN -> 0.5f;
      case UP -> 1.0f;
      case NORTH, SOUTH -> 0.8f;
      case WEST, EAST -> 0.6f;
    };
  }

  private static float coord(Direction.Axis axis, float x, float y, float z) {
    return switch (axis) {
      case X -> x;
      case Y -> y;
      case Z -> z;
    };
  }

  private record Quad(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, TextureAtlasSprite sprite,
                      float r, float g, float b, float a, int light, @Nullable Direction flow, float uvScale) {

    void face(Direction face, float... xyz) {
      float shade = shade(face);
      for (int i = 0; i < 12; i += 3) {
        float x = xyz[i];
        float y = xyz[i + 1];
        float z = xyz[i + 2];
        float u;
        float v;
        if (flow != null && flow.getAxis() != face.getAxis()) {
          float along = coord(flow.getAxis(), x, y, z);
          v = flow.getAxisDirection() == Direction.AxisDirection.POSITIVE ? along : 1.0f - along;
          u = coord(across(face.getAxis(), flow.getAxis()), x, y, z);
        } else {
          switch (face.getAxis()) {
            case Y -> {
              u = x;
              v = z;
            }
            case Z -> {
              u = x;
              v = 1.0f - y;
            }
            default -> {
              u = z;
              v = 1.0f - y;
            }
          }
        }
        consumer.vertex(pose, x, y, z)
          .color(r * shade, g * shade, b * shade, a)
          .uv(sprite.getU(u * uvScale), sprite.getV(v * uvScale))
          .uv2(light)
          .normal(normal, face.getStepX(), face.getStepY(), face.getStepZ())
          .endVertex();
      }
    }

    /** The axis that is neither {@code a} nor {@code b}. */
    private static Direction.Axis across(Direction.Axis a, Direction.Axis b) {
      for (Direction.Axis axis : Direction.Axis.values()) {
        if (axis != a && axis != b) return axis;
      }
      return a;
    }
  }
}
