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
package com.peco2282.bcreborn.builders.menu;

import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import com.peco2282.bcreborn.builders.BuildersMenuTypes;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.gui.widgets.Widget;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.registry.BCFillerPatterns;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FillerMenu extends BuildCraftMenu<FillerMenu> {
  private final FillerBlockEntity filler;
  private final ContainerData data;

  // Client constructor
  public FillerMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, getBlockEntity(playerInventory, data), new SimpleContainerData(1));
  }

  // Server constructor
  public FillerMenu(int windowId, Inventory playerInventory, FillerBlockEntity filler) {
    this(windowId, playerInventory, filler, new ContainerData() {
      @Override
      public int get(int index) {
        if (index == 0) {
          return filler.currentPattern;
        }
        return 0;
      }

      @Override
      public void set(int index, int value) {
        if (index == 0) {
          filler.currentPattern = value;
        }
      }

      @Override
      public int getCount() {
        return 1;
      }
    });
  }

  private FillerMenu(int windowId, Inventory playerInventory, FillerBlockEntity filler, ContainerData data) {
    super(BuildersMenuTypes.FILLER.get(), windowId, playerInventory);
    this.filler = filler;
    this.data = data;
    this.addDataSlots(data);
    addWidget(new PatternWidget());

    // Filler inventory
    for (int k = 0; k < 3; k++) {
      for (int j1 = 0; j1 < 9; j1++) {
        this.addSlot(new Slot(filler, j1 + k * 9, 8 + j1 * 18, 85 + k * 18));
      }
    }

    // Player inventory
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 153 + y * 18));
      }
    }

    // Hotbar
    for (int x = 0; x < 9; x++) {
      this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 211));
    }
  }

  /** Menu button id of the excavate toggle (original {@code setFlags} command). */
  public static final int BUTTON_EXCAVATE = 0;
  private static final int BUTTON_PARAMETER_BASE = 16;
  /** Pattern parameter slots (original {@code FillerParameterSlot}s at x = 77 + i * 18, y = 30). */
  public static final int PARAMETER_SLOTS = 4;

  /** Menu button id for a click on parameter slot {@code slot}. */
  public static int parameterButtonId(int slot, int mouseButton, boolean shift) {
    return BUTTON_PARAMETER_BASE + slot * 8 + Math.min(Math.max(mouseButton, 0), 2) * 2 + (shift ? 1 : 0);
  }

  /**
   * Server side of the GUI clicks (the original sent {@code setFlags} / {@code setParameters} commands).
   * <ul>
   *   <li>excavate: toggles {@link FillerBlockEntity#setExcavate}</li>
   *   <li>parameter slot: an empty slot gets the pattern's default parameter, otherwise the parameter handles the
   *   click ({@code IStatementParameter.onClick}, with the stack on the cursor); the filler then restarts</li>
   * </ul>
   */
  @Override
  public boolean clickMenuButton(Player player, int id) {
    if (filler == null) return false;
    if (id == BUTTON_EXCAVATE) {
      filler.setExcavate(!filler.isExcavate());
      return true;
    }
    int value = id - BUTTON_PARAMETER_BASE;
    if (value < 0 || value >= PARAMETER_SLOTS * 8) return false;
    int slot = value / 8;
    int mouseButton = (value % 8) / 2;
    boolean shift = (value & 1) != 0;
    IFillerPattern pattern = filler.getPattern();
    IStatementParameter[] parameters = filler.getPatternParameters();
    if (pattern == null || slot >= parameters.length) return false;
    IStatementParameter parameter = parameters[slot];
    if (parameter == null) {
      parameter = pattern.createParameter(slot);
    } else {
      parameter.onClick(null, pattern, getCarried(), new StatementMouseClick(mouseButton, shift));
    }
    filler.setPatternParameter(slot, parameter);
    return true;
  }

  public int getCurrentPattern() {
    return data.get(0);
  }

  public FillerBlockEntity getFiller() {
    return filler;
  }

  @Override
  public boolean stillValid(Player player) {
    return filler.stillValid(player);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return super.quickMoveStack(player, index);
  }

  class PatternWidget extends Widget {
    public PatternWidget() {
      super(38, 30, 0, 0, 16, 16);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(GuiGraphics guiGraphics, BuildCraftScreen<?> guiScreen, int guiX, int guiY, int mouseX, int mouseY) {
      int pattern = getCurrentPattern();

      if (pattern < 0 || pattern >= BCFillerPatterns.collection().size()) {
        pattern = pattern % BCFillerPatterns.collection().size();
      }
      guiGraphics.blit(guiX + x, guiY + y, 0, 16, 16, BCFillerPatterns.getCurrentlySelected(pattern).getIcon());
    }
  }
}
