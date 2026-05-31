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
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.builders.menu.BlueprintLibraryMenu;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BlueprintLibraryScreen extends BuildCraftScreen<BlueprintLibraryMenu> {
  private static final ResourceLocation TEXTURE = BCRebornBuilders.location("textures/gui/library_rw.png");
  private final BlueprintLibraryBlockEntity library;
  private Button deleteButton;

  public BlueprintLibraryScreen(BlueprintLibraryMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.library = menu.getLibrary();
    this.imageWidth = 244;
    this.imageHeight = 220;
  }

  @Override
  protected void init() {
    super.init();

    deleteButton = addRenderableWidget(Button.builder(Component.translatable("gui.del"), b -> BCNetworkManager.sendDeleteBlueprint(library.getBlockPos())).bounds(leftPos + 174, topPos + 109, 25, 20).build());

    library.refresh();
    updateDeleteButton();
  }

  private void updateDeleteButton() {
    deleteButton.active = library.getSelectedBlueprint() != -1;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    double x = mouseX - leftPos;
    double y = mouseY - topPos;

    if (x >= 8 && x <= 161) {
      int off = menu.getScrollbarWidget().getPosition();
      int ySlot = (int) ((y - 22) / 9) + off;

      if (ySlot >= 0 && ySlot < library.entries.size()) {
        BCNetworkManager.sendSelectBlueprint(library.getBlockPos(), ySlot);
        updateDeleteButton();
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  @Override
  protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

    int inP = menu.getProgressIn() * 22 / 100;
    int outP = menu.getProgressOut() * 22 / 100;

    graphics.blit(TEXTURE, leftPos + 194 + 22 - inP, topPos + 57, 234 + 22 - inP, 240, inP, 16);
    graphics.blit(TEXTURE, leftPos + 194, topPos + 79, 234, 224, outP, 16);
  }

  @Override
  protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    graphics.drawString(this.font, this.title, imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);

    int off = menu.getScrollbarWidget().getPosition();
    for (int i = off; i < (off + 12); i++) {
      if (i >= library.entries.size()) {
        break;
      }
      LibraryId bpt = library.entries.get(i);
      String name = bpt.name;

      if (name.length() > 32) {
        name = name.substring(0, 32);
      }

      if (i == library.getSelectedBlueprint()) {
        graphics.fill(8, 22 + 9 * (i - off), 8 + 146, 22 + 9 * (i - off + 1), 0x80ffffff);
      }

      graphics.drawString(this.font, name, 9, 23 + 9 * (i - off), 0x404040, false);
    }
  }
}
