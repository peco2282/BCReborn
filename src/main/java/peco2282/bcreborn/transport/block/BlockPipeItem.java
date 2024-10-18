package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class BlockPipeItem extends BaseBlockPipe {
  public BlockPipeItem(Properties properties, @NotNull String id, PipeMaterial material) {
    this(properties, id, material, PipeType.ITEM);
  }

  private BlockPipeItem(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
    super(properties, id, material, type);
  }

  @Override
  protected PipeType getPipeType() {
    return PipeType.ITEM;
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<BlockPipeItem> codec() {
    return codecInstance(BlockPipeItem::new);
  }


  @Override
  public @NotNull String getId() {
    return getPipeMaterial().getSerializedName() + "." + getPipeType().getSerializedName() + "." + id;
  }
}
