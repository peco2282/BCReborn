package peco2282.bcreborn.builder.saved;

import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.level.block.Block;

public class BluePrintTemplate {
  public void load(HolderGetter<Block> getter, CompoundTag tag) {
  }

  public CompoundTag save(RangeCache cache) {
    CompoundTag tag = new CompoundTag();
    tag.put("size", new Size(0, 0, 0).tag());
    return tag;
  }

  private record Size(int x, int y, int z) {
    IntArrayTag tag() {
      return new IntArrayTag(new int[]{x, y, z});
    }
  }
}
