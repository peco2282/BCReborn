package peco2282.bcreborn.transport.block;

import net.minecraft.util.StringRepresentable;

public enum PipeMaterial implements StringRepresentable {
  WOOD("wood"),
  STONE("stone"),
  COBBLESTONE("cobblestone"),
  IRON("iron"),
  GOLD("gold"),
  DIAMOND("diamond"),
  ;
  private final String material;

  PipeMaterial(String material) {
    this.material = material;
  }

  @Override
  public String getSerializedName() {
    return material;
  }
}