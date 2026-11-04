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
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.energy.EnergyFluids;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import com.peco2282.bcreborn.energy.block.entity.StoneEngineBlockEntity;
import com.peco2282.bcreborn.energy.menu.IronEngineMenu;
import com.peco2282.bcreborn.energy.menu.StoneEngineMenu;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the engine GUI data and piston sync ported from the original 7.1.x {@code GuiEngine},
 * {@code GuiCombustionEngine} and {@code TileEngineBase}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class EngineGuiGameTests {
  private static final BlockPos ENGINE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /**
   * Copies the server menu data into a client menu the way the vanilla data packet does
   * ({@code ClientboundContainerSetDataPacket} writes each value as a short).
   */
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
  public void ironEngineMenuSyncsLargeEnergyAndTanks(GameTestHelper helper) {
    helper.setBlock(ENGINE, EnergyBlocks.IRON_ENGINE.get().defaultBlockState());
    IronEngineBlockEntity engine = (IronEngineBlockEntity) helper.getBlockEntity(ENGINE);
    CompoundTag tag = engine.saveWithoutMetadata();
    tag.getCompound("engineEnergy").putInt("Energy", 70000);
    engine.load(tag);
    engine.fill(new FluidStack(EnergyFluids.OIL_SOURCE.get(), 4000), FluidAction.EXECUTE);
    engine.fill(new FluidStack(Fluids.WATER, 2500), FluidAction.EXECUTE);

    var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "EngineGuiTest"));
    IronEngineMenu server = new IronEngineMenu(1, player.getInventory(), engine);
    IronEngineMenu client = new IronEngineMenu(1, new Inventory(player));
    sync(server, client);

    check(helper, client.getEnergy() == 70000,
      "Energy above 32767 must survive the 16-bit data slots, got " + client.getEnergy());
    check(helper, client.getMaxEnergy() == engine.getMaxEnergyStored(), "Max energy must sync");
    check(helper, client.getFuel().getAmount() == 4000 && client.getFuel().getFluid() == EnergyFluids.OIL_SOURCE.get(),
      "The client must see the fuel tank, got " + client.getFuel().getAmount());
    check(helper, client.getCoolant().getAmount() == 2500 && client.getCoolant().getFluid() == Fluids.WATER,
      "The client must see the coolant tank");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stoneEngineShiftClickMovesFuel(GameTestHelper helper) {
    helper.setBlock(ENGINE, EnergyBlocks.STONE_ENGINE.get().defaultBlockState());
    StoneEngineBlockEntity engine = (StoneEngineBlockEntity) helper.getBlockEntity(ENGINE);
    var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "EngineShiftTest"));
    BlockPos abs = helper.absolutePos(ENGINE);
    player.setPos(abs.getX() + 0.5, abs.getY() + 0.5, abs.getZ() + 0.5);
    player.getInventory().setItem(0, new ItemStack(Items.COAL, 8));
    StoneEngineMenu menu = new StoneEngineMenu(1, player.getInventory(), engine);

    // Slot layout: engine slot 0, then 27 main inventory slots, then the 9 hotbar slots (hotbar 0 = menu slot 28).
    menu.quickMoveStack(player, 28);
    check(helper, engine.getItem(0).is(Items.COAL) && engine.getItem(0).getCount() == 8,
      "Shift-click must move fuel into the engine (it used to return an acacia door)");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void syncDoesNotOverwritePistonPosition(GameTestHelper helper) {
    helper.setBlock(ENGINE, EnergyBlocks.STONE_ENGINE.get().defaultBlockState());
    StoneEngineBlockEntity engine = (StoneEngineBlockEntity) helper.getBlockEntity(ENGINE);
    engine.progress = 0.3f;

    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    engine.writeData(buf);
    StoneEngineBlockEntity client = new StoneEngineBlockEntity(engine.getBlockPos(), engine.getBlockState());
    client.progress = 0.7f;
    client.readData(buf);
    check(helper, client.progress == 0.7f,
      "The client animates on its own; sync must not overwrite the piston position (original TileEngineBase)");
    check(helper, buf.readableBytes() == 0, "Sync must carry only stage / pumping / orientation");
    helper.succeed();
  }
}
