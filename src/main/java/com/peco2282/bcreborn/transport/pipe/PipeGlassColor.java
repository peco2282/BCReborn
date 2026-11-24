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
package com.peco2282.bcreborn.transport.pipe;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The stained glass colour of a pipe (original {@code TileGenericPipe.glassColor}), on the block entity and on the
 * pipe item.
 * <p>
 * It is not the Lapis / Daizuli colour ({@code PipeRuntime.pipeColor}): a Lapis pipe can be stained too. Pipes of
 * two different glass colours do not connect; an unstained pipe connects to every colour.
 * <p>
 * The original kept the colour in the item damage ({@code 1 + glassColor}). Here it is the block entity tag of the
 * item ({@code BlockEntityTag.GlassColor}), which the vanilla {@code BlockItem} loads into the placed pipe and the
 * loot table copies back when the pipe is broken.
 */
public final class PipeGlassColor {
  /** Key in the block entity NBT and in {@code BlockEntityTag} of the item. */
  public static final String TAG = "GlassColor";

  private PipeGlassColor() {
  }

  public static boolean isPipe(ItemStack stack) {
    return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof PipeBlock;
  }

  /** The glass colour of a pipe item, or null if it is not stained. */
  public static @Nullable DyeColor get(ItemStack stack) {
    CompoundTag tag = BlockItem.getBlockEntityData(stack);
    if (tag == null || !tag.contains(TAG)) return null;
    return DyeColor.byId(tag.getInt(TAG));
  }

  /** A copy of {@code stack} with the given glass colour; null removes it (and an empty block entity tag). */
  public static ItemStack with(ItemStack stack, @Nullable DyeColor color) {
    ItemStack result = stack.copy();
    CompoundTag tag = BlockItem.getBlockEntityData(result);
    tag = tag == null ? new CompoundTag() : tag.copy();
    if (color == null) {
      tag.remove(TAG);
    } else {
      tag.putInt(TAG, color.getId());
    }
    if (tag.isEmpty()) {
      result.removeTagKey(BlockItem.BLOCK_ENTITY_TAG);
      if (result.getTag() != null && result.getTag().isEmpty()) result.setTag(null);
    } else {
      result.getOrCreateTag().put(BlockItem.BLOCK_ENTITY_TAG, tag);
    }
    return result;
  }

  /** Original {@code TileGenericPipe.canPipeConnect}: two stained pipes connect only if they share the colour. */
  public static boolean canConnect(@Nullable DyeColor a, @Nullable DyeColor b) {
    return a == null || b == null || a == b;
  }
}
