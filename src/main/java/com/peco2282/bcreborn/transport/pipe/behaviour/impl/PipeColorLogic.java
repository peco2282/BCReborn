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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Colour helpers shared by the Lapis and Daizuli pipes.
 * <p>
 * The original used {@code EnumColor} (BLACK = 0 ... WHITE = 15), which is {@link DyeColor} in reverse order
 * ({@code EnumColor.fromId(15 - dye) == DyeColor.byId(dye)}). {@link #next} and {@link #previous} follow the
 * original {@code EnumColor.getNext()} / {@code getPrevious()} order.
 */
public final class PipeColorLogic {
  /** Original default: block metadata / {@code color} 0, i.e. {@code EnumColor.BLACK}. */
  public static final DyeColor DEFAULT_COLOR = DyeColor.BLACK;

  private PipeColorLogic() {
  }

  /** The pipe colour, or {@link #DEFAULT_COLOR} if none was set. */
  public static DyeColor colorOf(PipeBlockEntity pipe) {
    DyeColor color = pipe.getPipeColor();
    return color != null ? color : DEFAULT_COLOR;
  }

  /** Original {@code EnumColor.getNext()}. */
  public static DyeColor next(DyeColor color) {
    return DyeColor.byId((color.getId() + 15) % 16);
  }

  /** Original {@code EnumColor.getPrevious()}. */
  public static DyeColor previous(DyeColor color) {
    return DyeColor.byId((color.getId() + 1) % 16);
  }

  /** Original {@code ColorUtils.getColorIDFromDye()}: the colour of a held dye, or null. */
  @Nullable
  public static DyeColor dyeColor(ItemStack stack) {
    return stack.isEmpty() ? null : DyeColor.getColor(stack);
  }

  /** Sets the colour and tells the player, since the pipe colour is not rendered yet. */
  public static void setColor(PipeBlockEntity pipe, DyeColor color, @Nullable Player player) {
    if (pipe.getPipeColor() != color) {
      pipe.setPipeColor(color);
    }
    if (player != null) {
      player.displayClientMessage(Component.translatable("message.bcreborntransport.pipe_color",
        Component.translatable("color.minecraft." + color.getName())), true);
    }
  }
}
