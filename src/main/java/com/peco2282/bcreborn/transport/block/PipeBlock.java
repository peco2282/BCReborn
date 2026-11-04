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
package com.peco2282.bcreborn.transport.block;

import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.api.blocks.IColorRemovable;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableItem;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.transport.TransportBlockEntityTypes;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import com.peco2282.bcreborn.transport.menu.GateInterfaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;

public class PipeBlock extends BuildCraftBlock implements SimpleWaterloggedBlock, IColorRemovable {
  public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
  public static final BooleanProperty EAST = BlockStateProperties.EAST;
  public static final BooleanProperty WEST = BlockStateProperties.WEST;
  public static final BooleanProperty UP = BlockStateProperties.UP;
  public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  // 搬入口方向: 0-5 = Direction.ordinal(), 6 = なし
  public static final IntegerProperty EXTRACTION_SIDE = IntegerProperty.create("extraction_side", 0, 6);
  public static final int EXTRACTION_SIDE_NONE = 6;

  public static final Map<Direction, BooleanProperty> PROPERTY_MAP = new EnumMap<>(Direction.class);
  private static final VoxelShape CORE_SHAPE = box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
  private static final Map<Direction, VoxelShape> SIDE_SHAPES = new EnumMap<>(Direction.class);

  static {
    PROPERTY_MAP.put(Direction.NORTH, NORTH);
    PROPERTY_MAP.put(Direction.SOUTH, SOUTH);
    PROPERTY_MAP.put(Direction.EAST, EAST);
    PROPERTY_MAP.put(Direction.WEST, WEST);
    PROPERTY_MAP.put(Direction.UP, UP);
    PROPERTY_MAP.put(Direction.DOWN, DOWN);
  }

