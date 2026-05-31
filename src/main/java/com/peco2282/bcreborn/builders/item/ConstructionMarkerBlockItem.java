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
package com.peco2282.bcreborn.builders.item;

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConstructionMarkerBlockItem extends BlockItem {
  public ConstructionMarkerBlockItem(Block p_40565_, Properties p_40566_) {
    super(p_40565_, p_40566_);
  }


  public static boolean linkStarted(ItemStack marker) {
    return marker.getOrCreateTag().contains("x");
  }

  public static void link(ItemStack marker, Level world, BlockPos pos) {
    CompoundTag nbt = marker.getOrCreateTag();

    if (nbt.contains("x")) {
      int ox = nbt.getInt("x");
      int oy = nbt.getInt("y");
      int oz = nbt.getInt("z");

      BlockEntity tile1 = world.getBlockEntity(pos);

      if (!new Position(ox, oy, oz).isClose(new Position(pos.getX(), pos.getY(), pos.getZ()), 64)) {
        return;
      }

      if ((tile1 instanceof ArchitectBlockEntity architect)) {
        BlockEntity tile2 = world.getBlockEntity(pos);

        if (tile1 != tile2 && tile2 != null) {
          if (tile2 instanceof ArchitectBlockEntity
            || tile2 instanceof ConstructionMarkerBlockEntity
            || tile2 instanceof BuilderBlockEntity) {
            architect.addSubBlueprint(tile2);

            nbt.remove("x");
            nbt.remove("y");
            nbt.remove("z");
          }
        }

        return;
      }
    }

    nbt.putInt("x", pos.getX());
    nbt.putInt("y", pos.getY());
    nbt.putInt("z", pos.getZ());
  }

  @Override
  public InteractionResult useOn(UseOnContext p_40581_) {
    BlockEntity tile = p_40581_.getLevel().getBlockEntity(p_40581_.getClickedPos());
    CompoundTag nbt = p_40581_.getItemInHand().getOrCreateTag();

    if (nbt.contains("x")
      && !(tile instanceof BuilderBlockEntity
      || tile instanceof ArchitectBlockEntity
      || tile instanceof ConstructionMarkerBlockEntity)) {

      nbt.remove("x");
      nbt.remove("y");
      nbt.remove("z");

      return InteractionResult.SUCCESS;
    } else {
      return super.useOn(p_40581_);
    }
  }
}
