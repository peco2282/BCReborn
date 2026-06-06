package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.robotics.boards.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornRobotics.MODID)
public class RoboticsRedstoneRobots {
  private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();

  public static final RegistryObject<RedstoneBoardRobotEmptyNBT> EMPTY = register("empty", RedstoneBoardRobotEmptyNBT::new);

  public static final RegistryObject<BCBoardNBT> ROBOT_PICKER = register("robot_picker", () -> new BCBoardNBT(loc("robot_picker"), "picker", BoardRobotPicker::new, "green", RedstoneBoardNBT.COST_LOW));
  public static final RegistryObject<BCBoardNBT> ROBOT_CARRIER = register("robot_carrier", () -> new BCBoardNBT(loc("robot_carrier"), "carrier", BoardRobotCarrier::new, "green", RedstoneBoardNBT.COST_LOW));
  public static final RegistryObject<BCBoardNBT> ROBOT_FLUID_CARRIER = register("robot_fluid_carrier", () -> new BCBoardNBT(loc("robot_fluid_carrier"), "fluid_carrier", BoardRobotFluidCarrier::new, "green", RedstoneBoardNBT.COST_LOW));

  public static final RegistryObject<BCBoardNBT> ROBOT_LUMBERJACK = register("robot_lumberjack", () -> new BCBoardNBT(loc("robot_lumberjack"), "lumberjack", BoardRobotLumberjack::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_HARVESTER = register("robot_harvester", () -> new BCBoardNBT(loc("robot_harvester"), "harvester", BoardRobotHarvester::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_MINER = register("robot_miner", () -> new BCBoardNBT(loc("robot_miner"), "miner", BoardRobotMiner::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_PLANTER = register("robot_planter", () -> new BCBoardNBT(loc("robot_planter"), "planter", BoardRobotPlanter::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_FARMER = register("robot_farmer", () -> new BCBoardNBT(loc("robot_farmer"), "farmer", BoardRobotFarmer::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_LEAVE_CUTTER = register("robot_leave_cutter", () -> new BCBoardNBT(loc("robot_leave_cutter"), "leave_cutter", BoardRobotLeaveCutter::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_BUTCHER = register("robot_butcher", () -> new BCBoardNBT(loc("robot_butcher"), "butcher", BoardRobotButcher::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_SHOVELMAN = register("robot_shovelman", () -> new BCBoardNBT(loc("robot_shovelman"), "shovelman", BoardRobotShovelman::new, "blue", RedstoneBoardNBT.COST_MEDIUM));
  public static final RegistryObject<BCBoardNBT> ROBOT_PUMP = register("robot_pump", () -> new BCBoardNBT(loc("robot_pump"), "pump", BoardRobotPump::new, "blue", RedstoneBoardNBT.COST_MEDIUM));

  public static final RegistryObject<BCBoardNBT> ROBOT_DELIVERY = register("robot_delivery", () -> new BCBoardNBT(loc("robot_delivery"), "delivery", BoardRobotDelivery::new, "green", RedstoneBoardNBT.COST_HIGH));
  public static final RegistryObject<BCBoardNBT> ROBOT_KNIGHT = register("robot_knight", () -> new BCBoardNBT(loc("robot_knight"), "knight", BoardRobotKnight::new, "red", RedstoneBoardNBT.COST_HIGH));
  public static final RegistryObject<BCBoardNBT> ROBOT_BOMBER = register("robot_bomber", () -> new BCBoardNBT(loc("robot_bomber"), "bomber", BoardRobotBomber::new, "red", RedstoneBoardNBT.COST_HIGH));
  public static final RegistryObject<BCBoardNBT> ROBOT_STRIPES = register("robot_stripes", () -> new BCBoardNBT(loc("robot_stripes"), "stripes", BoardRobotStripes::new, "yellow", RedstoneBoardNBT.COST_HIGH));

  public static final RegistryObject<BCBoardNBT> ROBOT_BUILDER = register("robot_builder", () -> new BCBoardNBT(loc("robot_builder"), "builder", BoardRobotBuilder::new, "yellow", RedstoneBoardNBT.COST_VERY_HIGH));

  private static ResourceLocation loc(String name) {
    return BCRebornRobotics.location(name);
  }

  private static <T, R extends RedstoneBoardNBT<T>> RegistryObject<R> register(String name, Supplier<R> supplier) {
    return REGISTRY.registerRedstoneBoard(name, supplier);
  }
}
