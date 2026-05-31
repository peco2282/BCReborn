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
package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.items.IList;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.statements.*;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import com.peco2282.bcreborn.robotics.RobotUtils;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import net.minecraft.world.item.ItemStack;

public class StatementParameterRobot extends StatementParameterItemStack {

  public StatementParameterRobot() {
    super(ItemStack.EMPTY);
  }

  public static boolean matches(IStatementParameter param, EntityRobotBase robot) {
    ItemStack stack = param.getItemStack();
    if (!stack.isEmpty()) {
      if (stack.getItem() instanceof IList list) {
        if (list.matches(stack, RobotItem.createRobotStack(robot.getBoard().getNBTHandler(), robot.getEnergy()))) {
          return true;
        }
        for (ItemStack target : ((EntityRobot) robot).getWearables()) {
          if (!target.isEmpty() && list.matches(stack, target)) {
            return true;
          }
        }
      } else if (stack.getItem() instanceof RobotItem) {
        return RobotItem.getRobotNBT(stack) == robot.getBoard().getNBTHandler();
      } else if (robot instanceof EntityRobot) {
        for (ItemStack target : ((EntityRobot) robot).getWearables()) {
          if (!target.isEmpty() && StackHelper.isMatchingItem(stack, target, true, true)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack,
                      StatementMouseClick mouse) {
    if (stack.isEmpty() && (this.stack.isEmpty() || this.stack.getItem() instanceof RobotItem)) {
      RedstoneBoardRobotNBT nextBoard = RobotUtils.getNextBoard(this.stack, mouse.getButton() > 0);
      if (nextBoard != null) {
        this.stack = RobotItem.createRobotStack(nextBoard, 0);
      } else {
        this.stack = ItemStack.EMPTY;
      }
    } else {
      super.onClick(source, stmt, stack, mouse);
    }
  }

  @Override
  public String getUniqueTag() {
    return "buildcraft:robot";
  }
}
