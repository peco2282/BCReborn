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
import com.peco2282.bcreborn.api.facades.FacadeType;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.silicon.block.entity.IntegrationTableBlockEntity;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GateExpansionPulsar;
import com.peco2282.bcreborn.transport.gates.GateExpansionTimer;
import com.peco2282.bcreborn.transport.gates.ItemGate;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the dynamic Integration Table recipes ported from the original 7.1.x {@code GateExpansionRecipe} and
 * {@code AdvancedFacadeRecipe}, and the slot rules of {@code TileIntegrationTable}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class IntegrationTableGameTests {
  private static final BlockPos TABLE = new BlockPos(1, 2, 1);
  private static final int COST = 25000;

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static IntegrationTableBlockEntity place(GameTestHelper helper) {
    helper.setBlock(TABLE, SiliconBlocks.INTEGRATION_TABLE.get().defaultBlockState());
    return (IntegrationTableBlockEntity) helper.getBlockEntity(TABLE);
  }

  private static void tick(GameTestHelper helper, BuildCraftBlockEntity entity) {
    BuildCraftBlockEntity.<BuildCraftBlockEntity>ticker().tick(helper.getLevel(), entity.getBlockPos(), entity.getBlockState(), entity);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateTakesExpansionsFromChipsets(GameTestHelper helper) {
    IntegrationTableBlockEntity table = place(helper);
    table.setItem(0, ItemGate.makeGateItem(GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND));
    check(helper, !table.canCraft(), "A gate without expansions is not craftable");

    table.setItem(1, new ItemStack(SiliconItems.PULSATING_CHIPSET.get(), 2));
    table.setItem(2, new ItemStack(SiliconItems.QUARTZ_CHIPSET.get()));
    check(helper, table.canCraft() && table.getRequiredEnergy() == COST, "Gate expansion costs " + COST);

    table.receiveLaserEnergy(COST - 1);
    tick(helper, table);
    check(helper, table.getItem(9).isEmpty(), "Nothing is crafted before the energy is there");
    table.receiveLaserEnergy(1);
    tick(helper, table);

    ItemStack gate = table.getItem(9);
    check(helper, gate.getItem() instanceof ItemGate
        && ItemGate.getMaterial(gate) == GateDefinition.GateMaterial.IRON
        && ItemGate.hasGateExpansion(gate, GateExpansionPulsar.INSTANCE)
        && ItemGate.hasGateExpansion(gate, GateExpansionTimer.INSTANCE),
      "The gate must get both expansions, got " + gate.getTag());
    check(helper, table.getItem(0).isEmpty() && table.getItem(1).getCount() == 1 && table.getItem(2).isEmpty(),
      "One chipset per expansion is consumed");
    check(helper, table.getEnergy() == 0, "The energy is used");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void redstoneChipsetSwapsGateLogic(GameTestHelper helper) {
    IntegrationTableBlockEntity table = place(helper);
    table.setItem(0, ItemGate.makeGateItem(GateDefinition.GateMaterial.GOLD, GateDefinition.GateLogic.AND));
    table.setItem(1, new ItemStack(SiliconItems.REDSTONE_CHIPSET.get()));
    table.receiveLaserEnergy(COST);
    tick(helper, table);

    ItemStack gate = table.getItem(9);
    check(helper, gate.getItem() instanceof ItemGate && ItemGate.getLogic(gate) == GateDefinition.GateLogic.OR,
      "A Redstone Chipset swaps AND to OR");
    // Original GateExpansionRecipe.craft(): the chipset used for the swap is not consumed.
    check(helper, table.getItem(1).getCount() == 1, "The Redstone Chipset stays in the table");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void facadeBecomesPhasedWithWire(GameTestHelper helper) {
    IntegrationTableBlockEntity table = place(helper);
    FacadeItem facadeItem = TransportItems.FACADE.get();
    table.setItem(0, facadeItem.getFacadeForBlock(Blocks.STONE.defaultBlockState()));
    table.setItem(1, PipeWire.RED.getStack());
    check(helper, !table.canCraft(), "A wire alone does not make a phased facade");
    table.setItem(2, facadeItem.getFacadeForBlock(Blocks.OAK_PLANKS.defaultBlockState()));
    table.receiveLaserEnergy(COST);
    tick(helper, table);

    ItemStack result = table.getItem(9);
    FacadeItem.FacadeState[] states = FacadeItem.getStates(result);
    check(helper, FacadeItem.getType(result) == FacadeType.Phased && states.length == 2,
      "The result must be a phased facade with two states, got " + result.getTag());
    check(helper, states[0].state().is(Blocks.STONE) && states[0].wire() == null
        && states[1].state().is(Blocks.OAK_PLANKS) && states[1].wire() == PipeWire.RED,
      "The second state shows the added block while the red wire is active");
    check(helper, table.getItem(0).isEmpty() && table.getItem(1).isEmpty() && table.getItem(2).isEmpty(),
      "The facades and the wire are consumed");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void expansionSlotsFollowTheActiveRecipe(GameTestHelper helper) {
    IntegrationTableBlockEntity table = place(helper);
    ItemStack chipset = new ItemStack(SiliconItems.PULSATING_CHIPSET.get());
    check(helper, table.canPlaceItem(0, new ItemStack(Items.DIRT)), "Anything may go in the input slot");
    check(helper, !table.canPlaceItem(1, chipset), "Without an input no expansion is accepted");
    check(helper, table.getMaxExpansionCount() == 0, "Without a recipe there are no expansion slots");

    table.setItem(0, ItemGate.makeGateItem(GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND));
    check(helper, table.canPlaceItem(1, chipset) && table.canPlaceItem(8, chipset), "A gate accepts chipsets in every expansion slot");
    check(helper, !table.canPlaceItem(1, new ItemStack(Items.DIRT)), "A gate does not accept dirt");
    check(helper, !table.canPlaceItem(9, chipset), "The output slot accepts nothing");

    table.setItem(0, TransportItems.FACADE.get().getFacadeForBlock(Blocks.STONE.defaultBlockState()));
    check(helper, table.getMaxExpansionCount() == 2, "A facade has two expansion slots");
    check(helper, table.canPlaceItem(2, PipeWire.RED.getStack()) && !table.canPlaceItem(3, PipeWire.RED.getStack()),
      "Expansions beyond the recipe's count are rejected");
    helper.succeed();
  }
}
