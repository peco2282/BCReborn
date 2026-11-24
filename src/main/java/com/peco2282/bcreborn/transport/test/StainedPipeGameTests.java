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
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeGlassColor;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.recipes.PipeColoringRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Stained pipes, after the original {@code TileGenericPipe.glassColor}, {@code PipeColoringRecipe} and
 * {@code BlockGenericPipe.recolourBlock}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class StainedPipeGameTests {
  private static final BlockPos A = new BlockPos(0, 1, 1);
  private static final BlockPos B = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material) {
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  /** Whether the two pipes at A and B (A west of B) are joined, seen from both. */
  private static boolean joined(GameTestHelper helper) {
    boolean a = PipeBlock.isConnected(helper.getBlockState(A), Direction.EAST);
    boolean b = PipeBlock.isConnected(helper.getBlockState(B), Direction.WEST);
    if (a != b) helper.fail("The two pipes disagree about their joint: A=" + a + " B=" + b);
    return a;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void differentlyStainedPipesStayApart(GameTestHelper helper) {
    for (PipeType type : new PipeType[]{PipeType.ITEM, PipeType.FLUID, PipeType.ENERGY, PipeType.STRUCTURE}) {
      PipeBlockEntity a = placePipe(helper, A, type, PipeMaterial.COBBLESTONE);
      PipeBlockEntity b = placePipe(helper, B, type, PipeMaterial.COBBLESTONE);
      check(helper, joined(helper), type + ": two plain pipes must join");
      check(helper, a.setGlassColor(DyeColor.RED), type + ": staining must report a change");
      check(helper, !a.setGlassColor(DyeColor.RED), type + ": the same colour again is no change");
      check(helper, joined(helper), type + ": a stained pipe must join a plain one");
      b.setGlassColor(DyeColor.BLUE);
      check(helper, !joined(helper), type + ": red and blue pipes must not join");
      b.setGlassColor(DyeColor.RED);
      check(helper, joined(helper), type + ": two red pipes must join");
      b.setGlassColor(DyeColor.GREEN);
      a.setGlassColor(null);
      check(helper, joined(helper), type + ": a washed pipe must join a green one");
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stainedColourIsSavedAndKeptApartFromLapisColour(GameTestHelper helper) {
    PipeBlockEntity lapis = placePipe(helper, A, PipeType.ITEM, PipeMaterial.LAPIS);
    lapis.setPipeColor(DyeColor.YELLOW);
    lapis.setGlassColor(DyeColor.PURPLE);
    PipeBlockEntity copy = (PipeBlockEntity) PipeBlockEntity.loadStatic(helper.absolutePos(A), helper.getBlockState(A),
      lapis.saveWithFullMetadata());
    check(helper, copy != null && copy.getGlassColor() == DyeColor.PURPLE, "The glass colour must be saved");
    check(helper, copy.getPipeColor() == DyeColor.YELLOW, "The Lapis colour must stay its own value");
    helper.succeed();
  }

  private static FakePlayer player(GameTestHelper helper, ItemStack held) {
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "stainer"));
    player.setItemInHand(InteractionHand.MAIN_HAND, held);
    return player;
  }

  private static void brush(GameTestHelper helper, Player player, BlockPos pos, Direction side) {
    BlockPos abs = helper.absolutePos(pos);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), side, abs, false);
    player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void paintbrushStainsAndCleanBrushWashes(GameTestHelper helper) {
    PipeBlockEntity a = placePipe(helper, A, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    placePipe(helper, B, PipeType.ITEM, PipeMaterial.COBBLESTONE).setGlassColor(DyeColor.BLUE);

    ItemStack red = new ItemStack(CoreItems.COLORED_PAINTBRUSH.get(DyeColor.RED).get());
    FakePlayer painter = player(helper, red);
    brush(helper, painter, A, Direction.UP);
    check(helper, a.getGlassColor() == DyeColor.RED, "The red brush must stain the pipe, got " + a.getGlassColor());
    check(helper, red.getDamageValue() == 1, "Staining must wear the brush, damage " + red.getDamageValue());
    check(helper, !joined(helper), "The red pipe must leave the blue one");
    brush(helper, painter, A, Direction.UP);
    check(helper, red.getDamageValue() == 1, "Painting the same colour again must not wear the brush");

    ItemStack clean = new ItemStack(CoreItems.PAINTBRUSH.get());
    FakePlayer washer = player(helper, clean);
    brush(helper, washer, A, Direction.UP);
    check(helper, a.getGlassColor() == null, "The clean brush must wash the pipe, got " + a.getGlassColor());
    check(helper, clean.getDamageValue() == 1, "Washing must wear the brush");
    check(helper, joined(helper), "The washed pipe must join the blue one again");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stainedPipeDropsAndPlacesStained(GameTestHelper helper) {
    PipeBlockEntity a = placePipe(helper, A, PipeType.FLUID, PipeMaterial.GOLD);
    a.setGlassColor(DyeColor.LIME);
    ServerLevel level = helper.getLevel();
    BlockPos abs = helper.absolutePos(A);
    List<ItemStack> drops = Block.getDrops(helper.getBlockState(A), level, abs, a);
    check(helper, drops.size() == 1, "The pipe must drop one item, got " + drops);
    ItemStack drop = drops.get(0);
    check(helper, drop.is(TransportBlocks.get(PipeType.FLUID, PipeMaterial.GOLD).get().asItem()), "Wrong drop " + drop);
    check(helper, PipeGlassColor.get(drop) == DyeColor.LIME, "The drop must keep the colour, got " + drop.getTag());

    ItemStack picked = helper.getBlockState(A).getBlock().getCloneItemStack(helper.getBlockState(A), null, level, abs, null);
    check(helper, PipeGlassColor.get(picked) == DyeColor.LIME, "Pick block must keep the colour");

    // A plain pipe drops a plain item that stacks with new ones.
    PipeBlockEntity b = placePipe(helper, B, PipeType.FLUID, PipeMaterial.GOLD);
    ItemStack plain = Block.getDrops(helper.getBlockState(B), level, helper.absolutePos(B), b).get(0);
    check(helper, !plain.hasTag(), "A plain pipe must drop without a tag, got " + plain.getTag());

    // Placing the stained item gives a stained pipe (vanilla BlockItem loads BlockEntityTag).
    helper.setBlock(B, net.minecraft.world.level.block.Blocks.AIR);
    PipeBlockEntity placed = placePipe(helper, B, PipeType.FLUID, PipeMaterial.GOLD);
    a.setGlassColor(DyeColor.RED);
    check(helper, joined(helper), "A plain pipe placed next to a red one must join it");
    BlockItem.updateCustomBlockEntityTag(level, null, helper.absolutePos(B), drop);
    helper.getBlockState(B).getBlock().setPlacedBy(level, helper.absolutePos(B), helper.getBlockState(B), null, drop);
    check(helper, placed.getGlassColor() == DyeColor.LIME, "The placed pipe must take the colour, got " + placed.getGlassColor());
    check(helper, !joined(helper), "A lime pipe placed next to a red one must not join it");
    helper.succeed();
  }

  private static TransientCraftingContainer grid(ItemStack... items) {
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
    for (int i = 0; i < items.length; i++) grid.setItem(i, items[i]);
    return grid;
  }

  private static ItemStack craft(GameTestHelper helper, TransientCraftingContainer grid) {
    Optional<CraftingRecipe> recipe = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());
    return recipe.map(r -> r.assemble(grid, helper.getLevel().registryAccess())).orElse(ItemStack.EMPTY);
  }

  private static ItemStack[] pipes(ItemStack pipe, int count, ItemStack extra) {
    ItemStack[] items = new ItemStack[count + 1];
    for (int i = 0; i < count; i++) items[i] = pipe.copy();
    items[count] = extra;
    return items;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void coloringRecipeStainsAndWashes(GameTestHelper helper) {
    ItemStack pipe = new ItemStack(TransportBlocks.get(PipeType.ITEM, PipeMaterial.STONE).get());
    ItemStack stained = craft(helper, grid(pipes(pipe, 8, new ItemStack(Items.ORANGE_DYE))));
    check(helper, stained.is(pipe.getItem()) && stained.getCount() == PipeColoringRecipe.PIPES_PER_DYE,
      "Eight pipes and a dye must make eight pipes, got " + stained);
    check(helper, PipeGlassColor.get(stained) == DyeColor.ORANGE, "The pipes must be orange, got " + stained.getTag());

    check(helper, craft(helper, grid(pipes(pipe, 7, new ItemStack(Items.ORANGE_DYE)))).isEmpty(),
      "Seven pipes and a dye must not craft");
    ItemStack[] mixed = pipes(pipe, 8, new ItemStack(Items.ORANGE_DYE));
    mixed[0] = new ItemStack(TransportBlocks.get(PipeType.ITEM, PipeMaterial.WOOD).get());
    check(helper, craft(helper, grid(mixed)).isEmpty(), "Two kinds of pipe must not craft");
    ItemStack[] twoDyes = pipes(pipe, 7, new ItemStack(Items.ORANGE_DYE));
    twoDyes = java.util.Arrays.copyOf(twoDyes, 9);
    twoDyes[8] = new ItemStack(Items.ORANGE_DYE);
    check(helper, craft(helper, grid(twoDyes)).isEmpty(), "Two dyes must not craft");

    // Restaining ignores the old colour.
    ItemStack restained = craft(helper, grid(pipes(stained.copyWithCount(1), 8, new ItemStack(Items.BLACK_DYE))));
    check(helper, PipeGlassColor.get(restained) == DyeColor.BLACK, "Stained pipes must take the new dye");

    ItemStack orange = stained.copyWithCount(1);
    TransientCraftingContainer wash = grid(orange, orange.copy(), orange.copy(), new ItemStack(Items.WATER_BUCKET));
    ItemStack washed = craft(helper, wash);
    check(helper, washed.is(pipe.getItem()) && washed.getCount() == 3 && !washed.hasTag(),
      "Three pipes and a water bucket must give three plain pipes, got " + washed + " " + washed.getTag());
    check(helper, ItemStack.isSameItemSameTags(washed.copyWithCount(1), pipe), "Washed pipes must stack with new ones");
    var remaining = helper.getLevel().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, wash, helper.getLevel());
    check(helper, remaining.get(3).is(Items.BUCKET), "The water bucket must come back empty, got " + remaining.get(3));
    check(helper, craft(helper, grid(new ItemStack(Items.WATER_BUCKET))).isEmpty(), "A bucket alone is not this recipe");
    helper.succeed();
  }
}
