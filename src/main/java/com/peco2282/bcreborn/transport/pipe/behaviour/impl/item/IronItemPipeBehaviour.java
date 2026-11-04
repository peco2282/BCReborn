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

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import java.util.Collection;
import java.util.List;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Port of the original {@code PipeItemsIron}.
 * <p>
 * The original has no GUI; the output direction is switched by wrench and redstone (and, later, a Gate Action).
 * Connections behave like a normal pipe; only output is restricted to a single side via {@code outputOpen()}.
 */
public class IronItemPipeBehaviour implements ItemPipeBehaviour {

  public static final IronItemPipeBehaviour INSTANCE = new IronItemPipeBehaviour();

  private IronItemPipeBehaviour() {
  }

  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
    return IronPipeLogic.onWrenchUse(pipe, level, pos, player, hand, hit);
  }

  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    if (!oldState.is(pipe.getBlockState().getBlock())) {
      IronPipeLogic.onPlaced(pipe, level);
    }
  }

  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    IronPipeLogic.addDirectionActions(pipe, actions);
  }

  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    IronPipeLogic.onActionsActivated(pipe, actions);
  }

  @Override
  public void onNeighborChanged(PipeBlockEntity pipe, Level level, BlockPos fromPos) {
    IronPipeLogic.switchOnRedstone(pipe, level);
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    IronPipeLogic.ensureInitialized(pipe, level);
    IronPipeLogic.syncAppearance(pipe, level);
  }

  /**
   * Original {@code outputOpen(to)}: items only leave through the output side.
   * If the item entered from the output side, or the output side is not connected, there is no destination
   * and the item bounces back the way it came (allowBouncing = true).
   */
  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    Direction output = pipe.getIronPipeOutput();
    if (item.getEntryDirection() != output && PipeBlock.isConnected(pipe.getBlockState(), output)) {
      return output;
    }
    return item.getEntryDirection();
  }
}
