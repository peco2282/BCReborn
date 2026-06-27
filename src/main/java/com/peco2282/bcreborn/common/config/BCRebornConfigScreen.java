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
package com.peco2282.bcreborn.common.config;

import com.peco2282.bcreborn.Config;
import com.peco2282.bcreborn.common.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BCRebornConfigScreen extends Screen {
  private final Screen parent;
  private final TabManager manager = new TabManager(
    this::addRenderableWidget,
    this::removeWidget
  );
  private final Minecraft minecraft;
  private final int index;
  private TabNavigationBar navigationBar;
  private Button doneButton;

  public BCRebornConfigScreen(Minecraft minecraft, Screen parent) {
    this(minecraft, parent, 0);
  }

  public BCRebornConfigScreen(Minecraft minecraft, Screen parent, int index) {
    super(Component.translatable("screen.config.title").withStyle(style -> style.withFont(Fonts.ORBITRON)));
    this.minecraft = minecraft;
    this.parent = parent;
    this.index = index;
  }

  @Override
  protected void init() {
    this.navigationBar = TabNavigationBar.builder(this.manager, this.width)
      .addTabs(BCRebornConfigTab.TABS).build();
    this.addRenderableWidget(this.navigationBar);
    this.navigationBar.selectTab(this.index, false);
    this.repositionElements();

    this.doneButton = Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
      .bounds(this.width / 2 - 100, this.height - 30, 200, 20).build();
    this.addRenderableWidget(this.doneButton);
  }

  @Override
  public void repositionElements() {
    if (this.navigationBar != null) {
      this.navigationBar.setWidth(this.width);
      this.navigationBar.arrangeElements();
    }
    if (this.doneButton != null) {
      this.doneButton.setX(this.width / 2 - 100);
      this.doneButton.setY(this.height - 30);
    }
    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40);
    }
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(graphics);

    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      ConfigSelectionList list = configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40);
      list.render(graphics, mouseX, mouseY, partialTick);
    }

    super.render(graphics, mouseX, mouseY, partialTick);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      if (configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40).mouseClicked(mouseX, mouseY, button)) {
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      if (configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40).mouseReleased(mouseX, mouseY, button)) {
        return true;
      }
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      if (configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40).mouseScrolled(mouseX, mouseY, delta)) {
        return true;
      }
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      if (configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40).keyPressed(keyCode, scanCode, modifiers)) {
        return true;
      }
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    Tab currentTab = this.manager.getCurrentTab();
    if (currentTab instanceof BCRebornConfigTab configTab) {
      if (configTab.getList(this.minecraft, this.width, this.height, 40, this.height - 40).charTyped(codePoint, modifiers)) {
        return true;
      }
    }
    return super.charTyped(codePoint, modifiers);
  }

  @Override
  public void onClose() {
    this.minecraft.setScreen(this.parent);
    Config.SPEC.save();
  }
}
