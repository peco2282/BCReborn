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
import com.peco2282.bcreborn.builders.BuildersMenuTypes;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.energy.EnergyMenuTypes;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.FactoryMenuTypes;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.robotics.RoboticsMenuTypes;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.TransportMenuTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class BCLanguageProvider extends LanguageProvider {
  private static final Map<String, String> EN_US = new HashMap<>();

  public BCLanguageProvider(PackOutput output, String modid, String locale) {
    super(output, modid, locale);
  }

  public static String snake2Title(String value) {
    if (value.isBlank()) {
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

  @Override
  protected void addTranslations() {
    add("itemGroup.bcreborn", "BCReborn");

    // Block and Item Names
    addBuilders();
    addCore();
    addEnergy();
    addFactory();
    addRobotics();
    addSilicon();
    addTransport();

    // Original Translation
    addGates();
    addScreen();
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

    addMenu(BuildersMenuTypes.ARCHITECT, "Architect");
    addMenu(BuildersMenuTypes.BUILDER, "Builder");
    addMenu(BuildersMenuTypes.BLUEPRINT_LIBRARY, "Electronic Library");
    addMenu(BuildersMenuTypes.FILLER, "Filler");
  }

  private void addCore() {
    addBlock(CoreBlocks.WOODEN_ENGINE, "Wooden Engine");
    addBlock(CoreBlocks.PATH_MARKER, "Path Marker");
    addBlock(CoreBlocks.SPRING, "Spring");
    addBlock(CoreBlocks.BLUE_MARKER, "Blue Marker");
    addBlock(CoreBlocks.BUILD_TOOL, "Build Tool");

    addItem(CoreItems.WRENCH, "Wrench");
    addItem(CoreItems.WOODEN_GEAR, "Wooden Gear");
    addItem(CoreItems.STONE_GEAR, "Stone Gear");
    addItem(CoreItems.IRON_GEAR, "Iron Gear");
    addItem(CoreItems.GOLD_GEAR, "Golden Gear");
    addItem(CoreItems.DIAMOND_GEAR, "Diamond Gear");
    addItem(CoreItems.BUILD_TOOL_BOX, "Build Tool Box");
  }

  private void addEnergy() {
    addBlock(EnergyBlocks.STONE_ENGINE, "Stone Engine");
    addBlock(EnergyBlocks.IRON_ENGINE, "Iron Engine");
    addBlock(EnergyBlocks.CREATIVE_ENGINE, "Creative Engine");
    addBlock(EnergyFluids.FUEL_BLOCK, "Fuel Block");
    addBlock(EnergyFluids.OIL_BLOCK, "Oil Block");

    addItem(EnergyFluids.FUEL_BUCKET, "Fuel Bucket");
    addItem(EnergyFluids.OIL_BUCKET, "Oil Bucket");

    addMenu(EnergyMenuTypes.STONE_ENGINE, "Stone Engine");
    addMenu(EnergyMenuTypes.IRON_ENGINE, "Iron Engine");
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

    addMenu(FactoryMenuTypes.AUTO_WORKBENCH, "Auto Workbench");
    addMenu(FactoryMenuTypes.HOPPER, "Hopper");
    addMenu(FactoryMenuTypes.REFINERY, "Refinery");
  }

  private void addRobotics() {
    addBlock(RoboticsBlocks.REQUESTER, "Requester");
    addBlock(RoboticsBlocks.ZONE_PLAN, "Zone Plan");

    addItem(RoboticsItems.ROBOT, "Robot");
    addItem(RoboticsItems.ROBOT_STATION, "Robot Station");
    RoboticsItems.REDSTONE_BOARDS.getMap().forEach((name, item) -> addItem(item, snake2Title(name)));

    addMenu(RoboticsMenuTypes.REQUESTER, "Requester");
    addMenu(RoboticsMenuTypes.ZONE_PLAN, "Zone Plan");
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

    addMenu(SiliconMenuTypes.ADVANCED_CRAFTING_TABLE, "Advanced Crafting Table");
    addMenu(SiliconMenuTypes.ASSEMBLY_TABLE, "Assembly Table");
    addMenu(SiliconMenuTypes.CHARGING_TABLE, "Charging Table");
    addMenu(SiliconMenuTypes.INTEGRATION_TABLE, "Integration Table");
    addMenu(SiliconMenuTypes.PACKAGER, "Packager");
    addMenu(SiliconMenuTypes.PROGRAMMING_TABLE, "Programming Table");
    addMenu(SiliconMenuTypes.STAMPING_TABLE, "Stamping Table");
  }

  private void addTransport() {
    TransportBlocks.getPipeList().forEach(block -> {
      var key = block.getId().getPath().replace("pipe_", "");

      addBlock(block, snake2Title(key) + " Pipe");
    });

    var res = TransportItems.FACADE.getId();
    var name = res.getNamespace() + "." + res.getPath();
    addItem(TransportItems.FACADE, "Facade");
    add("item." + name + ".facade_basic_format", "%s Facade");
    add("item." + name + ".state_hollow_format", "Hollow %s");
    add("item." + name + ".name", "Phased Facade");
    add("item." + name + ".state_transparent", "Transparent");
    add("item." + name + ".state", "%s: %s");
    add("item." + name + ".state_default", "Default: %s");

    addMenu(TransportMenuTypes.DIAMOND_PIPE_MENU, "Diamond Pipe");
    addMenu(TransportMenuTypes.EMERALD_FLUID_PIPE_MENU, "Emerald Fluid Pipe");
    addMenu(TransportMenuTypes.EMERALD_PIPE_MENU, "Emerald Pipe");
    addMenu(TransportMenuTypes.EMZULI_PIPE_MENU, "Emzuli Pipe");
    addMenu(TransportMenuTypes.FILTERED_BUFFER_MENU, "Filtered Buffer");
    addMenu(TransportMenuTypes.GATE_INTERFACE_MENU, "Gate Interface");
  }

  private void addGates() {
    add("gate.action.extraction", "%s Extraction Preset");
    add("gate.action.pipe.item.color", "Paint Items %s");
    add("gate.action.machine.on", "On");
    add("gate.action.machine.off", "Off");
    add("gate.action.machine.loop", "Loop");
    add("gate.action.pulsar.constant", "Energy Pulsar");
    add("gate.action.pulsar.single", "Single Energy Pulse");
    add("gate.action.pipe.wire", "%s Pipe Signal");
    add("gate.action.redstone.signal", "Redstone Signal");
    add("gate.action.robot.goto_station", "Goto Station");
    add("gate.action.pipe.valve.open", "Open");
    add("gate.action.pipe.valve.input_only", "Input Only");
    add("gate.action.pipe.valve.output_only", "Output Only");
    add("gate.action.pipe.valve.closed", "Closed");
    add("gate.action.station.provide_items", "Provide Items");
    add("gate.action.station.accept_items", "Accept Items");
    add("gate.action.station.request_items", "Request Items");
    add("gate.action.station.drop_items_in_pipe", "Drop Items In Pipe");
    add("gate.action.station.allow_craft", "Allow Craft");
    add("gate.action.station.provide_machine_request", "Request Needed Items");
    add("gate.action.station.accept_fluids", "Accept Fluids");
    add("gate.action.station.povide_fluids", "Provide Fluids");
    add("gate.action.robot.work_in_area", "Work in Area");
    add("gate.action.robot.load_unload_area", "Load/Unload in Area");
    add("gate.action.robot.wakeup", "Wake Up");
    add("gate.action.station.forbid_robot", "Forbid Robot");
    add("gate.action.station.force_robot", "Force Robot");
    add("gate.action.robot.filter", "Filter");
    add("gate.action.robot.filter_tool", "Filter Tool");

    add("gate.expansion.fader", "Redstone Fader");
    add("gate.expansion.pulsar", "Autarchic Pulsar");
    add("gate.expansion.timer", "Clock Timer");
    add("gate.expansion.light_sensor", "Light Sensor");
    add("gate.expansion.rtc", "Real-Time Clock");
    add("gate.expansion.biological_sensor", "Biological Sensor");

    add("gate.logic.and", "AND");
    add("gate.logic.or", "OR");

    add("gate.material.iron", "Iron");
    add("gate.material.gold", "Gold");
    add("gate.material.diamond", "Diamond");
    add("gate.material.emerald", "Emerald");
    add("gate.material.quartz", "Quartz");

    add("gate.name", "%s %s Gate");
    add("gate.name.basic", "Basic Gate");

    add("gate.parameter.redstone.gateSideOnly", "Gate Side Only");

    add("gate.trigger.engine.blue", "Engine Blue");
    add("gate.trigger.engine.green", "Engine Green");
    add("gate.trigger.engine.yellow", "Engine Yellow");
    add("gate.trigger.engine.red", "Engine Red");
    add("gate.trigger.engine.overheat", "Engine Overheat");
    add("gate.trigger.fluid.empty", "Tank Empty");
    add("gate.trigger.fluid.contains", "Fluid in Tank");
    add("gate.trigger.fluid.space", "Space for Fluid");
    add("gate.trigger.fluid.full", "Tank Full");
    add("gate.trigger.fluidlevel.below", "Contains < %d%%");
    add("gate.trigger.inventory.empty", "Inventory Empty");
    add("gate.trigger.inventory.contains", "Items in Inventory");
    add("gate.trigger.inventory.space", "Space in Inventory");
    add("gate.trigger.inventory.full", "Inventory Full");
    add("gate.trigger.inventorylevel.below", "Contains < %d%%");
    add("gate.trigger.machine.done", "Work Done");
    add("gate.trigger.machine.scheduled", "Has Work");
    add("gate.trigger.redstone.input.active", "Redstone Signal On");
    add("gate.trigger.redstone.input.inactive", "Redstone Signal Off");
    add("gate.trigger.redstone.input.level", "Redstone Level %d");
    add("gate.trigger.pipe.empty", "Pipe Empty");
    add("gate.trigger.pipe.containsItems", "Items Traversing");
    add("gate.trigger.pipe.containsFluids", "Fluid Traversing");
    add("gate.trigger.pipe.containsEnergy", "Power Traversing");
    add("gate.trigger.pipe.requestsEnergy", "Power Requested");
    add("gate.trigger.pipe.tooMuchEnergy", "Power Overloaded");
    add("gate.trigger.pipe.wire.active", "%s Pipe Signal On");
    add("gate.trigger.pipe.wire.inactive", "%s Pipe Signal Off");
    add("gate.trigger.timer", "%s Sec Timer");
    add("gate.trigger.robot.sleep", "Sleep");
    add("gate.trigger.robot.in.station", "Robot In Station");
    add("gate.trigger.robot.linked", "Station Linked");
    add("gate.trigger.robot.reserved", "Station Reserved");
    add("gate.trigger.machine.energyStored.high", "High Energy Stored");
    add("gate.trigger.machine.energyStored.low", "Low Energy Stored");
    add("gate.trigger.machine.energyStored.below25", "Energy < 25%");
    add("gate.trigger.machine.energyStored.below50", "Energy < 50%");
    add("gate.trigger.machine.energyStored.below75", "Energy < 75%");
    add("gate.trigger.light.bright", "Bright");
    add("gate.trigger.light.dark", "Dark");
    add("gate.trigger.time.0", "Night");
    add("gate.trigger.time.6", "Morning");
    add("gate.trigger.time.12", "Afternoon");
    add("gate.trigger.time.18", "Evening");
    add("gate.trigger.fuelLevelBelow", "Fuel level below %d%%");
    add("gate.trigger.coolantLevelBelow", "Coolant level below %d%%");
  }

  private void addScreen() {
    add("screen.fluidtank.empty", "Empty");

    add("screen.building.resources", "Building Resources");
    add("screen.building.fluids", "Fluid Tanks");
    add("screen.del", "Del");
    add("screen.filling.resources", "Filling Resources");
    add("screen.inventory", "Inventory");
    add("screen.lock", "Lock");
    add("screen.needed", "Needed");
    add("screen.unlock", "Unlock");
    add("screen.energy", "Energy");
    add("screen.currentOutput", "Current Output");
    add("screen.stored", "Stored");
    add("screen.heat", "Heat");
    add("screen.assemblyRate", "Energy Rate");
    add("screen.assemblyCurrentRequired", "Energy Required");
    add("screen.clickcraft", "-Click to Craft-");
    add("screen.list.metadata", "Accept Variations");
    add("screen.list.oredict", "Accept Equivalents");
    add("screen.pipes.emerald.title", "Filters");
    add("screen.pipes.emerald.blocking", "Blocking");
    add("screen.pipes.emerald.blocking.tip", "Extraction is blocked if one element in filter is missing");
    add("screen.pipes.emerald.nonblocking", "Non Blocking");
    add("screen.pipes.emerald.nonblocking.tip", "Extraction continues with the next element in filter if one element is missing");
    add("screen.pipes.emzuli.title", "Extraction Presets");
    add("screen.pipes.emzuli.paint", "Paint Items %s");
    add("screen.pipes.emzuli.nopaint", "Don't Paint Items");
  }

  private void addMenu(RegistryObject<? extends MenuType<? extends AbstractContainerMenu>> menuType, String name) {
    var res = menuType.getId();
    add("menu." + res.getNamespace() + "." + res.getPath(), name);
  }
}
