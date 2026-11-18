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
package com.peco2282.bcreborn.core.advancement;

import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersItems;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import org.jetbrains.annotations.Nullable;

/**
 * The 20 achievements of the original BuildCraft 7.1 page, as advancements: each is earned by crafting its item, and
 * the tree follows the original parents.
 */
public class BCAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
  public static final String PREFIX = "advancement.bcreborn.";
  private static final ResourceLocation BACKGROUND = new ResourceLocation("minecraft", "textures/block/oak_planks.png");

  /** One original achievement: its stat name (the language key), item and parent. */
  public record Entry(String name, ItemLike item, @Nullable String parent) {
  }

  public static Entry[] entries() {
    return new Entry[]{
      new Entry("woodenGear", CoreItems.WOODEN_GEAR.get(), null),
      new Entry("stoneGear", CoreItems.STONE_GEAR.get(), "woodenGear"),
      new Entry("ironGear", CoreItems.IRON_GEAR.get(), "stoneGear"),
      new Entry("goldGear", CoreItems.GOLD_GEAR.get(), "ironGear"),
      new Entry("diamondGear", CoreItems.DIAMOND_GEAR.get(), "goldGear"),
      new Entry("wrench", CoreItems.WRENCH.get(), "stoneGear"),
      new Entry("engine1", CoreBlocks.WOODEN_ENGINE.get(), "woodenGear"),
      new Entry("engine2", EnergyBlocks.STONE_ENGINE.get(), "engine1"),
      new Entry("engine3", EnergyBlocks.IRON_ENGINE.get(), "engine2"),
      new Entry("aLotOfCrafting", FactoryBlocks.AUTO_WORKBENCH.get(), "woodenGear"),
      new Entry("straightDown", FactoryBlocks.MINING_WELL.get(), "ironGear"),
      new Entry("refineAndRedefine", FactoryBlocks.REFINERY.get(), "diamondGear"),
      new Entry("timeForSomeLogic", SiliconBlocks.ASSEMBLY_TABLE.get(), "diamondGear"),
      new Entry("tinglyLaser", SiliconBlocks.LASER.get(), "timeForSomeLogic"),
      new Entry("chunkDestroyer", BuildersBlock.QUARRY.get(), "diamondGear"),
      new Entry("architect", BuildersBlock.ARCHITECT.get(), "goldGear"),
      new Entry("builder", BuildersBlock.BUILDER.get(), "architect"),
      new Entry("blueprint", BuildersItems.BLUEPRINT_STANDARD.get(), "architect"),
      new Entry("template", BuildersItems.BLUEPRINT_TEMPLATE.get(), "blueprint"),
      new Entry("blueprintLibrary", BuildersBlock.BLUEPRINT_LIBRARY.get(), "builder"),
    };
  }

  /** The advancement id: the original name in snake case ({@code woodenGear} -> {@code main/wooden_gear}). */
  public static ResourceLocation id(String name) {
    return new ResourceLocation("bcreborncore", "main/" + name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(java.util.Locale.ROOT));
  }

  @Override
  public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper helper) {
    Map<String, Advancement> built = new HashMap<>();
    for (Entry entry : entries()) {
      Advancement.Builder builder = Advancement.Builder.advancement()
        .display(entry.item(), Component.translatable(PREFIX + entry.name()), Component.translatable(PREFIX + entry.name() + ".desc"),
          entry.parent() == null ? BACKGROUND : null, FrameType.TASK, true, true, false)
        .addCriterion("crafted", CraftedItemTrigger.craftedItem(entry.item()));
      if (entry.parent() != null) builder.parent(built.get(entry.parent()));
      built.put(entry.name(), builder.save(saver, id(entry.name()), helper));
    }
  }
}
