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
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Original {@code StripesHandlerPipes}: an item pipe reaching a stripes pipe extends the line by one block (the stripes
 * pipe moves forward and the new pipe takes its place); a void item pipe retracts it instead. Other pipes are placed
 * in front of the stripes pipe.
 */
public class StripesHandlerPipes implements IStripesHandler {
  public static final StripesHandlerPipes INSTANCE = new StripesHandlerPipes();

  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Nullable
  static PipeBlock pipeOf(ItemStack stack) {
    return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof PipeBlock pipe ? pipe : null;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    return pipeOf(stack) != null;
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
    PipeBlock pipe = pipeOf(stack);
    if (pipe == null || (pipe.getTransportType() == PipeType.ITEM && pipe.getPipeMaterial() == PipeMaterial.STRIPES)) {
      return false;
    }
    if (pipe.getTransportType() == PipeType.ITEM) {
      // Item pipe: move the stripes pipe at the end of the tick.
      PipeExtensionListener.INSTANCE.requestPipeExtension(stack, world, pos.relative(direction.getOpposite()), direction, activator);
    } else {
      // Other pipes: place it in front of the stripes pipe.
      BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
      ItemStack placing = stack.copy();
      ((BlockItem) stack.getItem()).place(new BlockPlaceContext(world, player, InteractionHand.MAIN_HAND, placing, hit));
      if (!placing.isEmpty()) activator.sendItem(placing, direction.getOpposite());
    }
    return true;
  }
}
