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
package com.peco2282.bcreborn.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class RobotEvent extends Event {
  public final Object robot;

  public RobotEvent(Object robot) {
    this.robot = robot;
  }

  @Cancelable
  public static class Place extends RobotEvent {
    public final Player player;

    public Place(Object robot, Player player) {
      super(robot);
      this.player = player;
    }
  }

  @Cancelable
  public static class Interact extends RobotEvent {
    public final Player player;
    public final ItemStack item;

    public Interact(Object robot, Player player, ItemStack item) {
      super(robot);
      this.player = player;
      this.item = item;
    }
  }

  @Cancelable
  public static class Dismantle extends RobotEvent {
    public final Player player;

    public Dismantle(Object robot, Player player) {
      super(robot);
      this.player = player;
    }
  }
}
