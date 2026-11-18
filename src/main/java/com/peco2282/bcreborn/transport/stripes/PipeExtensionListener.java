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
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Original {@code PipeExtensionListener}: at the end of the level tick, moves a stripes pipe one block forward and puts
 * the delivered item pipe in its old place, or (for a void item pipe) pulls it one block back and returns the pipe it
 * replaced. Whatever is left goes back down the line.
 */
public class PipeExtensionListener {
  public static final PipeExtensionListener INSTANCE = new PipeExtensionListener();

  private final Map<Level, Set<Request>> requests = new HashMap<>();

  private record Request(ItemStack stack, BlockPos pos, Direction direction, IStripesActivator activator) {
    @Override
    public boolean equals(Object other) {
      return other instanceof Request r && pos.equals(r.pos) && direction == r.direction;
    }

    @Override
    public int hashCode() {
      return Objects.hash(pos, direction);
    }
  }

  /**
   * @param pos       the stripes pipe
   * @param direction the way it extends
   */
  public void requestPipeExtension(ItemStack stack, Level world, BlockPos pos, Direction direction, IStripesActivator activator) {
    if (world.isClientSide) return;
    requests.computeIfAbsent(world, k -> new LinkedHashSet<>()).add(new Request(stack.copy(), pos, direction, activator));
  }

  @SubscribeEvent
  public void tick(TickEvent.LevelTickEvent event) {
    if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
    Set<Request> pending = requests.remove(level);
    if (pending == null) return;
    for (Request request : pending) {
      process(level, request);
    }
  }

  private static boolean isRetraction(ItemStack stack) {
    PipeBlock pipe = StripesHandlerPipes.pipeOf(stack);
    return pipe != null && pipe.getTransportType() == PipeType.ITEM && pipe.getPipeMaterial() == PipeMaterial.VOID;
  }

  private static void process(ServerLevel level, Request request) {
    BlockPos pos = request.pos();
    Direction direction = request.direction();
    ItemStack stack = request.stack();
    if (!(level.getBlockEntity(pos) instanceof PipeBlockEntity stripes)) {
      request.activator().dropItem(stack, direction);
      return;
    }
    boolean retract = isRetraction(stack);
    BlockPos target;
    if (retract) {
      // Original: two pipes must lie behind the stripes pipe; it moves onto the first one.
      BlockPos behind = pos.relative(direction.getOpposite());
      if (!(level.getBlockState(behind).getBlock() instanceof PipeBlock)
        || !(level.getBlockState(behind.relative(direction.getOpposite())).getBlock() instanceof PipeBlock)) {
        request.activator().sendItem(stack, direction.getOpposite());
        return;
      }
      target = behind;
    } else {
      target = pos.relative(direction);
      if (!level.isEmptyBlock(target)) {
        request.activator().sendItem(stack, direction.getOpposite());
        return;
      }
    }

    // Step 1: keep the stripes pipe (with the items inside it) and take it out.
    CompoundTag saved = stripes.saveWithoutMetadata();
    Block stripesBlock = stripes.getBlockState().getBlock();
    stripes.setMoving(true);
    level.removeBlock(pos, false);

    // Step 2: take out the pipe behind, or put the new pipe where the stripes pipe was.
    List<ItemStack> removed = new ArrayList<>();
    if (retract) {
      BlockState behind = level.getBlockState(target);
      BlockEntity behindEntity = level.getBlockEntity(target);
      removed.addAll(Block.getDrops(behind, level, target, behindEntity));
      level.removeBlock(target, false);
    } else {
      PipeBlock pipe = Objects.requireNonNull(StripesHandlerPipes.pipeOf(stack));
      level.setBlock(pos, Block.updateFromNeighbourShapes(pipe.defaultBlockState(), level, pos), Block.UPDATE_ALL);
      stack.shrink(1);
    }

    // Step 3: put the stripes pipe back at its new place.
    level.setBlock(target, Block.updateFromNeighbourShapes(stripesBlock.defaultBlockState(), level, target), Block.UPDATE_ALL);
    if (!(level.getBlockEntity(target) instanceof PipeBlockEntity moved)) return;
    moved.load(saved);
    moved.setChanged();
    level.setBlock(target, Block.updateFromNeighbourShapes(level.getBlockState(target), level, target), Block.UPDATE_ALL);

    // Step 4: send back what is left.
    IStripesActivator activator = StripesPipe.activator(moved);
    if (!stack.isEmpty()) activator.sendItem(stack, direction.getOpposite());
    for (ItemStack drop : removed) {
      activator.sendItem(drop, direction.getOpposite());
    }
  }
}
