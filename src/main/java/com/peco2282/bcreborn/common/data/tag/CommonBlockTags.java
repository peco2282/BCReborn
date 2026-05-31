package com.peco2282.bcreborn.common.data.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CommonBlockTags {
  private static final String MODID = "bcreborn";

  public static final TagKey<Block> BUILDERS = create("builders");
  public static final TagKey<Block> CORE = create("core");
  public static final TagKey<Block> ENERGY = create("energy");
  public static final TagKey<Block> FACTORY = create("factory");
  public static final TagKey<Block> ROBOTICS = create("robotics");
  public static final TagKey<Block> SILICON = create("silicon");
  public static final TagKey<Block> TRANSPORT = create("transport");

  public static final TagKey<Block> PIPE_ITEM = create("pipe_item");
  public static final TagKey<Block> PIPE_FLUID = create("pipe_fluid");
  public static final TagKey<Block> PIPE_ENERGY = create("pipe_energy");

  public static final TagKey<Block> PIPES = create("pipes");

  public static final TagKey<Block> ENGINES = create("engines");

  public static final TagKey<Block> FUEL = create("fuel_bucket");

  public static final TagKey<Block> LASER_TABLE = create("laser_table");

  private static TagKey<Block> create(String name) {
    return BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
  }
}
