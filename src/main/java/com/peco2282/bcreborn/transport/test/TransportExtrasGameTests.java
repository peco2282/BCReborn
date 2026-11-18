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
import com.peco2282.bcreborn.core.statements.ActionRedstoneOutput;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.FilteredBufferBlockEntity;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.item.GateCopierItem;
import com.peco2282.bcreborn.transport.menu.FilteredBufferMenu;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.pluggable.PowerAdapterPluggable;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import com.peco2282.bcreborn.common.gui.slots.IPhantomSlot;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

/** The Filtered Buffer, Gate Copier and Power Adapter of the original transport module. */
@GameTestHolder(BCRebornTransport.MODID)
public class TransportExtrasGameTests {
  private static final BlockPos POS = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static FilteredBufferBlockEntity buffer(GameTestHelper helper) {
    helper.setBlock(POS, TransportBlocks.FILTERED_BUFFER.get());
    return (FilteredBufferBlockEntity) helper.getBlockEntity(POS);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void filteredBufferTakesOnlyFilteredItems(GameTestHelper helper) {
    FilteredBufferBlockEntity buffer = buffer(helper);
    buffer.getFilters().setItem(0, new ItemStack(Items.COBBLESTONE));
    buffer.getFilters().setItem(1, new ItemStack(Items.IRON_PICKAXE));
    IItemHandler handler = buffer.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElseThrow(IllegalStateException::new);
    check(helper, handler.getSlots() == 9, "Nine storage slots");
    check(helper, handler.insertItem(0, new ItemStack(Items.COBBLESTONE, 64), false).isEmpty(), "Slot 0 takes cobblestone");
    check(helper, handler.insertItem(0, new ItemStack(Items.DIRT), false).getCount() == 1, "Slot 0 refuses dirt");
    check(helper, handler.insertItem(2, new ItemStack(Items.COBBLESTONE), false).getCount() == 1, "A slot without a filter takes nothing");
    ItemStack worn = new ItemStack(Items.IRON_PICKAXE);
    worn.setDamageValue(100);
    check(helper, handler.insertItem(1, worn, false).isEmpty(), "A damageable item fits its filter at any damage");
    check(helper, buffer.getItem(0).getCount() == 64 && buffer.getItem(1).is(Items.IRON_PICKAXE), "The items must be stored");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void filteredBufferKeepsAndDropsItsContents(GameTestHelper helper) {
    FilteredBufferBlockEntity buffer = buffer(helper);
    buffer.getFilters().setItem(3, new ItemStack(Items.COBBLESTONE));
    buffer.setItem(3, new ItemStack(Items.COBBLESTONE, 10));
    CompoundTag saved = buffer.saveWithoutMetadata();
    FilteredBufferBlockEntity copy = new FilteredBufferBlockEntity(buffer.getBlockPos(), buffer.getBlockState());
    copy.load(saved);
    check(helper, copy.getItem(3).getCount() == 10 && copy.getFilters().getItem(3).is(Items.COBBLESTONE), "Storage and filters must be saved");
    helper.getLevel().destroyBlock(helper.absolutePos(POS), true);
    int dropped = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(POS)).inflate(2)).stream()
      .filter(e -> e.getItem().is(Items.COBBLESTONE)).mapToInt(e -> e.getItem().getCount()).sum();
    check(helper, dropped == 10, "Breaking the buffer drops what it stores, dropped " + dropped);
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void filteredBufferFilterIsLockedWhileItsSlotHoldsItems(GameTestHelper helper) {
    FilteredBufferBlockEntity buffer = buffer(helper);
    buffer.getFilters().setItem(0, new ItemStack(Items.COBBLESTONE));
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "BufferTest"));
    FilteredBufferMenu menu = new FilteredBufferMenu(1, player.getInventory(), buffer);
    check(helper, menu.getSlot(0) instanceof IPhantomSlot filter && filter.canAdjust(), "An empty slot's filter can change");
    buffer.setItem(0, new ItemStack(Items.COBBLESTONE));
    check(helper, menu.getSlot(0) instanceof IPhantomSlot filter && !filter.canAdjust(), "A filter is locked while its slot holds items");
    check(helper, !menu.getSlot(3).mayPlace(new ItemStack(Items.DIRT)) && menu.getSlot(1).mayPlace(new ItemStack(Items.COBBLESTONE)),
      "Storage slots only take their filter's item");
    helper.succeed();
  }

  private static Gate gate(GameTestHelper helper, BlockPos pos, GateDefinition.GateMaterial material, GateDefinition.GateLogic logic) {
    helper.setBlock(pos, TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get());
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    Gate gate = new Gate(pipe.getPipe(), material, logic, Direction.UP);
    pipe.setPipePluggable(Direction.UP, new GatePluggable(gate));
    return ((GatePluggable) pipe.getPipePluggable(Direction.UP)).realGate;
  }

  /** Right-clicks the top of the pipe at {@code pos}, where its gate sits. */
  private static void click(GameTestHelper helper, FakePlayer player, BlockPos pos, boolean sneaking) {
    BlockPos abs = helper.absolutePos(pos);
    player.setShiftKeyDown(sneaking);
    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.45, 0), Direction.UP, abs, false);
    UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
    stack.getItem().onItemUseFirst(stack, context);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateCopierCopiesAndPastesRules(GameTestHelper helper) {
    Gate source = gate(helper, new BlockPos(0, 1, 1), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND);
    source.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    // An action every pipe offers (a pipe direction action is only valid on an iron pipe).
    source.setAction(0, source.getAllValidActions().stream().filter(a -> a instanceof ActionRedstoneOutput).findFirst().orElseThrow());
    Gate target = gate(helper, new BlockPos(2, 1, 1), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND);

    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "CopierTest"));
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(TransportItems.GATE_COPIER.get()));
    ItemStack copier = player.getItemInHand(InteractionHand.MAIN_HAND);
    click(helper, player, new BlockPos(2, 1, 1), true);
    check(helper, !GateCopierItem.hasCopy(copier) && target.getTrigger(0) == null, "An empty copier pastes nothing");
    click(helper, player, new BlockPos(0, 1, 1), false);
    check(helper, GateCopierItem.hasCopy(copier), "Right-clicking a gate copies it");
    click(helper, player, new BlockPos(2, 1, 1), true);
    check(helper, target.getTrigger(0) == source.getTrigger(0) && target.getAction(0) == source.getAction(0),
      "Sneak + right-click pastes the rules, got " + target.getTrigger(0) + " / " + target.getAction(0));
    player.setShiftKeyDown(false);
    helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
    click(helper, player, new BlockPos(1, 1, 1), false);
    copier.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
      new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(new BlockPos(1, 1, 1))), Direction.UP, helper.absolutePos(new BlockPos(1, 1, 1)), false)));
    check(helper, !GateCopierItem.hasCopy(copier), "Copying something that is not a gate clears the copier");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateCopierWarnsAboutSmallerGatesAndOtherLogic(GameTestHelper helper) {
    Gate source = gate(helper, new BlockPos(0, 1, 1), GateDefinition.GateMaterial.DIAMOND, GateDefinition.GateLogic.AND);
    source.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    Gate target = gate(helper, new BlockPos(2, 1, 1), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.OR);
    List<String> warnings = GateCopierItem.paste(target, GateCopierItem.copy(source));
    check(helper, warnings.contains("warning.slots") && warnings.contains("warning.logic"),
      "A smaller gate with other logic must warn, got " + warnings);
    check(helper, target.getTrigger(0) == source.getTrigger(0), "What fits is still pasted");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void powerAdapterLetsAPowerPipeChargeAStripesPipe(GameTestHelper helper) {
    BlockPos engine = new BlockPos(0, 1, 1);
    helper.setBlock(engine.below(), Blocks.REDSTONE_BLOCK);
    helper.setBlock(engine, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.EAST));
    BlockPos powerPos = new BlockPos(1, 1, 1);
    BlockPos stripesPos = new BlockPos(2, 1, 1);
    helper.setBlock(stripesPos, TransportBlocks.get(PipeType.ITEM, PipeMaterial.STRIPES).get());
    PipeBlockEntity stripes = (PipeBlockEntity) helper.getBlockEntity(stripesPos);
    helper.setBlock(powerPos, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get());
    check(helper, !PipeBlock.isConnected(helper.getBlockState(powerPos), Direction.EAST), "Without an adapter the power pipe stays apart");
    // Installing the adapter afterwards must connect the power pipe already there.
    stripes.setPipePluggable(Direction.WEST, new PowerAdapterPluggable());

    IEnergyStorage adapter = stripes.getCapability(ForgeCapabilities.ENERGY, Direction.WEST).orElseThrow(IllegalStateException::new);
    check(helper, adapter.receiveEnergy(1000, true) == PowerAdapterPluggable.MAX_POWER && !adapter.canExtract(),
      "The adapter passes at most 40 RF per call and gives nothing back");
    check(helper, PipeBlock.isConnected(helper.getBlockState(powerPos), Direction.EAST), "The power pipe must connect to the adapter");
    check(helper, !PipeBlock.isConnected(helper.getBlockState(stripesPos), Direction.WEST), "The adapter closes its side of the item pipe");
    helper.succeedWhen(() -> helper.assertTrue(stripes.getExtractionEnergy() > 0,
      "Power must reach the stripes pipe's battery through the adapter"));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void powerPipeIgnoresAnItemPipeWithoutAdapter(GameTestHelper helper) {
    BlockPos engine = new BlockPos(0, 1, 1);
    helper.setBlock(engine.below(), Blocks.REDSTONE_BLOCK);
    helper.setBlock(engine, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.EAST));
    helper.setBlock(new BlockPos(2, 1, 1), TransportBlocks.get(PipeType.ITEM, PipeMaterial.STRIPES).get());
    PipeBlockEntity stripes = (PipeBlockEntity) helper.getBlockEntity(new BlockPos(2, 1, 1));
    helper.setBlock(new BlockPos(1, 1, 1), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get());
    helper.runAfterDelay(40, () -> {
      check(helper, !PipeBlock.isConnected(helper.getBlockState(new BlockPos(1, 1, 1)), Direction.EAST), "No adapter, no connection");
      check(helper, stripes.getExtractionEnergy() == 0, "Without an adapter the item pipe gets no power from a power pipe");
      helper.succeed();
    });
  }
}
