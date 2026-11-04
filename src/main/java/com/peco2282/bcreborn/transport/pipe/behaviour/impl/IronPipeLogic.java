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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl;

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.statements.ActionPipeDirection;
import java.util.Collection;
import java.util.List;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code buildcraft.transport.pipes.PipeLogicIron}.
 * Output-direction switching logic shared by the Iron Item and Fluid Pipes.
 * <ul>
 *   <li>Wrench right-click on a connection arm: {@link #setFacing} to that side</li>
 *   <li>Wrench right-click on the pipe centre: {@link #switchPosition} to the next valid side</li>
 *   <li>Redstone input changes: {@link #switchPosition}</li>
 *   <li>On placement: start from UP and {@link #switchPosition}</li>
 * </ul>
 * The output direction lives in the block entity ({@link PipeBlockEntity#getIronPipeOutput()}). It is only mirrored
 * into {@link PipeBlock#EXTRACTION_SIDE} as appearance, so the output arm can be rendered differently
 * (see {@link #syncAppearance}).
 */
public final class IronPipeLogic {
  /** Half extent of CORE_SHAPE (4..12 / 16). Hits outside it are treated as a connection arm. */
  private static final double CORE_HALF_EXTENT = 4.0D / 16.0D;
  private static final double EPSILON = 1.0E-4D;

  private IronPipeLogic() {
  }

  /**
   * Original {@code PipeLogicIron.blockActivated(player, side)}.
   */
  public static InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
    if (!(player.getItemInHand(hand).getItem() instanceof IToolWrench wrench) || !wrench.canWrench(player, pos)) {
      return InteractionResult.PASS;
    }
    if (!level.isClientSide) {
      Direction before = pipe.getIronPipeOutput();
      Direction arm = resolveHitArm(pos, hit);
      if (arm == null) {
        switchPosition(pipe);
      } else {
        setFacing(pipe, arm);
      }
      Direction after = pipe.getIronPipeOutput();
      if (after != before) {
        player.displayClientMessage(Component.translatable("message.bcreborntransport.iron_pipe.output",
          Component.literal(after.getName())), true);
      }
      wrench.wrenchUsed(player, pos);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /**
   * Original {@code PipeLogicIron.onBlockPlaced()} + {@code initialize()}.
   */
  public static void onPlaced(PipeBlockEntity pipe, Level level) {
    pipe.setIronPipeOutput(Direction.UP);
    switchPosition(pipe);
    pipe.setIronLastRedstonePower(level.hasNeighborSignal(pipe.getBlockPos()));
  }

  /**
   * Original {@code initialize()}: records the current redstone state on the first tick after loading.
   */
  public static void ensureInitialized(PipeBlockEntity pipe, Level level) {
    if (pipe.getIronLastRedstonePower() == null) {
      pipe.setIronLastRedstonePower(level.hasNeighborSignal(pipe.getBlockPos()));
    }
  }

  /**
   * Mirrors the output side into {@link PipeBlock#EXTRACTION_SIDE} so the model shows the output arm with the
   * standard texture and every other arm as solid (original {@code PipeItemsIron.getIconIndex()}).
   * Runs from the server tick rather than from {@code onPlace}, so it never re-enters {@code setBlock}.
   */
  public static void syncAppearance(PipeBlockEntity pipe, Level level) {
    BlockState state = pipe.getBlockState();
    if (!state.hasProperty(PipeBlock.EXTRACTION_SIDE)) return;
    int wanted = pipe.getIronPipeOutput().get3DDataValue();
    if (state.getValue(PipeBlock.EXTRACTION_SIDE) != wanted) {
      level.setBlock(pipe.getBlockPos(), state.setValue(PipeBlock.EXTRACTION_SIDE, wanted), Block.UPDATE_CLIENTS);
    }
  }

  /**
   * Original {@code PipeLogicIron.switchOnRedstone()}.
   */
  public static void switchOnRedstone(PipeBlockEntity pipe, Level level) {
    boolean currentPower = level.hasNeighborSignal(pipe.getBlockPos());
    Boolean lastPower = pipe.getIronLastRedstonePower();
    if (lastPower == null) {
      pipe.setIronLastRedstonePower(currentPower);
      return;
    }
    if (currentPower != lastPower) {
      switchPosition(pipe);
      pipe.setIronLastRedstonePower(currentPower);
    }
  }

  /**
   * Original {@code PipeItemsIron.getActions()}: one "pipe direction" action per connected side.
   */
  public static void addDirectionActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (Direction dir : Direction.values()) {
      if (PipeBlock.isConnected(pipe.getBlockState(), dir)) {
        actions.add(TransportStatements.ACTION_PIPE_DIRECTION.get(dir).get());
      }
    }
  }

  /**
   * Original {@code PipeItemsIron.actionsActivated()}: the first active "pipe direction" action sets the output.
   */
  public static void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionPipeDirection direction) {
        setFacing(pipe, direction.direction);
        break;
      }
    }
  }

  /**
   * Original {@code PipeLogicIron.switchPosition()}. Searches for a valid side, starting after the current one.
   */
  public static void switchPosition(PipeBlockEntity pipe) {
    int current = pipe.getIronPipeOutput().get3DDataValue();
    for (int i = current + 1; i <= current + 6; ++i) {
      if (setFacing(pipe, Direction.from3DDataValue(i % 6))) {
        return;
      }
    }
  }

  /**
   * Original {@code PipeLogicIron.setFacing()}. Sets the side and returns true if it differs from the current one and is valid.
   */
  public static boolean setFacing(PipeBlockEntity pipe, Direction facing) {
    if (facing != pipe.getIronPipeOutput() && isValidFacing(pipe, facing)) {
      pipe.setIronPipeOutput(facing);
      return true;
    }
    return false;
  }

  /**
   * Original {@code PipeLogicIron.isValidFacing()} + {@code isValidConnectingTile()}.
   * The side must be connected, and the neighbour must be a non-Wood pipe of the same type or a block with the matching capability.
   */
  public static boolean isValidFacing(PipeBlockEntity pipe, Direction side) {
    if (!PipeBlock.isConnected(pipe.getBlockState(), side)) {
      return false;
    }
    Level level = pipe.getLevel();
    if (level == null) return true;
    BlockPos neighborPos = pipe.getBlockPos().relative(side);
    if (!level.isLoaded(neighborPos)) return true;

    BlockEntity tile = level.getBlockEntity(neighborPos);
    if (tile == null) {
      return true;
    }
    PipeType type = pipe.getPipeType();
    if (tile instanceof PipeBlockEntity other) {
      return other.getPipeType() == type && other.getPipeMaterial() != PipeMaterial.WOOD;
    }
    Direction opposite = side.getOpposite();
    return switch (type) {
      case ITEM -> tile.getCapability(ForgeCapabilities.ITEM_HANDLER, opposite).isPresent();
      case FLUID -> tile.getCapability(ForgeCapabilities.FLUID_HANDLER, opposite).isPresent();
      case ENERGY -> tile.getCapability(ForgeCapabilities.ENERGY, opposite).isPresent();
    };
  }

  /**
   * Reproduces the original {@code side} argument (the connection arm hit by the ray trace, or UNKNOWN for the centre).
   *
   * @return the direction of the hit arm, or null if the pipe centre was hit
   */
  @Nullable
  public static Direction resolveHitArm(BlockPos pos, BlockHitResult hit) {
    Vec3 local = hit.getLocation().subtract(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    double ax = Math.abs(local.x);
    double ay = Math.abs(local.y);
    double az = Math.abs(local.z);
    double max = Math.max(ax, Math.max(ay, az));
    if (max <= CORE_HALF_EXTENT + EPSILON) {
      return null;
    }
    if (max == ax) return local.x > 0 ? Direction.EAST : Direction.WEST;
    if (max == ay) return local.y > 0 ? Direction.UP : Direction.DOWN;
    return local.z > 0 ? Direction.SOUTH : Direction.NORTH;
  }
}
