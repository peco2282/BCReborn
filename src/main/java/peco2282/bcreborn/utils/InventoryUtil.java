/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

public class InventoryUtil {

  /**
   * Drops all the items in the provided list at the specified position within the given world.
   *
   * @param world the world in which the items are to be dropped
   * @param pos the block position where the items should be dropped
   * @param toDrop the list of item stacks to be dropped
   * @throws NullPointerException if any item stack in the list is null
   */
  public static void dropAll(Level world, BlockPos pos, NonNullList<ItemStack> toDrop) {
    for (ItemStack stack : toDrop) {
      if (stack == null) {
        throw new NullPointerException("Null stack!");
      }
      drop(world, pos, stack);
    }
  }

  /**
   * Drops a single item stack at the block position, handling potential block-specific resources.
   *
   * @param world the world in which the item is to be dropped
   * @param pos the block position to drop the item
   * @param stack the item stack to be dropped
   */
  public static void drop(Level world, BlockPos pos, @Nonnull ItemStack stack) {
    Block.dropResources(world.getBlockState(pos), world, pos); // .spawnAsEntity(world, pos, stack);
  }

  /**
   * Drops a single item stack at a specific 3D vector position.
   *
   * @param world the world in which the item is to be dropped
   * @param vec the 3D vector position where the item should be dropped
   * @param stack the item stack to be dropped
   */
  public static void drop(Level world, Vector3d vec, @Nonnull ItemStack stack) {
    drop(world, vec.x, vec.y, vec.z, stack);
  }

  /**
   * Drops a single item stack at the specified coordinates.
   *
   * @param world the world in which the item is to be dropped
   * @param x the x-coordinate of the drop position
   * @param y the y-coordinate of the drop position
   * @param z the z-coordinate of the drop position
   * @param stack the item stack to be dropped
   */
  public static void drop(Level world, double x, double y, double z, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }
    ItemEntity entity = new ItemEntity(world, x, y, z, stack);
    world.addFreshEntity(entity);
  }

  /**
   * Checks if all the provided item stacks contain the same item as the target.
   *
   * @param target the item to compare against
   * @param stacks the item stacks to be checked
   * @return true if all stacks contain the target item, false otherwise
   */
  public static boolean sameItemCheck(Item target, ItemStack... stacks) {
    if (target == null) return false;
    for (ItemStack stack : stacks) {
      if (stack == null) return false;
      if (!target.equals(stack.getItem())) return false;
    }
    return true;
  }
}
