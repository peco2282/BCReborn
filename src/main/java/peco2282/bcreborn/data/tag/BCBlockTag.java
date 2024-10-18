package peco2282.bcreborn.data.tag;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.BCReborn;

@SuppressWarnings("SameParameterValue")
public class BCBlockTag {
  // Type
  public static final TagKey<Block> ENGINE = create("engine");
  public static final TagKey<Block> PIPE = create("pipe");
  public static final TagKey<Block> ITEM_PIPE = create("item_pipe");
  public static final TagKey<Block> FLUID_PIPE = create("fluid_pipe");
  public static final TagKey<Block> ENERGY_PIPE = create("energy_pipe");

  // Package
  public static final TagKey<Block> CORE = create("core");
  public static final TagKey<Block> BUILDER = create("builder");
  public static final TagKey<Block> TRANSPORT = create("transport");

  private static TagKey<Block> create(String name) {
    return BlockTags.create(BCReborn.MODID, name);
  }
}
