/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.builder.block.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ModeChangeButton extends Button {
  public ModeChangeButton(int x, int y, int width, int height, Component message) {
    super(x, y, width, height, message, b -> {}, DEFAULT_NARRATION);
  }

  @Override
  protected void renderWidget(
      GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {}
}
