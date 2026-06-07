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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings({"unused", "deprecation"})
public class MappingRegistry {

  public Object2IntMap<Block> blockToId = new Object2IntOpenHashMap<>();
  public ArrayList<Block> idToBlock = new ArrayList<>();

  public Object2IntMap<Item> itemToId = new Object2IntOpenHashMap<>();
  public ArrayList<Item> idToItem = new ArrayList<>();

  public Object2IntMap<Class<? extends Entity>> entityToId = new Object2IntOpenHashMap<>();
  public ArrayList<Class<? extends Entity>> idToEntity = new ArrayList<>();

  private void registerItem(Item item) {
    if (!itemToId.containsKey(item)) {
      idToItem.add(item);
      itemToId.put(item, idToItem.size() - 1);
    }
  }

  private void registerBlock(Block block) {
    if (!blockToId.containsKey(block)) {
      idToBlock.add(block);
      blockToId.put(block, idToBlock.size() - 1);
    }
  }

  private void registerEntity(Class<? extends Entity> entityClass) {
    if (!entityToId.containsKey(entityClass)) {
      idToEntity.add(entityClass);
      entityToId.put(entityClass, idToEntity.size() - 1);
    }
  }

  public Item getItemForId(int id) throws MappingNotFoundException {
    if (id >= idToItem.size()) {
      throw new MappingNotFoundException("no item mapping at position " + id);
    }

    Item result = idToItem.get(id);

    if (result == null) {
      throw new MappingNotFoundException("no item mapping at position " + id);
    } else {
      return result;
    }
  }

  public int getIdForItem(Item item) {
    if (!itemToId.containsKey(item)) {
      registerItem(item);
    }

    return itemToId.getInt(item);
  }

  public int itemIdToRegistry(int id) {
    Item item = Item.byId(id);

    return getIdForItem(item);
  }

  public int itemIdToWorld(int id) throws MappingNotFoundException {
    Item item = getItemForId(id);

    return Item.getId(item);
  }

  public Block getBlockForId(int id) throws MappingNotFoundException {
    if (id >= idToBlock.size()) {
      throw new MappingNotFoundException("no block mapping at position " + id);
    }

    Block result = idToBlock.get(id);

    if (result == null) {
      throw new MappingNotFoundException("no block mapping at position " + id);
    } else {
      return result;
    }
  }

  public int getIdForBlock(Block block) {
    if (!blockToId.containsKey(block)) {
      registerBlock(block);
    }

    return blockToId.getInt(block);
  }

  public int blockIdToRegistry(int id) {
    Block block = BuiltInRegistries.BLOCK.byId(id);

    return getIdForBlock(block);
  }

  public int blockIdToWorld(int id) throws MappingNotFoundException {
    Block block = getBlockForId(id);

    return BuiltInRegistries.BLOCK.getId(block);
  }

  public Class<? extends Entity> getEntityForId(int id) throws MappingNotFoundException {
    if (id >= idToEntity.size()) {
      throw new MappingNotFoundException("no entity mapping at position " + id);
    }

    Class<? extends Entity> result = idToEntity.get(id);

    if (result == null) {
      throw new MappingNotFoundException("no entity mapping at position " + id);
    } else {
      return result;
    }
  }

  public int getIdForEntity(Class<? extends Entity> entity) {
    if (!entityToId.containsKey(entity)) {
      registerEntity(entity);
    }

    return entityToId.getInt(entity);
  }

  /**
   * Relocates a stack nbt from the world referential to the registry
   * referential.
   */
  public void stackToRegistry(CompoundTag nbt) {
    Item item = Item.byId(nbt.getShort("id"));
    nbt.putShort("id", (short) getIdForItem(item));
  }

  /**
   * Relocates a stack nbt from the registry referential to the world
   * referential.
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

  public void write(CompoundTag nbt) {
    ListTag blocksMapping = new ListTag();

    for (Block b : idToBlock) {
      CompoundTag sub = new CompoundTag();
      if (b != null) {
        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(b);
        if (name == null) {
          BCLog.logger.error("Block " + b.getName().getString() + " (" + b.getClass().getName() + ") has an empty registry name! This is a bug!");
        } else {
          sub.putString("name", name.toString());
        }
      }
      blocksMapping.add(sub);
    }

    nbt.put("blocksMapping", blocksMapping);

    ListTag itemsMapping = new ListTag();

    for (Item i : idToItem) {
      CompoundTag sub = new CompoundTag();
      if (i != null) {
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(i);
        if (name == null) {
          BCLog.logger.error("Item " + i.getDescriptionId() + " (" + i.getClass().getName() + ") has an empty registry name! This is a bug!");
        } else {
          sub.putString("name", name.toString());
        }
      }
      itemsMapping.add(sub);
    }

    nbt.put("itemsMapping", itemsMapping);

    ListTag entitiesMapping = new ListTag();

    for (Class<? extends Entity> e : idToEntity) {
      CompoundTag sub = new CompoundTag();
      sub.putString("name", e.getCanonicalName());
      entitiesMapping.add(sub);
    }

    nbt.put("entitiesMapping", entitiesMapping);
  }

  public void read(CompoundTag nbt) {
    ListTag blocksMapping = nbt.getList("blocksMapping", Tag.TAG_COMPOUND);

    for (int i = 0; i < blocksMapping.size(); ++i) {
      CompoundTag sub = blocksMapping.getCompound(i);
      if (!sub.contains("name")) {
        idToBlock.add(null);
        BCLog.logger.warn("Can't load a block - corrupt blueprint!");
        continue;
      }
      String name = sub.getString("name");
      ResourceLocation rl = ResourceLocation.tryParse(name);
      Block b = rl != null ? BuiltInRegistries.BLOCK.get(rl) : null;

      if (b != null) {
        registerBlock(b);
      } else {
        idToBlock.add(null);
        BCLog.logger.warn("Can't load block " + name);
      }
    }

    ListTag itemsMapping = nbt.getList("itemsMapping", Tag.TAG_COMPOUND);

    for (int i = 0; i < itemsMapping.size(); ++i) {
      CompoundTag sub = itemsMapping.getCompound(i);
      if (!sub.contains("name")) {
        idToItem.add(null);
        BCLog.logger.warn("Can't load an item - corrupt blueprint!");
        continue;
      }
      String name = sub.getString("name");
      ResourceLocation rl = ResourceLocation.tryParse(name);
      Item item = rl != null ? BuiltInRegistries.ITEM.get(rl) : null;

      if (item != null) {
        registerItem(item);
      } else {
        idToItem.add(null);
        BCLog.logger.warn("Can't load item " + name);
      }
    }

    ListTag entitiesMapping = nbt.getList("entitiesMapping", Tag.TAG_COMPOUND);

    for (int i = 0; i < entitiesMapping.size(); ++i) {
      CompoundTag sub = entitiesMapping.getCompound(i);
      String name = sub.getString("name");
      Class<? extends Entity> e = null;

      try {
        //noinspection unchecked
        e = (Class<? extends Entity>) Class.forName(name);
      } catch (ClassNotFoundException e1) {
        e1.printStackTrace();
      }

      if (e != null) {
        registerEntity(e);
      } else {
        idToEntity.add(null);
        BCLog.logger.warn("Can't load entity " + name);
      }
    }
  }
}
