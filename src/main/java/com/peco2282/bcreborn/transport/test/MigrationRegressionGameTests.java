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

import com.mojang.authlib.GameProfile;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.blueprints.*;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.packet.ServerPacketAccess;
import com.peco2282.bcreborn.common.utils.BlockMiner;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(BCRebornTransport.MODID)
@PrefixGameTestTemplate(false)
public class MigrationRegressionGameTests {
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void blueprintRequirementsKeepModernItemNbt(GameTestHelper helper) {
    ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
    tool.setDamageValue(37);
    tool.setHoverName(Component.literal("Migration test"));
    tool.enchant(Enchantments.BLOCK_EFFICIENCY, 3);
    tool.getOrCreateTag().putString("custom", "preserved");
    ItemStack blocks = new ItemStack(Items.OAK_STAIRS, 12);
    SchematicBlock schematic = new SchematicBlock();
    schematic.storedRequirements = new ItemStack[]{tool, blocks};
    MappingRegistry writer = new MappingRegistry();
    CompoundTag data = new CompoundTag();
    schematic.writeSchematicToNBT(data, writer);
    CompoundTag mappings = new CompoundTag();
    writer.write(mappings);
    MappingRegistry reader = new MappingRegistry();
    reader.read(mappings);
    SchematicBlock restored = new SchematicBlock();
    restored.readSchematicFromNBT(data, reader);
    check(helper, restored.storedRequirements.length == 2, "Both saved requirements must survive");
    check(helper, ItemStack.matches(tool, restored.storedRequirements[0]), "Tool tags/damage/enchantments must survive");
    check(helper, ItemStack.matches(blocks, restored.storedRequirements[1]), "Block ID and count must survive");
    check(helper, restored.defaultPermission == BuildingPermission.ALL, "Valid modern data must remain buildable");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void blueprintRejectsMissingIds(GameTestHelper helper) throws MappingNotFoundException {
    MappingRegistry mapping = new MappingRegistry();
    CompoundTag missing = new CompoundTag();
    missing.putString("id", "missing_mod:missing_item");
    missing.putByte("Count", (byte) 1);
    boolean rejected = false;
    try { mapping.stackToWorld(missing); } catch (MappingNotFoundException expected) { rejected = true; }
    check(helper, rejected, "Missing registry names must not silently become AIR");

    CompoundTag nested = new CompoundTag();
    nested.put("invalid", missing.copy());
    nested.put("valid", new ItemStack(Items.APPLE, 3).save(new CompoundTag()));
    mapping.scanAndTranslateStacksToWorld(nested);
    check(helper, !nested.contains("invalid") && ItemStack.of(nested.getCompound("valid")).getCount() == 3,
      "Removing invalid nested stacks must preserve other entries without concurrent modification");

    ListTag requirements = new ListTag();
    requirements.add(missing);
    CompoundTag schematicTag = new CompoundTag();
    mapping.writeBlockStateToNBT(schematicTag, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
    schematicTag.put("rq", requirements);
    SchematicBlock schematic = new SchematicBlock();
    schematic.readSchematicFromNBT(schematicTag, mapping);
    check(helper, schematic.defaultPermission == BuildingPermission.CREATIVE_ONLY, "Unknown requirements must not enable free survival builds");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void cachedFluidHandlersKeepTheirOwnDirection(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, new BlockPos(1, 1, 1), PipeType.FLUID, PipeMaterial.STONE);
    var northCap = pipe.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.NORTH);
    var north = northCap.orElseThrow(IllegalStateException::new);
    var southCap = pipe.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.SOUTH);
    var south = southCap.orElseThrow(IllegalStateException::new);
    var unsided = pipe.getCapability(ForgeCapabilities.FLUID_HANDLER).orElseThrow(IllegalStateException::new);
    var module = pipe.getFluidTransportModule();
    north.fill(new FluidStack(Fluids.WATER, 100), FluidAction.SIMULATE);
    check(helper, module.getTransferState(Direction.NORTH) == FluidTransportModule.TransferState.None, "Simulation must not change direction state");
    north.fill(new FluidStack(Fluids.WATER, 100), FluidAction.EXECUTE);
    check(helper, module.getTransferState(Direction.NORTH) == FluidTransportModule.TransferState.Input
      && module.getTransferState(Direction.SOUTH) == FluidTransportModule.TransferState.None,
      "Querying SOUTH later must not redirect the cached NORTH handler");
    unsided.fill(new FluidStack(Fluids.WATER, 100), FluidAction.EXECUTE);
    check(helper, module.getTransferState(Direction.SOUTH) == FluidTransportModule.TransferState.None,
      "Unsided fill must not reuse the last queried direction");
    south.fill(new FluidStack(Fluids.WATER, 100), FluidAction.EXECUTE);
    check(helper, module.getTransferState(Direction.SOUTH) == FluidTransportModule.TransferState.Input
      && pipe.getFluidTank().getFluidAmount() == 300, "Sided handlers must share one tank");
    pipe.invalidateCaps();
    check(helper, !northCap.isPresent() && !southCap.isPresent(), "All sided capabilities must be invalidated");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void disconnectedPowerPipesRejectDemandAndEnergy(GameTestHelper helper) {
    BlockPos center = new BlockPos(1, 1, 1);
    PipeBlockEntity source = placePipe(helper, center, PipeType.ENERGY, PipeMaterial.STONE);
    var cached = source.getCapability(ForgeCapabilities.ENERGY, Direction.EAST).orElseThrow(IllegalStateException::new);
    PipeBlockEntity target = placePipe(helper, center.east(), PipeType.ENERGY, PipeMaterial.COBBLESTONE);
    var input = source.getEnergyTransportModule();
    var output = target.getEnergyTransportModule();
    check(helper, !source.getCapability(ForgeCapabilities.ENERGY, Direction.EAST).isPresent()
      && cached.receiveEnergy(10, false) == 0 && cached.receiveEnergy(10, true) == 0,
      "Both fresh and cached capabilities must respect incompatible pipe materials");
    check(helper, output.receiveEnergy(Direction.WEST, 10) == 0, "Direct module injection must respect the same boundary");
    output.requestEnergy(Direction.WEST, 10);
    check(helper, output.nextPowerQuery[Direction.WEST.ordinal()] == 0, "Demand must not cross an incompatible boundary");
    seedPower(source, 40);
    input.tick(helper.getLevel(), source.getBlockPos());
    check(helper, input.displayPower[Direction.EAST.ordinal()] == 0, "Stale demand must not cause a disconnected transfer");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void compatiblePowerPipesStillTransfer(GameTestHelper helper) {
    BlockPos center = new BlockPos(1, 1, 1);
    PipeBlockEntity source = placePipe(helper, center, PipeType.ENERGY, PipeMaterial.STONE);
    PipeBlockEntity target = placePipe(helper, center.east(), PipeType.ENERGY, PipeMaterial.STONE);
    seedPower(source, 40);
    source.getEnergyTransportModule().tick(helper.getLevel(), source.getBlockPos());
    CompoundTag saved = new CompoundTag();
    target.getEnergyTransportModule().save(saved);
    check(helper, saved.getCompound("EnergyTransport").getDouble("internalNextPower" + Direction.WEST.ordinal()) > 0,
      "Compatible materials must still accept transferred power");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void machineMutationsRequireMatchingNearbyMenu(GameTestHelper helper) {
    BlockPos first = new BlockPos(1, 1, 1);
    BlockPos second = first.east();
    helper.setBlock(first, BuildersBlock.FILLER.get().defaultBlockState());
    helper.setBlock(second, BuildersBlock.FILLER.get().defaultBlockState());
    FillerBlockEntity filler = (FillerBlockEntity) helper.getBlockEntity(first);
    var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "MigrationTest"));
    BlockPos pos = filler.getBlockPos();
    player.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    try {
      check(helper, ServerPacketAccess.getMenuBlockEntity(player, pos, BuildersBlockEntityTypes.FILLER.get()).isEmpty(),
        "A nearby player without the machine menu must not mutate it");
      player.containerMenu = new FillerMenu(1, player.getInventory(), filler);
      check(helper, ServerPacketAccess.getMenuBlockEntity(player, pos, BuildersBlockEntityTypes.FILLER.get()).orElse(null) == filler,
        "The valid nearby menu must remain usable");
      check(helper, ServerPacketAccess.getMenuBlockEntity(player, helper.absolutePos(second), BuildersBlockEntityTypes.FILLER.get()).isEmpty(),
        "A menu for another block must not authorize this position");
      player.setPos(pos.getX() + 20, pos.getY(), pos.getZ());
      check(helper, ServerPacketAccess.getMenuBlockEntity(player, pos, BuildersBlockEntityTypes.FILLER.get()).isEmpty(),
        "An open menu must not authorize a distant machine");
      BlockPos unloaded = new BlockPos(30000000, pos.getY(), 30000000);
      player.setPos(unloaded.getX(), unloaded.getY(), unloaded.getZ());
      check(helper, ServerPacketAccess.getMenuBlockEntity(player, unloaded, BuildersBlockEntityTypes.FILLER.get()).isEmpty()
        && !helper.getLevel().hasChunkAt(unloaded), "Unloaded targets must not load chunks");
    } finally {
      player.containerMenu = player.inventoryMenu;
    }
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironEnginePenaltyWaitsForFullCooldownAndSurvivesReload(GameTestHelper helper) {
    BlockPos relative = new BlockPos(1, 1, 1);
    helper.setBlock(relative, EnergyBlocks.IRON_ENGINE.get().defaultBlockState());
    IronEngineBlockEntity engine = (IronEngineBlockEntity) helper.getBlockEntity(relative);
    engine.overheat();
    var ticker = BuildCraftBlockEntity.<IronEngineBlockEntity>ticker();
    for (int i = 0; i < 5; i++) ticker.tick(helper.getLevel(), engine.getBlockPos(), engine.getBlockState(), engine);
    CompoundTag saved = engine.saveWithFullMetadata();
    check(helper, saved.getInt("penaltyCoolingTime") == 5 && !engine.isBurning(),
      "The 1.7.10 ten-tick restart penalty must count down only after reaching minimum heat");
    IronEngineBlockEntity restored = new IronEngineBlockEntity(engine.getBlockPos(), engine.getBlockState());
    restored.setLevel(helper.getLevel());
    restored.load(saved);
    restored.fill(new FluidStack(EnergyFluids.OIL_SOURCE.get(), 10), FluidAction.EXECUTE);
    helper.setBlock(relative.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    for (int i = 0; i < 4; i++) ticker.tick(helper.getLevel(), restored.getBlockPos(), restored.getBlockState(), restored);
    check(helper, !restored.isBurning(), "Restored engine must wait for the remaining cooldown");
    ticker.tick(helper.getLevel(), restored.getBlockPos(), restored.getBlockState(), restored);
    check(helper, restored.isBurning() && restored.saveWithFullMetadata().getInt("penaltyCoolingTime") == 0,
      "Cooled engine must restart after the saved cooldown expires");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void energySimulationMatchesExecutionWithoutMutation(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, new BlockPos(1, 1, 1), PipeType.ENERGY, PipeMaterial.COBBLESTONE);
    var module = pipe.getEnergyTransportModule();
    var handler = pipe.getCapability(ForgeCapabilities.ENERGY, Direction.WEST).orElseThrow(IllegalStateException::new);
    check(helper, !pipe.getCapability(ForgeCapabilities.ENERGY).isPresent(), "Unsided injection must not fill an unrouted buffer");
    CompoundTag before = new CompoundTag();
    module.save(before);
    int first = handler.receiveEnergy(40, true);
    CompoundTag after = new CompoundTag();
    module.save(after);
    check(helper, before.equals(after) && first == 40, "SIMULATE must not step buffers or change stored state");
    check(helper, handler.receiveEnergy(40, false) == first, "First real input must match the quote");
    int remaining = handler.receiveEnergy(1000, true);
    check(helper, remaining > 0 && remaining < module.getMaxPower(), "Quote must account for same-tick input and losses");
    check(helper, handler.receiveEnergy(1000, false) == remaining, "Second real input must match the quote");
    before = new CompoundTag();
    module.save(before);
    check(helper, handler.receiveEnergy(1, false) == 0 && handler.receiveEnergy(-1, false) == 0,
      "A fraction of free space must not accept uncharged whole FE");
    after = new CompoundTag();
    module.save(after);
    check(helper, before.equals(after), "Rejected input must not change stored power");
    double stored = after.getCompound("EnergyTransport").getDouble("internalNextPower" + Direction.WEST.ordinal());
    check(helper, Math.abs(stored - (first + remaining) * (1.0 - module.getPowerResistance())) < 0.00001,
      "Stored power must equal charged input minus resistance loss");
    helper.runAfterDelay(1, () -> {
      int next = handler.receiveEnergy(40, true);
      check(helper, next == 40 && handler.receiveEnergy(40, false) == next,
        "A new tick must quote the newly available receiving buffer");
      helper.succeed();
    });
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironEnginePausesAndRestoresConsumedFuel(GameTestHelper helper) {
    BlockPos relative = new BlockPos(1, 1, 1);
    helper.setBlock(relative, EnergyBlocks.IRON_ENGINE.get().defaultBlockState());
    IronEngineBlockEntity engine = (IronEngineBlockEntity) helper.getBlockEntity(relative);
    var ticker = BuildCraftBlockEntity.<IronEngineBlockEntity>ticker();
    engine.fill(new FluidStack(EnergyFluids.OIL_SOURCE.get(), 1), FluidAction.EXECUTE);
    ticker.tick(helper.getLevel(), engine.getBlockPos(), engine.getBlockState(), engine);
    check(helper, engine.getFluidInTank(0).getAmount() == 1 && engine.getBurnTime() == 0,
      "Unpowered engine must not consume fuel or ignite");
    helper.setBlock(relative.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    ticker.tick(helper.getLevel(), engine.getBlockPos(), engine.getBlockState(), engine);
    check(helper, engine.getFluidInTank(0).isEmpty() && engine.isBurning(), "Powered engine must consume the last mB and ignite");
    CompoundTag saved = engine.saveWithFullMetadata();
    // A save may contain a longer remainder than today's fuel definition after a config change.
    saved.putInt("burnTime", 20);
    saved.putInt("totalBurnTime", 20);
    IronEngineBlockEntity restored = new IronEngineBlockEntity(engine.getBlockPos(), engine.getBlockState());
    restored.setLevel(helper.getLevel());
    restored.load(saved);
    ticker.tick(helper.getLevel(), restored.getBlockPos(), restored.getBlockState(), restored);
    check(helper, restored.getBurnTime() == 19 && restored.getEnergyStored() > 0,
      "An empty tank must still restore the in-progress fuel and generate energy");
    helper.setBlock(relative.below(), Blocks.AIR.defaultBlockState());
    float heat = restored.saveWithFullMetadata().getFloat("heat");
    ticker.tick(helper.getLevel(), restored.getBlockPos(), restored.getBlockState(), restored);
    check(helper, restored.getBurnTime() == 19 && !restored.isBurning()
      && restored.saveWithFullMetadata().getFloat("heat") <= heat, "Signal OFF must preserve the remainder and cool the engine");
    helper.setBlock(relative.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    ticker.tick(helper.getLevel(), restored.getBlockPos(), restored.getBlockState(), restored);
    check(helper, restored.getBurnTime() == 18, "Signal ON must resume the saved fuel");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironEngineHandlesMissingFuelIds(GameTestHelper helper) {
    BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
    var state = EnergyBlocks.IRON_ENGINE.get().defaultBlockState();
    IronEngineBlockEntity engine = new IronEngineBlockEntity(pos, state);
    engine.setLevel(helper.getLevel());
    CompoundTag missing = engine.saveWithFullMetadata();
    missing.putInt("burnTime", 20);
    missing.putString("currentFuel", "missing_mod:deleted_fuel");
    engine.load(missing);
    helper.setBlock(new BlockPos(1, 0, 1), Blocks.AIR.defaultBlockState());
    BuildCraftBlockEntity.<IronEngineBlockEntity>ticker().tick(helper.getLevel(), pos, state, engine);
    check(helper, engine.getBurnTime() == 0 && !engine.isBurning(), "Missing saved fuel must stop safely instead of freezing");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void fallingBlockBlueprintsDependOnSupportBelow(GameTestHelper helper) {
    for (var block : new net.minecraft.world.level.block.Block[]{Blocks.SAND, Blocks.GRAVEL}) {
      SchematicBlock schematic = new SchematicBlock();
      schematic.state = block.defaultBlockState();
      var prerequisites = schematic.getPrerequisiteBlocks(null);
      check(helper, prerequisites.contains(BlockPos.ZERO.below()) && !prerequisites.contains(BlockPos.ZERO.above()),
        "Falling blocks must be scheduled after the support below them");
    }
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void quarryMinesWaterloggedSolidsButLeavesWater(GameTestHelper helper) {
    BlockPos machine = new BlockPos(0, 1, 0);
    BlockPos target = new BlockPos(1, 1, 1);
    helper.setBlock(machine, BuildersBlock.QUARRY.get().defaultBlockState());
    helper.setBlock(target.below(), Blocks.BEDROCK.defaultBlockState());
    helper.setBlock(target, Blocks.OAK_STAIRS.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
    QuarryBlockEntity quarry = (QuarryBlockEntity) helper.getBlockEntity(machine);
    BlockPos absolute = helper.absolutePos(target);
    quarry.box.initialize(absolute.getX() - 1, absolute.getY(), absolute.getZ() - 1,
      absolute.getX() + 1, absolute.getY() + 4, absolute.getZ() + 1);
    check(helper, quarry.findTarget(true), "Waterlogged stairs must be considered a mining target");
    CompoundTag saved = quarry.saveWithFullMetadata();
    check(helper, saved.getInt("targetY") == absolute.getY(), "Quarry must select the solid, not blocks below it");
    BlockMiner miner = new BlockMiner(helper.getLevel(), quarry, absolute.getX(), absolute.getY(), absolute.getZ());
    miner.acceptEnergy(100000);
    check(helper, miner.hasMined() && helper.getBlockState(target).is(Blocks.WATER),
      "Mining a waterlogged solid must preserve its water source");
    check(helper, !quarry.findTarget(false), "The remaining water above bedrock must not be mined as a solid");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void quarryKeepsBlockedFramesAcrossReload(GameTestHelper helper) {
    BlockPos machine = new BlockPos(0, 1, 0);
    BlockPos frame = new BlockPos(1, 1, 1);
    helper.setBlock(machine, BuildersBlock.QUARRY.get().defaultBlockState());
    helper.setBlock(frame, Blocks.BEDROCK.defaultBlockState());
    QuarryBlockEntity quarry = (QuarryBlockEntity) helper.getBlockEntity(machine);
    var ticker = BuildCraftBlockEntity.<QuarryBlockEntity>ticker();
    ticker.tick(helper.getLevel(), quarry.getBlockPos(), quarry.getBlockState(), quarry);
    CompoundTag saved = quarry.saveWithFullMetadata();
    saved.putInt("stage", QuarryBlockEntity.Stage.BUILDING.ordinal());
    saved.putLongArray("frameList", new long[]{helper.absolutePos(frame).asLong()});
    quarry.load(saved);
    quarry.getBattery().setEnergy(100);
    ticker.tick(helper.getLevel(), quarry.getBlockPos(), quarry.getBlockState(), quarry);
    saved = quarry.saveWithFullMetadata();
    check(helper, saved.getLongArray("frameList").length == 1 && quarry.getBattery().getEnergyStored() == 100
      && saved.getInt("stage") == QuarryBlockEntity.Stage.BUILDING.ordinal(),
      "Obstructed frame must remain queued without spending energy");
    quarry.load(saved);
    helper.setBlock(frame, Blocks.AIR.defaultBlockState());
    ticker.tick(helper.getLevel(), quarry.getBlockPos(), quarry.getBlockState(), quarry);
    check(helper, helper.getBlockState(frame).is(BuildersBlock.FRAME.get()) && quarry.getBattery().getEnergyStored() == 75
      && quarry.saveWithFullMetadata().getInt("stage") == QuarryBlockEntity.Stage.IDLE.ordinal(),
      "Clearing the obstruction must resume and complete the queued frame exactly once");
    helper.succeed();
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material) {
    helper.setBlock(pos, TransportBlocks.get(type, material).get().defaultBlockState());
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  private static void seedPower(PipeBlockEntity pipe, int amount) {
    CompoundTag tag = new CompoundTag();
    CompoundTag power = new CompoundTag();
    power.putDouble("internalNextPower" + Direction.WEST.ordinal(), amount);
    power.putInt("nextPowerQuery" + Direction.EAST.ordinal(), amount);
    tag.put("EnergyTransport", power);
    pipe.getEnergyTransportModule().load(tag);
  }

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }
}
