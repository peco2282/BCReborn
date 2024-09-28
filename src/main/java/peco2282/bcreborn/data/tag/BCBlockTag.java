package peco2282.bcreborn.data.tag;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.BCReborn;

@SuppressWarnings("SameParameterValue")
public class BCBlockTag {
  public static final TagKey<Block> ENGINE = create("engine");

  private static TagKey<Block> create(String name) {
    return BlockTags.create(BCReborn.MODID, name);
  }
}
