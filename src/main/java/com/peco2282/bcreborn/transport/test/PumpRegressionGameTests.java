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
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.energy.fluids.SingleUseTank;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.PumpBlockEntity;
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(BCRebornTransport.MODID)
@PrefixGameTestTemplate(false)
public class PumpRegressionGameTests {
  private static final BlockPos MACHINE = new BlockPos(1, 2, 1);
  private static final BlockPos SOURCE = MACHINE.below();

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void freshPumpCollectsItsFirstSource(GameTestHelper helper) {
    PumpBlockEntity pump = setup(helper);
    pump.getBattery().setEnergy(100);
    for (int i = 0; i < 100 && pump.tank.isEmpty(); i++) tick(helper, pump);
    check(helper, pump.tank.getFluidAmount() == 1000 && pump.tank.getFluidType() == Fluids.WATER,
      "A fresh pump must find and collect water below itself");
    check(helper, helper.getBlockState(SOURCE).isAir() && pump.getEnergyStored() == 0,
      "First source must cost exactly 100 FE and remove exactly one source");
    check(helper, ((SingleUseTank) pump.tank).getAcceptedFluid() == Fluids.WATER,
      "Successful first pumping must lock the fluid type");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void insufficientEnergyRetainsSourceAndResumes(GameTestHelper helper) {
    PumpBlockEntity pump = ready(helper);
    for (int energy : new int[]{0, 9, 10, 99}) {
      pump.getBattery().setEnergy(energy);
      for (int i = 0; i < 20; i++) tick(helper, pump);
      check(helper, pump.getEnergyStored() == energy && pump.tank.isEmpty()
        && helper.getBlockState(SOURCE).is(Blocks.WATER), "Failed pumping must consume neither FE nor source");
    }
    pump.getBattery().setEnergy(100);
    tick(helper, pump);
    check(helper, pump.tank.getFluidAmount() == 1000 && pump.getEnergyStored() == 0,
      "Queued source must resume when the full energy cost is available");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void insufficientCapacityRetainsEnergyAndResumes(GameTestHelper helper) {
    PumpBlockEntity pump = ready(helper);
    pump.tank.fill(new FluidStack(Fluids.WATER, 15500), FluidAction.EXECUTE);
    pump.getBattery().setEnergy(100);
    for (int i = 0; i < 20; i++) tick(helper, pump);
    check(helper, pump.tank.getFluidAmount() == 15500 && pump.getEnergyStored() == 100
      && helper.getBlockState(SOURCE).is(Blocks.WATER), "Partial bucket space must not spend FE or delete the source");
    pump.tank.drain(500, FluidAction.EXECUTE);
    tick(helper, pump);
    check(helper, pump.tank.getFluidAmount() == 16000 && pump.getEnergyStored() == 0,
      "Pumping must resume when the entire bucket fits");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void fluidLockSurvivesNamedRoundTripAndDraining(GameTestHelper helper) {
    for (Fluid fluid : new Fluid[]{Fluids.WATER, EnergyFluids.OIL_SOURCE.get()}) {
      SingleUseTank tank = new SingleUseTank("tank", 16000);
      tank.fill(new FluidStack(fluid, 1000), FluidAction.EXECUTE);
      for (boolean empty : new boolean[]{false, true}) {
        if (empty) tank.drain(1000, FluidAction.EXECUTE);
        CompoundTag saved = tank.writeToNBT(new CompoundTag());
        check(helper, saved.getCompound("tank").contains("acceptedFluid", Tag.TAG_STRING)
          && saved.getCompound("tank").getString("acceptedFluid").equals(ForgeRegistries.FLUIDS.getKey(fluid).toString()),
          "Fluid lock must use a registry name");
        SingleUseTank restored = new SingleUseTank("tank", 16000);
        restored.readFromNBT(saved);
        check(helper, restored.getAcceptedFluid() == fluid && restored.getFluidAmount() == (empty ? 0 : 1000),
          "Contents and fluid lock must survive saving, including a drained tank");
        check(helper, restored.fill(new FluidStack(Fluids.LAVA, 1000), FluidAction.EXECUTE) == 0,
          "Restored lock must reject another fluid");
      }
    }
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void missingFluidLockClearsPreviousState(GameTestHelper helper) {
    SingleUseTank tank = new SingleUseTank("tank", 16000);
    tank.fill(new FluidStack(Fluids.LAVA, 1000), FluidAction.EXECUTE);
    CompoundTag empty = new SingleUseTank("tank", 16000).writeToNBT(new CompoundTag());
    tank.readFromNBT(empty);
    check(helper, tank.isEmpty() && tank.getAcceptedFluid() == null, "Absent lock must remain unassigned on reload");
    tank.fill(new FluidStack(Fluids.WATER, 1000), FluidAction.SIMULATE);
    check(helper, tank.getAcceptedFluid() == null, "Simulation must not choose a fluid");
    tank.fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE);
    tank.readFromNBT(new CompoundTag());
    check(helper, tank.isEmpty() && tank.getAcceptedFluid() == null, "Missing tank compound must clear stale state");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void unknownNamedFluidLocksStayClosed(GameTestHelper helper) {
    SingleUseTank tank = new SingleUseTank("tank", 16000);
    CompoundTag saved = tank.writeToNBT(new CompoundTag());
    CompoundTag inner = saved.getCompound("tank");
    for (String id : new String[]{"missing_mod:removed_fluid", "invalid fluid name"}) {
      inner.putString("acceptedFluid", id);
      tank.readFromNBT(saved);
      check(helper, tank.fill(new FluidStack(Fluids.WATER, 1000), FluidAction.EXECUTE) == 0,
        "Unknown or malformed saved fluid must not silently allow a different fluid");
    }
    helper.succeed();
  }

  private static PumpBlockEntity setup(GameTestHelper helper) {
    helper.setBlock(SOURCE.below(), Blocks.STONE.defaultBlockState());
    helper.setBlock(SOURCE, Blocks.WATER.defaultBlockState());
    helper.setBlock(MACHINE, FactoryBlocks.PUMP.get().defaultBlockState());
    helper.setBlock(MACHINE.east(), FactoryBlocks.TANK.get().defaultBlockState());
    // Keep output blocked so tests can account for every mB in the pump itself.
    TankBlockEntity output = (TankBlockEntity) helper.getBlockEntity(MACHINE.east());
    output.tank.fill(new FluidStack(Fluids.LAVA, 16000), FluidAction.EXECUTE);
    return (PumpBlockEntity) helper.getBlockEntity(MACHINE);
  }

  private static PumpBlockEntity ready(GameTestHelper helper) {
    PumpBlockEntity pump = setup(helper);
    CompoundTag saved = pump.saveWithFullMetadata();
    int y = helper.absolutePos(SOURCE).getY();
    saved.putInt("aimY", y);
    saved.putDouble("tubeY", y);
    pump.load(saved);
    pump.rebuildQueue();
    return pump;
  }

  private static void tick(GameTestHelper helper, PumpBlockEntity pump) {
    pump.tick(helper.getLevel(), pump.getBlockPos(), pump.getBlockState());
  }

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }
}
