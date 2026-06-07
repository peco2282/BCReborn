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

import com.peco2282.bcreborn.api.gates.GateExpansionController;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class GateExpansionLightSensor extends GateExpansionBuildcraft implements IGateExpansion {

  public static GateExpansionLightSensor INSTANCE = new GateExpansionLightSensor();

  private GateExpansionLightSensor() {
    super("light_sensor");
  }

  @Override
  public GateExpansionController makeController(BlockEntity pipeTile) {
    return new GateExpansionControllerLightSensor(pipeTile);
  }

  private class GateExpansionControllerLightSensor extends GateExpansionController {

    public GateExpansionControllerLightSensor(BlockEntity pipeTile) {
      super(GateExpansionLightSensor.this, pipeTile);
    }

    @Override
    public void addTriggers(List<ITriggerInternal> list) {
      super.addTriggers(list);
      // TODO: Light sensor triggers
    }
  }
}
