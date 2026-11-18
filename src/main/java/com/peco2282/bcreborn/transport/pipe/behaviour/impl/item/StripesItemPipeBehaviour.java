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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.statements.ActionPipeDirection;
import com.peco2282.bcreborn.transport.stripes.StripesPipe;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stripes pipe (original {@code PipeItemsStripes}): an item that would drop out of its open end is used by the stripes
 * handlers instead (placing blocks, planting, shearing, extending the pipe line, ...), and with engine power it breaks
 * the block in front and sends the drops back. A gate's "pipe direction" action picks the working side.
 */
public class StripesItemPipeBehaviour implements ItemPipeBehaviour {
  public static final StripesItemPipeBehaviour INSTANCE = new StripesItemPipeBehaviour();

  private StripesItemPipeBehaviour() {
  }

  /** Original {@code canPipeConnect()}: never to another stripes pipe (nor, as before, an obsidian pipe). */
  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock other) {
      PipeMaterial material = other.getPipeMaterial();
      return material != PipeMaterial.OBSIDIAN && material != PipeMaterial.STRIPES;
    }
    return true;
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    if (!level.isClientSide) StripesPipe.breakBlock(pipe);
  }

  /** Original {@code eventHandler(PipeEventItem.DropItem)}. */
  @Override
  public boolean onDropItem(PipeBlockEntity pipe, TravelingItem item, Direction direction) {
    Direction side = StripesPipe.actionDirection(pipe, direction);
    return side != null && StripesPipe.useItem(pipe, item.getStack(), side);
  }

  /** Original {@code getActions()}: a "pipe direction" action for every side that is not connected. */
  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (Direction dir : Direction.values()) {
      if (!PipeBlock.isConnected(pipe.getBlockState(), dir)) {
        actions.add(TransportStatements.ACTION_PIPE_DIRECTION.get(dir).get());
      }
    }
  }

  /** Original {@code actionsActivated()}: the first active "pipe direction" action, or none. */
  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    Direction direction = null;
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionPipeDirection action) {
        direction = action.direction;
        break;
      }
    }
    pipe.setStripesActionDirection(direction);
  }
}
