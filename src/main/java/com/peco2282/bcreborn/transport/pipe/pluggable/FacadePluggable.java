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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.TransportItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FacadePluggable extends PipePluggable {

  private BlockState state = Blocks.AIR.defaultBlockState();

  public FacadePluggable() {
  }

  public FacadePluggable(BlockState state) {
    this.state = state;
  }

  public BlockState getState() {
    return state;
  }

  @Override
  public void writeToNBT(CompoundTag nbt) {
    nbt.put("state", NbtUtils.writeBlockState(state));
  }

  @Override
  public void readFromNBT(CompoundTag nbt) {
    state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("state"));
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    data.writeVarInt(Block.getId(state));
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    state = Block.stateById(data.readVarInt());
  }

  @Override
  public ItemStack[] getDropItems(IPipeTile pipe) {
    ItemStack stack = new ItemStack(TransportItems.FACADE.get());
    CompoundTag nbt = stack.getOrCreateTagElement("facade");
    nbt.put("state", NbtUtils.writeBlockState(state));
    return new ItemStack[]{stack};
  }

  @Override
  public boolean isBlocking(IPipeTile pipe, Direction direction) {
    return true;
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    float min = 0.0f;
    float max = 1.0f;
    float thickness = 1.0f / 16.0f;

    switch (side) {
      case DOWN:
        return new AABB(min, 0, min, max, thickness, max);
      case UP:
        return new AABB(min, 1.0f - thickness, min, max, 1.0, max);
      case NORTH:
        return new AABB(min, min, 0, max, max, thickness);
      case SOUTH:
        return new AABB(min, min, 1.0f - thickness, max, max, 1.0);
      case WEST:
        return new AABB(0, min, min, thickness, max, max);
      case EAST:
        return new AABB(1.0f - thickness, min, min, 1.0, max, max);
      default:
        return new AABB(0, 0, 0, 1, 1, 1);
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return FacadePluggableRenderer.INSTANCE;
  }
}
