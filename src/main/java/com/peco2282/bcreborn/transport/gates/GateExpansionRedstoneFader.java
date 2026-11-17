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

import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.api.gates.GateExpansionController;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class GateExpansionRedstoneFader extends GateExpansionBuildcraft implements IGateExpansion {

  public static GateExpansionRedstoneFader INSTANCE = new GateExpansionRedstoneFader();

  private GateExpansionRedstoneFader() {
    super("fader");
  }

  @Override
  public GateExpansionController makeController(BlockEntity pipeTile) {
    return new GateExpansionControllerRedstoneFader(pipeTile);
  }

  private class GateExpansionControllerRedstoneFader extends GateExpansionController {

    public GateExpansionControllerRedstoneFader(BlockEntity pipeTile) {
      super(GateExpansionRedstoneFader.this, pipeTile);
    }

    @Override
    public void addTriggers(List<ITriggerInternal> list) {
      super.addTriggers(list);
      // Original: "redstone input is exactly N" for N = 1..15.
      TransportStatements.TRIGGER_REDSTONE_FADER_INPUT.values().forEach(trigger -> list.add(trigger.get()));
    }

    @Override
    public void addActions(List<IActionInternal> list) {
      super.addActions(list);
      // Original: "emit redstone level N" for N = 1..15.
      TransportStatements.ACTION_REDSTONE_FADER_OUTPUT.values().forEach(action -> list.add(action.get()));
    }
  }
}
