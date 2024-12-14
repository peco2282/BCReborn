package peco2282.bcreborn.lib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.utils.InventoryUtil;


/**
 * Abstract representation of a custom BlockEntity for Neptune blocks.
 * This class integrates functionality for handling explosions, drops, and custom inventory capabilities.
 *
 * @author peco2282
 */
public abstract class NeptuneBlockEntity extends BlockEntity implements BCBlockEntity, ICapabilityProvider {
  /**
   * Constructs a new NeptuneBlockEntity.
   *
   * @param p_155228_ The type of block entity.
   * @param p_155229_ The position of the block in the world.
   * @param p_155230_ The state of the block at the given position.
   */
  public NeptuneBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
  }
  /**
   * Handles the behavior of the block entity when it is exposed to an explosion.
   *
   * @param explosion The explosion instance affecting this block entity.
   */
  public void onExplode(Explosion explosion) {
  }

  /**
   * Handles the removal of the block entity, ensuring that all inventory contents are dropped in the world.
   */
  public void onRemove() {
    NonNullList<ItemStack> toDrop = NonNullList.create();
    addDrops(toDrop, 0);
    InventoryUtil.dropAll(level, worldPosition, toDrop);
  }
  /**
   * Adds the items to be dropped by this block entity into a provided list.
   *
   * @param toDrop  The list where items to be dropped will be added.
   * @param fortune The fortune level applied to determine drops.
   */
  public void addDrops(NonNullList<ItemStack> toDrop, int fortune) {
//    itemManager.addDrops(toDrop);
//    tankManager.addDrops(toDrop);
  }

}
