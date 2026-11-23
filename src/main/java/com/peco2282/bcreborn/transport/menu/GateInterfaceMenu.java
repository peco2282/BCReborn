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
package com.peco2282.bcreborn.transport.menu;

import java.util.Collections;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerData;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import com.peco2282.bcreborn.api.statements.StatementManager;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.transport.TransportMenuTypes;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.core.statements.DefaultTriggerProvider;
import com.peco2282.bcreborn.transport.gates.Gate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.Slot;

/**
 * Port of the original {@code ContainerGateInterface}.
 * <p>
 * The trigger and action candidates are worked out on the server when the menu opens (original {@code init()}:
 * every valid statement the gate's material has enough parameter slots for, sorted by id) and sent to the client
 * with the open-screen data ({@link #writeCandidates}). A click on a slot is sent as a vanilla menu button
 * ({@link #buttonId}); the server keeps the same lists, so the button id is all it needs. The gate itself reaches
 * the client with the pipe's block update.
 */
public class GateInterfaceMenu extends BuildCraftMenu<GateInterfaceMenu> {
  /** What a button click is about. */
  public static final int KIND_TRIGGER = 0;
  public static final int KIND_ACTION = 1;
  public static final int KIND_TRIGGER_PARAMETER = 2;
  public static final int KIND_ACTION_PARAMETER = 3;
  /** For a statement slot: original left click (next), right click (previous), shift click (clear). */
  public static final int CLICK_NEXT = 0;
  public static final int CLICK_PREVIOUS = 1;
  public static final int CLICK_CLEAR = 2;
  private static final int DEFAULT_HEIGHT = 166;

  private final PipeBlockEntity pipe;
  private final Direction side;
  private final List<IStatement> potentialTriggers;
  private final List<IStatement> potentialActions;
  /** Original actionsState, two bits per row, split over two shorts. */
  private final ContainerData actionStates;