  static {
    SIDE_SHAPES.put(Direction.NORTH, box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 4.0D));
    SIDE_SHAPES.put(Direction.SOUTH, box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D));
    SIDE_SHAPES.put(Direction.WEST, box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D));
    SIDE_SHAPES.put(Direction.EAST, box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D));
    SIDE_SHAPES.put(Direction.DOWN, box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D));
    SIDE_SHAPES.put(Direction.UP, box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D));
  }

  private final PipeType transportType;
  private final PipeMaterial material;

  public PipeBlock(PipeType transportType, PipeMaterial material, Properties properties) {
    super(properties);
    this.transportType = transportType;
    this.material = material;
    this.registerDefaultState(this.stateDefinition.any()
      .setValue(NORTH, false)
      .setValue(SOUTH, false)
      .setValue(EAST, false)
      .setValue(WEST, false)
      .setValue(UP, false)
      .setValue(DOWN, false)
      .setValue(WATERLOGGED, false)
      .setValue(EXTRACTION_SIDE, EXTRACTION_SIDE_NONE));
  }

  @Nullable
  public static PipeBlockEntity getPipe(Level level, BlockPos pos) {
    BlockEntity tile = level.getBlockEntity(pos);
    if (tile instanceof PipeBlockEntity) {
      return (PipeBlockEntity) tile;
    }
    return null;
  }

  /**
   * The side the player aimed at: the connection arm (or pluggable) that was hit, otherwise the clicked face.
   */
  private static Direction clickedSide(BlockPos pos, BlockHitResult hit) {
    Direction arm = IronPipeLogic.resolveHitArm(pos, hit);
    return arm != null ? arm : hit.getDirection();
  }

  private static boolean hasAnyWire(PipeBlockEntity pipe) {
    for (PipeWire wire : PipeWire.VALUES) {
      if (pipe.isWired(wire)) return true;
    }
    return false;
  }

  /**
   * Original {@code BlockGenericPipe.stripEquipment()}: removes the pluggable on {@code side} if there is one,
   * otherwise one installed wire, and gives the item to the player.
   */
  private static void stripEquipment(Level level, BlockPos pos, PipeBlockEntity pipe, Player player, Direction side) {
    PipePluggable<?> pluggable = pipe.getPipePluggable(side);
    if (pluggable != null) {
      ItemStack[] drops = pluggable.getDropItems(pipe);
      pipe.setPipePluggable(side, null);
      if (drops != null) {
        for (ItemStack drop : drops) {
          if (drop != null && !drop.isEmpty()) Utils.dropTryIntoPlayerInventory(level, pos, drop, player);
        }
      }
      return;
    }
    for (PipeWire wire : PipeWire.VALUES) {
      if (pipe.removeWire(wire)) {
        Utils.dropTryIntoPlayerInventory(level, pos, wire.getStack(), player);
        return;
      }
    }
  }

  public static boolean isValid(@Nullable PipeBlockEntity pipe) {
    return pipe != null && !pipe.isRemoved();
  }

  public static boolean isConnected(BlockState state, Direction dir) {
    return switch (dir) {
      case DOWN -> state.getValue(DOWN);
      case UP -> state.getValue(UP);
      case NORTH -> state.getValue(NORTH);
      case SOUTH -> state.getValue(SOUTH);
      case WEST -> state.getValue(WEST);
      case EAST -> state.getValue(EAST);
    };
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED, EXTRACTION_SIDE);
  }

  @Override
  public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof PipeBlockEntity pipeBE) {
      ItemStack stack = player.getItemInHand(hand);

      // Original: sneak + empty hand strips the pluggable on the clicked side first, then a wire.
      if (player.isShiftKeyDown() && stack.isEmpty()) {
        Direction side = clickedSide(pos, hit);
        if (pipeBE.hasPipePluggable(side) || hasAnyWire(pipeBE)) {
          if (!level.isClientSide) {
            stripEquipment(level, pos, pipeBE, player, side);
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }

      // Original addOrStripWire(): a wire item installs its colour, or strips it while sneaking.
      PipeWire wire = PipeWire.fromStack(stack);
      if (wire != null) {
        if (!pipeBE.isWired(wire)) {
          if (!level.isClientSide) {
            pipeBE.addWire(wire);
            if (!player.getAbilities().instabuild) {
              stack.shrink(1);
            }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (player.isShiftKeyDown()) {
          if (!level.isClientSide && pipeBE.removeWire(wire)) {
            Utils.dropTryIntoPlayerInventory(level, pos, wire.getStack(), player);
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }

      if (!stack.isEmpty() && stack.getItem() instanceof IPipePluggableItem pluggableItem) {
        Direction side = hit.getDirection();
        if (!pipeBE.hasPipePluggable(side)) {
          if (!level.isClientSide) {
            PipePluggable<?> pluggable = pluggableItem.createPipePluggable(pipeBE.getPipe(), side, stack);
            pipeBE.setPipePluggable(side, pluggable);
            if (!player.getAbilities().instabuild) {
              stack.shrink(1);
            }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }

      if (!player.isShiftKeyDown() && stack.isEmpty() && pipeBE.getPipe().hasGate(hit.getDirection())) {
        if (player instanceof ServerPlayer serverPlayer) {
          Direction side = hit.getDirection();
          NetworkHooks.openScreen(serverPlayer,
            new SimpleMenuProvider((id, inventory, user) ->
              new GateInterfaceMenu(id, inventory, pipeBE, side),
              Component.translatable("item.bcreborntransport.gate")),
            buf -> { buf.writeBlockPos(pos); buf.writeEnum(side); });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
      if (pipeBE.getBehaviour() != null && !player.isShiftKeyDown()) {
        if (stack.getItem() instanceof IToolWrench) {
          return pipeBE.getBehaviour().onWrenchUse(pipeBE, level, pos, player, hand, hit);
        } else {
          return pipeBE.getBehaviour().onUse(pipeBE, level, pos, player, hand, hit);
        }
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    VoxelShape shape = CORE_SHAPE;
    for (Direction direction : Direction.values()) {
      if (state.getValue(PROPERTY_MAP.get(direction))) {
        shape = Shapes.or(shape, SIDE_SHAPES.get(direction));
      }
    }
    return shape;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
    BlockState state = this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    for (Direction direction : Direction.values()) {
      state = state.setValue(PROPERTY_MAP.get(direction), canConnectTo(context.getLevel(), context.getClickedPos(), direction));
    }
    state = state.setValue(EXTRACTION_SIDE, EXTRACTION_SIDE_NONE);
    return state;
  }

  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
    var bet = switch (transportType) {
      case ITEM -> TransportBlockEntityTypes.ITEM_PIPE.get();
      case FLUID -> TransportBlockEntityTypes.FLUID_PIPE.get();
      case ENERGY -> TransportBlockEntityTypes.ENERGY_PIPE.get();
    };

    // 木製アイテムパイプ: 隣接ブロック変化時に extractionSide を自動更新する
    level.getBlockEntity(currentPos, bet).ifPresent(be -> {
      be.getBehaviour().updateShape(be, direction, neighborState, level, neighborPos);
      // Connections may change, which changes the wire network.
      be.scheduleWireUpdate();
    });

    BlockState newState = state;
    for (Direction dir : Direction.values()) {
      newState = newState.setValue(PROPERTY_MAP.get(dir), canConnectTo(level, currentPos, dir));
    }
    return newState;
  }

  public boolean canConnectTo(LevelAccessor level, BlockPos pos, Direction direction) {
    BlockPos neighborPos = pos.relative(direction);
    BlockState neighborState = level.getBlockState(neighborPos);
    Block neighborBlock = neighborState.getBlock();
    BlockEntity thisBE = level.getBlockEntity(pos);

    if (thisBE instanceof PipeBlockEntity pipeBE) {
      if (transportType == PipeType.ENERGY && !pipeBE.canTransferEnergy(direction)) return false;
      var behaviour = pipeBE.getBehaviour();
      if (behaviour != null) {
        if (!behaviour.canConnectTo(pipeBE, direction, neighborState)) {
          return false;
        }
      }
    }

    // 他のパイプとの接続
    if (neighborBlock instanceof PipeBlock otherPipe) {
      return otherPipe.getTransportType() == this.transportType;
    }

    // Capabilityを持つブロックとの接続判定
    BlockEntity be = level.getBlockEntity(neighborPos);
    if (be != null) {
      Direction opposite = direction.getOpposite();
      if (transportType == PipeType.ITEM && be.getCapability(ForgeCapabilities.ITEM_HANDLER, opposite).isPresent()) {
        return true;
      }
      if (transportType == PipeType.FLUID && be.getCapability(ForgeCapabilities.FLUID_HANDLER, opposite).isPresent()) {
        return true;
      }
      return transportType == PipeType.ENERGY && be.getCapability(ForgeCapabilities.ENERGY, opposite).isPresent();
    }

    return false;
  }

  @Override
  public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, level, pos, oldState, isMoving);
    if (level.isClientSide) return;
    boolean woodItem = this.material == PipeMaterial.WOOD && this.transportType == PipeType.ITEM;
    boolean iron = this.material == PipeMaterial.IRON && this.transportType != PipeType.ENERGY;
    if (!woodItem && !iron) return;

    BlockEntity thisBE = level.getBlockEntity(pos);
    if (!(thisBE instanceof PipeBlockEntity pipeBE)) return;

    pipeBE.getBehaviour().onPlace(pipeBE, level, oldState, isMoving);
  }

  /**
   * Original {@code BlockGenericPipe.canProvidePower()}: gates can emit redstone.
   */
  @Override
  @SuppressWarnings("deprecation")
  public boolean isSignalSource(BlockState state) {
    return true;
  }

  /**
   * Original {@code BlockGenericPipe.isProvidingWeakPower()} -> {@code Pipe.isPoweringTo()}.
   * {@code direction} points from the receiving block to this pipe, so the output goes the opposite way.
   */
  @Override
  @SuppressWarnings("deprecation")
  public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
    return level.getBlockEntity(pos) instanceof PipeBlockEntity pipe ? pipe.getGateRedstoneOutput(direction.getOpposite()) : 0;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, level, pos, neighborBlock, fromPos, isMoving);
    if (level.isClientSide) return;
    if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipeBE && pipeBE.getBehaviour() != null) {
      pipeBE.getBehaviour().onNeighborChanged(pipeBE, level, fromPos);
    }
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof PipeBlockEntity pipeBE) {
        pipeBE.dropItems();
        // Original Pipe.computeItemDrop(): installed wires drop with the pipe.
        for (PipeWire wire : PipeWire.VALUES) {
          if (pipeBE.isWired(wire)) {
            popResource(level, pos, wire.getStack());
          }
        }
        // Neighbouring wire networks lose this pipe; let them recompute.
        for (Direction dir : Direction.values()) {
          BlockPos neighborPos = pos.relative(dir);
          if (level.isLoaded(neighborPos) && level.getBlockEntity(neighborPos) instanceof PipeBlockEntity neighbor) {
            neighbor.scheduleWireUpdate();
          }
        }
        for (PipePluggable pluggable : pipeBE.sideProperties.pluggables) {
          if (pluggable != null) {
            for (ItemStack stack : pluggable.getDropItems(null)) {
              popResource(level, pos, stack);
            }
          }
        }
      }
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, PipeBlockEntity.getBlockEntityType(transportType), (level1, pos, state1, be) -> be.tick(level1, pos, state1));
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new PipeBlockEntity(pos, state, transportType, material);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  public PipeType getTransportType() {
    return transportType;
  }

  public PipeMaterial getPipeMaterial() {
    return material;
  }

  @Override
  public boolean removeColorFromBlock(Level world, BlockPos pos, Direction side) {
    BlockEntity entity = world.getBlockEntity(pos);
    if (entity instanceof PipeBlockEntity pipe) {
      pipe.setPipeColor(null);
    }
    return false;
  }
}
