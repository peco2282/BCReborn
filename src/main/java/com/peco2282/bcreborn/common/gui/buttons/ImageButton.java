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
package com.peco2282.bcreborn.common.gui.buttons;


import com.peco2282.bcreborn.common.gui.ScreenUtils;
import com.peco2282.bcreborn.common.gui.tooltips.IToolTipProvider;
import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;


@OnlyIn(Dist.CLIENT)
public class ImageButton extends Button implements IButtonClickEventTrigger, IToolTipProvider {
  public final int id;
  private final int size, u, v, baseU, baseV;
  private final ResourceLocation texture;
  private final ArrayList<IButtonClickEventListener> listeners = new ArrayList<>();
  private boolean active = false;
  private ToolTip toolTip;

  public ImageButton(int id, int x, int y, int size, ResourceLocation texture, int u, int v) {
    this(id, x, y, size, texture, 0, 0, u, v);
  }

  public ImageButton(int id, int x, int y, int size, ResourceLocation texture, int baseU, int baseV, int u, int v) {
    super(x, y, size, size, Component.literal(""), ScreenUtils.EMPTY_PRESS, ScreenUtils.EMPTY_NARRATION);
    this.id = id;
    this.size = size;
    this.u = u;
    this.v = v;
    this.baseU = baseU;
    this.baseV = baseV;
    this.texture = texture;
  }

  public int getSize() {
    return size;
  }

  public boolean isActive() {
    return active;
  }

  public void activate() {
    active = true;
  }

  public void deActivate() {
    active = false;
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    if (!visible) {
      return;
    }

    int buttonState = getButtonState(mouseX, mouseY);

    guiGraphics.blit(texture, getX(), getY(), baseU + buttonState * size, baseV, size, size);
    guiGraphics.blit(texture, getX() + 1, getY() + 1, u, v, size - 2, size - 2);
  }

  @Override
  public void onClick(double mouseX, double mouseY) {
    active = !active;
    notifyAllListeners();
  }

  @Override
  public void registerListener(IButtonClickEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(IButtonClickEventListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void notifyAllListeners() {
    for (IButtonClickEventListener listener : listeners) {
      listener.handleButtonClick(this, 0);
    }
  }

  private int getButtonState(int mouseX, int mouseY) {
    if (!this.active) {
      return 1;
    } else {
      return 3;
    }
  }

  private boolean isMouseOverButton(int mouseX, int mouseY) {
    return mouseX >= getX() && mouseY >= getY() && mouseX < getX() + size && mouseY < getY() + size;
  }

  @Override
  public ToolTip getToolTip() {
    return toolTip;
  }

  public ImageButton setToolTip(ToolTip tips) {
    this.toolTip = tips;
    return this;
  }

  @Override
  public boolean isToolTipVisible() {
    return visible;
  }

  @Override
  public boolean isMouseOver(int mouseX, int mouseY) {
    return isMouseOverButton(mouseX, mouseY);
  }
}
