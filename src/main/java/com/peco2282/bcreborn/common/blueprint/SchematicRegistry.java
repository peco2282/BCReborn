/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.*;
import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.schematics.*;
import com.peco2282.bcreborn.common.builder.schematics.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;

public final class SchematicRegistry implements ISchematicRegistry {

  public static final SchematicRegistry INSTANCE;

  public final HashMap<BlockState, SchematicFactory<? extends SchematicBlock>> schematicBlocks = new HashMap<>();
  public final HashMap<EntityType<? extends Entity>, SchematicFactory<? extends SchematicEntity>> schematicEntities = new HashMap<>();

  private final HashSet<String> modsForbidden = new HashSet<>();
  private final HashSet<String> blocksForbidden = new HashSet<>();

  static {
    INSTANCE = new SchematicRegistry();
    BuilderAPI.schematicRegistry = INSTANCE;
  }
  private SchematicRegistry() {
  }

  @Override
  public void registerSchematicBlock(Block block, BlockState state, SchematicFactory<? extends SchematicBlock> factory) {
    if (block == null) {
      BCLog.logger.warn("Builder: Mod tried to register a null block schematic! Ignoring.");
      return;
    }
    if (schematicBlocks.containsKey(state)) {
      BCLog.logger.warn("Builder: BlockState " + state + " is already associated with a schematic. Skipping.");
      return;
    }
    try {
      schematicBlocks.put(state, factory);
    } catch (IllegalArgumentException e) {
      BCLog.logger.warn("Builder: Could not register schematic for blockstate " + state + ": " + e.getMessage());
    }
  }

  @Override
  public void registerSchematicEntity(
    EntityType<? extends Entity> type,
    SchematicFactory<? extends SchematicEntity> factory) {
    if (schematicEntities.containsKey(type)) {
      BCLog.logger.warn("Builder: Entity " + EntityType.getKey(type) + " is already associated with a schematic. Skipping.");
      return;
    }
    try {
      schematicEntities.put(type, factory);
    } catch (IllegalArgumentException e) {
      BCLog.logger.warn("Builder: Could not register schematic for entity " + EntityType.getKey(type) + ": " + e.getMessage());
    }
  }
  public SchematicBlock createSchematicBlock(Block block) {
    return createSchematicBlock(block, block.defaultBlockState());
  }

  public SchematicBlock createSchematicBlock(BlockState state) {
    return createSchematicBlock(state.getBlock(), state);
  }

  public SchematicBlock createSchematicBlock(Block block, BlockState state) {
    if (block == null) return null;

    SchematicFactory<? extends SchematicBlock> factory = schematicBlocks.get(state);
    if (factory == null) {
      // Fall back to meta 0
      factory = schematicBlocks.get(state);
    }
    if (factory == null) return new SchematicRotatableBlock(state);

    SchematicBlock s = factory.create();
    s.state = state;
    return s;
  }

  public SchematicEntity createSchematicEntity(EntityType<? extends Entity> type) {
    if (!schematicEntities.containsKey(type)) return null;

    SchematicFactory<? extends SchematicEntity> c = schematicEntities.get(type);
    SchematicEntity s = c.create();
    s.entity = type;
    return s;
  }

  public boolean isAllowedForBuilding(Block block, BlockState state) {
    ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
    String nameStr = name.toString();
    String modId = name.getNamespace();
    return isSupported(block, state)
      && !blocksForbidden.contains(nameStr)
      && !modsForbidden.contains(modId);
  }

  @Override
  public boolean isSupported(Block block, BlockState state) {
    return schematicBlocks.containsKey(state);
  }

