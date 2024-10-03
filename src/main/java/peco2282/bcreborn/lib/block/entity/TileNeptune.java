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

public abstract class TileNeptune extends BlockEntity implements BCBlockEntity, ICapabilityProvider {
  public TileNeptune(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
  }
  public void onExplode(Explosion explosion) {
  }

  public void onRemove() {
    NonNullList<ItemStack> toDrop = NonNullList.create();
    addDrops(toDrop, 0);
    InventoryUtil.dropAll(level, worldPosition, toDrop);
  }
  public void addDrops(NonNullList<ItemStack> toDrop, int fortune) {
//    itemManager.addDrops(toDrop);
//    tankManager.addDrops(toDrop);
  }

}
