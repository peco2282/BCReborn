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
package com.peco2282.bcreborn.silicon.screen;

import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.c2s.SelectAssemblyRecipePacket;
import com.peco2282.bcreborn.silicon.block.entity.AssemblyTableBlockEntity;
import com.peco2282.bcreborn.silicon.block.entity.AssemblyTableBlockEntity.DisplayedRecipe;
import com.peco2282.bcreborn.silicon.menu.AssemblyTableMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Port of the original {@code GuiAssemblyTable}: 3 x 4 recipe slots on the right list the craftable recipes, then
 * the planned ones that cannot be crafted right now. Clicking a slot plans or cancels that recipe. Planned,
 * assembling and missing-ingredient recipes get the original overlays, and the bar shows the stored energy.
 */
public class AssemblyTableScreen extends LaserTableScreen<AssemblyTableMenu> {

  public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("bcrebornsilicon", "textures/gui/assembly_table.png");

  private final AssemblyTableBlockEntity assemblyTable;

  public AssemblyTableScreen(AssemblyTableMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title, (AssemblyTableBlockEntity) container.getSlot(0).container);
    this.assemblyTable = (AssemblyTableBlockEntity) table;
    this.imageWidth = 176;
    this.imageHeight = 205;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  protected ResourceLocation getMenuTexture() {
    return TEXTURE;
  }

  /** Original slot order: columns left to right, each column top to bottom. */
  private static int slotX(int index) {
    return 116 + 18 * (index / 4);
  }

  private static int slotY(int index) {
    return 36 + 18 * (index % 4);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

    List<DisplayedRecipe> recipes = assemblyTable.getDisplayedRecipes();
    for (int i = 0; i < recipes.size() && i < AssemblyTableBlockEntity.DISPLAY_SLOTS; i++) {
      DisplayedRecipe recipe = recipes.get(i);
      int sx = x + slotX(i);
      int sy = y + slotY(i);
      if (!recipe.craftable()) {
        guiGraphics.blit(TEXTURE, sx, sy, 215, 1, 16, 16);
      } else if (assemblyTable.isAssembling(recipe.id())) {
        guiGraphics.blit(TEXTURE, sx, sy, 196, 1, 16, 16);
      } else if (assemblyTable.isPlanned(recipe.id())) {
        guiGraphics.blit(TEXTURE, sx, sy, 177, 1, 16, 16);
      }
      guiGraphics.renderItem(recipe.result(), sx, sy);
      guiGraphics.renderItemDecorations(font, recipe.result(), sx, sy);
    }

    int h = table.getProgressScaled(70);
    guiGraphics.blit(TEXTURE, x + 86, y + 36 + 70 - h, 176, 18, 4, h);
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.renderTooltip(guiGraphics, mouseX, mouseY);
    int index = recipeSlotAt(mouseX, mouseY);
    List<DisplayedRecipe> recipes = assemblyTable.getDisplayedRecipes();
    if (index >= 0 && index < recipes.size()) {
      guiGraphics.renderTooltip(font, recipes.get(index).result(), mouseX, mouseY);
    }
  }

  private int recipeSlotAt(double mouseX, double mouseY) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    for (int i = 0; i < AssemblyTableBlockEntity.DISPLAY_SLOTS; i++) {
      int sx = x + slotX(i);
      int sy = y + slotY(i);
      if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16) {
        return i;
      }
    }
    return -1;
  }

  /** Original {@code slotClicked()}: toggles the plan of the clicked recipe on the server. */
  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    int index = recipeSlotAt(mouseX, mouseY);
    List<DisplayedRecipe> recipes = assemblyTable.getDisplayedRecipes();
    if (index >= 0 && index < recipes.size()) {
      DisplayedRecipe recipe = recipes.get(index);
      boolean select = !assemblyTable.isPlanned(recipe.id());
      BCNetworkManager.sendToServer(new SelectAssemblyRecipePacket(assemblyTable.getBlockPos(), recipe.id(), select));
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    renderTooltip(guiGraphics, mouseX, mouseY);
  }
}
