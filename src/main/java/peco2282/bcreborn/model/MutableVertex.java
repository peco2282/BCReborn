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

  /** Rotates this vertex around the X axis 90 degrees.
   *
   * @param scale The multiplier for scaling. Positive values will rotate clockwise, negative values rotate
   *            anti-clockwise. */
  public MutableVertex rotateX_90(float scale) {
    float ym = scale;
    float zm = -ym;

    float t = position.y * ym;
    position.y = position.z * zm;
    position.z = t;

    t = normal.y * ym;
    normal.y = normal.z * zm;
    normal.z = t;
    return this;
  }

  /** Rotates this vertex around the Y axis 90 degrees.
   *
   * @param scale The multiplier for scaling. Positive values will rotate clockwise, negative values rotate
   *            anti-clockwise. */
  public MutableVertex rotateY_90(float scale) {
    float xm = scale;
    float zm = -xm;

    float t = position.x * xm;
    position.x = position.z * zm;
    position.z = t;

    t = normal.x * xm;
    normal.x = normal.z * zm;
    normal.z = t;
    return this;
  }

  /** Rotates this vertex around the Z axis 90 degrees.
   *
   * @param scale The multiplier for scaling. Positive values will rotate clockwise, negative values rotate
   *            anti-clockwise. */
  public MutableVertex rotateZ_90(float scale) {
    float xm = scale;
    float ym = -xm;

    float t = position.x * xm;
    position.x = position.y * ym;
    position.y = t;

    t = normal.x * xm;
    normal.x = normal.y * ym;
    normal.y = t;
    return this;
  }

  /** Rotates this vertex around the X axis by 180 degrees. */
  public MutableVertex rotateX_180() {
    position.y = -position.y;
    position.z = -position.z;
    normal.y = -normal.y;
    normal.z = -normal.z;
    return this;
  }

  /** Rotates this vertex around the Y axis by 180 degrees. */
  public MutableVertex rotateY_180() {
    position.x = -position.x;
    position.z = -position.z;
    normal.x = -normal.x;
    normal.z = -normal.z;
    return this;
  }

  /** Rotates this vertex around the Z axis by 180 degrees. */
  public MutableVertex rotateZ_180() {
    position.x = -position.x;
    position.y = -position.y;
    normal.x = -normal.x;
    normal.y = -normal.y;
    return this;
  }

  public MutableVertex translate(float x, float y, float z) {
    position.x += x;
    position.y += y;
    position.z += z;
    return this;
  }
}