  public static void init() {
    BuilderAPI.schematicRegistry = INSTANCE;


    ISchematicRegistry schemes = INSTANCE;
    schemes.registerSchematicBlock(Blocks.AIR, SchematicAir::new);

    schemes.registerSchematicBlock(Blocks.SNOW, SchematicIgnore::new);
    schemes.registerSchematicBlock(Blocks.TALL_GRASS, SchematicIgnore::new);
//    schemes.registerSchematicBlock(Blocks.DOUBLE_PLANT, SchematicIgnore::new);
    schemes.registerSchematicBlock(Blocks.ICE, SchematicIgnore::new);
    schemes.registerSchematicBlock(Blocks.PISTON_HEAD, SchematicIgnore::new);

    schemes.registerSchematicBlock(Blocks.DIRT, SchematicDirt::new);
    schemes.registerSchematicBlock(Blocks.GRASS, SchematicDirt::new);

    schemes.registerSchematicBlock(Blocks.CACTUS, SchematicCactus::new);

    schemes.registerSchematicBlock(Blocks.FARMLAND, SchematicFarmland::new);
    schemes.registerSchematicBlock(Blocks.WHEAT, () -> new SchematicSeeds(Items.WHEAT_SEEDS));
    schemes.registerSchematicBlock(Blocks.PUMPKIN_STEM, () -> new SchematicSeeds(Items.PUMPKIN_SEEDS));
    schemes.registerSchematicBlock(Blocks.MELON_STEM, () -> new SchematicSeeds(Items.MELON_SEEDS));
    schemes.registerSchematicBlock(Blocks.NETHER_WART, () -> new SchematicSeeds(Items.NETHER_WART));

    schemes.registerSchematicBlock(Blocks.FLOWER_POT, SchematicTile::new);

    schemes.registerSchematicBlock(Blocks.TRIPWIRE, SchematicTripwire::new);
    schemes.registerSchematicBlock(Blocks.TRIPWIRE_HOOK, SchematicTripWireHook::new);

//    schemes.registerSchematicBlock(Blocks.SKULL, SchematicSkull::new);

//    schemes.registerSchematicBlock(Blocks.LADDER, SchematicRotateMetaSupported::new);
//    schemes.registerSchematicBlock(Blocks.FENCE_GATE, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.LOG, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.LOG2, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.HAY_BLOCK, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.QUARTZ_BLOCK, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.HOPPER, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.ANVIL, SchematicRotateMeta::new);

//    schemes.registerSchematicBlock(Blocks.FURNACE, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.LIT_FURNACE, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.CHEST, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.TRAPPED_CHEST, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.DISPENSER, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.DROPPER, SchematicRotateMeta::new);

    schemes.registerSchematicBlock(Blocks.ENDER_CHEST, SchematicEnderChest::new);

//    schemes.registerSchematicBlock(Blocks.VINE, SchematicRotateMeta::new);
//    schemes.registerSchematicBlock(Blocks.TRAPDOOR, SchematicRotateMeta::new);

    schemes.registerSchematicBlock(Blocks.STONE, SchematicStone::new);
    schemes.registerSchematicBlock(Blocks.COAL_ORE, SchematicStone::new);
    schemes.registerSchematicBlock(Blocks.LAPIS_ORE, SchematicStone::new);
    schemes.registerSchematicBlock(Blocks.DIAMOND_ORE, SchematicStone::new);
    schemes.registerSchematicBlock(Blocks.REDSTONE_ORE, SchematicStone::new);
//    schemes.registerSchematicBlock(Blocks.LIT_REDSTONE_ORE, SchematicStone::new);
    schemes.registerSchematicBlock(Blocks.EMERALD_ORE, SchematicStone::new);

//    schemes.registerSchematicBlock(Blocks.LEAVES, SchematicMetadataMask::new, 3);
//    schemes.registerSchematicBlock(Blocks.LEAVES2, SchematicMetadataMask::new, 3);
//    schemes.registerSchematicBlock(Blocks.SAPLING, SchematicMetadataMask::new, 7);
//
//    schemes.registerSchematicBlock(Blocks.MONSTER_EGG, SchematicSilverfish::new);

    schemes.registerSchematicBlock(Blocks.GRAVEL, SchematicGravel::new);

    schemes.registerSchematicBlock(Blocks.REDSTONE_WIRE, () -> new SchematicRedstoneWire(new ItemStack(Items.REDSTONE)));
    schemes.registerSchematicBlock(Blocks.CAKE, () -> new SchematicCustomStack(new ItemStack(Items.CAKE)));
    schemes.registerSchematicBlock(Blocks.GLOWSTONE, () -> new SchematicCustomStack(new ItemStack(Blocks.GLOWSTONE)));

    schemes.registerSchematicBlock(Blocks.REPEATER, () -> new SchematicRedstoneDiode(Items.REPEATER));
//    schemes.registerSchematicBlock(Blocks.UNPOWERED_REPEATER, () -> new SchematicRedstoneDiode(Items.REPEATER));
    schemes.registerSchematicBlock(Blocks.COMPARATOR, () -> new SchematicRedstoneDiode(Items.COMPARATOR));
//    schemes.registerSchematicBlock(Blocks.UNPOWERED_COMPARATOR, () -> new SchematicRedstoneDiode(Items.COMPARATOR));

    schemes.registerSchematicBlock(Blocks.DAYLIGHT_DETECTOR, SchematicTile::new);
    schemes.registerSchematicBlock(Blocks.JUKEBOX, SchematicJukebox::new);
    schemes.registerSchematicBlock(Blocks.NOTE_BLOCK, SchematicTile::new);

    schemes.registerSchematicBlock(Blocks.REDSTONE_LAMP, SchematicRedstoneLamp::new);
//    schemes.registerSchematicBlock(Blocks.LIT_REDSTONE_LAMP, SchematicRedstoneLamp::new);

    schemes.registerSchematicBlock(Blocks.GLASS_PANE, SchematicGlassPane::new);
//    schemes.registerSchematicBlock(Blocks.STAINED_GLASS_PANE, SchematicGlassPane::new);

    schemes.registerSchematicBlock(Blocks.PISTON, SchematicPiston::new);
//    schemes.registerSchematicBlock(Blocks.PISTON_EXTENSION, SchematicPiston::new);
    schemes.registerSchematicBlock(Blocks.STICKY_PISTON, SchematicPiston::new);

//    schemes.registerSchematicBlock(Blocks.LIT_PUMPKIN, SchematicPumpkin::new);

//    schemes.registerSchematicBlock(Blocks.WOODEN_DOOR, () -> new SchematicDoor(new ItemStack(Items.WOODEN_DOOR)));
    schemes.registerSchematicBlock(Blocks.IRON_DOOR, () -> new SchematicDoor(new ItemStack(Items.IRON_DOOR)));

//    schemes.registerSchematicBlock(Blocks.BED, SchematicBed::new);

//    schemes.registerSchematicBlock(Blocks.WALL_SIGN, () -> new SchematicSign(true));
//    schemes.registerSchematicBlock(Blocks.STANDING_SIGN, () -> new SchematicSign(false));

    schemes.registerSchematicBlock(Blocks.NETHER_PORTAL, SchematicPortal::new);
    schemes.registerSchematicBlock(Blocks.END_PORTAL, SchematicPortal::new);
    schemes.registerSchematicBlock(Blocks.END_PORTAL_FRAME, SchematicPortal::new);

    schemes.registerSchematicBlock(Blocks.RAIL, SchematicRail::new);
    schemes.registerSchematicBlock(Blocks.ACTIVATOR_RAIL, SchematicRail::new);
    schemes.registerSchematicBlock(Blocks.DETECTOR_RAIL, SchematicRail::new);
    schemes.registerSchematicBlock(Blocks.POWERED_RAIL, SchematicRail::new);

    schemes.registerSchematicBlock(Blocks.BEACON, SchematicTile::new);
    schemes.registerSchematicBlock(Blocks.BREWING_STAND, SchematicBrewingStand::new);
    schemes.registerSchematicBlock(Blocks.ENCHANTING_TABLE, SchematicTile::new);

    schemes.registerSchematicBlock(Blocks.FIRE, SchematicFire::new);

    schemes.registerSchematicBlock(Blocks.BEDROCK, SchematicBlockCreative::new);

    schemes.registerSchematicBlock(Blocks.COMMAND_BLOCK, SchematicTileCreative::new);
    schemes.registerSchematicBlock(Blocks.SPAWNER, SchematicTileCreative::new);

    // Standard entities

    schemes.registerSchematicEntity(EntityType.MINECART, () -> new SchematicMinecart(Items.MINECART));
    schemes.registerSchematicEntity(EntityType.FURNACE_MINECART, () -> new SchematicMinecart(Items.FURNACE_MINECART));
    schemes.registerSchematicEntity(EntityType.TNT_MINECART, () -> new SchematicMinecart(Items.TNT_MINECART));
    schemes.registerSchematicEntity(EntityType.CHEST_MINECART, () -> new SchematicMinecart(Items.CHEST_MINECART));
    schemes.registerSchematicEntity(EntityType.HOPPER_MINECART, () -> new SchematicMinecart(Items.HOPPER_MINECART));

    schemes.registerSchematicEntity(EntityType.PAINTING, () -> new SchematicHanging(Items.PAINTING));
    schemes.registerSchematicEntity(EntityType.ITEM_FRAME, () -> new SchematicHanging(Items.ITEM_FRAME));

//    // BuildCraft blocks
//    schemes.registerSchematicBlock(architectBlock, SchematicRotateMeta.class, new int[]{2, 5, 3, 4}, true);
    schemes.registerSchematicBlock(BuildersBlock.BUILDER.get(), SchematicBuilderLike::new);
    schemes.registerSchematicBlock(BuildersBlock.FILLER.get(), SchematicBuilderLike::new);
//    schemes.registerSchematicBlock(libraryBlock, SchematicRotateMeta.class, new int[]{2, 5, 3, 4}, true);
    schemes.registerSchematicBlock(BuildersBlock.QUARRY.get(), SchematicBuilderLike::new);
    schemes.registerSchematicBlock(BuildersBlock.CONSTRUCTION_MARKER.get(), SchematicIgnore::new);
    schemes.registerSchematicBlock(BuildersBlock.FRAME.get(), SchematicFree::new);

  }
}
