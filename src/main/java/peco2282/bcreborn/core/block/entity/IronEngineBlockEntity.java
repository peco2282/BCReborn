package peco2282.bcreborn.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class IronEngineBlockEntity extends EngineBlockEntity {
  public IronEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCCoreBlockEntityTypes.IRON_ENGINE.get(), p_155229_, p_155230_);
  }

  @Override
  public ItemStack removeItem(int p_333934_, int p_332088_) {
    return super.removeItem(p_333934_, p_332088_);
  }
}
