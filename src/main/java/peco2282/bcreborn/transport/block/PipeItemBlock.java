package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.pipe.*;

public class PipeItemBlock extends BasePipeBlock {
  public PipeItemBlock(Properties properties, @NotNull String id, PipeMaterial material) {
    this(properties, id, material, PipeType.ITEM);
  }

  private PipeItemBlock(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
    super(properties, id, material, type);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return switch (getPipeMaterial()) {
      case WOOD -> new WoddenItemPipeBlockEntity(pos, state);
      case STONE -> new StoneItemPipeBlockEntity(pos, state);
      case COBBLESTONE -> new CobbleStoneItemPipeBlockEntity(pos, state);
      case IRON -> new IronItemPipeBlockEntity(pos, state);
      case GOLD -> new GoldItemPipeBlockEntity(pos, state);
      case DIAMOND -> new DiamondItemPipeBlockEntity(pos, state);
    };
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return BaseEntityBlock.createTickerHelper(p_153214_, BCTransportBlockEntities.WOODEN_ITEM_PIPE.get(), WoddenItemPipeBlockEntity::tick);
  }

  @Override
  protected @NotNull MapCodec<PipeItemBlock> codec() {
    return codecInstance(PipeItemBlock::new);
  }


  @Override
  public @NotNull String getId() {
    return getPipeMaterial().getSerializedName() + "." + getPipeType().getSerializedName() + "." + id;
  }
}
