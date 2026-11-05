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

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.PipeColorLogic;
import com.peco2282.bcreborn.transport.statements.ActionPipeColor;
import com.peco2282.bcreborn.transport.statements.ActionPipeDirection;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Port of the original {@code PipeItemsDaizuli}: a colour sorter.
 * <p>
 * Like an Iron pipe it has one output side ({@link IronPipeLogic}, stored as the iron output). Items painted with the
 * pipe colour (see {@link LapisItemPipeBehaviour}) go only to the output side; every other item goes anywhere
 * except the output side. If nothing is left the item bounces back.
 * <ul>
 *   <li>Wrench on an arm / the centre: set / cycle the output side (original {@code PipeLogicIron.blockActivated})</li>
 *   <li>Sneak + wrench: next colour; a dye sets its colour</li>
 *   <li>Gate actions: pipe colour and pipe direction</li>
 * </ul>
 * Unlike the Iron pipe, redstone does not switch the output (the original never forwarded neighbour changes).
 */
public class DaizuliItemPipeBehaviour implements ItemPipeBehaviour {

  public static final DaizuliItemPipeBehaviour INSTANCE = new DaizuliItemPipeBehaviour();

  private DaizuliItemPipeBehaviour() {
  }

  /** Original {@code transport.allowBouncing = true}. */
  @Override
  public boolean allowsBouncing() {
    return true;
  }

  /** Original {@code eventHandler(PipeEventItem.FindDest)}. */
  @Override
  public void filterDestinations(PipeBlockEntity pipe, TravelingItem item, List<Direction> destinations) {
    Direction output = pipe.getIronPipeOutput();
    if (item.getColor() == PipeColorLogic.colorOf(pipe) && destinations.contains(output)) {
      destinations.clear();
      destinations.add(output);
      return;
    }
    destinations.remove(output);
  }

  /** Original {@code eventHandler(PipeEventItem.AdjustSpeed)}: {@code slowdownAmount /= 4}. */
  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    LapisItemPipeBehaviour.adjustSpeedQuarterSlowdown(item);
  }

  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
    if (player.isShiftKeyDown()) {
      if (!(player.getItemInHand(hand).getItem() instanceof IToolWrench wrench) || !wrench.canWrench(player, pos)) {
        return InteractionResult.PASS;
      }
      if (!level.isClientSide) {
        PipeColorLogic.setColor(pipe, PipeColorLogic.next(PipeColorLogic.colorOf(pipe)), player);
        wrench.wrenchUsed(player, pos);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return IronPipeLogic.onWrenchUse(pipe, level, pos, player, hand, hit);
  }

  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    DyeColor dye = PipeColorLogic.dyeColor(player.getItemInHand(hand));
    if (dye == null) {
      return InteractionResult.PASS;
    }
    if (!level.isClientSide) {
      PipeColorLogic.setColor(pipe, dye, player);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    if (!oldState.is(pipe.getBlockState().getBlock())) {
      IronPipeLogic.onPlaced(pipe, level);
    }
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    IronPipeLogic.syncAppearance(pipe, level);
  }

  /** Original {@code getActions()}: every colour, plus a direction action per connected side. */
  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (DyeColor color : DyeColor.values()) {
      actions.add(TransportStatements.ACTION_PIPE_COLOR.get(color).get());
    }
    IronPipeLogic.addDirectionActions(pipe, actions);
  }

  /** Original {@code actionsActivated()}: the first colour action and the first direction action apply. */
  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionPipeColor color) {
        if (pipe.getPipeColor() != color.color) {
          pipe.setPipeColor(color.color);
        }
        break;
      }
    }
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionPipeDirection direction) {
        IronPipeLogic.setFacing(pipe, direction.direction);
        break;
      }
    }
  }
}
