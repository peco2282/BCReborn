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
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GateExpansionNote extends GateExpansionBuildcraft implements IGateExpansion {

  public static GateExpansionNote INSTANCE = new GateExpansionNote();

  private GateExpansionNote() {
    super("note");
  }

  @Override
  public GateExpansionController makeController(BlockEntity pipeTile) {
    return new GateExpansionControllerNote(pipeTile);
  }

  private class GateExpansionControllerNote extends GateExpansionController {

    public GateExpansionControllerNote(BlockEntity pipeTile) {
      super(GateExpansionNote.this, pipeTile);
    }
  }
}
