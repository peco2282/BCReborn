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
package com.peco2282.bcreborn.api.blueprints;


import com.peco2282.bcreborn.api.core.BCLog;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

/**
 * Registry that manages the mapping of blocks, items, and entities between a blueprint and the world.
 * This handles coordinate and ID translations.
 */
@SuppressWarnings({"unused", "deprecation"})
public class MappingRegistry {
  private final MappingTable<Block> BLOCK = new MappingTable<>();
  private final MappingTable<Item> ITEM = new MappingTable<>();
  private final MappingTable<EntityType<? extends Entity>> ENTITY = new MappingTable<>();

  private void registerItem(Item item) {
    ITEM.register(item);
  }

  private void registerBlock(Block block) {
    BLOCK.register(block);
  }

  private void registerEntity(EntityType<? extends Entity> entityClass) {
    ENTITY.register(entityClass);
  }

  /**
   * Gets the item for a specific mapping ID.
   *
   * @param id The mapping ID.
   * @return The {@link Item}.
   * @throws MappingNotFoundException if the mapping does not exist.
   */
  public Item getItemForId(int id) throws MappingNotFoundException {
    return ITEM.get(id);
  }

  /**
   * Gets the mapping ID for a specific item.
   *
   * @param item The item.
   * @return The mapping ID.
   */
  public int getIdForItem(Item item) {
    return ITEM.getId(item);
  }

  /**
   * Translates a world item ID to a registry mapping ID.
   *
   * @param id The world item ID.
   * @return The registry mapping ID.
   */
  public int itemIdToRegistry(int id) {
    Item item = Item.byId(id);

    return getIdForItem(item);
  }

  /**
   * Translates a registry mapping ID to a world item ID.
   *
   * @param id The registry mapping ID.
   * @return The world item ID.
   * @throws MappingNotFoundException if the mapping does not exist.
   */
  public int itemIdToWorld(int id) throws MappingNotFoundException {
    Item item = getItemForId(id);

    return Item.getId(item);
  }

  /**
   * Gets the block for a specific mapping ID.
   *
   * @param id The mapping ID.
   * @return The {@link Block}.
   * @throws MappingNotFoundException if the mapping does not exist.
   */
  public Block getBlockForId(int id) throws MappingNotFoundException {
    return BLOCK.get(id);
  }

  /**
   * Gets the mapping ID for a specific block.
   *
   * @param block The block.
   * @return The mapping ID.
   */
  public int getIdForBlock(Block block) {
    return BLOCK.getId(block);
  }

  /**
   * Translates a world block ID to a registry mapping ID.
   *
   * @param id The world block ID.
   * @return The registry mapping ID.
   */
  public int blockIdToRegistry(int id) {
    Block block = BuiltInRegistries.BLOCK.byId(id);

    return getIdForBlock(block);
  }

  /**
   * Translates a registry mapping ID to a world block ID.
   *
   * @param id The registry mapping ID.
   * @return The world block ID.
   * @throws MappingNotFoundException if the mapping does not exist.
   */
  public int blockIdToWorld(int id) throws MappingNotFoundException {
    Block block = BLOCK.get(id);

    return BuiltInRegistries.BLOCK.getId(block);
  }

  /**
   * Gets the entity type for a specific mapping ID.
   *
   * @param id The mapping ID.
   * @return The {@link EntityType}.
   * @throws MappingNotFoundException if the mapping does not exist.
   */
  public EntityType<? extends Entity> getEntityForId(int id) throws MappingNotFoundException {
    return ENTITY.get(id);
  }

  /**
   * Gets the mapping ID for a specific entity type.
   *
   * @param entity The entity type.
   * @return The mapping ID.
   */
  public int getIdForEntity(EntityType<? extends Entity> entity) {
    return ENTITY.getId(entity);
  }

