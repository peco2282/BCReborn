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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.robots.*;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.robotics.ai.*;
import com.peco2282.bcreborn.robotics.boards.*;
import com.peco2282.bcreborn.robotics.station.DockingStationPipe;

import java.util.function.Function;

@InitRegister(modId = BCRebornRobotics.MODID)
public interface RoboticsAIType {
    // com.peco2282.bcreborn.robotics.ai
    AIRobotType<AIRobotAttack> ATTACK = create("attack", AIRobotAttack::new);
    AIRobotType<AIRobotBreak> BREAK = create("break", AIRobotBreak::new);
    AIRobotType<AIRobotDeliverRequested> DELIVER_REQUESTED = create("deliver_requested", AIRobotDeliverRequested::new);
    AIRobotType<AIRobotDisposeItems> DISPOSE_ITEMS = create("dispose_items", AIRobotDisposeItems::new);
    AIRobotType<AIRobotFetchAndEquipItemStack> FETCH_AND_EQUIP_ITEM_STACK = create("fetch_and_equip_item_stack", AIRobotFetchAndEquipItemStack::new);
    AIRobotType<AIRobotFetchItem> FETCH_ITEM = create("fetch_item", AIRobotFetchItem::new);
    AIRobotType<AIRobotGoAndLinkToDock> GO_AND_LINK_TO_DOCK = create("go_and_link_to_dock", AIRobotGoAndLinkToDock::new);
    AIRobotType<AIRobotGotoBlock> GOTO_BLOCK = create("goto_block", AIRobotGotoBlock::new);
    AIRobotType<AIRobotGotoSleep> GOTO_SLEEP = create("goto_sleep", AIRobotGotoSleep::new);
    AIRobotType<AIRobotGotoStation> GOTO_STATION = create("goto_station", AIRobotGotoStation::new);
    AIRobotType<AIRobotGotoStationAndLoad> GOTO_STATION_AND_LOAD = create("goto_station_and_load", AIRobotGotoStationAndLoad::new);
    AIRobotType<AIRobotGotoStationAndLoadFluids> GOTO_STATION_AND_LOAD_FLUIDS = create("goto_station_and_load_fluids", AIRobotGotoStationAndLoadFluids::new);
    AIRobotType<AIRobotGotoStationAndUnload> GOTO_STATION_AND_UNLOAD = create("goto_station_and_unload", AIRobotGotoStationAndUnload::new);
    AIRobotType<AIRobotGotoStationAndUnloadFluids> GOTO_STATION_AND_UNLOAD_FLUIDS = create("goto_station_and_unload_fluids", AIRobotGotoStationAndUnloadFluids::new);
    AIRobotType<AIRobotGotoStationToLoad> GOTO_STATION_TO_LOAD = create("goto_station_to_load", AIRobotGotoStationToLoad::new);
    AIRobotType<AIRobotGotoStationToLoadFluids> GOTO_STATION_TO_LOAD_FLUIDS = create("goto_station_to_load_fluids", AIRobotGotoStationToLoadFluids::new);
    AIRobotType<AIRobotGotoStationToUnload> GOTO_STATION_TO_UNLOAD = create("goto_station_to_unload", AIRobotGotoStationToUnload::new);
    AIRobotType<AIRobotGotoStationToUnloadFluids> GOTO_STATION_TO_UNLOAD_FLUIDS = create("goto_station_to_unload_fluids", AIRobotGotoStationToUnloadFluids::new);
    AIRobotType<AIRobotHarvest> HARVEST = create("harvest", AIRobotHarvest::new);
    AIRobotType<AIRobotLoad> LOAD = create("load", AIRobotLoad::new);
    AIRobotType<AIRobotLoadFluids> LOAD_FLUIDS = create("load_fluids", AIRobotLoadFluids::new);
    AIRobotType<AIRobotMain> MAIN = create("main", AIRobotMain::new);
    AIRobotType<AIRobotPlant> PLANT = create("plant", AIRobotPlant::new);
    AIRobotType<AIRobotPumpBlock> PUMP_BLOCK = create("pump_block", AIRobotPumpBlock::new);
    AIRobotType<AIRobotRecharge> RECHARGE = create("recharge", AIRobotRecharge::new);
    AIRobotType<AIRobotSearchAndGotoBlock> SEARCH_AND_GOTO_BLOCK = create("search_and_goto_block", AIRobotSearchAndGotoBlock::new);
    AIRobotType<AIRobotSearchAndGotoStation> SEARCH_AND_GOTO_STATION = create("search_and_goto_station", AIRobotSearchAndGotoStation::new);
    AIRobotType<AIRobotSearchBlock> SEARCH_BLOCK = create("search_block", AIRobotSearchBlock::new);
    AIRobotType<AIRobotSearchEntity> SEARCH_ENTITY = create("search_entity", AIRobotSearchEntity::new);
    AIRobotType<AIRobotSearchRandomGroundBlock> SEARCH_RANDOM_GROUND_BLOCK = create("search_random_ground_block", AIRobotSearchRandomGroundBlock::new);
    AIRobotType<AIRobotSearchStackRequest> SEARCH_STACK_REQUEST = create("search_stack_request", AIRobotSearchStackRequest::new);
    AIRobotType<AIRobotSearchStation> SEARCH_STATION = create("search_station", AIRobotSearchStation::new);
    AIRobotType<AIRobotShutdown> SHUTDOWN = create("shutdown", AIRobotShutdown::new);
    AIRobotType<AIRobotSleep> SLEEP = create("sleep", AIRobotSleep::new);
    AIRobotType<AIRobotStraightMoveTo> STRAIGHT_MOVE_TO = create("straight_move_to", AIRobotStraightMoveTo::new);
    AIRobotType<AIRobotStripesHandler> STRIPES_HANDLER = create("stripes_handler", AIRobotStripesHandler::new);
    AIRobotType<AIRobotUnload> UNLOAD = create("unload", AIRobotUnload::new);
    AIRobotType<AIRobotUnloadFluids> UNLOAD_FLUIDS = create("unload_fluids", AIRobotUnloadFluids::new);
    AIRobotType<AIRobotUseToolOnBlock> USE_TOOL_ON_BLOCK = create("use_tool_on_block", AIRobotUseToolOnBlock::new);

