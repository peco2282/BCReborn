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

  public static void dropAll(Level world, BlockPos pos, NonNullList<ItemStack> toDrop) {
    for (ItemStack stack : toDrop) {
      if (stack == null) {
        throw new NullPointerException("Null stack!");
      }
      drop(world, pos, stack);
    }
  }

  public static void drop(Level world, BlockPos pos, @Nonnull ItemStack stack) {
    Block.dropResources(world.getBlockState(pos), world, pos);// .spawnAsEntity(world, pos, stack);
  }

  public static void drop(Level world, Vector3d vec, @Nonnull ItemStack stack) {
    drop(world, vec.x, vec.y, vec.z, stack);
  }

  public static void drop(Level world, double x, double y, double z, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    }
    ItemEntity entity = new ItemEntity(world, x, y, z, stack);
    world.addFreshEntity(entity);
  }

  public static boolean sameItemCheck(Item target, ItemStack... stacks) {
    if (target == null) return false;
    for (ItemStack stack: stacks) {
      if (stack == null) return false;
      if (!target.equals(stack.getItem())) return false;
    }
    return true;
  }
}
