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
package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersItems;
import com.peco2282.bcreborn.core.BlocksCore;
import com.peco2282.bcreborn.core.ItemsCore;
import com.peco2282.bcreborn.energy.BlocksEnergy;
import com.peco2282.bcreborn.energy.FluidsEnergy;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.transport.BlocksTransport;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.HashMap;
import java.util.Map;

public class BCLanguageProvider extends LanguageProvider {
  private static final Map<String, String> EN_US = new HashMap<>();

  public BCLanguageProvider(PackOutput output, String modid, String locale) {
    super(output, modid, locale);
  }

  @Override
  protected void addTranslations() {
    add("itemGroup.bcreborn", "BCReborn");

    addBuilders();
    addCore();
    addEnergy();
    addFactory();
    addRobotics();
    addSilicon();
    addTransport();
  }

  private void addBuilders() {
    addBlock(BuildersBlock.ARCHITECT, "Architect");
    addBlock(BuildersBlock.BUILDER, "Builder");
    addBlock(BuildersBlock.FRAME, "Frame");
    addBlock(BuildersBlock.CONSTRUCTION_MARKER, "Construction Marker");
    addBlock(BuildersBlock.BLUEPRINT_LIBRARY, "Blueprint Library");
    addBlock(BuildersBlock.QUARRY, "Quarry");
    addBlock(BuildersBlock.FILLER, "Filler");

    addItem(BuildersItems.BLUEPRINT_STANDARD, "Blueprint Standard");
    addItem(BuildersItems.BLUEPRINT_TEMPLATE, "Blueprint Template");
  }

  private void addCore() {
    addBlock(BlocksCore.WOODEN_ENGINE, "Wooden Engine");
    addBlock(BlocksCore.PATH_MARKER, "Path Marker");
    addBlock(BlocksCore.SPRING, "Spring");
    addBlock(BlocksCore.BLUE_MARKER, "Blue Marker");
    addBlock(BlocksCore.BUILD_TOOL, "Build Tool");

    addItem(ItemsCore.WRENCH, "Wrench");
    addItem(ItemsCore.WOODEN_GEAR, "Wooden Gear");
    addItem(ItemsCore.STONE_GEAR, "Stone Gear");
    addItem(ItemsCore.IRON_GEAR, "Iron Gear");
    addItem(ItemsCore.GOLD_GEAR, "Golden Gear");
    addItem(ItemsCore.DIAMOND_GEAR, "Diamond Gear");
    addItem(ItemsCore.BUILD_TOOL_BOX, "Build Tool Box");
  }

  private void addEnergy() {
    addBlock(BlocksEnergy.STONE_ENGINE, "Stone Engine");
    addBlock(BlocksEnergy.IRON_ENGINE, "Iron Engine");
    addBlock(BlocksEnergy.CREATIVE_ENGINE, "Creative Engine");
    addBlock(FluidsEnergy.FUEL_BLOCK, "Fuel Block");
    addBlock(FluidsEnergy.OIL_BLOCK, "Oil Block");

    addItem(FluidsEnergy.FUEL_BUCKET, "Fuel Bucket");
    addItem(FluidsEnergy.OIL_BUCKET, "Oil Bucket");
  }

  private void addFactory() {
    addBlock(FactoryBlocks.AUTO_WORKBENCH, "Auto Workbench");
    addBlock(FactoryBlocks.HOPPER, "Hopper");
    addBlock(FactoryBlocks.FLOOD_GATE, "Flood Gate");
    addBlock(FactoryBlocks.MINING_WELL, "Mining Well");
    addBlock(FactoryBlocks.PLAIN_PIPE, "Plain Pipe");
    addBlock(FactoryBlocks.PUMP, "Pump");
    addBlock(FactoryBlocks.REFINERY, "Refinery");
    addBlock(FactoryBlocks.TANK, "Tank");
  }


  private void addRobotics() {
    addBlock(RoboticsBlocks.REQUESTER, "Requester");
    addBlock(RoboticsBlocks.ZONE_PLAN, "Zone Plan");

    addItem(RoboticsItems.ROBOT, "Robot");
    addItem(RoboticsItems.ROBOT_STATION, "Robot Station");
    RoboticsItems.REDSTONE_BOARDS.getMap().forEach((name, item) -> addItem(item, snake2Title(name)));
  }


  private void addSilicon() {
    addBlock(SiliconBlocks.LASER, "Laser");
    addBlock(SiliconBlocks.ASSEMBLY_TABLE, "Assembly Table");
    addBlock(SiliconBlocks.ADVANCED_CRAFTING_TABLE, "Advanced Crafting Table");
    addBlock(SiliconBlocks.INTEGRATION_TABLE, "Integration Table");
    addBlock(SiliconBlocks.CHARGING_TABLE, "Charging Table");
    addBlock(SiliconBlocks.PROGRAMMING_TABLE, "Programming Table");
    addBlock(SiliconBlocks.STAMPING_TABLE, "Stamping Table");
    addBlock(SiliconBlocks.PACKAGER, "Packager");

    addItem(SiliconItems.REDSTONE_CHIPSET, "Redstone Chipset");
    addItem(SiliconItems.IRON_CHIPSET, "Iron Chipset");
    addItem(SiliconItems.GOLD_CHIPSET, "Gold Chipset");
    addItem(SiliconItems.DIAMOND_CHIPSET, "Diamond Chipset");
    addItem(SiliconItems.PULSATING_CHIPSET, "Pulsating Chipset");
    addItem(SiliconItems.QUARTZ_CHIPSET, "Quartz Chipset");
    addItem(SiliconItems.COMP_CHIPSET, "Comp Chipset");
    addItem(SiliconItems.EMERALD_CHIPSET, "Emerald Chipset");
    addItem(SiliconItems.PACKAGE_ITEM, "Package");
  }

  private void addTransport() {
    BlocksTransport.getPipeList().forEach(block -> {
      var key = block.getId().getPath().replace("pipe_", "");

      addBlock(block, snake2Title(key) + " Pipe");
    });
  }

  public static String snake2Title(String value) {
    if (value == null || value.isBlank()) {
      return value;
    }
    StringBuilder result = new StringBuilder();
    for (String part : value.split("_")) {
      if (part.isEmpty()) {
        continue;
      }

      if (!result.isEmpty()) {
        result.append(' ');
      }

      result
        .append(Character.toUpperCase(part.charAt(0)))
        .append(part.substring(1).toLowerCase());
    }

    return result.toString();
  }
}