    // end

    // com.peco2282.bcreborn.robotics.boards

    AIRobotType<BoardRobotBomber> BOMBER = create("bomber", BoardRobotBomber::new);
    AIRobotType<BoardRobotBuilder> BUILDER = create("builder", BoardRobotBuilder::new);
    AIRobotType<BoardRobotButcher> BUTCHER = create("butcher", BoardRobotButcher::new);
    AIRobotType<BoardRobotCarrier> CARRIER = create("carrier", BoardRobotCarrier::new);
    AIRobotType<BoardRobotDelivery> DELIVERY = create("delivery", BoardRobotDelivery::new);
    AIRobotType<BoardRobotEmpty> EMPTY = create("empty", BoardRobotEmpty::new);
    AIRobotType<BoardRobotFarmer> FARMER = create("farmer", BoardRobotFarmer::new);
    AIRobotType<BoardRobotFluidCarrier> FLUID_CARRIER = create("fluid_carrier", BoardRobotFluidCarrier::new);
    AIRobotType<BoardRobotHarvester> HARVESTER = create("harvester", BoardRobotHarvester::new);
    AIRobotType<BoardRobotKnight> KNIGHT = create("knight", BoardRobotKnight::new);
    AIRobotType<BoardRobotLeaveCutter> LEAVE_CUTTER = create("leave_cutter", BoardRobotLeaveCutter::new);
    AIRobotType<BoardRobotLumberjack> LUMBERJACK = create("lumberjack", BoardRobotLumberjack::new);
    AIRobotType<BoardRobotMiner> MINER = create("miner", BoardRobotMiner::new);
    AIRobotType<BoardRobotPicker> PICKER = create("picker", BoardRobotPicker::new);
    AIRobotType<BoardRobotPlanter> PLANTER = create("planter", BoardRobotPlanter::new);
    AIRobotType<BoardRobotPump> PUMP = create("pump", BoardRobotPump::new);
    AIRobotType<BoardRobotShovelman> SHOVELMAN = create("shovelman", BoardRobotShovelman::new);
    AIRobotType<BoardRobotStripes> STRIPES = create("stripes", BoardRobotStripes::new);

    DockingStationType<DockingStationPipe> PIPE = RobotManager.registerDockingStationType(BCRebornRobotics.location("pipe"), DockingStationPipe::new);

    static <T extends AIRobot<T>> AIRobotType<T> create(String id, Function<RobotEntityBase, T> type) {
        return RobotManager.registerRobotType(BCRebornRobotics.location(id), type);
    }
}
