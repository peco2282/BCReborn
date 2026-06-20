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

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StripesHandlerPipeWires implements IStripesHandler {
  @Override
  public StripesHandlerType getType() {
    return StripesHandlerType.ITEM_USE;
  }

  @Override
  public boolean shouldHandle(ItemStack stack) {
    // return stack.getItem() instanceof ItemPipeWire;
    return PipeWire.RED.isPipeWire(stack) || PipeWire.BLUE.isPipeWire(stack) ||
      PipeWire.GREEN.isPipeWire(stack) || PipeWire.YELLOW.isPipeWire(stack);
  }

  @Override
  public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
    int pipesToTry = 8;

    PipeWire wire = null;
    for (PipeWire w : PipeWire.VALUES) {
      if (w.isPipeWire(stack)) {
        wire = w;
        break;
      }
    }

    if (wire == null) return false;

    DyeColor wireColor = DyeColor.values()[wire.ordinal()]; // Red, Blue, Green, Yellow

    Position p = new Position(pos.getX(), pos.getY(), pos.getZ());
    p.orientation = direction;

    //noinspection ConstantValue,LoopStatementThatDoesntLoop
    while (pipesToTry > 0) {
      p.moveBackwards(1.0);

      PipeBlockEntity pipeBE = PipeBlock.getPipe(world, p.toBlockPos());
      if (pipeBE != null) {
        // TODO: check if wired
        // PipeBlockEntity doesn't have a simple isWired, but we can check if we want to set it.
        // Based on BuildCraft logic, it sets the wire if it's not already there.
        pipeBE.setWireSignal(wireColor, true);
        pipeBE.setChanged();
        world.sendBlockUpdated(pipeBE.getBlockPos(), pipeBE.getBlockState(), pipeBE.getBlockState(), 3);
        return true;
      } else {
        // Not a pipe, don't follow chain
        break;
      }
    }

    return false;
  }
}
