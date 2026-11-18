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
package com.peco2282.bcreborn.transport.test;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.advancement.BCAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/** The original 7.1 achievements, as advancements earned by crafting their item. */
@GameTestHolder(BCRebornTransport.MODID)
public class AdvancementGameTests {
  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static Advancement advancement(GameTestHelper helper, String name) {
    return helper.getLevel().getServer().getAdvancements().getAdvancement(BCAdvancements.id(name));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void everyOriginalAchievementIsLoaded(GameTestHelper helper) {
    for (BCAdvancements.Entry entry : BCAdvancements.entries()) {
      Advancement advancement = advancement(helper, entry.name());
      check(helper, advancement != null, "Missing advancement " + BCAdvancements.id(entry.name()));
      Advancement parent = entry.parent() == null ? null : advancement(helper, entry.parent());
      check(helper, advancement.getParent() == parent, entry.name() + " must follow " + entry.parent());
    }
    check(helper, BCAdvancements.entries().length == 20, "The original page has 20 achievements");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void craftingAWoodenGearEarnsItsAdvancement(GameTestHelper helper) {
    // Forge never grants advancements to a FakePlayer, so use a plain server player (not in the player list, so the
    // chat announcement reaches no connection).
    ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
      new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "AdvancementTest"));
    Advancement gear = advancement(helper, "woodenGear");
    player.getInventory().add(new ItemStack(CoreItems.WOODEN_GEAR.get()));
    check(helper, !player.getAdvancements().getOrStartProgress(gear).isDone(), "Picking a gear up does not count, only crafting it");
    ForgeEventFactory.firePlayerCraftingEvent(player, new ItemStack(CoreItems.WOODEN_GEAR.get()), new SimpleContainer(9));
    check(helper, player.getAdvancements().getOrStartProgress(gear).isDone(), "Crafting a wooden gear earns the advancement");
    check(helper, !player.getAdvancements().getOrStartProgress(advancement(helper, "stoneGear")).isDone(), "Other advancements are not earned");
    helper.succeed();
  }
}
