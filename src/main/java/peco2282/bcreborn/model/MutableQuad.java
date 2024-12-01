package peco2282.bcreborn.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import peco2282.bcreborn.utils.DirectionUtil;

public class MutableQuad {
  public final MutableVertex vertex_0 = new MutableVertex();
  public final MutableVertex vertex_1 = new MutableVertex();
  public final MutableVertex vertex_2 = new MutableVertex();
  public final MutableVertex vertex_3 = new MutableVertex();

  private int tintIndex;
  private Direction face;
  private boolean shade;
  private TextureAtlasSprite sprite = null;

  public MutableQuad(int tintIndex, Direction face) {
    this(tintIndex, face, false);
  }

  public MutableQuad(int tintIndex, Direction face, boolean shade) {
    this.tintIndex = tintIndex;
    this.face = face;
    this.shade = shade;
  }

  public MutableQuad normal(Vector3f vector) {
    this.vertex_0.setNormal(vector);
    this.vertex_1.setNormal(vector);
    this.vertex_2.setNormal(vector);
    this.vertex_3.setNormal(vector);
    return this;
  }

  public MutableQuad rotate(Direction from, Direction to, float ox, float oy, float oz) {
    if (from == to) {
      // don't bother rotating: there is nothing to rotate!
      return this;
    }

    translate(-ox, -oy, -oz);
    // @formatter:off
    switch (from.getAxis()) {
      case X: {
        int mult = DirectionUtil.getFrontOffsetX(from);
        switch (to.getAxis()) {
          case X: rotateY_180(); break;
          case Y: rotateZ_90(mult * DirectionUtil.getFrontOffsetY(to)); break;
          case Z: rotateY_90(mult * DirectionUtil.getFrontOffsetZ(to)); break;
        }
        break;
      }
      case Y: {
        int mult = DirectionUtil.getFrontOffsetY(from);
        switch (to.getAxis()) {
          case X: rotateZ_90(-mult * DirectionUtil.getFrontOffsetX(to)); break;
          case Y: rotateZ_180(); break;
          case Z: rotateX_90(mult * DirectionUtil.getFrontOffsetZ(to)); break;
        }
        break;
      }
      case Z: {
        int mult = -DirectionUtil.getFrontOffsetZ(from);
        switch (to.getAxis()) {
          case X: rotateY_90(mult * DirectionUtil.getFrontOffsetX(to)); break;
          case Y: rotateX_90(mult * DirectionUtil.getFrontOffsetY(to)); break;
          case Z: rotateY_180(); break;
        }
        break;
      }
    }
    // @formatter:on
    translate(ox, oy, oz);
    return this;
  }
  public MutableQuad translate(float x, float y, float z) {
    vertex_0.translate(x, y, z);
    vertex_1.translate(x, y, z);
    vertex_2.translate(x, y, z);
    vertex_3.translate(x, y, z);
    return this;
  }

  public MutableQuad rotateX_90(float scale) {
    vertex_0.rotateX_90(scale);
    vertex_1.rotateX_90(scale);
    vertex_2.rotateX_90(scale);
    vertex_3.rotateX_90(scale);
    return this;
  }

  public MutableQuad rotateY_90(float scale) {
    vertex_0.rotateY_90(scale);
    vertex_1.rotateY_90(scale);
    vertex_2.rotateY_90(scale);
    vertex_3.rotateY_90(scale);
    return this;
  }

  public MutableQuad rotateZ_90(float scale) {
    vertex_0.rotateZ_90(scale);
    vertex_1.rotateZ_90(scale);
    vertex_2.rotateZ_90(scale);
    vertex_3.rotateZ_90(scale);
    return this;
  }

  public MutableQuad rotateY_180() {
    vertex_0.rotateY_180();
    vertex_1.rotateY_180();
    vertex_2.rotateY_180();
    vertex_3.rotateY_180();
    return this;
  }

  public MutableQuad rotateZ_180() {
    vertex_0.rotateZ_180();
    vertex_1.rotateZ_180();
    vertex_2.rotateZ_180();
    vertex_3.rotateZ_180();
    return this;
  }

}
