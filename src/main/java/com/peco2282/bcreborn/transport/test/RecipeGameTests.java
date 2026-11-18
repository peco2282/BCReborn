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
import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.silicon.block.entity.AssemblyTableBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.ItemGate;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.recipes.FacadeAssemblyRecipes;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * The recipes that made fluid pipes, power pipes, gates and facades obtainable (ROADMAP 6-1): pipe sealant, the
 * shapeless pipe conversions, the gate and facade recipes of the Assembly Table.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class RecipeGameTests {
  private static final BlockPos TABLE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /** The crafting grid result for the given items, or empty. */
  private static ItemStack craft(GameTestHelper helper, ItemLike... items) {
    AbstractContainerMenu menu = new AbstractContainerMenu(null, -1) {
      @Override
      public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
      }

      @Override
      public boolean stillValid(Player player) {
        return true;
      }
    };
    TransientCraftingContainer grid = new TransientCraftingContainer(menu, 3, 3);
    for (int i = 0; i < items.length; i++) {
      grid.setItem(i, new ItemStack(items[i]));
    }
    Optional<CraftingRecipe> recipe = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
    return recipe.map(r -> r.assemble(grid, helper.getLevel().registryAccess())).orElse(ItemStack.EMPTY);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void sealantAndPipeConversionsCraft(GameTestHelper helper) {
    ItemStack sealant = craft(helper, Items.GREEN_DYE);
    check(helper, sealant.is(TransportItems.PIPE_SEALANT.get()) && sealant.getCount() == 1,
      "Green dye must make one pipe sealant, got " + sealant);
    int fluid = 0;
    int energy = 0;
    for (PipeMaterial material : PipeMaterial.values()) {
      ItemLike itemPipe = TransportBlocks.get(PipeType.ITEM, material).get();
      if (PipeType.FLUID.supports(material)) {
        ItemStack result = craft(helper, itemPipe, TransportItems.PIPE_SEALANT.get());
        check(helper, result.is(TransportBlocks.get(PipeType.FLUID, material).get().asItem()) && result.getCount() == 1,
          material + ": item pipe + sealant must make one fluid pipe, got " + result);
        fluid++;
      }
      if (PipeType.ENERGY.supports(material)) {
        ItemStack result = craft(helper, itemPipe, Items.REDSTONE);
        check(helper, result.is(TransportBlocks.get(PipeType.ENERGY, material).get().asItem()) && result.getCount() == 1,
          material + ": item pipe + redstone must make one power pipe, got " + result);
        energy++;
      }
    }
    check(helper, fluid > 0 && energy > 0, "Some fluid and power pipes must exist");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void everyGateHasAnAssemblyRecipe(GameTestHelper helper) {
    for (GateDefinition.GateMaterial material : GateDefinition.GateMaterial.VALUES) {
      ResourceLocation id = ResourceLocation.parse("bcrebornsilicon:" + material.getTag() + "_gate");
      AssemblyRecipe recipe = BuildcraftRecipeRegistry.assembly().getRecipe(id);
      check(helper, recipe != null, "Missing gate recipe " + id);
      check(helper, ItemGate.getMaterial(recipe.result()) == material && ItemGate.getLogic(recipe.result()) == GateDefinition.GateLogic.AND,
        id + " must make an AND gate of its material, makes " + recipe.result().getTag());
      check(helper, recipe.ingredients().size() == 1 + material.maxWireColor,
        id + " needs a chipset and " + material.maxWireColor + " wires, has " + recipe.ingredients().size() + " ingredients");
    }
    helper.succeed();
  }

  private static AssemblyTableBlockEntity table(GameTestHelper helper) {
    helper.setBlock(TABLE, SiliconBlocks.ASSEMBLY_TABLE.get().defaultBlockState());
    return (AssemblyTableBlockEntity) helper.getBlockEntity(TABLE);
  }

  private static void tick(GameTestHelper helper, AssemblyTableBlockEntity table) {
    BuildCraftBlockEntity.<AssemblyTableBlockEntity>ticker().tick(helper.getLevel(), table.getBlockPos(), table.getBlockState(), table);
  }

  private static List<ItemEntity> dropped(GameTestHelper helper) {
    return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(TABLE)).inflate(2));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void assemblyTableMakesIronGate(GameTestHelper helper) {
    ResourceLocation id = ResourceLocation.parse("bcrebornsilicon:iron_gate");
    AssemblyTableBlockEntity table = table(helper);
    table.setItem(0, new ItemStack(SiliconItems.IRON_CHIPSET.get()));
    table.setItem(1, new ItemStack(TransportItems.PIPE_WIRE_RED.get()));
    table.setItem(2, new ItemStack(TransportItems.PIPE_WIRE_BLUE.get()));
    check(helper, table.getPotentialOutputs().stream().anyMatch(r -> r.id().equals(id)), "An iron chipset and two wires make an iron gate");
    table.planOutput(id);
    table.receiveLaserEnergy(200000);
    tick(helper, table);
    List<ItemEntity> gates = dropped(helper).stream().filter(e -> e.getItem().is(TransportItems.GATE.get())).toList();
    check(helper, gates.size() == 1 && ItemGate.getMaterial(gates.get(0).getItem()) == GateDefinition.GateMaterial.IRON,
      "The table must make one iron gate, made " + gates.stream().map(e -> e.getItem().getTag()).toList());
    check(helper, table.getItem(0).isEmpty() && table.getItem(1).isEmpty() && table.getItem(2).isEmpty(),
      "The chipset and both wires must be used");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void assemblyTableMakesFacades(GameTestHelper helper) {
    ResourceLocation id = FacadeAssemblyRecipes.id(Blocks.STONE);
    check(helper, BuildcraftRecipeRegistry.assembly().getRecipe(id) != null, "A facade recipe must exist for stone");
    check(helper, BuildcraftRecipeRegistry.assembly().getRecipe(FacadeAssemblyRecipes.id(Blocks.TORCH)) == null,
      "A block that is not a full cube must have no facade recipe");
    AssemblyTableBlockEntity table = table(helper);
    table.setItem(0, new ItemStack(TransportBlocks.get(PipeType.STRUCTURE, PipeMaterial.COBBLESTONE).get(), 3));
    table.setItem(1, new ItemStack(Blocks.STONE, 2));
    check(helper, table.getPotentialOutputs().stream().anyMatch(r -> r.id().equals(id)), "Three structure pipes and stone make facades");
    table.planOutput(id);
    table.receiveLaserEnergy(FacadeAssemblyRecipes.ENERGY);
    tick(helper, table);
    int facades = 0;
    for (ItemEntity entity : dropped(helper)) {
      ItemStack stack = entity.getItem();
      if (stack.is(TransportItems.FACADE.get())) {
        check(helper, FacadeItem.getStates(stack).length == 1 && FacadeItem.getStates(stack)[0].state().is(Blocks.STONE),
          "The facade must show stone");
        facades += stack.getCount();
      }
    }
    check(helper, facades == FacadeAssemblyRecipes.FACADES_PER_CRAFT, "One craft must make six facades, made " + facades);
    check(helper, table.getItem(0).isEmpty() && table.getItem(1).getCount() == 1,
      "Three structure pipes and one stone must be used, left " + table.getItem(0) + " and " + table.getItem(1));
    helper.succeed();
  }
}