  /**
   * Translates an {@link ItemStack} NBT tag from the world referential to the registry referential.
   *
   * @param nbt The NBT tag to modify.
   */
  public void stackToRegistry(CompoundTag nbt) {
    Item item = Item.byId(nbt.getShort("id"));
    nbt.putShort("id", (short) getIdForItem(item));
  }

  /**
   * Translates an {@link ItemStack} NBT tag from the registry referential to the world referential.
   *
   * @param nbt The NBT tag to modify.
   * @throws MappingNotFoundException if the mapping does not exist.
   */
  public void stackToWorld(CompoundTag nbt) throws MappingNotFoundException {
    Item item = getItemForId(nbt.getShort("id"));
    nbt.putShort("id", (short) Item.getId(item));
  }

  private boolean isStackLayout(CompoundTag nbt) {
    return nbt.contains("id") &&
      nbt.contains("Count") &&
      nbt.contains("Damage") &&
      nbt.get("id") instanceof ShortTag &&
      nbt.get("Count") instanceof ByteTag &&
      nbt.get("Damage") instanceof ShortTag;
  }

  /**
   * Scans an NBT tag and translates all item stacks found within to the registry referential.
   *
   * @param nbt The NBT tag to scan and modify.
   */
  public void scanAndTranslateStacksToRegistry(CompoundTag nbt) {
    if (isStackLayout(nbt)) {
      stackToRegistry(nbt);
    }

    for (String key : nbt.getAllKeys()) {
      if (nbt.get(key) instanceof CompoundTag) {
        scanAndTranslateStacksToRegistry(nbt.getCompound(key));
      }

      if (nbt.get(key) instanceof ListTag list) {
        if (list.getElementType() == Tag.TAG_COMPOUND) {
          for (int i = 0; i < list.size(); ++i) {
            scanAndTranslateStacksToRegistry(list.getCompound(i));
          }
        }
      }
    }
  }

  /**
   * Scans an NBT tag and translates all item stacks found within to the world referential.
   *
   * @param nbt The NBT tag to scan and modify.
   * @throws MappingNotFoundException if any mapping is not found.
   */
  public void scanAndTranslateStacksToWorld(CompoundTag nbt) throws MappingNotFoundException {
    if (isStackLayout(nbt)) {
      stackToWorld(nbt);
    }

    for (String key : nbt.getAllKeys()) {
      if (nbt.get(key) instanceof CompoundTag) {
        try {
          scanAndTranslateStacksToWorld(nbt.getCompound(key));
        } catch (MappingNotFoundException e) {
          nbt.remove(key);
        }
      }

      if (nbt.get(key) instanceof ListTag list) {
        if (list.getElementType() == Tag.TAG_COMPOUND) {
          for (int i = list.size() - 1; i >= 0; --i) {
            try {
              scanAndTranslateStacksToWorld(list.getCompound(i));
            } catch (MappingNotFoundException e) {
              list.remove(i);
            }
          }
        }
      }
    }
  }

  /**
   * Writes a {@link BlockState} to an NBT tag using registry mapping.
   *
   * @param nbt   The NBT tag.
   * @param state The block state.
   */
  public void writeBlockStateToNBT(CompoundTag nbt, BlockState state) {
    if (state == null) {
      return;
    }
    nbt.putInt("blockId", getIdForBlock(state.getBlock()));
    CompoundTag props = new CompoundTag();
    for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
      Property<?> prop = entry.getKey();
      props.putString(prop.getName(), getName(prop, entry.getValue()));
    }
    nbt.put("properties", props);
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<T>> String getName(Property<T> prop, Comparable<?> value) {
    return prop.getName((T) value);
  }

