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
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.RefineryBlockEntity;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.block.entity.AssemblyTableBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.*;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BCRebornTransport.MODID)
@PrefixGameTestTemplate(false)
public class ProductionCompatibilityGameTests {
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void refineryConsumesOnlyWhenOutputFits(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, FactoryBlocks.REFINERY.get().defaultBlockState());
    RefineryBlockEntity refinery = (RefineryBlockEntity) helper.getBlockEntity(pos);
    refinery.fill(new FluidStack(EnergyFluids.OIL_SOURCE.get(), 2), IFluidHandler.FluidAction.EXECUTE);
    refinery.getBattery().setEnergy(240);
    refinery.result.fill(new FluidStack(EnergyFluids.FUEL_SOURCE.get(), 4000), IFluidHandler.FluidAction.EXECUTE);
    refinery.tick(helper.getLevel(), refinery.getBlockPos(), refinery.getBlockState());
    check(helper, refinery.getEnergyStored() == 240 && refinery.tanks[0].getFluidAmount() == 2,
      "Full output must not consume oil or energy");
    refinery.drain(1, IFluidHandler.FluidAction.EXECUTE);
    refinery.tick(helper.getLevel(), refinery.getBlockPos(), refinery.getBlockState());
    check(helper, refinery.result.getFluidAmount() == 4000 && refinery.tanks[0].getFluidAmount() == 1
      && refinery.getEnergyStored() == 120, "One mB must cost exactly 120 RF");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void assemblyNeedsLaserEnergyAndConsumesIngredients(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, SiliconBlocks.ASSEMBLY_TABLE.get().defaultBlockState());
    AssemblyTableBlockEntity table = (AssemblyTableBlockEntity) helper.getBlockEntity(pos);
    var recipe = BuildcraftRecipeRegistry.assembly().getRecipe(ResourceLocation.parse("bcrebornsilicon:redstone_chipset"));
    check(helper, recipe != null, "Assembly recipe was not loaded");
    table.setItem(0, new ItemStack(Items.REDSTONE));
    BuildCraftBlockEntity.<AssemblyTableBlockEntity>ticker().tick(helper.getLevel(), table.getBlockPos(), table.getBlockState(), table);
    check(helper, table.getItem(0).getCount() == 1 && table.requiresLaserEnergy(), "Unpowered table must retain inputs and request laser power");
    table.receiveLaserEnergy(recipe.energy());
    BuildCraftBlockEntity.<AssemblyTableBlockEntity>ticker().tick(helper.getLevel(), table.getBlockPos(), table.getBlockState(), table);
    check(helper, table.getItem(0).isEmpty() && table.getEnergy() == 0, "Craft must consume ingredients and laser energy once: " + table.getItem(0) + ", energy=" + table.getEnergy() + ", state=" + table.saveWithFullMetadata());
    var outputs = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
      new net.minecraft.world.phys.AABB(table.getBlockPos()).inflate(1));
    check(helper, outputs.stream().anyMatch(e -> ItemStack.isSameItemSameTags(e.getItem(), recipe.result())), "Crafted chipset must be output");
    table.setItem(0, new ItemStack(Items.DIAMOND));
    check(helper, !table.canCraft(), "Diamond alone must not satisfy the redstone and diamond recipe");
    table.setItem(1, new ItemStack(Items.REDSTONE));
    var diamondRecipe = BuildcraftRecipeRegistry.assembly().getRecipe(ResourceLocation.parse("bcrebornsilicon:diamond_chipset"));
    check(helper, diamondRecipe != null && diamondRecipe.ingredients().size() == 2,
      "Diamond chipset recipe must retain two separate requirements");
    table.receiveLaserEnergy(diamondRecipe.energy());
    BuildCraftBlockEntity.<AssemblyTableBlockEntity>ticker().tick(helper.getLevel(), table.getBlockPos(), table.getBlockState(), table);
    check(helper, table.getItem(0).isEmpty() && table.getItem(1).isEmpty() && table.getEnergy() == 0,
      "Diamond chipset must consume both ingredients and its full energy cost");
    check(helper, BuildcraftRecipeRegistry.assembly().getRecipe(ResourceLocation.parse("bcrebornsilicon:comp_chipset")) != null,
      "Comparator chipset recipe must have a distinct ID");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void singlePulsarDoesNotRepeatAfterReload(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, TransportBlocks.get(PipeType.ITEM, PipeMaterial.WOOD).get().defaultBlockState());
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.AND, Direction.UP);
    gate.addGateExpansion(GateExpansionPulsar.INSTANCE);
    var controller = gate.expansions.get(GateExpansionPulsar.INSTANCE);
    controller.resolveAction(TransportStatements.ACTION_SINGLE_ENERGY_PULSE.get(), 1);
    for (int i = 0; i < 30; i++) controller.tick(gate);
    check(helper, pipe.getExtractionEnergy() == 10, "Single pulse must deliver only 10 RF");
    CompoundTag saved = new CompoundTag();
    gate.writeToNBT(saved);
    Gate restored = GateFactory.makeGate(pipe.getPipe(), saved).orElseThrow();
    var restoredController = restored.expansions.get(GateExpansionPulsar.INSTANCE);
    for (int i = 0; i < 20; i++) restoredController.tick(restored);
    check(helper, pipe.getExtractionEnergy() == 10, "Reload must not repeat a single pulse");
    restoredController.startResolution();
    restoredController.tick(restored);
    restoredController.resolveAction(TransportStatements.ACTION_SINGLE_ENERGY_PULSE.get(), 1);
    for (int i = 0; i < 10; i++) restoredController.tick(restored);
    check(helper, pipe.getExtractionEnergy() == 20, "New activation must deliver the next pulse");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void quarryDefaultAreaIsOutsideMachine(GameTestHelper helper) {
    BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      var state = BuildersBlock.QUARRY.get().defaultBlockState().setValue(BuildCraftBlock.HORIZONTAL_FACING, direction);
      QuarryBlockEntity quarry = new QuarryBlockEntity(pos, state);
      quarry.setLevel(helper.getLevel());
      quarry.initialize();
      var box = quarry.getBox();
      check(helper, box.xMax - box.xMin == 10 && box.zMax - box.zMin == 10, "Default frame must be 11x11");
      check(helper, pos.getX() < box.xMin || pos.getX() > box.xMax || pos.getZ() < box.zMin || pos.getZ() > box.zMax,
        "Quarry must not be inside its own mining area");
      CompoundTag saved = quarry.saveWithFullMetadata();
      saved.putInt("stage", QuarryBlockEntity.Stage.MOVING.ordinal());
      saved.putBoolean("movingHorizontally", true);
      saved.putInt("targetX", box.xMax - 1);
      saved.putDouble("headSpeed", 0.2);
      QuarryBlockEntity restored = new QuarryBlockEntity(pos, state);
      restored.setLevel(helper.getLevel());
      restored.load(saved);
      BuildCraftBlockEntity.<QuarryBlockEntity>ticker().tick(helper.getLevel(), pos, state, restored);
      check(helper, restored.getHeadPosX(1) == quarry.getHeadPosX(1)
        && restored.saveWithFullMetadata().getDouble("headSpeed") == 0, "Unpowered restored quarry must keep its head still");
    }
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void powerDisplayMeasuresThroughputWithoutExplosion(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.COBBLESTONE).get().defaultBlockState());
    helper.setBlock(pos.east(), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.DIAMOND).get().defaultBlockState());
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    PipeBlockEntity output = (PipeBlockEntity) helper.getBlockEntity(pos.east());
    var module = pipe.getEnergyTransportModule();
    for (int i = 0; i < 80; i++) {
      CompoundTag state = new CompoundTag();
      CompoundTag power = new CompoundTag();
      power.putDouble("internalPower" + Direction.WEST.ordinal(), 0);
      power.putDouble("internalNextPower" + Direction.WEST.ordinal(), 80);
      power.putInt("nextPowerQuery" + Direction.EAST.ordinal(), 80);
      power.putInt("overload", module.getOverload());
      state.put("EnergyTransport", power);
      module.load(state);
      CompoundTag empty = new CompoundTag();
      empty.put("EnergyTransport", new CompoundTag());
      output.getEnergyTransportModule().load(empty);
      module.tick(helper.getLevel(), pipe.getBlockPos());
    }
    check(helper, module.displayPower[Direction.EAST.ordinal()] == 80 && module.displayPower[Direction.WEST.ordinal()] == 80,
      "Both incoming and outgoing faces must show transferred power");
    check(helper, module.isOverloaded() && !pipe.isRemoved(), "Overload must be displayed without destroying the pipe");
    helper.succeed();
  }

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }
}
