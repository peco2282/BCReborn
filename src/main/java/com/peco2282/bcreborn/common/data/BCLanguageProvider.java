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
    addConfig();
  }

  private void addConfig() {
    add("screen.config.title", "BCReborn Configuration");

    // Modules
    addConfigModule("general", "General", "General BCReborn settings");
    addConfigModule("core", "Core", "Core BCReborn settings");
    addConfigModule("builder", "Builder", "Builder settings");
    addConfigModule("energy", "Energy", "Energy and engine settings");
    addConfigModule("factory", "Factory", "Factory block and item settings");
    addConfigModule("robotics", "Robotics", "Robotics and station settings");
    addConfigModule("silicon", "Silicon", "Silicon, chipsets and tables settings");
    addConfigModule("transport", "Transport", "Pipe and transport settings");

    // General
    addConfigSection("general", "display", "Display");
    addConfigEntry("general", "colorBlindMode", "Color Blind Mode", "Should I enable colorblind mode?");
    addConfigEntry("general", "hideFluidValues", "Hide Fluid Values", "Should all fluid values (mB, mB/t) be hidden?");
    addConfigEntry("general", "hidePowerValues", "Hide Power Values", "Should all power values (RF, RF/t) be hidden?");
    addConfigSection("general", "debug", "Debug");
    addConfigEntry("general", "printFacadeList", "Print Facade List", "Print a list of all registered facades.");
    addConfigEntry("general", "printBlueprintSchematicList", "Print Blueprint Schematic List", "Print a list of all registered blueprint schematics.");

    // Core
    addConfigSection("core", "general", "General");
    addConfigEntry("core", "itemLifespan", "Item Lifespan", "How long, in seconds, should items stay on the ground? (Vanilla = 300, default = 60)");
    addConfigEntry("core", "markerRange", "Marker Range", "Set the maximum marker range.");
    addConfigEntry("core", "builderMaxIterationsPerItemFactor", "Builder Max Iterations Per Item Factor", "Lower this number if BuildCraft builders/fillers are causing TPS lag. Raise it if you think they are being too slow.");
    addConfigEntry("core", "canEnginesExplode", "Can Engines Explode", "Should engines explode upon overheat?");
    addConfigEntry("core", "useServerDataOnClient", "Use Server Data On Client", "Allows BCReborn to use the integrated server's data on the client on singleplayer worlds. Disable if you're getting the odd crash caused by it.");
    addConfigEntry("core", "updateCheck", "Update Check", "Should I check the BCReborn version on startup?");
    addConfigEntry("core", "miningBreaksPlayerProtectedBlocks", "Mining Breaks Player Protected Blocks", "Should BCReborn miners be allowed to break blocks using player-specific protection?");
    addConfigSection("core", "network", "Network");
    addConfigEntry("core", "updateFactor", "Update Factor", "How often, in ticks, should network update packets be sent? Increasing this might help network performance.");
    addConfigEntry("core", "longUpdateFactor", "Long Update Factor", "How often, in ticks, should full network sync packets be sent? Increasing this might help network performance.");
    addConfigSection("core", "power", "Power");
    addConfigEntry("core", "miningUsageMultiplier", "Mining Usage Multiplier", "What should the multiplier of all mining-related power usage be?");

    // Builders
    addConfigSection("builders", "general", "General");
    addConfigEntry("builders", "dropBrokenBlocks", "Drop Broken Blocks", "Should the builder and filler drop the cleared blocks?");
    addConfigSection("builders", "quarry", "Quarry");
    addConfigEntry("builders", "quarryDoChunkLoading", "Quarry Do Chunk Loading", "Should the quarry keep the chunks it is working on loaded?");
    addConfigEntry("builders", "quarryOneTimeUse", "Quarry One Time Use", "Should the quarry only be usable once after placing?");
    addConfigEntry("builders", "miningDepth", "Mining Depth", "Maximum mining depth for the mining well.");
    addConfigSection("builders", "blueprints", "Blueprints");
    addConfigEntry("builders", "serverDatabaseDirectory", "Server Database Directory", "Location for the server blueprint database (used by the Electronic Library).");
    addConfigEntry("builders", "clientDatabaseDirectory", "Client Database Directory", "Location for the client blueprint database (used by the Electronic Library).");

    // Energy
    addConfigSection("energy", "general", "General");
    addConfigEntry("energy", "fuelFuelCombustion", "Fuel Fuel Combustion", "The amount of energy that fuel produces when combusted.");
    addConfigEntry("energy", "fuelFuelCombustionEnergyOutput", "Fuel Fuel Combustion Energy Output", "The amount of energy that fuel produces per tick when combusted.");
    addConfigEntry("energy", "fuelOilCombustion", "Fuel Oil Combustion", "The amount of energy that oil produces when combusted.");
    addConfigEntry("energy", "fuelOilCombustionEnergyOutput", "Fuel Oil Combustion Energy Output", "The amount of energy that oil produces per tick when combusted.");
    addConfigEntry("energy", "canOilBurn", "Can Oil Burn", "Should oil be burnable?");
    addConfigEntry("energy", "isOilDense", "Is Oil Dense", "Should oil be dense?");
    addConfigEntry("energy", "pumpsConsumeWater", "Pumps Consume Water", "Should pumps consume water?");
    addConfigEntry("energy", "pumpsNeedRealPower", "Pumps Need Real Power", "Should pumps need real power?");
    addConfigEntry("energy", "pumpDimensionControl", "Pump Dimension Control", "Allows admins to whitelist or blacklist pumping of specific fluids in specific dimensions.");
    addConfigSection("energy", "worldgen", "World Generation");
    addConfigEntry("energy", "worldgenEnable", "Enable", "Should BCReborn generate anything in the world?");
    addConfigEntry("energy", "generateWaterSprings", "Generate Water Springs", "Should BCReborn generate water springs?");
    addConfigEntry("energy", "oilWellGenerationRate", "Oil Well Generation Rate", "How high should be the probability of an oil well generating?");
    addConfigEntry("energy", "spawnOilSprings", "Spawn Oil Springs", "Should I spawn oil springs?");

    // Factory
    addConfigSection("factory", "blocks", "Blocks");
    addConfigEntry("factory", "autoWorkbenchBlock", "Auto Workbench Block", "Should the auto workbench be enabled?");
    addConfigEntry("factory", "filteredBufferBlock", "Filtered Buffer Block", "Should the filtered buffer be enabled?");
    addConfigEntry("factory", "packagerBlock", "Packager Block", "Should the packager be enabled?");
    addConfigEntry("factory", "requester", "Requester Block", "Should the requester be enabled?");
    addConfigSection("factory", "items", "Items");
    addConfigEntry("factory", "blueprintItem", "Blueprint Item", "Should the blueprint item be enabled?");
    addConfigEntry("factory", "templateItem", "Template Item", "Should the template item be enabled?");
    addConfigEntry("factory", "list", "List Item", "Should the list item be enabled?");
    addConfigEntry("factory", "mapLocation", "Map Location Item", "Should the map location item be enabled?");
    addConfigEntry("factory", "packageItem", "Package Item", "Should the package item be enabled?");

    // Robotics
    addConfigSection("robotics", "items", "Items");
    addConfigEntry("robotics", "robot", "Robot Item", "Should the robot item be enabled?");
    addConfigEntry("robotics", "robotStation", "Robot Station Item", "Should the robot station item be enabled?");
    addConfigSection("robotics", "blocks", "Blocks");
    addConfigEntry("robotics", "architectBlock", "Architect Block", "Should the architect block be enabled?");
    addConfigEntry("robotics", "constructionMarkerBlock", "Construction Marker Block", "Should the construction marker block be enabled?");

    // Silicon
    addConfigSection("silicon", "items", "Items");
    addConfigEntry("silicon", "redstoneChipset", "Redstone Chipset", "Should the redstone chipset be enabled?");
    addConfigEntry("silicon", "redstoneCrystal", "Redstone Crystal", "Should the redstone crystal be enabled?");
    addConfigEntry("silicon", "redstoneBoard", "Redstone Board", "Should the redstone board be enabled?");
    addConfigEntry("silicon", "gateCopier", "Gate Copier", "Should the gate copier be enabled?");
    addConfigEntry("silicon", "pipeGate", "Pipe Gate", "Should the pipe gate be enabled?");
    addConfigEntry("silicon", "pipeLens", "Pipe Lens", "Should the pipe lens be enabled?");
    addConfigEntry("silicon", "pipeWire", "Pipe Wire", "Should the pipe wire be enabled?");
    addConfigSection("silicon", "blocks", "Blocks");
    addConfigEntry("silicon", "laserBlock", "Laser Block", "Should the laser block be enabled?");
    addConfigEntry("silicon", "laserTableBlock", "Laser Table Block", "Should the laser table block be enabled?");
    addConfigEntry("silicon", "libraryBlock", "Library Block", "Should the library block be enabled?");
    addConfigEntry("silicon", "zonePlan", "Zone Plan Block", "Should the zone plan block be enabled?");
    addConfigSection("silicon", "power", "Power");
    addConfigEntry("silicon", "chipsetCostMultiplier", "Chipset Cost Multiplier", "The cost multiplier for Chipsets");

    // Transport
    addConfigSection("transport", "general", "General");
    addConfigEntry("transport", "baseFluidRate", "Base Fluid Rate", "The base rate at which fluids flow through pipes.");
    addConfigEntry("transport", "pipeHardness", "Pipe Hardness", "How hard should pipes be to break?");
    addConfigEntry("transport", "gateCostMultiplier", "Gate Cost Multiplier", "The cost multiplier for Gates");
    addConfigEntry("transport", "kinesisPowerLossOnTravel", "Kinesis Power Loss On Travel", "Should kinesis pipes lose power on travel?");
    addConfigSection("transport", "facade", "Facade");
    addConfigEntry("transport", "facadeBlacklistAsWhitelist", "Facade Blacklist As Whitelist", "Should the facade blacklist be treated as a whitelist?");
    addConfigEntry("transport", "facadeNoLaserRecipe", "Facade No Laser Recipe", "Should the facade not have a laser recipe?");
    addConfigEntry("transport", "facadeShowAllInCreative", "Facade Show All In Creative", "Should all facades be shown in creative mode?");
    addConfigEntry("transport", "slimeballWaterproofRecipe", "Slimeball Waterproof Recipe", "Should slimeballs be used for waterproof recipes?");
    addConfigSection("transport", "items", "Pipe Items");
    addConfigEntry("transport", "pipeItemsWood", "Wooden Item Pipe", "Should the wooden item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsCobblestone", "Cobblestone Item Pipe", "Should the cobblestone item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsStone", "Stone Item Pipe", "Should the stone item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsIron", "Iron Item Pipe", "Should the iron item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsGold", "Gold Item Pipe", "Should the gold item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsDiamond", "Diamond Item Pipe", "Should the diamond item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsEmerald", "Emerald Item Pipe", "Should the emerald item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsObsidian", "Obsidian Item Pipe", "Should the obsidian item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsSandstone", "Sandstone Item Pipe", "Should the sandstone item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsVoid", "Void Item Pipe", "Should the void item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsClay", "Clay Item Pipe", "Should the clay item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsQuartz", "Quartz Item Pipe", "Should the quartz item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsLapis", "Lapis Item Pipe", "Should the lapis item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsDaizuli", "Daizuli Item Pipe", "Should the daizuli item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsEmzuli", "Emzuli Item Pipe", "Should the emzuli item pipe be enabled?");
    addConfigEntry("transport", "pipeItemsStripes", "Stripes Item Pipe", "Should the stripes item pipe be enabled?");
    addConfigSection("transport", "fluids", "Pipe Fluids");
    addConfigEntry("transport", "pipeFluidsWood", "Wooden Fluid Pipe", "Should the wooden fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsCobblestone", "Cobblestone Fluid Pipe", "Should the cobblestone fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsStone", "Stone Fluid Pipe", "Should the stone fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsIron", "Iron Fluid Pipe", "Should the iron fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsGold", "Gold Fluid Pipe", "Should the gold fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsDiamond", "Diamond Fluid Pipe", "Should the diamond fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsEmerald", "Emerald Fluid Pipe", "Should the emerald fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsSandstone", "Sandstone Fluid Pipe", "Should the sandstone fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsVoid", "Void Fluid Pipe", "Should the void fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsClay", "Clay Fluid Pipe", "Should the clay fluid pipe be enabled?");
    addConfigEntry("transport", "pipeFluidsQuartz", "Quartz Fluid Pipe", "Should the quartz fluid pipe be enabled?");
    addConfigSection("transport", "power", "Pipe Power");
    addConfigEntry("transport", "pipePowerWood", "Wooden Power Pipe", "Should the wooden power pipe be enabled?");
    addConfigEntry("transport", "pipePowerCobblestone", "Cobblestone Power Pipe", "Should the cobblestone power pipe be enabled?");
    addConfigEntry("transport", "pipePowerStone", "Stone Power Pipe", "Should the stone power pipe be enabled?");
    addConfigEntry("transport", "pipePowerIron", "Iron Power Pipe", "Should the iron power pipe be enabled?");
    addConfigEntry("transport", "pipePowerGold", "Gold Power Pipe", "Should the gold power pipe be enabled?");
    addConfigEntry("transport", "pipePowerDiamond", "Diamond Power Pipe", "Should the diamond power pipe be enabled?");
    addConfigEntry("transport", "pipePowerEmerald", "Emerald Power Pipe", "Should the emerald power pipe be enabled?");
    addConfigEntry("transport", "pipePowerSandstone", "Sandstone Power Pipe", "Should the sandstone power pipe be enabled?");
    addConfigEntry("transport", "pipePowerQuartz", "Quartz Power Pipe", "Should the quartz power pipe be enabled?");
    addConfigSection("transport", "structure", "Pipe Structure");
    addConfigEntry("transport", "pipeStructureCobblestone", "Cobblestone Structure Pipe", "Should the cobblestone structure pipe be enabled?");
  }

  private void addConfigModule(String module, String title, String description) {
    add("screen.config.module." + module + ".title", title);
    add("screen.config.module." + module + ".description", description);
  }

  private void addConfigSection(String module, String section, String title) {
    add("screen.config.section." + module + "." + section + ".title", title);
  }

  private void addConfigEntry(String module, String entry, String title, String tooltip) {
    add("screen.config.entry." + module + "." + entry + ".title", title);
    add("screen.config.entry." + module + "." + entry + ".tooltip", tooltip);
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
    addItem(CoreItems.DEBUGGER, "Debugger");
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
