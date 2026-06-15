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
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionStationForbidRobot extends BCStatement implements IActionInternal {
  private final boolean invert;

  public ActionStationForbidRobot(boolean invert) {
    super("station." + (invert ? "force" : "forbid") + "_robot");
    this.invert = invert;
  }

  public static boolean isForbidden(DockingStation<?> station, RobotEntityBase robot) {
    for (StatementSlot s : station.getActiveActions()) {
      if (s.statement instanceof ActionStationForbidRobot) {
        if (((ActionStationForbidRobot) s.statement).invert ^ ActionStationForbidRobot.isForbidden(s, robot)) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean isForbidden(StatementSlot slot, RobotEntityBase robot) {
    for (IStatementParameter p : slot.parameters) {
      if (p instanceof StatementParameterRobot && StatementParameterRobot.matches(p, robot)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.action.station." + (invert ? "force" : "forbid") + "_robot");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornRobotics.location("triggers/action_station_robot_" + (invert ? "mandatory" : "forbidden")));
  }

  @Override
  public int minParameters() {
    return 1;
  }

  @Override
  public int maxParameters() {
    return 3;
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return new StatementParameterRobot();
  }

  @Override
  public void actionActivate(IStatementContainer source,
                             IStatementParameter[] parameters) {

  }
}
