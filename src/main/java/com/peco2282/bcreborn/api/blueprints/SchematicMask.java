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
package com.peco2282.bcreborn.api.blueprints;

import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

public class SchematicMask extends SchematicBlockBase {

  public boolean isConcrete = true;

  public SchematicMask() {

  }

  public SchematicMask(boolean isConcrete) {
    this.isConcrete = isConcrete;
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    BlockPos pos = new BlockPos(x, y, z);
    if (isConcrete) {
      if (stacks.isEmpty() || !isReplaceable(context, pos)) {
        return;
      }
      // Original: clear the soft block, find the first non-soft neighbour (DOWN first) and place the consumed
      // stack against it as the BuildCraft fake player. (Previously this only set air, so fillers placed nothing.)
      Level level = context.world();
      if (!(level instanceof ServerLevel server)) {
        return;
      }
      ItemStack stack = stacks.getFirst();
      level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
      Direction against = Direction.DOWN;
      for (Direction dir : Direction.values()) {
        if (!BuildCraftAPI.isSoftBlock(level, pos.relative(dir))) {
          against = dir;
          break;
        }
      }
      FakePlayer player = FakePlayerFactory.getMinecraft(server);
      BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), against.getOpposite(), pos, false);
      if (stack.getItem() instanceof BlockItem blockItem) {
        blockItem.place(new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack.copy(), hit));
      } else {
        ItemStack previous = player.getItemInHand(InteractionHand.MAIN_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
        try {
          player.getItemInHand(InteractionHand.MAIN_HAND).useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        } finally {
          player.setItemInHand(InteractionHand.MAIN_HAND, previous);
        }
      }
    } else {
      context.world().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }
  }

  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    if (isConcrete) {
      return !isReplaceable(context, pos);
    } else {
      return isReplaceable(context, pos);
    }
  }

  private boolean isReplaceable(IBuilderContext context, BlockPos pos) {
    BlockState state = context.world().getBlockState(pos);
    return state.isAir() || state.canBeReplaced();
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    nbt.putBoolean("isConcrete", isConcrete);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    isConcrete = nbt.getBoolean("isConcrete");
  }
}
