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

public class GateInterfaceMenu extends BuildCraftMenu<GateInterfaceMenu> {
  private final PipeBlockEntity pipe;
  private final Direction side;

  public GateInterfaceMenu(int p_38852_, Inventory p_38853_, FriendlyByteBuf buf) {
    this(p_38852_, p_38853_, getBlockEntity(p_38853_, buf), buf.readEnum(Direction.class));
  }

  public GateInterfaceMenu(int p_38852_, Inventory p_38853_, PipeBlockEntity pipe, Direction side) {
    super(TransportMenuTypes.GATE_INTERFACE_MENU.get(), p_38852_, p_38853_);
    this.pipe = pipe;
    this.side = side;

    int guiHeight = 166; // Default height

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(p_38853_, x + y * 9 + 9, 8 + x * 18, guiHeight - 84 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(p_38853_, x, 8 + x * 18, guiHeight - 26));
    }
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return pipe != null && !pipe.isRemoved() && p_38874_.level().getBlockEntity(pipe.getBlockPos()) == pipe
      && p_38874_.distanceToSqr((double) pipe.getBlockPos().getX() + 0.5D, (double) pipe.getBlockPos().getY() + 0.5D, (double) pipe.getBlockPos().getZ() + 0.5D) <= 64.0D;
  }

  public Gate getGate() {
    return pipe != null && pipe.getPipe().getGate(side) instanceof Gate gate ? gate : null;
  }

  @Override
  public boolean clickMenuButton(Player player, int id) {
    var gate = getGate();
    if (player.level().isClientSide || !stillValid(player) || gate == null || id < 0 || id >= gate.material.numSlots * 2) return false;
    int row = id / 2;
    boolean trigger = id % 2 == 0;
    List<IStatement> choices = new ArrayList<>();
    if (trigger) {
      choices.add(DefaultTriggerProvider.triggerRedstoneActive);
      choices.add(DefaultTriggerProvider.triggerRedstoneInactive);
      choices.addAll(gate.getAllValidTriggers());
    } else {
      choices.addAll(gate.getAllValidActions());
      List<IActionInternal> internal = new ArrayList<>();
      gate.addActions(internal);
      choices.addAll(internal);
    }
    choices = choices.stream().distinct().sorted(Comparator.comparing(s -> s.getUniqueTag().toString())).toList();
    var current = trigger ? gate.getTrigger(row) : gate.getAction(row);
    int next = choices.indexOf(current) + 1;
    var statement = next < choices.size() ? choices.get(next) : null;
    if (trigger) gate.setTrigger(row, statement); else gate.setAction(row, statement);
    pipe.setChanged();
    return true;
  }
}
