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
package com.peco2282.bcreborn.transport.gates;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.peco2282.bcreborn.api.gates.GateExpansionController;
import com.peco2282.bcreborn.api.gates.IGate;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.statements.*;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import com.peco2282.bcreborn.api.statements.containers.ISidedStatementContainer;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.PipeWire;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Gate implements IGate, ISidedStatementContainer, IRedstoneStatementContainer {

  public static int MAX_STATEMENTS = 8;
  public static int MAX_PARAMETERS = 3;
  public final IPipe pipe;
  public final GateDefinition.GateMaterial material;
  public final GateDefinition.GateLogic logic;
  public final BiMap<IGateExpansion, GateExpansionController> expansions = HashBiMap.create();
  private final HashMultiset<IStatement> statementCounts = HashMultiset.create();
  private final int[] actionGroups = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
  public IStatement[] triggers = new IStatement[MAX_STATEMENTS];
  public IStatementParameter[][] triggerParameters = new IStatementParameter[MAX_STATEMENTS][MAX_PARAMETERS];
  public IStatement[] actions = new IStatement[MAX_STATEMENTS];
  public IStatementParameter[][] actionParameters = new IStatementParameter[MAX_STATEMENTS][MAX_PARAMETERS];
  public ActionActiveState[] actionsState = new ActionActiveState[MAX_STATEMENTS];
  public ArrayList<StatementSlot> activeActions = new ArrayList<>();
  public byte broadcastSignal, prevBroadcastSignal;
  public int redstoneOutput = 0;
  public int redstoneOutputSide = 0;
  /**
   * this is the internal pulsing state of the gate. Intended to be managed
   * by the server side only, the client is supposed to be referring to the
   * state of the renderer, and update moveStage accordingly.
   */
  public boolean isPulsing = false;
  private Direction direction;

  // / CONSTRUCTOR
  public Gate(IPipe pipe, GateDefinition.GateMaterial material, GateDefinition.GateLogic logic, Direction direction) {
    this.pipe = pipe;
    this.material = material;
    this.logic = logic;
    this.direction = direction;

    Arrays.fill(actionsState, ActionActiveState.Deactivated);
  }

  public void setTrigger(int position, IStatement trigger) {
    if (trigger != triggers[position]) {
      Arrays.fill(triggerParameters[position], null);
    }
    triggers[position] = trigger;
  }

  public IStatement getTrigger(int position) {
    return triggers[position];
  }

  public void setAction(int position, IStatement action) {
    if (action != actions[position]) {
      Arrays.fill(actionParameters[position], null);
    }

    actions[position] = action;

    recalculateActionGroups();
  }

  public IStatement getAction(int position) {
    return actions[position];
  }

  public void setTriggerParameter(int trigger, int param, IStatementParameter p) {
    triggerParameters[trigger][param] = p;
  }

  public void setActionParameter(int action, int param, IStatementParameter p) {
    actionParameters[action][param] = p;

    recalculateActionGroups();
  }

  public IStatementParameter getTriggerParameter(int trigger, int param) {
    return triggerParameters[trigger][param];
  }

  public IStatementParameter getActionParameter(int action, int param) {
    return actionParameters[action][param];
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public void addGateExpansion(IGateExpansion expansion) {
    if (!expansions.containsKey(expansion)) {
      expansions.put(expansion, expansion.makeController(pipe != null && pipe.getTile() instanceof BlockEntity be ? be : null));
    }
  }

  public void writeStatementsToNBT(CompoundTag data) {
    for (int i = 0; i < material.numSlots; ++i) {
      if (triggers[i] != null) {
        data.putString("trigger[" + i + "]", triggers[i].getUniqueTag().toString());
      }

      if (actions[i] != null) {
        data.putString("action[" + i + "]", actions[i].getUniqueTag().toString());
      }

      for (int j = 0; j < material.numTriggerParameters; ++j) {
        if (triggerParameters[i][j] != null) {
          CompoundTag cpt = new CompoundTag();
          cpt.putString("kind", triggerParameters[i][j].getUniqueTag().toString());
          triggerParameters[i][j].writeToNBT(cpt);
          data.put("triggerParameters[" + i + "][" + j + "]", cpt);
        }
      }

      for (int j = 0; j < material.numActionParameters; ++j) {
        if (actionParameters[i][j] != null) {
          CompoundTag cpt = new CompoundTag();
          cpt.putString("kind", actionParameters[i][j].getUniqueTag().toString());
          actionParameters[i][j].writeToNBT(cpt);
          data.put("actionParameters[" + i + "][" + j + "]", cpt);
        }
      }
    }
  }

  // / SAVING & LOADING
  public void writeToNBT(CompoundTag data) {
    data.putString("material", material.name());
    data.putString("logic", logic.name());
    data.putInt("direction", direction.ordinal());
    ListTag exList = new ListTag();
    for (GateExpansionController con : expansions.values()) {
      CompoundTag conNBT = new CompoundTag();
      conNBT.putString("type", con.getType().getUniqueIdentifier().toString());
      CompoundTag conData = new CompoundTag();
      con.writeToNBT(conData);
      conNBT.put("data", conData);
      exList.add(conNBT);
    }
    data.put("expansions", exList);

    writeStatementsToNBT(data);

    data.putByte("wireState", broadcastSignal);

    data.putByte("redstoneOutput", (byte) redstoneOutput);
  }

  public void readStatementsFromNBT(CompoundTag data) {
    // Clear
    for (int i = 0; i < material.numSlots; ++i) {
      triggers[i] = null;
      actions[i] = null;
      for (int j = 0; j < material.numTriggerParameters; j++) {
        triggerParameters[i][j] = null;
      }
      for (int j = 0; j < material.numActionParameters; j++) {
        actionParameters[i][j] = null;
      }
    }

    // Read
    for (int i = 0; i < material.numSlots; ++i) {
      if (data.contains("trigger[" + i + "]")) {
        triggers[i] = StatementManager.getStatement(ResourceLocation.parse(data.getString("trigger[" + i + "]")));
      }

      if (data.contains("action[" + i + "]")) {
        actions[i] = StatementManager.getStatement(ResourceLocation.parse(data.getString("action[" + i + "]")));
      }

      // This is for legacy trigger loading
      if (data.contains("triggerParameters[" + i + "]")) {
        triggerParameters[i][0] = new StatementParameterItemStack(ItemStack.EMPTY);
        triggerParameters[i][0].readFromNBT(data.getCompound("triggerParameters[" + i + "]"));
      }

      for (int j = 0; j < material.numTriggerParameters; ++j) {
        if (data.contains("triggerParameters[" + i + "][" + j + "]")) {
          CompoundTag cpt = data.getCompound("triggerParameters[" + i + "][" + j + "]");
          triggerParameters[i][j] = StatementManager.createParameter(cpt.getString("kind"));
          triggerParameters[i][j].readFromNBT(cpt);
        }
      }

      for (int j = 0; j < material.numActionParameters; ++j) {
        if (data.contains("actionParameters[" + i + "][" + j + "]")) {
          CompoundTag cpt = data.getCompound("actionParameters[" + i + "][" + j + "]");
          actionParameters[i][j] = StatementManager.createParameter(cpt.getString("kind"));
          actionParameters[i][j].readFromNBT(cpt);
        }
      }
    }

    recalculateActionGroups();
  }

  public boolean verifyGateStatements() {
    List<IStatement> triggerList = getAllValidTriggers();
    List<IStatement> actionList = getAllValidActions();
    boolean warning = false;

    for (int i = 0; i < MAX_STATEMENTS; ++i) {
      if ((triggers[i] != null || actions[i] != null) && i >= material.numSlots) {
        triggers[i] = null;
        actions[i] = null;
        warning = true;
        continue;
      }

      if (triggers[i] != null) {
        if (!triggerList.contains(triggers[i]) ||
          triggers[i].minParameters() > material.numTriggerParameters) {
          triggers[i] = null;
          warning = true;
        }
      }

      if (actions[i] != null) {
        if (!actionList.contains(actions[i]) ||
          actions[i].minParameters() > material.numActionParameters) {
          actions[i] = null;
          warning = true;
        }
      }
    }

    if (warning) {
      recalculateActionGroups();
    }

    return !warning;
  }

  public void readFromNBT(CompoundTag data) {
    readStatementsFromNBT(data);

    if (data.contains("wireState[0]")) {
      for (PipeWire wire : PipeWire.VALUES) {
        if (data.getBoolean("wireState[" + wire.ordinal() + "]")) {
          broadcastSignal |= (byte) (1 << wire.ordinal());
        }
      }
    } else {
      broadcastSignal = data.getByte("wireState");
    }

    redstoneOutput = data.getByte("redstoneOutput");
  }

  // GUI
  public void openGui(Player player) {
    if (!player.level().isClientSide) {
      NetworkHooks.openScreen(
        (net.minecraft.server.level.ServerPlayer) player,
        new MenuProvider() {
          @Override
          public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
            return null; // Should not be called if FriendlyByteBuf is used
          }

          @Override
          public Component getDisplayName() {
            return Component.literal("Gate");
          }
        },
        buf -> buf.writeBlockPos(pipe.getTile().getPos())
      );
    }
  }

  // UPDATING
  public void tick() {
    for (GateExpansionController expansion : expansions.values()) {
      expansion.tick(this);
    }
  }

  public ItemStack getGateItem() {
    return ItemStack.EMPTY; // TODO: ItemGate
  }

  public void dropGate() {
    // pipe.dropItem(getGateItem());
  }

  public void resetGate() {
    if (redstoneOutput != 0) {
      redstoneOutput = 0;
      // pipe.updateNeighbors(true);
    }
  }

  public boolean isGateActive() {
    for (ActionActiveState state : actionsState) {
      if (state == ActionActiveState.Activated) {
        return true;
      }
    }

    return false;
  }

  public boolean isGatePulsing() {
    return isPulsing;
  }

  public int getRedstoneOutput() {
    return redstoneOutput;
  }

  public int getSidedRedstoneOutput() {
    return redstoneOutputSide;
  }

  public void setRedstoneOutput(boolean sideOnly, int value) {
    redstoneOutputSide = value;

    if (!sideOnly) {
      redstoneOutput = value;
    }
  }

  public void startResolution() {
    for (GateExpansionController expansion : expansions.values()) {
      expansion.startResolution();
    }
  }

  public void resolveActions() {
    int oldRedstoneOutput = redstoneOutput;
    redstoneOutput = 0;

    int oldRedstoneOutputSide = redstoneOutputSide;
    redstoneOutputSide = 0;

    boolean wasActive = !activeActions.isEmpty();

    prevBroadcastSignal = broadcastSignal;
    broadcastSignal = 0;

    // Tell the gate to prepare for resolving actions. (Disable pulser)
    startResolution();

    // Computes the actions depending on the triggers
    for (int it = 0; it < MAX_STATEMENTS; ++it) {
      actionsState[it] = ActionActiveState.Deactivated;

      IStatement trigger = triggers[it];
      IStatementParameter[] parameter = triggerParameters[it];

      if (trigger != null) {
        if (isTriggerActive(trigger, parameter)) {
          actionsState[it] = ActionActiveState.Partial;
        }
      }
    }

    activeActions.clear();

    for (int it = 0; it < MAX_STATEMENTS; ++it) {
      boolean allActive = true;
      boolean oneActive = false;

      if (actions[it] == null) {
        continue;
      }

      for (int j = 0; j < MAX_STATEMENTS; ++j) {
        if (actionGroups[j] == it) {
          if (actionsState[j] != ActionActiveState.Partial) {
            allActive = false;
          } else {
            oneActive = true;
          }
        }
      }

      if ((logic == GateDefinition.GateLogic.AND && allActive && oneActive) || (logic == GateDefinition.GateLogic.OR && oneActive)) {
        if (logic == GateDefinition.GateLogic.AND) {
          for (int j = 0; j < MAX_STATEMENTS; ++j) {
            if (actionGroups[j] == it) {
              actionsState[j] = ActionActiveState.Activated;
            }
          }
        }

        StatementSlot slot = new StatementSlot();
        slot.statement = actions[it];
        slot.parameters = actionParameters[it];
        activeActions.add(slot);
      }

      if (logic == GateDefinition.GateLogic.OR && actionsState[it] == ActionActiveState.Partial) {
        actionsState[it] = ActionActiveState.Activated;
      }
    }

    statementCounts.clear();

    for (int it = 0; it < MAX_STATEMENTS; ++it) {
      if (actionsState[it] == ActionActiveState.Activated) {
        statementCounts.add(actions[it], 1);
      }
    }

    // Activate the actions
    for (StatementSlot slot : activeActions) {
      IStatement action = slot.statement;
      if (action instanceof IActionInternal) {
        ((IActionInternal) action).actionActivate(this, slot.parameters);
      } else if (action instanceof IActionExternal) {
        for (Direction side : Direction.values()) {
          BlockEntity tile = this.getPipe().getTile().getNeighborTile(side);
          if (tile != null) {
            ((IActionExternal) action).actionActivate(tile, side, this, slot.parameters);
          }
        }
      } else {
        continue;
      }

      // Custom gate actions take precedence over defaults.
      if (resolveAction(action)) {
        continue;
      }

      for (Direction side : Direction.values()) {
        BlockEntity tile = pipe.getTile().getNeighborTile(side);
        if (tile instanceof IActionReceptor recept) {
          recept.actionActivated(action, slot.parameters);
        }
      }
    }

    // pipe.actionsActivated(activeActions);

    if (oldRedstoneOutput != redstoneOutput || oldRedstoneOutputSide != redstoneOutputSide) {
      // pipe.updateNeighbors(true);
    }

    if (prevBroadcastSignal != broadcastSignal) {
      // pipe.scheduleWireUpdate();
    }

    boolean isActive = !activeActions.isEmpty();

    if (wasActive != isActive) {
      pipe.getTile().scheduleRenderUpdate();
    }
  }

  public boolean resolveAction(IStatement action) {
    for (GateExpansionController expansion : expansions.values()) {
      if (expansion.resolveAction(action, statementCounts.count(action))) {
        return true;
      }
    }
    return false;
  }

  public boolean isTriggerActive(IStatement trigger, IStatementParameter[] parameters) {
    if (trigger == null) {
      return false;
    }

    if (trigger instanceof ITriggerInternal) {
      if (((ITriggerInternal) trigger).isTriggerActive(this, parameters)) {
        return true;
      }
    } else if (trigger instanceof ITriggerExternal) {
      for (Direction side : Direction.values()) {
        BlockEntity tile = this.getPipe().getTile().getNeighborTile(side);
        if (tile != null) {
          if (tile instanceof ITriggerExternalOverride) {
            ITriggerExternalOverride.Result result = ((ITriggerExternalOverride) tile).override(side, this, parameters);
            if (result == ITriggerExternalOverride.Result.TRUE) {
              return true;
            } else if (result == ITriggerExternalOverride.Result.FALSE) {
              continue;
            }
          }
          if (((ITriggerExternal) trigger).isTriggerActive(tile, side, this, parameters)) {
            return true;
          }
        }
      }
    }

    // TODO: This can probably be refactored with regular triggers instead
    // of yet another system.
    for (GateExpansionController expansion : expansions.values()) {
      if (expansion.isTriggerActive(trigger, parameters)) {
        return true;
      }
    }

    return false;
  }

  // TRIGGERS
  public void addTriggers(List<ITriggerInternal> list) {
    for (PipeWire wire : PipeWire.VALUES) {
      if (pipe.isWired(wire) && wire.ordinal() < material.maxWireColor) {
        // list.add(BuildCraftTransport.triggerPipeWireActive[wire.ordinal()]);
        // list.add(BuildCraftTransport.triggerPipeWireInactive[wire.ordinal()]);
      }
    }

    for (GateExpansionController expansion : expansions.values()) {
      expansion.addTriggers(list);
    }
  }

  public List<IStatement> getAllValidTriggers() {
    ArrayList<IStatement> allTriggers = new ArrayList<>(64);
    allTriggers.addAll(StatementManager.getInternalTriggers(this));

    for (Direction o : Direction.values()) {
      BlockEntity tile = pipe.getTile().getNeighborTile(o);
      allTriggers.addAll(StatementManager.getExternalTriggers(o, tile));
    }

    return allTriggers;
  }

  // ACTIONS
  public void addActions(List<IActionInternal> list) {
    for (PipeWire wire : PipeWire.VALUES) {
      if (pipe.isWired(wire) && wire.ordinal() < material.maxWireColor) {
        // list.add(BuildCraftTransport.actionPipeWire[wire.ordinal()]);
      }
    }

    for (GateExpansionController expansion : expansions.values()) {
      expansion.addActions(list);
    }
  }

  public List<IStatement> getAllValidActions() {
    ArrayList<IStatement> allActions = new ArrayList<>(64);
    allActions.addAll(StatementManager.getInternalActions(this));

    for (Direction o : Direction.values()) {
      BlockEntity tile = pipe.getTile().getNeighborTile(o);
      allActions.addAll(StatementManager.getExternalActions(o, tile));
    }

    return allActions;
  }

  //@Override TODO
  public void setPulsing(boolean pulsing) {
    if (pulsing != isPulsing) {
      isPulsing = pulsing;
      pipe.getTile().scheduleRenderUpdate();
    }
  }

  private void recalculateActionGroups() {
    for (int i = 0; i < MAX_STATEMENTS; ++i) {
      actionGroups[i] = i;

      for (int j = i - 1; j >= 0; --j) {
        if (actions[i] != null && actions[j] != null
          && actions[i].getUniqueTag().equals(actions[j].getUniqueTag())) {
          boolean sameParams = true;

          for (int p = 0; p < MAX_PARAMETERS; ++p) {
            if ((actionParameters[i][p] != null && actionParameters[j][p] == null)
              || (actionParameters[i][p] == null && actionParameters[j][p] != null)
              || (actionParameters[i][p] != null
              && actionParameters[j][p] != null
              && !actionParameters[i][p].equals(actionParameters[j][p]))) {
              sameParams = false;
              break;
            }
          }

          if (sameParams) {
            actionGroups[i] = j;
          }
        }
      }
    }
  }

  public void broadcastSignal(PipeWire color) {
    broadcastSignal |= (byte) (1 << color.ordinal());
  }

  public IPipe getPipe() {
    return pipe;
  }

  @Override
  public Direction getSide() {
    return direction;
  }

  @Override
  public BlockEntity getTile() {
    return pipe.getTile() instanceof BlockEntity be ? be : null;
  }

  @Override
  public List<IStatement> getTriggers() {
    return Arrays.asList(triggers).subList(0, material.numSlots);
  }

  @Override
  public List<IStatement> getActions() {
    return Arrays.asList(actions).subList(0, material.numSlots);
  }

  @Override
  public List<StatementSlot> getActiveActions() {
    return activeActions;
  }

  @Override
  public List<IStatementParameter> getTriggerParameters(int index) {
    if (index < 0 || index >= material.numSlots) {
      return null;
    }
    return Arrays.asList(triggerParameters[index]).subList(0, material.numTriggerParameters);
  }

  @Override
  public List<IStatementParameter> getActionParameters(int index) {
    if (index < 0 || index >= material.numSlots) {
      return null;
    }
    return Arrays.asList(actionParameters[index]).subList(0, material.numActionParameters);
  }

  @Override
  public int getRedstoneInput(Direction side) {
    // return side == Direction.UNKNOWN ? pipe.container.redstoneInput : pipe.container.redstoneInputSide[side.ordinal()];
    return 0; // TODO
  }

  @Override
  public void setRedstoneOutput(Direction side, int value) {
    if (side != this.getSide() && side != null) {
      return;
    }

    setRedstoneOutput(side != null, value);
  }

  public enum ActionActiveState {
    Deactivated, Partial, Activated
  }
}
