/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.lib.block.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.lib.block.menu.BCMenu;

/**
 * Base class for container screens in the mod, extending the {@link AbstractContainerScreen}. This
 * provides core functionality for rendering container GUIs.
 *
 * @param <T> The type of the menu that this screen interacts with, extending {@link BCMenu}.
 * @author peco2282
 */
public abstract class BCContainerScreen<T extends BCMenu> extends AbstractContainerScreen<T> {
  /**
   * Constructs a BCContainerScreen.
   *
   * @param p_97741_ The menu instance for this screen.
   * @param p_97742_ The player's inventory.
   * @param p_97743_ The title text component for the screen.
   */
  public BCContainerScreen(T p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
  }

  /**
   * Gets the texture resource location used for rendering the screen.
   *
   * @return A {@link ResourceLocation} representing the screen texture.
   */
  @NotNull
  protected abstract ResourceLocation getTexture();
}
