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
package com.peco2282.bcreborn.common.utils;

import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.tiles.IBlockEntityAreaProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.api.power.IEngine;
import com.peco2282.bcreborn.api.power.ILaserTarget;
import com.peco2282.bcreborn.api.transport.IInjectable;
import com.peco2282.bcreborn.common.inventory.ITransactor;
import com.peco2282.bcreborn.common.inventory.InvUtils;
import com.peco2282.bcreborn.common.inventory.Transactor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Utils {
  public static final boolean CAULDRON_DETECTED;
  public static final XorShift128Random RANDOM = new XorShift128Random();
  private static final List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));

  static {
    boolean cauldron = false;
    try {
      cauldron = Utils.class.getClassLoader().loadClass("org.spigotmc.SpigotConfig") != null;
    } catch (ClassNotFoundException ignored) {
    }
    CAULDRON_DETECTED = cauldron;
  }


  /**
   * Deactivate constructor
   */
  private Utils() {
  }

  public static boolean isRegistered(Block block) {
    return BuiltInRegistries.BLOCK.containsKey(BuiltInRegistries.BLOCK.getKey(block));
  }

  public static boolean isRegistered(Item item) {
    return BuiltInRegistries.ITEM.containsKey(BuiltInRegistries.ITEM.getKey(item));
  }

  public static boolean isRegistered(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    return isRegistered(stack.getItem());
  }

  /**
   * Tries to add the passed stack to any valid inventories around the given
   * coordinates.
   *
   * @param stack the item stack to be added
   * @param world the world where the inventories are located
   * @param pos   the position of the inventories
   * @return amount used
   */
  public static int addToRandomInventoryAround(Level world, BlockPos pos, ItemStack stack) {
    Collections.shuffle(directions);
    for (Direction orientation : directions) {
      BlockPos neighbor = pos.relative(orientation);

      BlockEntity tileInventory = BlockUtils.getTileEntity(world, neighbor);
      ITransactor transactor = Transactor.getTransactorFor(tileInventory);
      if (transactor != null && !(tileInventory instanceof IEngine) && !(tileInventory instanceof ILaserTarget)) {
        ItemStack added = transactor.add(stack, orientation.getOpposite(), true);
        if (!added.isEmpty()) {
          return added.getCount();
        }
      }
    }
    return 0;
  }

  public static int addToRandomInjectableAround(Level world, BlockPos pos, @Nullable Direction from, ItemStack stack) {
    List<IInjectable> possiblePipes = new ArrayList<>();
    List<Direction> pipeDirections = new ArrayList<>();

    for (Direction side : Direction.values()) {
      if (from != null && from.getOpposite() == side) {
        continue;
      }

      BlockPos neighbor = pos.relative(side);

      BlockEntity tile = BlockUtils.getTileEntity(world, neighbor);

      if (tile instanceof IInjectable injectable) {
        if (!injectable.canInjectItems(side.getOpposite())) {
          continue;
        }

        possiblePipes.add(injectable);
        pipeDirections.add(side.getOpposite());
      }
    }

    if (!possiblePipes.isEmpty()) {
      int choice = RANDOM.nextInt(possiblePipes.size());

      IInjectable pipeEntry = possiblePipes.get(choice);

      return pipeEntry.injectItem(stack, true, pipeDirections.get(choice), null);
    }
    return 0;
  }

  public static void dropTryIntoPlayerInventory(Level world, BlockPos pos, ItemStack stack, Player player) {
    if (player.getInventory().add(stack)) {
      if (player instanceof ServerPlayer serverPlayer) {
        serverPlayer.containerMenu.broadcastChanges();
      }
    }
    InvUtils.dropItems(world, stack, pos.getX(), pos.getY(), pos.getZ());
  }

  /**
   * Port of the original {@code Utils.getNearbyAreaProvider()}: finds a loaded area provider (a landmark group)
   * whose box the given position touches, according to {@link IBlockEntityAreaProvider#isValidFromLocation}.
   * <p>
   * The original scanned {@code world.loadedTileEntityList}; here only the already-loaded chunks within
   * {@link MarkerBlockEntity#MARKER_RANGE} are scanned, since a valid provider's box can never be further away.
   * No chunk is loaded by this lookup.
   */
  @Nullable
  public static IAreaProvider getNearbyAreaProvider(Level world, BlockPos pos) {
    int range = MarkerBlockEntity.MARKER_RANGE + 1;
    int minChunkX = (pos.getX() - range) >> 4;
    int maxChunkX = (pos.getX() + range) >> 4;
    int minChunkZ = (pos.getZ() - range) >> 4;
    int maxChunkZ = (pos.getZ() + range) >> 4;
    for (int cx = minChunkX; cx <= maxChunkX; cx++) {
      for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
        LevelChunk chunk = world.getChunkSource().getChunkNow(cx, cz);
        if (chunk == null) continue;
        for (BlockEntity tile : chunk.getBlockEntities().values()) {
          if (tile instanceof IBlockEntityAreaProvider provider && !tile.isRemoved() && provider.isValidFromLocation(pos)) {
            return provider;
          }
        }
      }
    }
    return null;
  }

  public static void preDestroyBlock(Level world, BlockPos pos) {
    BlockEntity tile = world.getBlockEntity(pos);

    if (tile instanceof Container container && !world.isClientSide) {
      InvUtils.dropItems(world, container, pos.getX(), pos.getY(), pos.getZ());
      InvUtils.wipeInventory(container);
    }
  }

  public static boolean isFakePlayer(Player player) {
    return player instanceof FakePlayer;
  }

  public static int[] createSlotArray(int first, int count) {
    int[] slots = new int[count];
    for (int k = first; k < first + count; k++) {
      slots[k - first] = k;
    }
    return slots;
  }
}