  /** Client constructor: the candidates come with the open-screen data. */
  public GateInterfaceMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
    this(containerId, inventory, getBlockEntity(inventory, buf), buf.readEnum(Direction.class), readStatements(buf), readStatements(buf),
      new SimpleContainerData(2));
  }

  /** Server constructor. */
  public GateInterfaceMenu(int containerId, Inventory inventory, PipeBlockEntity pipe, Direction side) {
    this(containerId, inventory, pipe, side, candidates(gateOf(pipe, side), true), candidates(gateOf(pipe, side), false), null);
  }

  private GateInterfaceMenu(int containerId, Inventory inventory, PipeBlockEntity pipe, Direction side,
                            List<IStatement> triggers, List<IStatement> actions, @Nullable ContainerData clientStates) {
    super(TransportMenuTypes.GATE_INTERFACE_MENU.get(), containerId, inventory);
    this.pipe = pipe;
    this.side = side;
    this.potentialTriggers = triggers;
    this.potentialActions = actions;
    this.actionStates = clientStates != null ? clientStates : new ContainerData() {
      @Override
      public int get(int index) {
        Gate gate = getGate();
        int packed = 0;
        for (int row = 0; row < 4; row++) {
          int i = index * 4 + row;
          if (gate != null && i < gate.actionsState.length && gate.actionsState[i] != null) {
            packed |= gate.actionsState[i].ordinal() << (row * 2);
          }
        }
        return packed;
      }

      @Override
      public void set(int index, int value) {
      }

      @Override
      public int getCount() {
        return 2;
      }
    };
    addDataSlots(actionStates);

    // Original: the player inventory sits at the bottom of the material's taller or shorter background.
    Gate gate = gateOf(pipe, side);
    int guiHeight = gate != null ? gate.material.guiHeight : DEFAULT_HEIGHT;
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, guiHeight - 84 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(inventory, x, 8 + x * 18, guiHeight - 26));
    }
  }

  @Nullable
  private static Gate gateOf(@Nullable PipeBlockEntity pipe, Direction side) {
    return pipe != null && pipe.getPipe().getGate(side) instanceof Gate gate ? gate : null;
  }

  /**
   * Original {@code ContainerGateInterface.init()}: the valid statements, without those that need more parameters
   * than the material has slots for, sorted by their id.
   */
  private static List<IStatement> candidates(@Nullable Gate gate, boolean triggers) {
    if (gate == null) return Collections.emptyList();
    List<IStatement> list = new ArrayList<>();
    if (triggers) {
      list.add(DefaultTriggerProvider.triggerRedstoneActive);
      list.add(DefaultTriggerProvider.triggerRedstoneInactive);
      list.addAll(gate.getAllValidTriggers());
    } else {
      list.addAll(gate.getAllValidActions());
      List<IActionInternal> internal = new ArrayList<>();
      gate.addActions(internal);
      list.addAll(internal);
    }
    int maxParameters = triggers ? gate.material.numTriggerParameters : gate.material.numActionParameters;
    return list.stream()
      .filter(statement -> statement != null && statement.minParameters() <= maxParameters)
      .distinct()
      .sorted(Comparator.comparing(statement -> statement.getUniqueTag().toString()))
      .toList();
  }

  /** Writes what the client constructor reads after the pipe position. */
  public static void writeOpenData(FriendlyByteBuf buf, PipeBlockEntity pipe, Direction side) {
    buf.writeBlockPos(pipe.getBlockPos());
    buf.writeEnum(side);
    Gate gate = gateOf(pipe, side);
    writeStatements(buf, candidates(gate, true));
    writeStatements(buf, candidates(gate, false));
  }

  private static void writeStatements(FriendlyByteBuf buf, List<IStatement> statements) {
    buf.writeVarInt(statements.size());
    for (IStatement statement : statements) buf.writeResourceLocation(statement.getUniqueTag());
  }

  private static List<IStatement> readStatements(FriendlyByteBuf buf) {
    int size = buf.readVarInt();
    List<IStatement> list = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      IStatement statement = StatementManager.getStatement(buf.readResourceLocation());
      if (statement != null) list.add(statement);
    }
    return list;
  }

  /** The candidates, as the original kept them in ContainerGateInterface (also on the client). */
  public List<IStatement> getPotentialTriggers() {
    return potentialTriggers;
  }

  public List<IStatement> getPotentialActions() {
    return potentialActions;
  }

  /** Original actionsState of a row: Deactivated, Partial or Activated. */
  public Gate.ActionActiveState getActionState(int row) {
    if (row < 0 || row >= 8) return Gate.ActionActiveState.Deactivated;
    int packed = actionStates.get(row / 4);
    return Gate.ActionActiveState.values()[(packed >> ((row % 4) * 2)) & 0x03];
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return pipe != null && !pipe.isRemoved() && p_38874_.level().getBlockEntity(pipe.getBlockPos()) == pipe
      && p_38874_.distanceToSqr((double) pipe.getBlockPos().getX() + 0.5D, (double) pipe.getBlockPos().getY() + 0.5D, (double) pipe.getBlockPos().getZ() + 0.5D) <= 64.0D;
  }

  public Gate getGate() {
    return gateOf(pipe, side);
  }

  /**
   * Packs a click into a menu button id.
   *
   * @param kind      {@link #KIND_TRIGGER} .. {@link #KIND_ACTION_PARAMETER}
   * @param row       the statement row
   * @param parameter the parameter slot (parameter kinds only)
   * @param click     for statements {@link #CLICK_NEXT} / {@link #CLICK_PREVIOUS} / {@link #CLICK_CLEAR}; for
   *                  parameters the mouse button
   * @param shift     whether shift was held (parameters only)
   */
  public static int buttonId(int kind, int row, int parameter, int click, boolean shift) {
    return kind | (row << 2) | (parameter << 5) | (click << 7) | ((shift ? 1 : 0) << 9);
  }

  /** Original ContainerGateInterface setTrigger / setAction / set...Parameter, all decided on the server. */
  @Override
  public boolean clickMenuButton(Player player, int id) {
    Gate gate = getGate();
    if (player.level().isClientSide || !stillValid(player) || gate == null || id < 0) return false;
    int kind = id & 0x3;
    int row = (id >> 2) & 0x7;
    int parameter = (id >> 5) & 0x3;
    int click = (id >> 7) & 0x3;
    boolean shift = ((id >> 9) & 0x1) != 0;
    if (row >= gate.material.numSlots) return false;

    switch (kind) {
      case KIND_TRIGGER, KIND_ACTION -> {
        boolean trigger = kind == KIND_TRIGGER;
        List<IStatement> choices = trigger ? potentialTriggers : potentialActions;
        IStatement current = trigger ? gate.getTrigger(row) : gate.getAction(row);
        IStatement changed = cycle(choices, current, click);
        if (trigger) gate.setTrigger(row, changed); else gate.setAction(row, changed);
        // Original doSlotClick(): the parameters are reset to the new statement's required defaults.
        int slots = trigger ? gate.material.numTriggerParameters : gate.material.numActionParameters;
        for (int p = 0; p < slots; p++) {
          IStatementParameter fresh = changed != null && p < changed.minParameters() ? changed.createParameter(p) : null;
          if (trigger) gate.setTriggerParameter(row, p, fresh); else gate.setActionParameter(row, p, fresh);
        }
      }
      case KIND_TRIGGER_PARAMETER, KIND_ACTION_PARAMETER -> {
        boolean trigger = kind == KIND_TRIGGER_PARAMETER;
        int slots = trigger ? gate.material.numTriggerParameters : gate.material.numActionParameters;
        IStatement statement = trigger ? gate.getTrigger(row) : gate.getAction(row);
        if (statement == null || parameter >= slots || parameter >= statement.maxParameters()) return false;
        IStatementParameter param = trigger ? gate.getTriggerParameter(row, parameter) : gate.getActionParameter(row, parameter);
        if (param == null) param = statement.createParameter(parameter);
        if (param == null) return false;
        ItemStack carried = getCarried();
        param.onClick(gate, statement, carried, new StatementMouseClick(click, shift));
        if (trigger) gate.setTriggerParameter(row, parameter, param); else gate.setActionParameter(row, parameter, param);
      }
      default -> {
        return false;
      }
    }
    pipe.setChanged();
    if (pipe.getLevel() != null) {
      pipe.getLevel().sendBlockUpdated(pipe.getBlockPos(), pipe.getBlockState(), pipe.getBlockState(), 3);
    }
    return true;
  }

  /**
   * Original {@code doSlotClick()}: an empty slot takes the first (or, backwards, the last) candidate; a filled one
   * the next (previous) one, and after the last one it is cleared.
   */
  @Nullable
  static IStatement cycle(List<IStatement> choices, @Nullable IStatement current, int click) {
    if (click == CLICK_CLEAR || choices.isEmpty()) return null;
    boolean backwards = click == CLICK_PREVIOUS;
    if (current == null) {
      return backwards ? choices.get(choices.size() - 1) : choices.get(0);
    }
    int index = choices.indexOf(current);
    if (index < 0) {
      return backwards ? choices.get(choices.size() - 1) : choices.get(0);
    }
    int next = backwards ? index - 1 : index + 1;
    return next >= 0 && next < choices.size() ? choices.get(next) : null;
  }
}
