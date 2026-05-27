package com.peco2282.bcreborn.robotics.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.common.gui.AdvancedSlot;
import com.peco2282.bcreborn.common.gui.GuiAdvancedInterface;
import com.peco2282.bcreborn.robotics.ZonePlan;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;

public class ZonePlanScreen extends GuiAdvancedInterface<ZonePlanMenu> {
	public static final int WINDOWED_MAP_WIDTH = 213;
	public static final int WINDOWED_MAP_HEIGHT = 100;

	private static final ResourceLocation TMP_TEXTURE = BCReborn.getLocation("textures/gui/zone_planner_gui.png");

	private int mapWidth = WINDOWED_MAP_WIDTH;
	private int mapHeight = WINDOWED_MAP_HEIGHT;

	private DynamicTexture mapTexture;
	private NativeImage mapImage;
	private ResourceLocation mapTextureLocation;

	private DynamicTexture selectionTexture;
	private NativeImage selectionImage;
	private ResourceLocation selectionTextureLocation;


	private final ZonePlanBlockEntity zonePlan;

	private int selX1 = 0;
	private int selX2 = 0;
	private int selY1 = 0;
	private int selY2 = 0;

	private boolean inSelection = false;

	private int mapXMin = 0;
	private int mapYMin = 0;

	private float blocksPerPixel = 1.0f;
	private int cx;
	private int cz;

	private AreaSlot colorSelected = null;

	private float alpha = 0.8F;

	private static class AreaSlot extends AdvancedSlot {
		public DyeColor color;

		public AreaSlot(ZonePlanScreen gui, int x, int y, DyeColor iColor) {
			super(gui, x, y);
			this.color = iColor;
		}

		@Override
		public String getDescription() {
			return color.getName();
		}
	}

	public ZonePlanScreen(ZonePlanMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = 256;
		this.imageHeight = 228;
		this.zonePlan = menu.getTile();
		menu.gui = this;
		menu.currentAreaSelection = new ZonePlan();
		this.cx = zonePlan.getBlockPos().getX();
		this.cz = zonePlan.getBlockPos().getZ();

		createMapTextures();

		resetNullSlots(16);
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				slots.set(i * 4 + j, new AreaSlot(this, 8 + 18 * i, 146 + 18 * j, DyeColor.values()[i * 4 + j]));
			}
		}
		colorSelected = (AreaSlot) slots.get(0);
		uploadMap();
		menu.loadArea(colorSelected.color.getId());

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
		if (mapTexture != null) {
			mapTexture.close();
			mapTexture = null;
		}
		if (selectionTexture != null) {
			selectionTexture.close();
			selectionTexture = null;
		}
		mapImage = null;
		selectionImage = null;
		mapTextureLocation = null;
		selectionTextureLocation = null;
	}

	private void uploadMap() {
		menu.computeMap(cx, cz, mapWidth, mapHeight, blocksPerPixel);
	}

	private boolean isFullscreen() {
		return mapHeight > WINDOWED_MAP_HEIGHT;
	}

	@Override
	protected ResourceLocation getMenuTexture() {
		return TMP_TEXTURE;
	}

	@Override
	protected void initilaizeLedger(Inventory inventory) {
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		if (!isFullscreen()) {
			graphics.blit(TMP_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		}

		if (mapWidth <= WINDOWED_MAP_WIDTH) {
			mapXMin = leftPos + 8 + (WINDOWED_MAP_WIDTH - mapWidth) / 2;
		} else {
			mapXMin = (width - mapWidth) / 2;
		}

		if (mapHeight <= WINDOWED_MAP_HEIGHT) {
			mapYMin = topPos + 9 + (WINDOWED_MAP_HEIGHT - mapHeight) / 2;
		} else {
			mapYMin = (height - mapHeight) / 2;
		}

		if (mapTextureLocation != null) {
			graphics.blit(mapTextureLocation, mapXMin, mapYMin, 0, 0, mapWidth, mapHeight, mapWidth, mapHeight);
		}

		if (selectionTextureLocation != null) {
			graphics.blit(selectionTextureLocation, mapXMin, mapYMin, 0, 0, mapWidth, mapHeight, mapWidth, mapHeight);
		}

		if (inSelection && selX2 != 0) {
			int x1 = Math.min(selX1, selX2);
			int x2 = Math.max(selX1, selX2);
			int y1 = Math.min(selY1, selY2);
			int y2 = Math.max(selY1, selY2);

			int color = colorSelected.color.getTextColor();
			int argb = ((int) (alpha * 255.0F) << 24) | color;
			graphics.fill(x1, y1, x2 + 1, y2 + 1, argb);
		}

		if (!isFullscreen()) {
			drawBackgroundSlots(graphics, mouseX, mouseY);

			if (colorSelected != null) {
				graphics.blit(TMP_TEXTURE, leftPos + colorSelected.x, topPos + colorSelected.y, 0, 228, 16, 16);
			}

			graphics.blit(
					TMP_TEXTURE,
					leftPos + 236,
					topPos + 27,
					16,
					228,
					8,
					(int) ((zonePlan.progress / (float) ZonePlanBlockEntity.CRAFT_TIME) * 27)
			);
		}
		//		graphics.blit(TMP_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
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

	private static int rgba(int r, int g, int b, int a) {
		return a << 24 | b << 16 | g << 8 | r;
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

		int blockStartX = Math.round(cx - mapWidth * blocksPerPixel / 2.0F);
		int blockStartZ = Math.round(cz - mapHeight * blocksPerPixel / 2.0F);
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
}