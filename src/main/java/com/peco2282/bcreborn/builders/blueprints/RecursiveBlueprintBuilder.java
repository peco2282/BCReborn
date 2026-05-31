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
package com.peco2282.bcreborn.builders.blueprints;


import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.blueprint.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class RecursiveBlueprintBuilder {

  private final ArrayList<CompoundTag> subBlueprints;
  private boolean returnedThis = false;
  private BlueprintBase blueprint;
  private RecursiveBlueprintBuilder current;
  private int nextSubBlueprint = 0;
  private final int x;
  private final int y;
  private final int z;
  private final Direction dir;
  private final Level world;
  private final Box box = new Box();

  public RecursiveBlueprintBuilder(BlueprintBase iBlueprint, Level iWorld, int iX, int iY, int iZ,
                                   Direction iDir) {
    blueprint = iBlueprint;
    subBlueprints = iBlueprint.subBlueprintsNBT;
    world = iWorld;
    x = iX;
    y = iY;
    z = iZ;
    dir = iDir;
  }

  public BptBuilderBase nextBuilder() {
    if (!returnedThis) {
      blueprint = blueprint.adjustToWorld(world, x, y, z, dir);

      returnedThis = true;

      BptBuilderBase builder;

      if (blueprint instanceof Blueprint) {
        builder = new BptBuilderBlueprint((Blueprint) blueprint, world, x, y, z);
      } else if (blueprint instanceof Template) {
        builder = new BptBuilderTemplate(blueprint, world, x, y, z);
      } else {
        return null;
      }

      box.initialize(builder);

      return builder;
    }

    // Free memory associated with this blueprint
    blueprint = null;

    if (current != null) {
      BptBuilderBase builder = current.nextBuilder();

      if (builder != null) {
        return builder;
      }
    }

    if (nextSubBlueprint >= subBlueprints.size()) {
      return null;
    }

    CompoundTag nbt = subBlueprints.get(nextSubBlueprint);
    BlueprintBase bpt = BlueprintBase.loadBluePrint(nbt.getCompound("bpt"));

    int nx = box.xMin + nbt.getInt("x");
    int ny = box.yMin + nbt.getInt("y");
    int nz = box.zMin + nbt.getInt("z");

    Direction nbtDir = Direction.from3DDataValue(nbt.getByte("dir"));

    current = new RecursiveBlueprintBuilder(bpt, world, nx, ny, nz, nbtDir);
    nextSubBlueprint++;

    return current.nextBuilder();
  }
}
