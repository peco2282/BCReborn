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
package com.peco2282.bcreborn.transport.item;

import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableItem;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.LensPluggable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code ItemLens}. The original used one item with a damage value per colour and kind
 * (lens / filter); here each combination has its own item. A null colour is the clear variant.
 */
public class LensItem extends Item implements IPipePluggableItem {
  @Nullable
  private final DyeColor color;
  private final boolean filter;

  public LensItem(@Nullable DyeColor color, boolean filter) {
    super(new Item.Properties());
    this.color = color;
    this.filter = filter;
  }

  @Nullable
  public DyeColor getColor() {
    return color;
  }

  public boolean isFilter() {
    return filter;
  }

  /** Tint of the glass layer of the item model; white for the clear variant. */
  public int getTint() {
    if (color == null) return 0xFFFFFF;
    float[] rgb = color.getTextureDiffuseColors();
    return ((int) (rgb[0] * 255) << 16) | ((int) (rgb[1] * 255) << 8) | (int) (rgb[2] * 255);
  }

  @Override
  public PipePluggable<?> createPipePluggable(IPipe pipe, Direction side, ItemStack stack) {
    return new LensPluggable(color, filter);
  }
}
