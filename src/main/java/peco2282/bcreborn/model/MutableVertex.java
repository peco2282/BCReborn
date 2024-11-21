package peco2282.bcreborn.model;

import org.joml.Vector3f;

@SuppressWarnings({"PointlessArithmeticExpression", "PointlessBitwiseExpression"})
public class MutableVertex {
  private static final int color = 0xFF;
  private Vector3f position;
  private Vector3f normal;
  private short r, g, b, a;
  private float texU, texV;
  private byte light_block, light_sky;

  public MutableVertex() {
    position = new Vector3f().add(0, 1, 0);
    r = g = b = color;
  }


  public void toBakedItem(int[] data, int offset) {
    // POSITION_3F
    data[offset + 0] = Float.floatToRawIntBits(position.x());
    data[offset + 1] = Float.floatToRawIntBits(position.y());
    data[offset + 2] = Float.floatToRawIntBits(position.z());
    // COLOR_4UB
    data[offset + 3] = colorRGBA();
    // TEX_2F
    data[offset + 4] = Float.floatToRawIntBits(texU);
    data[offset + 5] = Float.floatToRawIntBits(texV);
    // NORMAL_3B
    data[offset + 6] = normalToPackedInt();
  }

  private int colorRGBA() {
    return (r & 0xFF) << 0 | (g & 0xFF) << 8 | (b & 0xFF) << 16 | (a & 0xFF) << 24;
  }

  public void fromBakedBlock(int[] data, int offset) {
    // POSITION_3F
    position.set(
        Float.intBitsToFloat(data[offset + 0]),
        Float.intBitsToFloat(data[offset + 1]),
        Float.intBitsToFloat(data[offset + 2])
    );
    // COLOR_4UB
    colouri(data[offset + 3]);
    // TEX_2F
    texU = Float.intBitsToFloat(data[offset + 4]);
    texV = Float.intBitsToFloat(data[offset + 5]);
    // TEX_2S
    lighti(data[offset + 6]);
    normalf(0, 1, 0);
  }

  private void normalf(int x, int y, int z) {
    normal.set(x, y, z);
  }

  public void colouri(int rgba) {
    colouri(rgba, rgba >> 8, rgba >> 16, rgba >>> 24);
  }

  public void colouri(int r, int g, int b, int a) {
    this.r = (short) (r & 0xFF);
    this.g = (short) (g & 0xFF);
    this.b = (short) (b & 0xFF);
    this.a = (short) (a & 0xFF);
  }

  public int normalToPackedInt() {
    return normalAsByte(normal.x(), 0)
        | normalAsByte(normal.y(), 8)
        | normalAsByte(normal.z(), 16);
  }

  private static int normalAsByte(float norm, int offset) {
    int as = (int) (norm * 0x7F);
    return as << offset;
  }

  public void lighti(int combined) {
    lighti(combined >> 4, combined >> 20);
  }

  public void lighti(int block, int sky) {
    light_block = (byte) block;
    light_sky = (byte) sky;
  }

  public MutableVertex setPosition(Vector3f vector) {
    position.set(vector);
    return this;
  }

  public MutableVertex setUV(float u, float v) {
    this.texU = u;
    this.texV = v;
    return this;
  }

  public MutableVertex setNormal(Vector3f vector) {
    normal.set(vector);
    return this;
  }
}
