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

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.items.IMapLocation;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRobotRegistry;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.robotics.ai.AIRobotGoAndLinkToDock;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import com.peco2282.bcreborn.robotics.util.RobotUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Function;

public class ActionRobotGotoStation extends BCStatement implements IActionInternal {

  public ActionRobotGotoStation() {
    super("robot.goto_station");
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.action.robot.goto_station");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornRobotics.location("triggers/action_robot_goto_station"));
  }

  @Override
  public void actionActivate(IStatementContainer container, IStatementParameter[] parameters) {
    if (container.getTile() == null || container.getTile().getLevel() == null) return;
    IRobotRegistry registry = RobotManager.registry().getRegistry(container.getTile().getLevel());

    List<DockingStation<?>> stations = RobotUtils.getStations(container.getTile());

    for (DockingStation<?> station : stations) {
      if (station.robotTaking() != null) {
        RobotEntity robot = (RobotEntity) station.robotTaking();
        AIRobot<?> ai = robot.getOverridingAI();

        if (ai != null) {
          continue;
        }

        DockingStation<?> newStation = station;

        if (parameters[0] instanceof StatementParameterItemStack) {
          newStation = getStation((StatementParameterItemStack) parameters[0], registry);
        }

        if (newStation != null) {
          robot.overrideAI(new AIRobotGoAndLinkToDock(robot, newStation));
        }
      }
    }
  }

  private DockingStation<?> getStation(StatementParameterItemStack stackParam,
                                       IRobotRegistry registry) {
    ItemStack item = stackParam.getItemStack();

    if (item != null && !item.isEmpty() && item.getItem() instanceof IMapLocation map) {
      BlockIndex index = map.getPoint(item);

      if (index != null) {
        Direction side = map.getPointSide(item);

        return registry.getStation(index.toBlockPos(), side);
      }
    }
    return null;
  }

  @Override
  public int maxParameters() {
    return 1;
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return new StatementParameterItemStack(ItemStack.EMPTY);
  }

}
