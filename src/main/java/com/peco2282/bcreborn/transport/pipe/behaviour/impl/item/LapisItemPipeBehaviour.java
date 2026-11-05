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
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.PipeColorLogic;
import com.peco2282.bcreborn.transport.statements.ActionPipeColor;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Port of the original {@code PipeItemsLapis}: paints every item that reaches the pipe centre with the pipe colour.
 * <ul>
 *   <li>Wrench: next colour, sneak + wrench: previous colour; a dye sets its colour</li>
 *   <li>Gate "pipe colour" actions recolour the pipe</li>
 *   <li>Items are slowed down four times less than in a normal pipe</li>
 * </ul>
 * The paint lives on the travelling item ({@link TravelingItem#getColor()}), not on the stack, and is read by the
 * Daizuli pipe. The Lapis pipe itself does not route by colour.
 */
public class LapisItemPipeBehaviour implements ItemPipeBehaviour {

  public static final LapisItemPipeBehaviour INSTANCE = new LapisItemPipeBehaviour();

  private LapisItemPipeBehaviour() {
  }

  /** Original {@code eventHandler(PipeEventItem.ReachedCenter)}. */
  @Override
  public void onReachedCenter(PipeBlockEntity pipe, TravelingItem item) {
    item.setColor(PipeColorLogic.colorOf(pipe));
  }

  /** Original {@code eventHandler(PipeEventItem.AdjustSpeed)}: {@code slowdownAmount /= 4}. */
  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    adjustSpeedQuarterSlowdown(item);
  }

  static void adjustSpeedQuarterSlowdown(TravelingItem item) {
    float speed = item.getSpeed();
    float slowdown = (speed - 0.01f) * 0.01f;
    item.setSpeed(Math.max(0.01f, speed - slowdown / 4f));
  }

  /** Original {@code PipeItemsLapis.getActions()}: one "pipe colour" action per dye colour. */
  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (DyeColor color : DyeColor.values()) {
      actions.add(TransportStatements.ACTION_PIPE_COLOR.get(color).get());
    }
  }

  /** Original {@code PipeItemsLapis.actionsActivated()}: the first active colour action recolours the pipe. */
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
  }

  /** Original {@code blockActivated()}: wrench cycles the colour (sneak: backwards). */
  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
    if (!(player.getItemInHand(hand).getItem() instanceof IToolWrench wrench) || !wrench.canWrench(player, pos)) {
      return InteractionResult.PASS;
    }
    if (!level.isClientSide) {
      DyeColor current = PipeColorLogic.colorOf(pipe);
      PipeColorLogic.setColor(pipe, player.isShiftKeyDown() ? PipeColorLogic.previous(current) : PipeColorLogic.next(current), player);
      wrench.wrenchUsed(player, pos);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** Original {@code blockActivated()}: a dye sets the pipe colour. */
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
}
