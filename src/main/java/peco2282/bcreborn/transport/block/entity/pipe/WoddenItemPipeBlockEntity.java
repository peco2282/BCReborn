package peco2282.bcreborn.transport.block.entity.pipe;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.pipe.IItemExtractor;

public class WoddenItemPipeBlockEntity extends ItemPipeBlockEntity implements IItemExtractor {
  public WoddenItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.WOODEN_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.WOOD);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull WoddenItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  @Override
  protected void update(Level level, BlockPos pos, BlockState state) {
  }

  @Override
  public IntObjectMap<ItemStack> extractItem(Direction direction) {
    BlockPos targetPos = getBlockPos().relative(direction);
    BlockEntity target = getLevel().getBlockEntity(targetPos);
    IntObjectMap<ItemStack> items = new IntObjectHashMap<>();
    if (target instanceof Container inventory) {
      for (int i = 0; i < inventory.getContainerSize(); i++) {
        ItemStack stack = inventory.getItem(i);
        if (stack.isEmpty()) continue;
        items.put(i, stack);
        getStorage().add(i, stack);
      }
    }
    return items;
  }
}
