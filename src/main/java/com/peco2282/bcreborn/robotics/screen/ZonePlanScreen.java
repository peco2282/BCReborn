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
package com.peco2282.bcreborn.robotics.screen;

import org.lwjgl.glfw.GLFW;
import com.peco2282.bcreborn.common.packet.c2s.RequestZonePlanComputeMapPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import com.mojang.blaze3d.platform.NativeImage;
import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.common.gui.AdvancedSlot;
import com.peco2282.bcreborn.common.screen.AdvancedInterfaceScreen;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;

/**
 * Port of the original {@code GuiZonePlan}: a map of the area around the planner and sixteen zones, one per colour.
 * <ul>
 *   <li>click a colour to edit its zone;</li>
 *   <li>left drag on the map adds a rectangle to the zone, shift + left drag removes it;</li>
 *   <li>right drag moves the map, the mouse wheel zooms;</li>
 *   <li>the text field names the maps the planner writes.</li>
 * </ul>
 */
public class ZonePlanScreen extends AdvancedInterfaceScreen<ZonePlanMenu> {
  public static final int WINDOWED_MAP_WIDTH = 213;
  public static final int WINDOWED_MAP_HEIGHT = 100;
  private static final int MAP_X = 8;
  private static final int MAP_Y = 9;

  private static final ResourceLocation TEXTURE = BCRebornRobotics.location("textures/gui/zone_planner_gui.png");
  private final ZonePlanBlockEntity zonePlan;
  private final int mapWidth = WINDOWED_MAP_WIDTH;
  private final int mapHeight = WINDOWED_MAP_HEIGHT;
  private final float alpha = 0.8F;
  private float blocksPerPixel = 1.0f;
  private int cx;
  private int cz;
  private DynamicTexture mapTexture;
  private NativeImage mapImage;
  private ResourceLocation mapTextureLocation;
  private DynamicTexture selectionTexture;
  private NativeImage selectionImage;
  private ResourceLocation selectionTextureLocation;
  private AreaSlot colorSelected = null;
  private EditBox nameField;

  /** Rectangle selection in progress (screen coordinates), or not. */
  private boolean inSelection;
  private boolean clearSelection;
  private double selX1, selY1, selX2, selY2;
  /** Map drag in progress: where it started and how far the map has moved (pixels). */
  private boolean inMove;
  private double moveStartX, moveStartY;
  private int moveOffsetX, moveOffsetY;

