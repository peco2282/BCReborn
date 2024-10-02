package peco2282.bcreborn.data.tag;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import peco2282.bcreborn.BCReborn;

public class BCFluidTag {
  public static final TagKey<Fluid> BURNABLE = create("burnable");
  public static final TagKey<Fluid> BURNABLE_SOURCE = create("burnable_source");
  public static final TagKey<Fluid> BURNABLE_FLOWING = create("burnable_flowing");
  private static TagKey<Fluid> create(String key) {
    return FluidTags.create(BCReborn.location(key));
  }
}
