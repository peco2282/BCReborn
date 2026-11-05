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
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.menu.EmzuliPipeMenu;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import com.peco2282.bcreborn.transport.pipe.runtime.EmzuliState;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the colour pipes ported from the original 7.1.x {@code PipeItemsLapis}, {@code PipeItemsDaizuli},
 * {@code PipeItemsEmzuli} and {@code ContainerEmzuliPipe}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class ColorPipeGameTests {
  private static final BlockPos A = new BlockPos(0, 1, 1);
  private static final BlockPos B = new BlockPos(1, 1, 1);
  private static final BlockPos C = new BlockPos(2, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    BlockState state = TransportBlocks.get(PipeType.ITEM, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  private static FakePlayer player(GameTestHelper helper, BlockPos pos, String name) {
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
    BlockPos abs = helper.absolutePos(pos);
    player.setPos(abs.getX() + 0.5, abs.getY() + 2, abs.getZ() + 0.5);
    return player;
  }

  /** Right-clicks the pipe centre from above. */
  private static void use(GameTestHelper helper, BlockPos pos, FakePlayer player, ItemStack held, boolean sneak) {
    BlockPos abs = helper.absolutePos(pos);
    player.setItemInHand(InteractionHand.MAIN_HAND, held);
    player.setShiftKeyDown(sneak);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.25, 0), Direction.UP, abs, false);
    helper.getBlockState(pos).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
  }

  private static StatementSlot slot(IStatement statement) {
    StatementSlot slot = new StatementSlot();
    slot.statement = statement;
    return slot;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void lapisPaintsTravellingItemNotStack(GameTestHelper helper) {
    PipeBlockEntity lapis = placePipe(helper, A, PipeMaterial.LAPIS);
    PipeBlockEntity next = placePipe(helper, B, PipeMaterial.COBBLESTONE);
    lapis.setPipeColor(DyeColor.RED);
    lapis.injectItem(new ItemStack(Items.COBBLESTONE), Direction.WEST);

    helper.succeedWhen(() -> {
      List<TravelingItem> items = next.getTravelingItems();
      helper.assertTrue(!items.isEmpty(), "The item must reach the next pipe");
      TravelingItem item = items.get(0);
      helper.assertTrue(item.getColor() == DyeColor.RED,
        "The paint must travel with the item into the next pipe, got " + item.getColor());
      helper.assertTrue(!item.getStack().hasTag(), "The paint must not be written into the stack NBT");
      TravelingItem restored = TravelingItem.load(item.save());
      helper.assertTrue(restored.getColor() == DyeColor.RED, "The paint must be saved with the travelling item");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void lapisWrenchAndDyeChangeColour(GameTestHelper helper) {
    PipeBlockEntity lapis = placePipe(helper, B, PipeMaterial.LAPIS);
    FakePlayer player = player(helper, B, "LapisTest");

    use(helper, B, player, new ItemStack(CoreItems.WRENCH.get()), false);
    check(helper, lapis.getPipeColor() == DyeColor.RED,
      "Wrench must move from the default black to the next colour, red (original EnumColor order), got " + lapis.getPipeColor());
    use(helper, B, player, new ItemStack(CoreItems.WRENCH.get()), true);
    check(helper, lapis.getPipeColor() == DyeColor.BLACK, "Sneak + wrench must go back to black, got " + lapis.getPipeColor());
    use(helper, B, player, new ItemStack(Items.LIGHT_BLUE_DYE), false);
    check(helper, lapis.getPipeColor() == DyeColor.LIGHT_BLUE, "A dye must set its colour, got " + lapis.getPipeColor());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void daizuliSortsByColour(GameTestHelper helper) {
    helper.setBlock(B.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(B.south(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity daizuli = placePipe(helper, B, PipeMaterial.DAIZULI);
    check(helper, daizuli.getIronPipeOutput() == Direction.NORTH, "Placement must pick the first valid output, got " + daizuli.getIronPipeOutput());
    daizuli.setPipeColor(DyeColor.RED);

    daizuli.injectItemWithSpeed(new ItemStack(Items.REDSTONE), Direction.WEST, 0.01f, DyeColor.RED);
    daizuli.injectItemWithSpeed(new ItemStack(Items.LAPIS_LAZULI), Direction.WEST, 0.01f, DyeColor.BLUE);
    daizuli.injectItemWithSpeed(new ItemStack(Items.STICK), Direction.WEST, 0.01f, null);
    List<TravelingItem> items = daizuli.getTravelingItems();
    check(helper, items.get(0).getNextDirection() == Direction.NORTH, "An item of the pipe colour must go to the output, got " + items.get(0).getNextDirection());
    check(helper, items.get(1).getNextDirection() == Direction.SOUTH, "Another colour must avoid the output, got " + items.get(1).getNextDirection());
    check(helper, items.get(2).getNextDirection() == Direction.SOUTH, "An unpainted item must avoid the output, got " + items.get(2).getNextDirection());

    // Wrench on the centre cycles the output (PipeLogicIron); sneak + wrench changes the colour.
    FakePlayer player = player(helper, B, "DaizuliTest");
    use(helper, B, player, new ItemStack(CoreItems.WRENCH.get()), false);
    check(helper, daizuli.getIronPipeOutput() == Direction.SOUTH, "Wrench must switch the output, got " + daizuli.getIronPipeOutput());
    use(helper, B, player, new ItemStack(CoreItems.WRENCH.get()), true);
    check(helper, daizuli.getPipeColor() == DyeColor.GREEN, "Sneak + wrench must pick the next colour (red -> green), got " + daizuli.getPipeColor());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void daizuliGateActions(GameTestHelper helper) {
    helper.setBlock(B.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(B.east(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity daizuli = placePipe(helper, B, PipeMaterial.DAIZULI);
    Gate gate = new Gate(daizuli.getPipe(), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND, Direction.UP);
    daizuli.setPipePluggable(Direction.UP, new GatePluggable(gate));
    List<IStatement> actions = gate.getAllValidActions();
    check(helper, actions.contains(TransportStatements.ACTION_PIPE_COLOR.get(DyeColor.CYAN).get()), "Daizuli must offer the colour actions");
    check(helper, actions.contains(TransportStatements.ACTION_PIPE_DIRECTION.get(Direction.EAST).get()), "Daizuli must offer a direction per connected side");
    check(helper, !actions.contains(TransportStatements.ACTION_PIPE_DIRECTION.get(Direction.DOWN).get()), "No direction for an unconnected side");

    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, TransportStatements.ACTION_PIPE_COLOR.get(DyeColor.CYAN).get());
    gate.setTrigger(1, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(1, TransportStatements.ACTION_PIPE_DIRECTION.get(Direction.EAST).get());
    helper.runAfterDelay(3, () -> {
      check(helper, daizuli.getPipeColor() == DyeColor.CYAN, "The colour action must recolour the pipe, got " + daizuli.getPipeColor());
      check(helper, daizuli.getIronPipeOutput() == Direction.EAST, "The direction action must set the output, got " + daizuli.getIronPipeOutput());
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void emzuliExtractsOnlyActivePresetAndPaints(GameTestHelper helper) {
    helper.setBlock(A, Blocks.CHEST.defaultBlockState());
    ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(A);
    chest.setItem(0, new ItemStack(Items.DIRT, 16));
    chest.setItem(1, new ItemStack(Items.COBBLESTONE, 16));
    PipeBlockEntity emzuli = placePipe(helper, B, PipeMaterial.EMZULI);
    placePipe(helper, C, PipeMaterial.COBBLESTONE);
    check(helper, emzuli.getExtractionSide() == Direction.WEST, "The Emzuli pipe must extract from the chest, got " + emzuli.getExtractionSide());
    check(helper, emzuli.getBattery() != null, "The Emzuli pipe needs a battery for engine power");
    emzuli.getBattery().setEnergy(1000);

    EmzuliState state = emzuli.getEmzuliState();
    check(helper, state != null, "An Emzuli pipe must have Emzuli state");
    // Slot 1 = blue preset: cobblestone, painted yellow. Slot 0 = red preset: dirt.
    state.getFilters().setItem(1, new ItemStack(Items.COBBLESTONE));
    state.getFilters().setItem(0, new ItemStack(Items.DIRT));
    state.setSlotColor(1, DyeColor.YELLOW);

    helper.runAfterDelay(30, () -> {
      check(helper, chest.getItem(0).getCount() == 16 && chest.getItem(1).getCount() == 16,
        "Without an active extraction preset nothing may be extracted (original activeFlags.isEmpty())");
      emzuli.actionsActivated(List.of(slot(TransportStatements.ACTION_EXTRACTION_PRESET.get(DyeColor.BLUE).get())));
      check(helper, state.isPresetActive(1) && !state.isPresetActive(0), "The blue preset must enable slot 1 only");

      helper.succeedWhen(() -> {
        helper.assertTrue(chest.getItem(1).getCount() < 16, "The blue slot must extract cobblestone");
        helper.assertTrue(chest.getItem(0).getCount() == 16, "The inactive red slot must not extract dirt");
        TravelingItem item = emzuli.getTravelingItems().isEmpty()
          ? ((PipeBlockEntity) helper.getBlockEntity(C)).getTravelingItems().stream().findFirst().orElse(null)
          : emzuli.getTravelingItems().get(0);
        helper.assertTrue(item != null && item.getStack().is(Items.COBBLESTONE), "A cobblestone item must travel");
        helper.assertTrue(item.getColor() == DyeColor.YELLOW, "Items from slot 1 must be painted yellow, got " + item.getColor());
      });
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void emzuliGateOffersPresets(GameTestHelper helper) {
    PipeBlockEntity emzuli = placePipe(helper, B, PipeMaterial.EMZULI);
    Gate gate = new Gate(emzuli.getPipe(), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND, Direction.UP);
    emzuli.setPipePluggable(Direction.UP, new GatePluggable(gate));
    List<IStatement> actions = gate.getAllValidActions();
    for (DyeColor color : EmzuliState.PRESET_COLORS) {
      check(helper, actions.contains(TransportStatements.ACTION_EXTRACTION_PRESET.get(color).get()), "Missing preset " + color);
    }
    check(helper, !actions.contains(TransportStatements.ACTION_EXTRACTION_PRESET.get(DyeColor.PINK).get()),
      "Only the red, blue, green and yellow presets exist in the original");
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, TransportStatements.ACTION_EXTRACTION_PRESET.get(DyeColor.GREEN).get());
    helper.runAfterDelay(3, () -> {
      check(helper, emzuli.getEmzuliState().isPresetActive(2), "An active gate preset action must enable the green slot");
      helper.succeed();
    });
  }

  private static void sync(AbstractContainerMenu server, AbstractContainerMenu client) {
    server.addSlotListener(new ContainerListener() {
      @Override
      public void slotChanged(AbstractContainerMenu menu, int slot, ItemStack stack) {
      }

      @Override
      public void dataChanged(AbstractContainerMenu menu, int id, int value) {
        client.setData(id, (short) value);
      }
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void emzuliPaintButtonsSyncAndSave(GameTestHelper helper) {
    PipeBlockEntity emzuli = placePipe(helper, B, PipeMaterial.EMZULI);
    FakePlayer player = player(helper, B, "EmzuliMenuTest");
    EmzuliPipeMenu server = new EmzuliPipeMenu(1, player.getInventory(), emzuli);
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    buf.writeBlockPos(emzuli.getBlockPos());
    EmzuliPipeMenu client = new EmzuliPipeMenu(1, new Inventory(player), buf);
    sync(server, client);

    server.clickMenuButton(player, EmzuliPipeMenu.buttonId(2, EmzuliPipeMenu.BUTTON_LEFT));
    server.clickMenuButton(player, EmzuliPipeMenu.buttonId(0, EmzuliPipeMenu.BUTTON_RIGHT));
    server.clickMenuButton(player, EmzuliPipeMenu.buttonId(3, EmzuliPipeMenu.BUTTON_LEFT));
    server.clickMenuButton(player, EmzuliPipeMenu.buttonId(3, EmzuliPipeMenu.BUTTON_MIDDLE));
    server.broadcastChanges();
    check(helper, client.getSlotColor(2) == DyeColor.BLACK, "Left click from 'none' must give black (original list order), got " + client.getSlotColor(2));
    check(helper, client.getSlotColor(0) == DyeColor.WHITE, "Right click from 'none' must wrap to white, got " + client.getSlotColor(0));
    check(helper, client.getSlotColor(3) == null, "Middle click must clear the paint");
    check(helper, EmzuliState.cycle(DyeColor.WHITE, true) == null && EmzuliState.cycle(DyeColor.BLACK, true) == DyeColor.RED,
      "The paint list must be none, black, red, ... white");

    emzuli.getEmzuliState().getFilters().setItem(1, new ItemStack(Items.IRON_INGOT));
    CompoundTag saved = emzuli.saveWithoutMetadata();
    PipeBlockEntity restored = new PipeBlockEntity(emzuli.getBlockPos(), emzuli.getBlockState(), PipeType.ITEM, PipeMaterial.EMZULI);
    restored.load(saved);
    EmzuliState state = restored.getEmzuliState();
    check(helper, state.getSlotColor(0) == DyeColor.WHITE && state.getSlotColor(2) == DyeColor.BLACK,
      "Slot colours must be saved (original slotColors[i])");
    check(helper, state.getFilters().getItem(1).is(Items.IRON_INGOT), "Filters must be saved");
    helper.succeed();
  }
}
