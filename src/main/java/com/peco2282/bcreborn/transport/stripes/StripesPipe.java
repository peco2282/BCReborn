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
package com.peco2282.bcreborn.transport.stripes;

import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.IFluidBlock;
import org.jetbrains.annotations.Nullable;

/**
 * What the original {@code PipeItemsStripes} does: use the items that reach its open end through the stripes
 * handlers ({@code eventHandler(DropItem)}), and break the block in front with engine power ({@code updateEntity()}).
 */
public final class StripesPipe {
  /** Original: {@code RFBattery(320 * 50, 640, 0)}; a break costs 10 RF. */
  public static final int BATTERY = 320 * 50;
  public static final int MAX_RECEIVE = 640;
  public static final int BREAK_ENERGY = 10;

  private StripesPipe() {
  }

  /** Original {@code IStripesActivator} of the pipe: send an item back into it, or drop it in front. */
  public static IStripesActivator activator(PipeBlockEntity pipe) {
    return new IStripesActivator() {
      @Override
      public void sendItem(ItemStack stack, Direction direction) {
        if (stack.isEmpty()) return;
        // The item travels towards {@code direction}, so it enters from the opposite side.
        pipe.injectItem(stack.copy(), direction.getOpposite());
      }

      @Override
      public void dropItem(ItemStack stack, Direction direction) {
        if (stack.isEmpty()) return;
        BlockPos at = pipe.getBlockPos().relative(direction);
        Block.popResource(pipe.getLevel(), at, stack.copy());
      }
    };
  }

  /** Original {@code Pipe.getOpenOrientation()}: the side opposite the only connection, or null. */
  @Nullable
  public static Direction openOrientation(PipeBlockEntity pipe) {
    Direction connected = null;
    for (Direction dir : Direction.values()) {
      if (PipeBlock.isConnected(pipe.getBlockState(), dir)) {
        if (connected != null) return null;
        connected = dir;
      }
    }
    return connected == null ? null : connected.getOpposite();
  }

  /** The direction the pipe works in: a gate's "pipe direction" action, otherwise {@code fallback}. */
  @Nullable
  public static Direction actionDirection(PipeBlockEntity pipe, @Nullable Direction fallback) {
    Direction action = pipe.getStripesActionDirection();
    return action != null ? action : fallback;
  }

  /** The BuildCraft fake player at {@code target}, looking along {@code direction} (original pitch / yaw table). */
  public static Player player(ServerLevel level, BlockPos target, Direction direction) {
    Player player = BCFakePlayer.getBuildCraftPlayer(level, target).get();
    player.setPos(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
    float pitch = switch (direction) {
      case DOWN -> 90;
      case UP -> 270;
      default -> 0;
    };
    float yaw = switch (direction) {
      case NORTH -> 180;
      case WEST -> 90;
      case EAST -> 270;
      default -> 0;
    };
    player.setXRot(pitch);
    player.setYRot(yaw);
    player.setYHeadRot(yaw);
    return player;
  }

  /**
   * Original {@code eventHandler(DropItem)}: the first item-use handler that takes the stack uses it on the block in
   * front of the pipe.
   *
   * @return true if a handler used the stack (it is no longer dropped)
   */
  public static boolean useItem(PipeBlockEntity pipe, ItemStack stack, Direction direction) {
    if (!(pipe.getLevel() instanceof ServerLevel level) || stack.isEmpty()) return false;
    BlockPos target = pipe.getBlockPos().relative(direction);
    Player player = player(level, target, direction);
    IStripesActivator activator = activator(pipe);
    for (IStripesHandler handler : handlers()) {
      if (handler.getType() == IStripesHandler.StripesHandlerType.ITEM_USE && handler.shouldHandle(stack)
        && handler.handle(level, target, direction, stack, player, activator)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Original {@code updateEntity()}: with 10 RF, break the block in front (not bedrock-like blocks or fluids). A
   * block-break handler may take over (the minecart handler works even on air); otherwise the drops go back into the
   * pipe.
   */
  public static void breakBlock(PipeBlockEntity pipe) {
    if (!(pipe.getLevel() instanceof ServerLevel level) || pipe.getExtractionEnergy() < BREAK_ENERGY) return;
    Direction direction = actionDirection(pipe, openOrientation(pipe));
    if (direction == null) return;
    BlockPos target = pipe.getBlockPos().relative(direction);
    if (!level.isLoaded(target)) return;
    BlockState state = level.getBlockState(target);
    if (state.getDestroySpeed(level, target) < 0) return;
    if (state.getBlock() instanceof LiquidBlock || state.getBlock() instanceof IFluidBlock) return;
    ItemStack stack = new ItemStack(state.getBlock());
    Player player = player(level, target, direction);
    pipe.consumeExtractionEnergy(BREAK_ENERGY);

    IStripesActivator activator = activator(pipe);
    for (IStripesHandler handler : handlers()) {
      if (handler.getType() == IStripesHandler.StripesHandlerType.BLOCK_BREAK && handler.shouldHandle(stack)
        && handler.handle(level, target, direction, stack, player, activator)) {
        return;
      }
    }
    if (state.isAir()) return;
    List<ItemStack> drops = Block.getDrops(state, level, target, level.getBlockEntity(target), player, ItemStack.EMPTY);
    level.removeBlock(target, false);
    for (ItemStack drop : drops) {
      activator.sendItem(drop, direction.getOpposite());
    }
  }

  private static List<IStripesHandler> handlers() {
    return PipeManager.getStripesHandlers();
  }
}
