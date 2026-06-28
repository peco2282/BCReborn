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
package com.peco2282.bcreborn.common.utils;

import com.peco2282.bcreborn.api.blueprints.BuilderAPI;
import com.peco2282.bcreborn.core.CoreConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BlockUtils {
  // Dummy constants for missing ones in BCRebornCore
  private static final boolean miningAllowPlayerProtectedBlocks = false;

  /**
   * Deactivate constructor
   */
  private BlockUtils() {
  }

  public static List<ItemStack> getItemStackFromBlock(ServerLevel world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);

    if (state.isAir()) {
      return List.of();
    }

    BlockEntity entity = world.getBlockEntity(pos);

    List<ItemStack> dropsList = Block.getDrops(state, world, pos, entity);
    // In 1.20.1, harvest check logic is different. Defaulting to 1.0f for now.
    float dropChance = 1.0f;

    ArrayList<ItemStack> returnList = new ArrayList<>();
    for (ItemStack s : dropsList) {
      if (world.random.nextFloat() <= dropChance) {
        returnList.add(s);
      }
    }

    return returnList;
  }

  public static boolean breakBlock(ServerLevel world, BlockPos pos) {
    return breakBlock(world, pos, CoreConfig.getItemLifespan() * 20);
  }

  public static boolean breakBlock(ServerLevel world, BlockPos pos, int forcedLifespan) {
    List<ItemStack> items = new ArrayList<>();

    if (breakBlock(world, pos, items)) {
      for (ItemStack item : items) {
        dropItem(world, pos, forcedLifespan, item);
      }
      return true;
    }
    return false;
  }

  public static Player getFakePlayerWithTool(ServerLevel world, BlockPos pos, ItemStack tool) {
    Player player = BCFakePlayer.createBuildCraftPlayer(world, pos);
    int i = 0;

    while (player.getMainHandItem() != tool && i < 9) {
      if (i > 0) {
        player.getInventory().setItem(i - 1, ItemStack.EMPTY);
      }

      player.getInventory().setItem(i, tool);
      i++;
    }

    return player;
  }

  public static boolean harvestBlock(ServerLevel world, BlockPos pos, ItemStack tool) {
    BlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    Player player = getFakePlayerWithTool(world, pos, tool);

    BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, state, player);
    MinecraftForge.EVENT_BUS.post(breakEvent);

    if (breakEvent.isCanceled()) {
      return false;
    }

    if (!state.canHarvestBlock(world, pos, player)) {
      return false;
    }

    block.playerWillDestroy(world, pos, state, player);
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

    return true;
  }

  public static boolean breakBlock(ServerLevel world, BlockPos pos, List<ItemStack> drops) {
    BlockState state = world.getBlockState(pos);
    BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, state,
      BCFakePlayer.createBuildCraftPlayer(world, pos));
    MinecraftForge.EVENT_BUS.post(breakEvent);

    if (breakEvent.isCanceled()) {
      return false;
    }

    if (!state.isAir() && !world.isClientSide
      && world.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).get()) {
      List<ItemStack> blockDrops = getItemStackFromBlock(world, pos);
      if (!blockDrops.isEmpty()) {
        drops.addAll(blockDrops);
      }
    }
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

    return true;
  }

  public static void dropItem(ServerLevel world, BlockPos pos, int forcedLifespan, ItemStack stack) {
    float var = 0.7F;
    double dx = world.random.nextFloat() * var + (1.0F - var) * 0.5D;
    double dy = world.random.nextFloat() * var + (1.0F - var) * 0.5D;
    double dz = world.random.nextFloat() * var + (1.0F - var) * 0.5D;
    ItemEntity entityitem = new ItemEntity(world, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, stack);

    entityitem.lifespan = forcedLifespan; // In 1.20.1, lifespan is handled differently or via accessor
    entityitem.setPickUpDelay(10);

    world.addFreshEntity(entityitem);
  }

  public static boolean canChangeBlock(Level world, BlockPos pos) {
    return canChangeBlock(world.getBlockState(pos).getBlock(), world, pos);
  }

  public static boolean canChangeBlock(Block block, Level world, BlockPos pos) {
    if (world.getBlockState(pos).isAir()) {
      return true;
    }

    if (isUnbreakableBlock(world, pos, block)) {
      return false;
    }
    FluidState fs = world.getFluidState(pos);
    if (fs.is(Fluids.LAVA) || fs.is(Fluids.FLOWING_LAVA)) {
      return false;
    } else if (block instanceof IFluidBlock && ((IFluidBlock) block).getFluid() != null) {
      Fluid f = ((IFluidBlock) block).getFluid();
      return f.getAmount(fs) < 3000;
    }

    return true;
  }

  public static float getBlockHardnessMining(Level world, BlockPos pos, Block b, ItemStack tool) {
    if (world instanceof ServerLevel && !miningAllowPlayerProtectedBlocks) {
      float relativeHardness = world.getBlockState(pos).getDestroyProgress(getFakePlayerWithTool((ServerLevel) world, pos, tool), world, pos);

      if (relativeHardness <= 0.0F) {
        return -1.0F;
      }
    }

    return world.getBlockState(pos).getDestroySpeed(world, pos);
  }

  public static boolean isUnbreakableBlock(Level world, BlockPos pos, Block b) {
    return getBlockHardnessMining(world, pos, b, ItemStack.EMPTY) < 0;
  }

  public static boolean isUnbreakableBlock(Level world, int x, int y, int z) {
    return isUnbreakableBlock(world, new BlockPos(x, y, z));
  }

  public static boolean isUnbreakableBlock(Level world, BlockPos pos) {
    return isUnbreakableBlock(world, pos, world.getBlockState(pos).getBlock());
  }

  /**
   * Returns true if a block cannot be harvested without a tool.
   */
  public static boolean isToughBlock(Level world, BlockPos pos) {
    return world.getBlockState(pos).requiresCorrectToolForDrops();
  }

  public static boolean isFullFluidBlock(Level world, BlockPos pos) {
    return isFullFluidBlock(world.getBlockState(pos).getBlock(), world, pos);
  }

  public static boolean isFullFluidBlock(Block block, Level world, BlockPos pos) {
    if (block instanceof IFluidBlock) {
      return ((IFluidBlock) block).getFilledPercentage(world, pos) == 1.0f;
    } else {
      return world.getFluidState(pos).isSource();
    }
  }

  public static Fluid getFluid(Block block) {
    if (block instanceof IFluidBlock) {
      return ((IFluidBlock) block).getFluid();
    } else {
      return block.getFluidState(block.defaultBlockState()).getType(); // Passing null as default if no state available
    }
  }

  public static FluidStack drainBlock(Level world, BlockPos pos, boolean doDrain) {
    return drainBlock(world.getBlockState(pos).getBlock(), world, pos, doDrain);
  }

  public static FluidStack drainBlock(Block block, Level world, BlockPos pos, boolean doDrain) {
    if (block instanceof IFluidBlock fluidBlock) {
      return fluidBlock.drain(world, pos, doDrain ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);
    } else {
      FluidState fluidState = world.getFluidState(pos);
      if (fluidState.isSource()) {
        Fluid fluid = fluidState.getType();
        if (doDrain) {
          world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return new FluidStack(fluid, FluidType.BUCKET_VOLUME);
      }
      return FluidStack.EMPTY;
    }
  }

  /**
   * Create an explosion which only affects a single block.
   */
  public static void explodeBlock(Level world, BlockPos pos) {
    if (world.isClientSide) {
      return;
    }

    Explosion explosion = new Explosion(world, null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 3f, true, Explosion.BlockInteraction.KEEP);
    // explosion.getToBlow().add(pos); // Explosion is immutable or handled differently in 1.20.1
    // world.explode(...) is preferred
    world.explode(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 3f, true, Level.ExplosionInteraction.BLOCK);

    for (Player player : world.players()) {
      if (!(player instanceof ServerPlayer)) {
        continue;
      }

      if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 4096) {
        // ((ServerPlayer) player).connection.send(new ClientboundExplodePacket(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 3f, new ArrayList<>(), null));
      }
    }
  }

  public static int computeBlockBreakEnergy(Level world, BlockPos pos) {
    return (int) Math.floor(BuilderAPI.BREAK_ENERGY * world.getBlockState(pos).getDestroySpeed(world, pos) * CoreConfig.getMiningUsageMultiplier());
  }

  /**
   * The following functions let you avoid unnecessary chunk loads, which is nice.
   */
  @Nullable
  public static BlockEntity getTileEntity(Level world, BlockPos pos) {
    return getTileEntity(world, pos, false);
  }

  @Nullable
  public static BlockEntity getTileEntity(Level world, BlockPos pos, boolean force) {
    if (pos.getY() < world.getMinBuildHeight() || pos.getY() > world.getMaxBuildHeight()) {
      return null;
    }
    if (!force) {
      return world.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null ? world.getBlockEntity(pos) : null;
    } else {
      return world.getBlockEntity(pos);
    }
  }

  public static Block getBlock(Level world, BlockPos pos) {
    return getBlock(world, pos, false);
  }

  public static Block getBlock(Level world, BlockPos pos, boolean force) {
    if (pos.getY() < world.getMinBuildHeight() || pos.getY() > world.getMaxBuildHeight()) {
      return Blocks.AIR;
    }
    if (!force) {
      return world.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null ? world.getBlockState(pos).getBlock() : Blocks.AIR;
    } else {
      return world.getBlockState(pos).getBlock();
    }
  }

  public static void onComparatorUpdate(Level world, BlockPos pos, Block block) {
    world.updateNeighborsAt(pos, block);
  }

  @Nullable
  public static ChestBlockEntity getOtherDoubleChest(BlockEntity inv) {
    if (inv instanceof ChestBlockEntity chest) {
      // In 1.20.1, we'd use ChestBlock.getContainer or similar, but for now we'll return null to satisfy the signature.
      // TODO: Implement correctly using 1.20.1 double chest logic
      return chest;
    }
    return null;
  }
}
