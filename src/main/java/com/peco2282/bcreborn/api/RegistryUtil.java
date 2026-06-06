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
package com.peco2282.bcreborn.api;

import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.registry.BCRegistryKeys;
import com.peco2282.bcreborn.api.statements.IStatement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface RegistryUtil {
  /**
   * Retrieves a named holder set of blocks matching the specified tag key.
   *
   * @param key The tag key for the blocks.
   * @return A named {@link HolderSet} of blocks associated with the tag.
   */
  static HolderSet.Named<Block> fromBlockTag(TagKey<Block> key) {
    return BuiltInRegistries.BLOCK.getOrCreateTag(key);
  }

  /**
   * Retrieves a named holder set of items matching the specified tag key.
   *
   * @param key The tag key for the items.
   * @return A named {@link HolderSet} of items associated with the tag.
   */
  static HolderSet.Named<Item> fromItemTag(TagKey<Item> key) {
    return BuiltInRegistries.ITEM.getOrCreateTag(key);
  }

  /**
   * Converts all blocks from a tag key into a flat list of {@link Block} objects.
   *
   * @param key The tag key for the blocks.
   * @return A list of {@link Block} objects contained in the tag.
   */
  static List<Block> flattenBlock(TagKey<Block> key) {
    return fromBlockTag(key).stream().map(Holder::get).toList();
  }

  /**
   * Converts all items from a tag key into a flat list of {@link Item} objects.
   *
   * @param key The tag key for the items.
   * @return A list of {@link Item} objects contained in the tag.
   */
  static List<Item> flattenItem(TagKey<Item> key) {
    return fromItemTag(key).stream().map(Holder::get).toList();
  }

  static Set<Map.Entry<ResourceKey<Schematic>, Schematic>> getSchematics() {
    return getRegistry(BCRegistryKeys.SCHEMATIC).getEntries();
  }

  static Set<Map.Entry<ResourceKey<IStatement>, IFillerPattern>> getFillerPatterns() {
    return getRegistry(BCRegistryKeys.STATEMENT).getEntries()
      .stream()
      .filter(it -> it.getValue() instanceof IFillerPattern)
      .map(it -> Map.entry(it.getKey(), (IFillerPattern) it.getValue()))
      .collect(Collectors.toUnmodifiableSet());
  }

  static RedstoneBoardNBT<?> getRedstoneBoard(CompoundTag tag) {
    return getRedstoneBoard(tag.getString("id"));
  }

  static RedstoneBoardNBT<?> getRedstoneBoard(String id) {
    return getRedstoneBoardsList().stream().filter(it -> it.getID().equals(ResourceLocation.parse(id))).findFirst().orElseGet(RedstoneBoardNBT.getEmpty());
  }

  static List<RedstoneBoardNBT<?>> getRedstoneBoardsList() {
    return getRegistry(BCRegistryKeys.REDSTONE_BOARD).getEntries().stream()
      .map(Map.Entry::getValue)
      .<RedstoneBoardNBT<?>>map(it -> (RedstoneBoardNBT<?>) it)
      .toList();
  }

  @Contract(value = "null->fail; _->!null", pure = true)
  static <T> IForgeRegistry<T> getRegistry(ResourceKey<Registry<T>> key) {
    if (key == null) {
      throw new IllegalArgumentException("ResourceKey cannot be null");
    }
    return RegistryManager.ACTIVE.getRegistry(key);
  }
}
