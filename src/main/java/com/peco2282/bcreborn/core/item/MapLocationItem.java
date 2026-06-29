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
package com.peco2282.bcreborn.core.item;

import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.IPathProvider;
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.items.IMapLocation;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MapLocationItem extends BuildCraftItem implements IMapLocation {
  public static final String TAG_NAME = "name";
  public static final String TAG_KIND = "kind";
  public static final String TAG_PATH = "path";

  public static final byte KIND_SPOT = 0;
  public static final byte KIND_AREA = 1;
  public static final byte KIND_PATH = 2;
  public static final byte KIND_ZONE = 3;

  public MapLocationItem() {
    super(new Properties());
  }

  public static IBox getPointBox(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_SPOT) {
      BlockPos pos = BlockPos.of(tag.getLong("Pos"));
      return new Box(pos, pos);
    }

    return Box.EMPTY;
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return stack.getOrCreateTag().contains(TAG_KIND) ? 1 : 16;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    CompoundTag tag = stack.getOrCreateTag();

    String name = tag.getString(TAG_NAME);
    if (!name.isEmpty()) {
      tooltip.add(Component.literal(name));
    }

    if (!tag.contains(TAG_KIND)) {
      return;
    }

    switch (tag.getByte(TAG_KIND)) {
      case KIND_SPOT -> {
        Direction side = Direction.values()[tag.getByte("side")];
        tooltip.add(Component.literal("{" + tag.getInt("x") + ", " + tag.getInt("y") + ", " + tag.getInt("z") + ", " + side + "}"));
      }
      case KIND_AREA -> {
        int x = tag.getInt("xMin");
        int y = tag.getInt("yMin");
        int z = tag.getInt("zMin");
        int xLength = tag.getInt("xMax") - x + 1;
        int yLength = tag.getInt("yMax") - y + 1;
        int zLength = tag.getInt("zMax") - z + 1;

        tooltip.add(Component.literal("{" + x + ", " + y + ", " + z + "} + {" + xLength + " x " + yLength + " x " + zLength + "}"));
      }
      case KIND_PATH -> {
        long[] path = tag.getLongArray(TAG_PATH);
        if (path.length > 0) {
          BlockPos first = BlockPos.of(path[0]);
          tooltip.add(Component.literal("{" + first.getX() + ", " + first.getY() + ", " + first.getZ() + "} + " + path.length + " elements"));
        }
      }
      case KIND_ZONE -> {
        // ZonePlan 移植後に詳細表示を追加する。
      }
      default -> {
      }
    }
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    ItemStack stack = context.getItemInHand();
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockEntity blockEntity = level.getBlockEntity(pos);
    CompoundTag tag = stack.getOrCreateTag();

    if (blockEntity instanceof IPathProvider pathProvider) {
      tag.putByte(TAG_KIND, KIND_PATH);
      long[] path = pathProvider.getPath().stream().mapToLong(BlockPos::asLong).toArray();

      tag.putLongArray(TAG_PATH, path);
    } else if (blockEntity instanceof IAreaProvider areaProvider) {
      tag.putByte(TAG_KIND, KIND_AREA);
      tag.putInt("xMin", areaProvider.xMin());
      tag.putInt("yMin", areaProvider.yMin());
      tag.putInt("zMin", areaProvider.zMin());
      tag.putInt("xMax", areaProvider.xMax());
      tag.putInt("yMax", areaProvider.yMax());
      tag.putInt("zMax", areaProvider.zMax());
    } else {
      tag.putByte(TAG_KIND, KIND_SPOT);
      tag.putByte("side", (byte) context.getClickedFace().ordinal());
      tag.putLong("Pos", pos.asLong());
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public Component getName(ItemStack stack) {
    return Component.literal(stack.getOrCreateTag().getString(TAG_NAME));
  }

  @Override
  public boolean setName(ItemStack stack, Component name) {
    stack.getOrCreateTag().putString(TAG_NAME, name.getString());
    return true;
  }

  @Override
  public MapLocationType getType(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (!tag.contains(TAG_KIND)) {
      return MapLocationType.CLEAN;
    }

    return switch (tag.getByte(TAG_KIND)) {
      case KIND_SPOT -> MapLocationType.SPOT;
      case KIND_AREA -> MapLocationType.AREA;
      case KIND_PATH -> MapLocationType.PATH;
      case KIND_ZONE -> MapLocationType.ZONE;
      default -> MapLocationType.CLEAN;
    };
  }

  @Override
  public BlockPos getPoint(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_SPOT) {
      return BlockPos.of(tag.getLong("Pos"));
    }

    return BlockPos.ZERO;
  }

  @Override
  public IBox getBox(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_AREA) {
      return new Box(
        tag.getInt("xMin"),
        tag.getInt("yMin"),
        tag.getInt("zMin"),
        tag.getInt("xMax"),
        tag.getInt("yMax"),
        tag.getInt("zMax")
      );
    }

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_SPOT) {
      return getPointBox(stack);
    }

    return Box.EMPTY;
  }

  @Override
  public IZone getZone(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_AREA) {
      return getBox(stack);
    }

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_SPOT) {
      return getPointBox(stack);
    }

    // ZonePlan 移植後に KIND_ZONE を復元する。
    return Box.EMPTY;
  }

  @Override
  public List<BlockPos> getPath(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_PATH) {
      return Arrays.stream(tag.getLongArray(TAG_PATH)).mapToObj(BlockPos::of).toList();
    }

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_SPOT) {
      return List.of(BlockPos.of(tag.getLong("Pos")));
    }

    return List.of();
  }

  @Override
  public Direction getPointSide(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();

    if (tag.contains(TAG_KIND) && tag.getByte(TAG_KIND) == KIND_SPOT) {
      return Direction.values()[tag.getByte("side")];
    }

    return Direction.UP;
  }
}