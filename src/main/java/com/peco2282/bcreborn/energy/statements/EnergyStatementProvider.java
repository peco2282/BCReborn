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
package com.peco2282.bcreborn.energy.statements;

import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.ITriggerProvider;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Original {@code EnergyStatementProvider} and the trigger fields of {@code BuildCraftEnergy}: a gate next to an
 * engine offers its heat stages; next to a combustion engine also fuel and coolant below 25 % / 50 %.
 */
public final class EnergyStatementProvider implements ITriggerProvider {
  public static final EnergyStatementProvider INSTANCE = new EnergyStatementProvider();

  public static final ITriggerExternal TRIGGER_BLUE_ENGINE_HEAT = new TriggerEngineHeat(EngineBlockEntity.EnergyStage.BLUE);
  public static final ITriggerExternal TRIGGER_GREEN_ENGINE_HEAT = new TriggerEngineHeat(EngineBlockEntity.EnergyStage.GREEN);
  public static final ITriggerExternal TRIGGER_YELLOW_ENGINE_HEAT = new TriggerEngineHeat(EngineBlockEntity.EnergyStage.YELLOW);
  public static final ITriggerExternal TRIGGER_RED_ENGINE_HEAT = new TriggerEngineHeat(EngineBlockEntity.EnergyStage.RED);
  public static final ITriggerExternal TRIGGER_ENGINE_OVERHEAT = new TriggerEngineHeat(EngineBlockEntity.EnergyStage.OVERHEAT);
  public static final ITriggerExternal TRIGGER_FUEL_BELOW_25 = new TriggerFuelBelowThreshold(0.25F);
  public static final ITriggerExternal TRIGGER_FUEL_BELOW_50 = new TriggerFuelBelowThreshold(0.50F);
  public static final ITriggerExternal TRIGGER_COOLANT_BELOW_25 = new TriggerCoolantBelowThreshold(0.25F);
  public static final ITriggerExternal TRIGGER_COOLANT_BELOW_50 = new TriggerCoolantBelowThreshold(0.50F);

  private EnergyStatementProvider() {
  }

  @Override
  public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
    return List.of();
  }

  @Override
  public Collection<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity tile) {
    LinkedList<ITriggerExternal> triggers = new LinkedList<>();
    if (tile instanceof EngineBlockEntity<?>) {
      triggers.add(TRIGGER_BLUE_ENGINE_HEAT);
      triggers.add(TRIGGER_GREEN_ENGINE_HEAT);
      triggers.add(TRIGGER_YELLOW_ENGINE_HEAT);
      triggers.add(TRIGGER_RED_ENGINE_HEAT);
      triggers.add(TRIGGER_ENGINE_OVERHEAT);
    }
    if (tile instanceof IronEngineBlockEntity) {
      triggers.add(TRIGGER_COOLANT_BELOW_25);
      triggers.add(TRIGGER_COOLANT_BELOW_50);
      triggers.add(TRIGGER_FUEL_BELOW_25);
      triggers.add(TRIGGER_FUEL_BELOW_50);
    }
    return triggers;
  }
}
