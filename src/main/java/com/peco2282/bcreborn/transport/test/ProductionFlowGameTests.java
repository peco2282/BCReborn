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
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.factory.block.entity.HopperBlockEntity;
import com.peco2282.bcreborn.factory.block.entity.RefineryBlockEntity;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.silicon.item.PackageItem;
import com.peco2282.bcreborn.silicon.menu.AdvancedCraftingTableMenu;
import com.peco2282.bcreborn.silicon.menu.PackagerMenu;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.TransportBlocks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import com.peco2282.bcreborn.silicon.block.entity.*;

@GameTestHolder(BCRebornTransport.MODID)
@PrefixGameTestTemplate(false)
public class ProductionFlowGameTests {
  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static void tick(GameTestHelper helper, BuildCraftBlockEntity entity) {
    BuildCraftBlockEntity.<BuildCraftBlockEntity>ticker().tick(helper.getLevel(), entity.getBlockPos(), entity.getBlockState(), entity);
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void refinedOilPowersAnEnergyConsumer(GameTestHelper helper) {
    BlockPos refineryPos = new BlockPos(0, 1, 0);
    BlockPos enginePos = new BlockPos(0, 1, 1);
    helper.setBlock(refineryPos, FactoryBlocks.REFINERY.get().defaultBlockState());
    RefineryBlockEntity refinery = (RefineryBlockEntity) helper.getBlockEntity(refineryPos);
    refinery.fill(new FluidStack(EnergyFluids.OIL_SOURCE.get(), 10), FluidAction.EXECUTE);
    refinery.getBattery().setEnergy(1200);
    for (int i = 0; i < 10; i++) tick(helper, refinery);
    check(helper, refinery.result.getFluidAmount() == 10 && refinery.tanks[0].isEmpty()
      && refinery.getEnergyStored() == 0, "Refining must consume the exact oil and energy");
    helper.setBlock(enginePos, EnergyBlocks.IRON_ENGINE.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.EAST));
    IronEngineBlockEntity engine = (IronEngineBlockEntity) helper.getBlockEntity(enginePos);
    check(helper, engine.fill(refinery.drain(10, FluidAction.EXECUTE), FluidAction.EXECUTE) == 10,
      "Iron engine must accept refined fuel");
    tick(helper, engine);
    check(helper, engine.getFluidInTank(0).getAmount() == 10 && engine.getEnergyStored() == 0,
      "Unpowered engine must retain refined fuel");
    helper.setBlock(enginePos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.setBlock(new BlockPos(1, 1, 1), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    helper.setBlock(new BlockPos(2, 1, 1), FactoryBlocks.MINING_WELL.get().defaultBlockState());
    helper.succeedWhen(() -> {
      var sink = (BuildCraftBlockEntity) helper.getBlockEntity(new BlockPos(2, 1, 1));
      check(helper, sink.getBattery().getEnergyStored() > 0, "Refined fuel must deliver FE through a pipe to a machine");
    });
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void advancedCraftingKeepsPatternAndStopsWhenFull(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, SiliconBlocks.ADVANCED_CRAFTING_TABLE.get().defaultBlockState());
    var table = (AdvancedCraftingTableBlockEntity) helper.getBlockEntity(pos);
    table.setItem(24, new ItemStack(Items.OAK_PLANKS));
    table.setItem(27, new ItemStack(Items.OAK_PLANKS));
    table.setItem(0, new ItemStack(Items.BIRCH_PLANKS, 2));
    tick(helper, table);
    check(helper, table.getItem(0).getCount() == 2 && table.getItem(15).isEmpty(), "Unpowered crafting must retain materials");
    for (int i = 15; i < 24; i++) table.setItem(i, new ItemStack(Items.DIRT, 64));
    table.receiveLaserEnergy(5000);
    tick(helper, table);
    check(helper, table.getEnergy() == 5000 && table.getItem(0).getCount() == 2, "Full output must retain inputs and laser energy");
    var saved = table.saveWithFullMetadata();
    table.load(saved);
    table.setItem(15, ItemStack.EMPTY);
    tick(helper, table);
    check(helper, table.getItem(15).is(Items.STICK) && table.getItem(15).getCount() == 4
      && table.getItem(0).isEmpty() && table.getEnergy() == 0 && table.getItem(24).is(Items.OAK_PLANKS),
      "Restored recipe must accept tag-equivalent real inputs while keeping its ghost pattern");
    var cap = table.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
    var handler = cap.orElseThrow(IllegalStateException::new);
    check(helper, handler.extractItem(24, 1, false).isEmpty()
      && !handler.insertItem(24, new ItemStack(Items.STONE), false).isEmpty()
      && handler.extractItem(15, 4, true).getCount() == 4, "Automation must expose outputs but never recipe ghosts");
    table.invalidateCaps();
    check(helper, !cap.isPresent(), "Machine capability must invalidate");
    table.reviveCaps();
    check(helper, table.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent(), "Machine capability must revive");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void craftingRemaindersNeedOutputSpace(GameTestHelper helper) {
    helper.setBlock(new BlockPos(1, 1, 1), SiliconBlocks.ADVANCED_CRAFTING_TABLE.get().defaultBlockState());
    var table = (AdvancedCraftingTableBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
    var ingredients = new Item[]{Items.MILK_BUCKET, Items.MILK_BUCKET, Items.MILK_BUCKET,
      Items.SUGAR, Items.EGG, Items.SUGAR, Items.WHEAT, Items.WHEAT, Items.WHEAT};
    for (int i = 0; i < 9; i++) {
      table.setItem(24 + i, new ItemStack(ingredients[i]));
      table.setItem(i, new ItemStack(ingredients[i]));
    }
    for (int i = 16; i < 24; i++) table.setItem(i, new ItemStack(Items.DIRT, 64));
    table.receiveLaserEnergy(5000);
    tick(helper, table);
    check(helper, table.getItem(0).is(Items.MILK_BUCKET) && table.getEnergy() == 5000,
      "A craft must reserve space for buckets as well as its result");
    table.setItem(16, ItemStack.EMPTY);
    tick(helper, table);
    check(helper, table.getItem(15).is(Items.CAKE) && table.getItem(16).is(Items.BUCKET)
      && table.getItem(16).getCount() == 3 && table.getEnergy() == 0, "Craft must return all three buckets exactly once");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void packagerUsesRealInventoryAndPersistsFilters(GameTestHelper helper) {
    helper.setBlock(new BlockPos(1, 1, 1), SiliconBlocks.PACKAGER.get().defaultBlockState());
    var table = (PackagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
    table.pattern.setItem(0, new ItemStack(Items.DIAMOND));
    table.pattern.setItem(1, new ItemStack(Items.DIAMOND));
    table.setItem(0, new ItemStack(Items.DIAMOND));
    table.setItem(9, new ItemStack(Items.PAPER));
    table.load(table.saveWithFullMetadata());
    helper.succeedWhen(() -> {
      if (helper.getLevel().getGameTime() % 5 != 0) helper.fail("Waiting for packaging interval");
      tick(helper, table);
      check(helper, table.getItem(11).isEmpty() && table.getItem(9).is(Items.PAPER), "Missing duplicate ingredient must not consume paper");
      table.setItem(0, new ItemStack(Items.DIAMOND, 2));
      tick(helper, table);
      ItemStack result = table.getItem(11);
      check(helper, result.getItem() instanceof PackageItem && PackageItem.getStack(result, 0).is(Items.DIAMOND)
        && PackageItem.getStack(result, 1).is(Items.DIAMOND) && table.getItem(0).isEmpty()
        && table.getItem(9).isEmpty(), "Packaging must use two real diamonds and one paper");
      table.setItem(0, new ItemStack(Items.DIAMOND, 2));
      table.setItem(9, new ItemStack(Items.PAPER));
      tick(helper, table);
      check(helper, table.getItem(0).getCount() == 2 && table.getItem(9).getCount() == 1,
        "Full package output must stop consumption");
    });
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void programmingAndIntegrationPreserveEnergyAndSelections(GameTestHelper helper) {
    helper.setBlock(new BlockPos(0, 1, 1), SiliconBlocks.PROGRAMMING_TABLE.get().defaultBlockState());
    var programmer = (ProgrammingTableBlockEntity) helper.getBlockEntity(new BlockPos(0, 1, 1));
    programmer.setItem(0, new ItemStack(RoboticsItems.REDSTONE_BOARDS.get("clean").get()));
    check(helper, !programmer.canCraft(), "Programming must wait for an explicit selection");
    programmer.cycleOption(1);
    // The empty/reset program is intentionally free; select an actual robot role.
    for (int i = 0; i < 40 && programmer.getRequiredEnergy() == 0; i++) programmer.cycleOption(1);
    var selected = programmer.getItem(2).copy();
    check(helper, !selected.isEmpty(), "Registered board programs must be selectable");
    int cost = programmer.getRequiredEnergy();
    check(helper, cost > 0, "Programming must have a laser cost");
    programmer.receiveLaserEnergy(cost);
    programmer.load(programmer.saveWithFullMetadata());
    programmer.setItem(1, new ItemStack(Items.DIRT, 64));
    tick(helper, programmer);
    check(helper, !programmer.getItem(0).isEmpty() && programmer.getEnergy() == cost, "Blocked programming output must preserve input and energy");
    programmer.setItem(1, ItemStack.EMPTY);
    tick(helper, programmer);
    check(helper, ItemStack.isSameItemSameTags(selected, programmer.getItem(1)) && programmer.getItem(0).isEmpty()
      && programmer.getEnergy() == 0, "Selected program must survive reload and be applied once");
    helper.setBlock(new BlockPos(2, 1, 1), SiliconBlocks.INTEGRATION_TABLE.get().defaultBlockState());
    var integrator = (IntegrationTableBlockEntity) helper.getBlockEntity(new BlockPos(2, 1, 1));
    integrator.setItem(0, RobotItem.createRobotStack(RoboticsRedstoneRobots.EMPTY.get(), 12345));
    integrator.setItem(1, programmer.getItem(1).copy());
    integrator.setItem(9, new ItemStack(Items.DIRT, 64));
    integrator.receiveLaserEnergy(IntegrationTableBlockEntity.ROBOT_INTEGRATION_ENERGY);
    tick(helper, integrator);
    check(helper, integrator.getEnergy() == IntegrationTableBlockEntity.ROBOT_INTEGRATION_ENERGY && !integrator.getItem(0).isEmpty(), "Full integration output must not consume anything");
    integrator.setItem(9, ItemStack.EMPTY);
    tick(helper, integrator);
    check(helper, integrator.getItem(0).isEmpty() && integrator.getItem(1).isEmpty() && integrator.getEnergy() == 0
      && RobotItem.getEnergy(integrator.getItem(9)) == 12345
      && integrator.getItem(9).getTag().getCompound("board").equals(selected.getTag()),
      "Integration must apply board NBT without losing robot charge");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void recipeGhostsCannotBecomeRealItems(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, SiliconBlocks.ADVANCED_CRAFTING_TABLE.get().defaultBlockState());
    var table = (AdvancedCraftingTableBlockEntity) helper.getBlockEntity(pos);
    var player = FakePlayerFactory.get(helper.getLevel(),
      new GameProfile(UUID.randomUUID(), "ProductionMenuTest"));
    var absolute = helper.absolutePos(pos);
    player.setPos(absolute.getX() + 0.5, absolute.getY() + 0.5, absolute.getZ() + 0.5);
    var menu = new AdvancedCraftingTableMenu(1, player.getInventory(), table);
    menu.setCarried(new ItemStack(Items.DIAMOND, 12));
    menu.clicked(0, 0, ClickType.PICKUP, player);
    check(helper, table.getItem(24).is(Items.DIAMOND) && table.getItem(24).getCount() == 1
      && menu.getCarried().getCount() == 12, "Pattern editing must copy only a ghost");
    table.setItem(33, new ItemStack(Items.DIAMOND_BLOCK));
    check(helper, menu.quickMoveStack(player, 0).isEmpty() && menu.quickMoveStack(player, 9).isEmpty(),
      "Neither ghost nor preview may be shift-clicked into the player inventory");
    table.setItem(0, new ItemStack(Items.IRON_INGOT, 2));
    helper.setBlock(pos, Blocks.AIR.defaultBlockState());
    var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class,
      new AABB(absolute).inflate(1));
    check(helper, drops.stream().mapToInt(e -> e.getItem().is(Items.IRON_INGOT) ? e.getItem().getCount() : 0).sum() == 2
      && drops.stream().noneMatch(e -> e.getItem().is(Items.DIAMOND) || e.getItem().is(Items.DIAMOND_BLOCK)),
      "Breaking a machine must return real materials without duplicating pattern or preview items");
    helper.setBlock(pos, SiliconBlocks.PACKAGER.get().defaultBlockState());
    var packager = (PackagerBlockEntity) helper.getBlockEntity(pos);
    var packagerMenu = new PackagerMenu(2, player.getInventory(), packager);
    packagerMenu.setCarried(new ItemStack(Items.GOLD_INGOT));
    packagerMenu.clicked(10, 0, ClickType.PICKUP, player);
    check(helper, packager.pattern.getItem(0).is(Items.GOLD_INGOT) && packager.getItem(0).isEmpty(),
      "Packager filters must not overwrite the real input inventory");
    for (var slot : packagerMenu.slots) slot.getItem();
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void inventoryReloadClearsPreviouslyFilledSlots(GameTestHelper helper) {
    var inventory = new SimpleInventory(2, "test", 1);
    check(helper, inventory.isEmpty(), "New inventory must report empty");
    inventory.setItem(0, new ItemStack(Items.DIAMOND, 5));
    check(helper, !inventory.isEmpty() && inventory.getItem(0).getCount() == 1, "Inventory must enforce its declared slot limit");
    CompoundTag saved = new CompoundTag();
    inventory.writeTag(saved);
    var restored = new com.peco2282.bcreborn.common.SimpleInventory(2, "test", 1);
    restored.readTag(saved);
    check(helper, restored.getItem(0).is(Items.DIAMOND) && restored.getItem(0).getCount() == 1,
      "Saved inventory lists must be written to NBT and restored into a new instance");
    inventory.readTag(new CompoundTag());
    check(helper, inventory.isEmpty() && inventory.getItem(0).isEmpty(), "Reload must discard absent slots");
    inventory.setItem(0, new ItemStack(Items.GOLD_INGOT));
    check(helper, inventory.removeItem(0, 64).getCount() == 1 && inventory.isEmpty(), "Extraction must return available items");
    inventory.setItem(1, new ItemStack(Items.IRON_INGOT));
    inventory.clearContent();
    check(helper, inventory.isEmpty(), "Clearing must remove all contents");
    helper.succeed();
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void integrationRequiresEveryDatapackIngredient(GameTestHelper helper) {
    var previous = com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry.integration();
    var id = net.minecraft.resources.ResourceLocation.parse("bcreborntransport:test_integration");
    var recipe = new com.peco2282.bcreborn.api.recipes.IntegrationRecipe(id,
      net.minecraft.world.item.crafting.Ingredient.of(Items.IRON_INGOT),
      java.util.List.of(net.minecraft.world.item.crafting.Ingredient.of(Items.REDSTONE),
        net.minecraft.world.item.crafting.Ingredient.of(Items.REDSTONE)),
      new ItemStack(Items.COMPASS), 321, 2);
    try {
      com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry.integration(
        new com.peco2282.bcreborn.api.recipes.IIntegrationRecipeManager() {
          public com.peco2282.bcreborn.api.recipes.IntegrationRecipe getRecipe(net.minecraft.resources.ResourceLocation key) {
            return key.equals(id) ? recipe : null;
          }
          public java.util.Collection<com.peco2282.bcreborn.api.recipes.IntegrationRecipe> getRecipes() {
            return java.util.List.of(recipe);
          }
          public boolean contains(net.minecraft.resources.ResourceLocation key) { return id.equals(key); }
        });
      helper.setBlock(new BlockPos(1, 1, 1), SiliconBlocks.INTEGRATION_TABLE.get().defaultBlockState());
      var table = (IntegrationTableBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
      table.setItem(0, new ItemStack(Items.IRON_INGOT));
      table.setItem(1, new ItemStack(Items.REDSTONE));
      table.receiveLaserEnergy(321);
      tick(helper, table);
      check(helper, table.getItem(9).isEmpty() && table.getEnergy() == 321 && table.getItem(0).getCount() == 1,
        "One redstone must not satisfy two expansion requirements");
      table.setItem(2, new ItemStack(Items.REDSTONE));
      table.load(table.saveWithFullMetadata());
      tick(helper, table);
      check(helper, table.getItem(9).is(Items.COMPASS) && table.getEnergy() == 0
        && table.getItem(0).isEmpty() && table.getItem(1).isEmpty() && table.getItem(2).isEmpty(),
        "Data-driven integration must consume each required ingredient and the exact cost");
    } finally {
      com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry.integration(previous);
    }
    helper.succeed();
  }

  private static class CapabilityChest extends ChestBlockEntity {
    final ItemStackHandler sink = new ItemStackHandler(1) {
      @Override public int getSlotLimit(int slot) { return 1; }
    };
    final LazyOptional<IItemHandler> cap = LazyOptional.of(() -> sink);
    CapabilityChest(BlockPos pos, BlockState state) { super(pos, state); }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
      if (capability == ForgeCapabilities.ITEM_HANDLER) return side == Direction.UP ? cap.cast() : LazyOptional.empty();
      return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() { super.invalidateCaps(); cap.invalidate(); }
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void hopperHonorsCapabilityCapacityAndPipeInsertion(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    helper.setBlock(pos, FactoryBlocks.HOPPER.get().defaultBlockState());
    helper.setBlock(pos.below(), Blocks.CHEST.defaultBlockState());
    var target = new CapabilityChest(helper.absolutePos(pos.below()), Blocks.CHEST.defaultBlockState());
    helper.getLevel().setBlockEntity(target);
    var hopper = (HopperBlockEntity) helper.getBlockEntity(pos);
    hopper.setItem(0, new ItemStack(Items.DIAMOND, 3));
    helper.succeedWhen(() -> {
      if (helper.getLevel().getGameTime() % 2 != 0) helper.fail("Waiting for hopper interval");
      tick(helper, hopper);
      check(helper, target.sink.getStackInSlot(0).getCount() == 1 && hopper.getItem(0).getCount() == 2,
        "Hopper must use the upper capability and deduct only its accepted count");
      tick(helper, hopper);
      check(helper, hopper.getItem(0).getCount() == 2 && target.isEmpty(), "Full capability must not fall back to the container");
      helper.setBlock(pos.below(), TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState());
      tick(helper, hopper);
      check(helper, hopper.getItem(0).getCount() == 1, "Hopper must insert into item pipe capability");
      helper.setBlock(pos.below(), TransportBlocks.get(PipeType.ITEM, PipeMaterial.WOOD).get().defaultBlockState());
      tick(helper, hopper);
      check(helper, hopper.getItem(0).getCount() == 1, "A rejecting pipe must leave source items untouched");
    });
  }
}
