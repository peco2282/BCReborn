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
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.nbt.CompoundTag;

public class AIRobotHarvest extends AIRobot<AIRobotHarvest> {

  private final int delay = 0;
  private BlockIndex blockFound;

  public AIRobotHarvest(RobotEntityBase iRobot) {
    super(RoboticsAIType.HARVEST, iRobot);
  }

  public AIRobotHarvest(RobotEntityBase iRobot, BlockIndex iBlockFound) {
    super(RoboticsAIType.HARVEST, iRobot);
    blockFound = iBlockFound;
  }

  @Override
  public void update() {
    if (blockFound == null) {
      setSuccess(false);
      terminate();
      return;
    }
    // Implementation simplified for now
    terminate();
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    if (blockFound != null) {
      CompoundTag sub = new CompoundTag();
      blockFound.writeTo(sub);
      nbt.put("blockFound", sub);
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("blockFound")) {
      blockFound = new BlockIndex(nbt.getCompound("blockFound"));
    }
  }
}