  public ZonePlanScreen(ZonePlanMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.imageWidth = 256;
    this.imageHeight = 228;
    this.inventoryLabelY = Integer.MIN_VALUE;
    this.titleLabelY = Integer.MIN_VALUE;
    this.zonePlan = menu.getTile();
    menu.gui = this;
    menu.currentAreaSelection = new ZonePlan();
    this.cx = zonePlan.getBlockPos().getX();
    this.cz = zonePlan.getBlockPos().getZ();

    resetNullSlots(16);
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        slots.set(i * 4 + j, new AreaSlot(this, 8 + 18 * i, 146 + 18 * j, DyeColor.values()[i * 4 + j]));
      }
    }
    colorSelected = (AreaSlot) slots.get(0);
  }

  @Override
  protected void init() {
    super.init();
    if (mapTexture == null) {
      createMapTextures();
      uploadMap();
      menu.loadArea(colorSelected.color.getId());
    }
    nameField = new EditBox(font, leftPos + 30, topPos + 127, 190, 12, Component.translatable("gui.bcrebornrobotics.zone_plan.name"));
    nameField.setMaxLength(32);
    nameField.setValue(zonePlan.mapName);
    nameField.setResponder(menu::setName);
    addRenderableWidget(nameField);
  }

  @Override
  public void removed() {
    super.removed();
    closeMapTextures();
  }

  /** ABGR, as NativeImage stores pixels. */
  private static int rgba(int r, int g, int b, int a) {
    return a << 24 | b << 16 | g << 8 | r;
  }

  private void createMapTextures() {
    closeMapTextures();

    this.mapImage = new NativeImage(NativeImage.Format.RGBA, mapWidth, mapHeight, false);
    this.mapTexture = new DynamicTexture(mapImage);
    this.mapTextureLocation = minecraft.getTextureManager()
      .register("zone_plan_map/" + System.nanoTime(), mapTexture);

    this.selectionImage = new NativeImage(NativeImage.Format.RGBA, mapWidth, mapHeight, false);
    this.selectionTexture = new DynamicTexture(selectionImage);
    this.selectionTextureLocation = minecraft.getTextureManager()
      .register("zone_plan_selection/" + System.nanoTime(), selectionTexture);
  }

  private void closeMapTextures() {
    if (mapTextureLocation != null) minecraft.getTextureManager().release(mapTextureLocation);
    if (selectionTextureLocation != null) minecraft.getTextureManager().release(selectionTextureLocation);
    mapTexture = null;
    selectionTexture = null;
    mapImage = null;
    selectionImage = null;
    mapTextureLocation = null;
    selectionTextureLocation = null;
  }

  private void uploadMap() {
    menu.computeMap(cx, cz, mapWidth, mapHeight, blocksPerPixel);
  }

  private int mapLeft() {
    return leftPos + MAP_X;
  }

  private int mapTop() {
    return topPos + MAP_Y;
  }

  private boolean isOnMap(double mouseX, double mouseY) {
    return mouseX >= mapLeft() && mouseX < mapLeft() + mapWidth && mouseY >= mapTop() && mouseY < mapTop() + mapHeight;
  }

  /** The first block column of the map's left / top pixel (original: centred on cx / cz). */
  private int blockStartX() {
    return Math.round(cx - mapWidth * blocksPerPixel / 2.0F);
  }

  private int blockStartZ() {
    return Math.round(cz - mapHeight * blocksPerPixel / 2.0F);
  }

  @Override
  protected ResourceLocation getMenuTexture() {
    return TEXTURE;
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

    graphics.enableScissor(mapLeft(), mapTop(), mapLeft() + mapWidth, mapTop() + mapHeight);
    if (mapTextureLocation != null) {
      graphics.blit(mapTextureLocation, mapLeft() + moveOffsetX, mapTop() + moveOffsetY, 0, 0, mapWidth, mapHeight, mapWidth, mapHeight);
    }
    if (selectionTextureLocation != null) {
      graphics.blit(selectionTextureLocation, mapLeft() + moveOffsetX, mapTop() + moveOffsetY, 0, 0, mapWidth, mapHeight, mapWidth, mapHeight);
    }
    if (inSelection) {
      int x1 = (int) Math.min(selX1, selX2);
      int x2 = (int) Math.max(selX1, selX2);
      int y1 = (int) Math.min(selY1, selY2);
      int y2 = (int) Math.max(selY1, selY2);
      int color = clearSelection ? 0x000000 : colorSelected.color.getTextColor();
      graphics.fill(x1, y1, x2 + 1, y2 + 1, ((int) (alpha * 128.0F) << 24) | color);
    }
    graphics.disableScissor();

    drawBackgroundSlots(graphics, mouseX, mouseY);

    if (colorSelected != null) {
      graphics.blit(TEXTURE, leftPos + colorSelected.x, topPos + colorSelected.y, 0, 228, 16, 16);
    }

    int progress = (int) ((menu.getProgress() / (float) ZonePlanBlockEntity.CRAFT_TIME) * 27);
    graphics.blit(TEXTURE, leftPos + 236, topPos + 27, 16, 228, 8, progress);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(graphics, mouseX, mouseY);
    if (menu.getCarried().isEmpty()) {
      if (getSlotAtLocation(mouseX, mouseY) != null) {
        drawTooltipForSlotAt(graphics, mouseX, mouseY);
      } else if (isOnMap(mouseX, mouseY) && !inSelection && !inMove) {
        int x = Math.round(blockStartX() + (float) ((mouseX - mapLeft()) * blocksPerPixel));
        int z = Math.round(blockStartZ() + (float) ((mouseY - mapTop()) * blocksPerPixel));
        graphics.renderTooltip(font, Component.literal(x + ", " + z), mouseX, mouseY);
      }
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (isOnMap(mouseX, mouseY)) {
      if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
        inSelection = true;
        clearSelection = Screen.hasShiftDown();
        selX1 = selX2 = mouseX;
        selY1 = selY2 = mouseY;
        return true;
      }
      if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
        inMove = true;
        moveStartX = mouseX;
        moveStartY = mouseY;
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    if (inSelection) {
      selX2 = Math.max(mapLeft(), Math.min(mapLeft() + mapWidth - 1, mouseX));
      selY2 = Math.max(mapTop(), Math.min(mapTop() + mapHeight - 1, mouseY));
      return true;
    }
    if (inMove) {
      moveOffsetX = (int) (mouseX - moveStartX);
      moveOffsetY = (int) (mouseY - moveStartY);
      return true;
    }
    return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (inSelection && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
      inSelection = false;
      applySelection();
      return true;
    }
    if (inMove && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
      inMove = false;
      cx -= Math.round(moveOffsetX * blocksPerPixel);
      cz -= Math.round(moveOffsetY * blocksPerPixel);
      moveOffsetX = 0;
      moveOffsetY = 0;
      clearMap();
      uploadMap();
      refreshSelectedArea();
      return true;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (isOnMap(mouseX, mouseY) && delta != 0) {
      float next = delta > 0 ? blocksPerPixel / 2 : blocksPerPixel * 2;
      if (next >= RequestZonePlanComputeMapPacket.MIN_BLOCKS_PER_PIXEL && next <= RequestZonePlanComputeMapPacket.MAX_BLOCKS_PER_PIXEL) {
        blocksPerPixel = next;
        clearMap();
        uploadMap();
        refreshSelectedArea();
      }
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    // The name field takes every key while it has the focus (the inventory key would close the screen).
    if (nameField != null && nameField.isFocused()) {
      if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
        nameField.setFocused(false);
        return true;
      }
      return nameField.keyPressed(keyCode, scanCode, modifiers) || nameField.canConsumeInput();
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected void slotClicked(AdvancedSlot slot, int mouseButton) {
    if (slot instanceof AreaSlot area && area != colorSelected) {
      colorSelected = area;
      menu.currentAreaSelection = new ZonePlan();
      refreshSelectedArea();
      menu.loadArea(area.color.getId());
    }
  }

  /** Original mouseMovedOrUp(): every block column under the rectangle is added to (or removed from) the zone. */
  private void applySelection() {
    if (menu.currentAreaSelection == null) return;
    int x1 = (int) (Math.min(selX1, selX2) - mapLeft());
    int x2 = (int) (Math.max(selX1, selX2) - mapLeft());
    int y1 = (int) (Math.min(selY1, selY2) - mapTop());
    int y2 = (int) (Math.max(selY1, selY2) - mapTop());
    int bx1 = Math.round(blockStartX() + x1 * blocksPerPixel);
    int bz1 = Math.round(blockStartZ() + y1 * blocksPerPixel);
    int bx2 = Math.round(blockStartX() + (x2 + 1) * blocksPerPixel) - 1;
    int bz2 = Math.round(blockStartZ() + (y2 + 1) * blocksPerPixel) - 1;
    for (int x = bx1; x <= Math.max(bx1, bx2); x++) {
      for (int z = bz1; z <= Math.max(bz1, bz2); z++) {
        menu.currentAreaSelection.set(x, z, !clearSelection);
      }
    }
    menu.saveArea(colorSelected.color.getId());
    refreshSelectedArea();
  }

  private void clearMap() {
    if (mapImage == null || mapTexture == null) return;
    mapImage.fillRect(0, 0, mapWidth, mapHeight, 0);
    mapTexture.upload();
  }

  public void applyMapImageBytes(int offset, byte[] data) {
    if (mapImage == null || mapTexture == null) {
      return;
    }

    int totalPixels = mapWidth * mapHeight;

    for (int i = 0; i < data.length; ++i) {
      int pixel = offset + i;

      if (pixel < 0 || pixel >= totalPixels) {
        continue;
      }

      int x = pixel % mapWidth;
      int y = pixel / mapWidth;

      int color = MapColor.byId(data[i] & 0xFF).col;
      int r = color >> 16 & 255;
      int g = color >> 8 & 255;
      int b = color & 255;

      mapImage.setPixelRGBA(x, y, rgba(r, g, b, 255));
    }

    mapTexture.upload();
  }

  public void refreshSelectedArea() {
    if (selectionImage == null || selectionTexture == null || colorSelected == null || menu.currentAreaSelection == null) {
      return;
    }

    int color = colorSelected.color.getTextColor();
    int rAdd = color >> 16 & 255;
    int gAdd = color >> 8 & 255;
    int bAdd = color & 255;
    int alphaInt = (int) (alpha * 255.0F);

    int blockStartX = blockStartX();
    int blockStartZ = blockStartZ();
    int sampleSize = Math.max(1, (int) Math.ceil(blocksPerPixel));

    for (int i = 0; i < mapWidth; ++i) {
      for (int j = 0; j < mapHeight; ++j) {
        double r = 0;
        double g = 0;
        double b = 0;

        for (int stepi = 0; stepi < sampleSize; ++stepi) {
          for (int stepj = 0; stepj < sampleSize; ++stepj) {
            int x = Math.round(blockStartX + i * blocksPerPixel) + stepi;
            int z = Math.round(blockStartZ + j * blocksPerPixel) + stepj;

            if (menu.currentAreaSelection.get(x, z)) {
              r += rAdd;
              g += gAdd;
              b += bAdd;
            }
          }
        }

        r /= sampleSize * sampleSize;
        g /= sampleSize * sampleSize;
        b /= sampleSize * sampleSize;

        if (r != 0 || g != 0 || b != 0) {
          selectionImage.setPixelRGBA(i, j, rgba((int) r, (int) g, (int) b, alphaInt));
        } else {
          selectionImage.setPixelRGBA(i, j, 0);
        }
      }
    }

    selectionTexture.upload();
  }

  private static class AreaSlot extends AdvancedSlot {
    public DyeColor color;

    public AreaSlot(ZonePlanScreen gui, int x, int y, DyeColor iColor) {
      super(gui, x, y);
      this.color = iColor;
    }

    @Override
    public String getDescription() {
      return Component.translatable("color.minecraft." + color.getName()).getString();
    }

    @Override
    public boolean isDefined() {
      return true;
    }

    /** Original: the slot shows its colour (EnumColor icon); here a square of the dye colour. */
    @Override
    public void drawSprite(GuiGraphics graphics, int cornerX, int cornerY) {
      graphics.fill(cornerX + x + 1, cornerY + y + 1, cornerX + x + 15, cornerY + y + 15, 0xFF000000 | color.getTextColor());
    }
  }
}
