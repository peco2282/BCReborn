package peco2282.bcreborn.model;

public record UvFace(float minU, float minV, float maxU, float maxV) {
  public UvFace(float minU, float minV, float maxU, float maxV) {
    this.minU = minU / 16F;
    this.minV = minV / 16F;
    this.maxU = maxU / 16F;
    this.maxV = maxV / 16F;
  }
  public static final UvFace DEFAULT = new UvFace(0F, 0F, 1F, 1F);
}
