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
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.robotics.RobotUtils;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Function;

public class TriggerRobotInStation extends BCStatement implements ITriggerInternal {

  public TriggerRobotInStation() {
    super("robot.in.station");
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.trigger.robot.in.station");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornRobotics.location("triggers/trigger_robot_in_station"));
  }

  @Override
  public int minParameters() {
    return 0;
  }

  @Override
  public int maxParameters() {
    return 0;
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return new StatementParameterRobot();
  }

  @Override
  public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
    List<DockingStation> stations = RobotUtils.getStations(container.getTile());

    for (DockingStation station : stations) {
      if (station.robotTaking() != null) {
        EntityRobot robot = (EntityRobot) station.robotTaking();

        if (robot.getDockingStation() == station) {
          if (parameters.length > 0 && parameters[0] instanceof StatementParameterRobot && !parameters[0].getItemStack().isEmpty()) {
            if (StatementParameterRobot.matches(parameters[0], robot)) {
              return true;
            }
          } else {
            return true;
          }
        }
      }
    }

    return false;
  }
}
