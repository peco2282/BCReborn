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
package com.peco2282.bcreborn.builders.screen;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.common.gui.SpriteSheat;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code GuiFiller}: pattern selection (left / right buttons), up to four pattern parameter
 * slots, the excavate toggle and the filling resources.
 */
public class FillerScreen extends BuildCraftScreen<FillerMenu> {
  private static final ResourceLocation TEXTURE = BCRebornBuilders.location("textures/gui/filler.png");
  private static final int EXCAVATE_X = 150;
  private static final int EXCAVATE_Y = 30;
  private static final int PARAMETER_X = 77;
  private static final int PARAMETER_Y = 30;
  private final FillerBlockEntity filler;

  public FillerScreen(FillerMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.filler = menu.getFiller();
    this.imageWidth = 175;
    this.imageHeight = 240;
  }

  @Override
  protected void init() {
    super.init();
    addRenderableWidget(new FillerButton(leftPos + 20, topPos + 30, 10, 16, SpriteSheat.BUTTON_LEFT_NORMAL, SpriteSheat.BUTTON_LEFT_SELECTED, b -> changePattern(-1)));
    addRenderableWidget(new FillerButton(leftPos + 62, topPos + 30, 10, 16, SpriteSheat.BUTTON_RIGHT_NORMAL, SpriteSheat.BUTTON_RIGHT_SELECTED, b -> changePattern(1)));
  }

  private void changePattern(int delta) {
    BCNetworkManager.sendSetFillerPattern(filler.getBlockPos(), delta);
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  private boolean overExcavate(double mouseX, double mouseY) {
    return isHovering(EXCAVATE_X, EXCAVATE_Y, 16, 16, mouseX, mouseY);
  }

  /** @return the parameter slot under the mouse, or -1 */
  private int parameterSlotAt(double mouseX, double mouseY) {
    for (int i = 0; i < FillerMenu.PARAMETER_SLOTS; i++) {
      if (isHovering(PARAMETER_X + i * 18, PARAMETER_Y, 16, 16, mouseX, mouseY)) return i;
    }
    return -1;
  }

  @Nullable
  private IStatementParameter parameter(int slot) {
    IStatementParameter[] parameters = filler.getPatternParameters();
    return slot < parameters.length ? parameters[slot] : null;
  }

  private boolean isParameterAllowed(int slot) {
    IFillerPattern pattern = filler.getPattern();
    return pattern != null && slot < pattern.maxParameters();
  }

  /**
   * The original parameters drew atlas icons; here the icon is picked from the description key and the PNG in
   * {@code textures/item/filler_parameter} is drawn directly.
   */
  @Nullable
  static ResourceLocation parameterTexture(IStatementParameter parameter) {
    String description = parameter.getDescription();
    if (description == null) return null;
    String name = switch (description) {
      case "direction.west" -> "arrow_left";
      case "direction.east" -> "arrow_right";
      case "direction.north" -> "arrow_up";
      case "direction.south" -> "arrow_down";
      case "direction.up" -> "stairs_ascend";
      case "direction.down" -> "stairs_descend";
      case "fillerpattern.parameter.filled" -> "filled";
      case "fillerpattern.parameter.hollow" -> "hollow";
      default -> description.startsWith("direction.center.") ? "center_" + description.substring("direction.center.".length()) : null;
    };
    return name == null ? null : BCRebornCore.location("textures/item/filler_parameter/" + name + ".png");
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    drawWidgets(graphics, mouseX, mouseY);

    // Original EXCAVATE_ON (224, -16) / EXCAVATE_OFF (240, -16) button texture sets: hover state 1 = normal, 2 = hover.
    int u = filler.isExcavate() ? 224 : 240;
    graphics.blit(TEXTURE, leftPos + EXCAVATE_X, topPos + EXCAVATE_Y, u, overExcavate(mouseX, mouseY) ? 16 : 0, 16, 16);

    for (int i = 0; i < FillerMenu.PARAMETER_SLOTS; i++) {
      int x = leftPos + PARAMETER_X + i * 18;
      int y = topPos + PARAMETER_Y;
      if (!isParameterAllowed(i)) {
        // Original AdvancedSlot: slots the pattern does not use are greyed out.
        graphics.fill(x, y, x + 16, y + 16, 0x80000000);
        continue;
      }
      IStatementParameter parameter = parameter(i);
      if (parameter == null) continue;
      ItemStack stack = parameter.getItemStack();
      ResourceLocation texture = parameterTexture(parameter);
      if (texture != null) {
        graphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
      } else if (!stack.isEmpty()) {
        graphics.renderItem(stack, x, y);
      }
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (minecraft != null && minecraft.gameMode != null) {
      if (overExcavate(mouseX, mouseY) && button == 0) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, FillerMenu.BUTTON_EXCAVATE);
        return true;
      }
      int slot = parameterSlotAt(mouseX, mouseY);
      if (slot >= 0 && isParameterAllowed(slot)) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId,
          FillerMenu.parameterButtonId(slot, button, Screen.hasShiftDown()));
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    renderTooltip(graphics, mouseX, mouseY);
    if (overExcavate(mouseX, mouseY)) {
      graphics.renderTooltip(font, Component.translatable("tip.filler.excavate." + (filler.isExcavate() ? "on" : "off")), mouseX, mouseY);
    }
    int slot = parameterSlotAt(mouseX, mouseY);
    if (slot >= 0) {
      IStatementParameter parameter = parameter(slot);
      if (parameter != null && parameter.getDescription() != null) {
        graphics.renderTooltip(font, Component.translatable(parameter.getDescription()), mouseX, mouseY);
      }
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    graphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040, false);
    IFillerPattern pattern = filler.getPattern();
    if (pattern != null && pattern.getDescription() != null) {
      Component description = Component.translatable(pattern.getDescription());
      graphics.drawString(font, description, imageWidth / 2 - font.width(description) / 2, 56, 0x404040, false);
    }
    graphics.drawString(font, Component.translatable("screen.filling.resources"), 8, 74, 0x404040, false);
    graphics.drawString(font, Component.translatable("gui.inventory"), 8, 142, 0x404040, false);
  }

  private static class FillerButton extends Button {
    private final SpriteSheat noActive;
    private final SpriteSheat hover;

    public FillerButton(int x, int y, int width, int height, SpriteSheat texture, SpriteSheat hover, Button.OnPress onPress) {
      super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
      this.noActive = texture;
      this.hover = hover;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.isHoveredOrFocused()) {
        this.hover.blit(graphics, getX(), getY());
      } else {
        this.noActive.blit(graphics, getX(), getY());
      }
    }
  }
}