  /**
   * Reads a {@link BlockState} from an NBT tag using registry mapping.
   *
   * @param nbt The NBT tag.
   * @return The block state.
   * @throws MappingNotFoundException if the block mapping is not found.
   */
  public BlockState readBlockStateFromNBT(CompoundTag nbt) throws MappingNotFoundException {
    Block block = getBlockForId(nbt.getInt("blockId"));
    BlockState state = block.defaultBlockState();
    if (nbt.contains("properties")) {
      CompoundTag props = nbt.getCompound("properties");
      for (String key : props.getAllKeys()) {
        Property<?> prop = block.getStateDefinition().getProperty(key);
        if (prop != null) {
          state = setValueHelper(state, prop, props.getString(key));
        }
      }
    }
    return state;
  }

  private <T extends Comparable<T>> BlockState setValueHelper(BlockState state, Property<T> prop, String valueName) {
    return prop.getValue(valueName).map(t -> state.setValue(prop, t)).orElse(state);
  }

  /**
   * Writes the entire mapping registry to an NBT tag.
   *
   * @param nbt The NBT tag.
   */
  public void write(CompoundTag nbt) {
    ListTag blocksMapping = new ListTag();

    for (Block b : BLOCK) {
      CompoundTag sub = new CompoundTag();
      if (b != null) {
        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(b);
        sub.putString("name", name.toString());
      }
      blocksMapping.add(sub);
    }

    nbt.put("blocksMapping", blocksMapping);

    ListTag itemsMapping = new ListTag();

    for (Item i : ITEM) {
      CompoundTag sub = new CompoundTag();
      if (i != null) {
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(i);
        sub.putString("name", name.toString());
      }
      itemsMapping.add(sub);
    }

    nbt.put("itemsMapping", itemsMapping);

    ListTag entitiesMapping = new ListTag();

    for (EntityType<? extends Entity> e : ENTITY) {
      CompoundTag sub = new CompoundTag();
      sub.putString("name", EntityType.getKey(e).toString());
      entitiesMapping.add(sub);
    }

    nbt.put("entitiesMapping", entitiesMapping);
  }

  /**
   * Reads the entire mapping registry from an NBT tag.
   *
   * @param nbt The NBT tag.
   */
  public void read(CompoundTag nbt) {
    ListTag blocksMapping = nbt.getList("blocksMapping", Tag.TAG_COMPOUND);

    for (int i = 0; i < blocksMapping.size(); ++i) {
      CompoundTag sub = blocksMapping.getCompound(i);
      if (!sub.contains("name")) {
        BLOCK.addMissing();
        BCLog.logger.warn("Can't load a block - corrupt blueprint!");
        continue;
      }
      String name = sub.getString("name");
      ResourceLocation rl = ResourceLocation.tryParse(name);
      Block b = rl != null ? BuiltInRegistries.BLOCK.get(rl) : null;

      if (b != null) {
        BLOCK.register(b);
      } else {
        BLOCK.addMissing();
        BCLog.logger.warn("Can't load block " + name);
      }
    }

    ListTag itemsMapping = nbt.getList("itemsMapping", Tag.TAG_COMPOUND);

    for (int i = 0; i < itemsMapping.size(); ++i) {
      CompoundTag sub = itemsMapping.getCompound(i);
      if (!sub.contains("name")) {
        ITEM.addMissing();
        BCLog.logger.warn("Can't load an item - corrupt blueprint!");
        continue;
      }
      String name = sub.getString("name");
      ResourceLocation rl = ResourceLocation.tryParse(name);
      Item item = rl != null ? BuiltInRegistries.ITEM.get(rl) : null;

      if (item != null) {
        ITEM.register(item);
      } else {
        ITEM.addMissing();
        BCLog.logger.warn("Can't load item " + name);
      }
    }

    ListTag entitiesMapping = nbt.getList("entitiesMapping", Tag.TAG_COMPOUND);

    for (int i = 0; i < entitiesMapping.size(); ++i) {
      CompoundTag sub = entitiesMapping.getCompound(i);
      String name = sub.getString("name");

      EntityType.byString(name).ifPresentOrElse(
        this::registerEntity,
        () -> {
          ENTITY.addMissing();
          BCLog.logger.warn("Can't load entity " + name);
        }
      );
    }
  }
}
