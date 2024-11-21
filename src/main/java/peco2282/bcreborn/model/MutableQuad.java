package peco2282.bcreborn.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

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
}
