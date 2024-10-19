package peco2282.bcreborn.core.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.block.IEngine;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.api.enums.EnumEngineType;
import peco2282.bcreborn.api.enums.EnumPowerStage;
import peco2282.bcreborn.core.block.entity.BCCoreBlockEntityTypes;
import peco2282.bcreborn.core.block.entity.EngineBlockEntity;
import peco2282.bcreborn.lib.block.TileBaseNeptune;
import peco2282.bcreborn.utils.PropertyBuilder;

@SuppressWarnings("UnnecessaryBoxing")
public class BlockEngine extends TileBaseNeptune implements IEngine, RotatableFacing {
  public static final VoxelShape SHAPE_UP = Shapes.or(
      box(0, 0, 0, 16, 4, 16),
      box(4, 4, 4, 12, 16, 12)
  );
  public static final VoxelShape SHAPE_DOWN = Shapes.or(
      box(0, 12, 0, 16, 16, 16),
      box(4, 0, 4, 12, 12, 12)
  );
  public static final VoxelShape SHAPE_SOUTH = Shapes.or(
      box(0, 0, 0, 16, 16, 4),
      box(4, 4, 4, 12, 12, 16)
  );
  public static final VoxelShape SHAPE_NORTH = Shapes.or(
      box(4, 4, 0, 12, 12, 12),
      box(0, 0, 12, 16, 16, 16)
  );
  public static final VoxelShape SHAPE_EAST = Shapes.or(
      box(0, 0, 0, 4, 16, 16),
      box(4, 4, 4, 16, 12, 12)
  );
  public static final VoxelShape SHAPE_WEST = Shapes.or(
      box(0, 4, 4, 12, 12, 12),
      box(12, 0, 0, 16, 16, 16)
  );
  public static final MapCodec<BlockEngine> CODEC = RecordCodecBuilder
      .mapCodec(instance -> instance.group(
          propertiesCodec(),
          Codec.STRING.fieldOf("id").forGetter(TileBaseNeptune::getId),
          EnumEngineType.CODEC.fieldOf("engine_type").forGetter(BlockEngine::type)
      ).apply(instance, BlockEngine::new));
  private final EnumEngineType type;

  public BlockEngine(@NotNull String id, EnumEngineType type) {
    this(
        Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().lightLevel(s -> 1),
        id,
        type
    );
  }

  private BlockEngine(Properties properties, @NotNull String id, EnumEngineType type) {
    super(properties, id,
        PropertyBuilder.builder()
            .add(BCProperties.ACTIVE, Boolean.valueOf(false))
            .add(BCProperties.ENERGY_STAGE, EnumPowerStage.BLACK)
            .add(BCProperties.BLOCK_FACING, Direction.EAST)
            .add(BCProperties.ENGINE_TYPE, type)
            .add(BCProperties.ENGINE_MODEL, Integer.valueOf(1)));
    this.type = type;
  }

  public EnumEngineType type() {
    return type;
  }

  @Override
  protected VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
    Direction direction = p_60555_.getValue(BCProperties.BLOCK_FACING);
    return switch (direction) {
      case DOWN -> SHAPE_DOWN;
      case UP -> SHAPE_UP;
      case NORTH -> SHAPE_NORTH;
      case SOUTH -> SHAPE_SOUTH;
      case WEST -> SHAPE_WEST;
      case EAST -> SHAPE_EAST;
    };
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BCProperties.ACTIVE, BCProperties.ENERGY_STAGE, BCProperties.BLOCK_FACING, BCProperties.ENGINE_TYPE, BCProperties.ENGINE_MODEL);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new EngineBlockEntity(pos, state);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, BlockHitResult p_60508_) {
    if (p_60504_.isClientSide()) {
      return super.useWithoutItem(p_60503_, p_60504_, p_60505_, p_60506_, p_60508_);
    } else {
      if (type == EnumEngineType.IRON || type == EnumEngineType.STONE) {
        p_60506_.openMenu(new EngineBlockEntity.Provider(type));
        return InteractionResult.CONSUME;
      } else return super.useWithoutItem(p_60503_, p_60504_, p_60505_, p_60506_, p_60508_);
    }
  }

  @Override
  protected void neighborChanged(BlockState p_60509_, Level p_60510_, BlockPos p_60511_, Block p_60512_, BlockPos p_60513_, boolean p_60514_) {
    super.neighborChanged(p_60509_, p_60510_, p_60511_, p_60512_, p_60513_, p_60514_);
    if (p_60510_.isClientSide()) return;
    boolean signal = p_60510_.hasNeighborSignal(p_60511_);
    BlockState state = p_60509_;
    if (signal) {
      if (!state.getValue(BCProperties.ACTIVE)) {
        state = state.setValue(BCProperties.ACTIVE, Boolean.valueOf(true));
        p_60510_.setBlockAndUpdate(p_60511_, state);
      }
    } else {
      if (state.getValue(BCProperties.ACTIVE)) {
        state = state.setValue(BCProperties.ACTIVE, Boolean.valueOf(false)).setValue(BCProperties.ENGINE_MODEL, 1);
        p_60510_.setBlockAndUpdate(p_60511_, state);
      }
    }

//    p_60510_.setBlock(p_60511_, p_60509_, 2);
  }

  @Override
  public @NotNull BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
    return super.getStateForPlacement(p_49820_).setValue(BCProperties.ACTIVE, Boolean.valueOf(false)).setValue(BCProperties.ENGINE_MODEL, Integer.valueOf(1));
  }

  @Override
  protected @NotNull MapCodec<BlockEngine> codec() {
    return CODEC;
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return BaseEntityBlock.createTickerHelper(p_153214_, BCCoreBlockEntityTypes.ENGINE.get(), EngineBlockEntity::tick);
  }

  @Override
  public boolean isActive(Level level, BlockPos pos, BlockState state) {
    return state.getValue(BCProperties.ACTIVE) && state.getValue(BCProperties.ENERGY_STAGE).isRunning();
  }

  @Override
  public long perTick(Level level, BlockState state) {
    int power = state.getValue(BCProperties.ENERGY_STAGE).power();
    return (long) type.output * power;
  }

  @Override
  public boolean canGenerate() {
    return true;
  }
}
