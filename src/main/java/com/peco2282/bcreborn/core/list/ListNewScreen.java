/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.list;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.common.gui.AdvancedSlot;
import com.peco2282.bcreborn.common.gui.GuiAdvancedInterface;
import com.peco2282.bcreborn.common.gui.buttons.GuiImageButton;
import com.peco2282.bcreborn.common.gui.buttons.IButtonClickEventListener;
import com.peco2282.bcreborn.common.gui.buttons.IButtonClickEventTrigger;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import com.peco2282.bcreborn.core.ItemsCore;
import com.peco2282.bcreborn.core.item.ListItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class ListNewScreen extends GuiAdvancedInterface<ListNewMenu> implements IButtonClickEventListener {
	private static final ResourceLocation TEXTURE_BASE = BCRebornCore.location("textures/gui/list_new.png");
	private static final int BUTTON_COUNT = 3;

	private final Map<Integer, Map<ListMatchHandler.Type, List<ItemStack>>> exampleCache = new HashMap<>();
	private EditBox textField;
	private Player player;

	private static class ListSlot extends AdvancedSlot {
		public int lineIndex;
		public int slotIndex;

		public ListSlot(ListNewScreen gui, int x, int y, int iLineIndex, int iSlotIndex) {
			super(gui, x, y);

			lineIndex = iLineIndex;
			slotIndex = iSlotIndex;
		}

		@Override
		public ItemStack getItemStack() {
			ListNewMenu container = (ListNewMenu) gui.getMenu();
			if (slotIndex == 0 || !container.lines[lineIndex].isOneStackMode()) {
				return container.lines[lineIndex].getStack(slotIndex);
			} else {
				List<ItemStack> data = ((ListNewScreen) gui).getExamplesList(lineIndex, container.lines[lineIndex].getSortingType());
				if (data.size() >= slotIndex) {
					return data.get(slotIndex - 1);
				} else {
					return ItemStack.EMPTY;
				}
			}
		}

		@Override
		public void drawSprite(GuiGraphics graphics,int cornerX, int cornerY) {
			if (!shouldDrawHighlight()) {
				RenderSystem.setShaderTexture(0, TEXTURE_BASE);
				graphics.blit(TEXTURE_BASE, cornerX + x, cornerY + y, 176, 0, 16, 16);
			}

			super.drawSprite(graphics, cornerX, cornerY);
		}

		@Override
		public boolean shouldDrawHighlight() {
			ListNewMenu container = (ListNewMenu) gui.getMenu();
			return slotIndex == 0 || !container.lines[lineIndex].isOneStackMode();
		}
	}

	public ListNewScreen(ListNewMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);

		width = 176;
		height = 191;

		player = inventory.player;
	}

	private void clearExamplesCache(int lineId) {
		Map<ListMatchHandler.Type, List<ItemStack>> exampleList = exampleCache.get(lineId);
		if (exampleList != null) {
			exampleList.clear();
		}
	}

	private List<ItemStack> getExamplesList(int lineId, ListMatchHandler.Type type) {
		Map<ListMatchHandler.Type, List<ItemStack>> exampleList = exampleCache.get(lineId);
		if (exampleList == null) {
			exampleList = new EnumMap<ListMatchHandler.Type, List<ItemStack>>(ListMatchHandler.Type.class);
			exampleCache.put(lineId, exampleList);
		}

		ListNewMenu container = getMenu();

		if (!exampleList.containsKey(type)) {
			List<ItemStack> examples = container.lines[lineId].getExamples();
			ItemStack input = container.lines[lineId].stacks.get(0);
			if (!input.isEmpty()) {
				List<ItemStack> repetitions = new ArrayList<ItemStack>();
				for (ItemStack is : examples) {
					if (StackHelper.isMatchingItem(input, is, true, false)) {
						repetitions.add(is);
					}
				}
				examples.removeAll(repetitions);
			}
			exampleList.put(type, examples);
		}
		return exampleList.get(type);
	}

	@Override
	public void init() {
		super.init();

		exampleCache.clear();
		slots.clear();
		clearWidgets();

		for (int sy = 0; sy < ListHandlerNew.HEIGHT; sy++) {
			for (int sx = 0; sx < ListHandlerNew.WIDTH; sx++) {
				slots.add(new ListSlot(this, 8 + sx * 18, 32 + sy * 33, sy, sx));
			}
			int bOff = sy * BUTTON_COUNT;
			int bOffX = 8 + ListHandlerNew.WIDTH * 18 - BUTTON_COUNT * 11;
			int bOffY = 32 + sy * 33 + 18;

			addRenderableWidget(new GuiImageButton(bOff + 0, leftPos + bOffX, topPos + bOffY, 11, TEXTURE_BASE, 176, 16, 176, 28));
			addRenderableWidget(new GuiImageButton(bOff + 1, leftPos + bOffX + 11, topPos + bOffY, 11, TEXTURE_BASE, 176, 16, 185, 28));
			addRenderableWidget(new GuiImageButton(bOff + 2, leftPos + bOffX + 22, topPos + bOffY, 11, TEXTURE_BASE, 176, 16, 194, 28));
		}

		for (Renderable o : renderables) {
			if (o instanceof GuiImageButton b) {
				int lineId = b.id / BUTTON_COUNT;
				int buttonId = b.id % BUTTON_COUNT;
				if (getMenu().lines[lineId].getOption(buttonId)) {
					b.activate();
				}

				b.registerListener(this);
			}
		}

		textField = new EditBox(this.font, leftPos + 10, topPos + 10, 156, 12, Component.empty());
		textField.setMaxLength(32);
		textField.setValue(ItemsCore.LIST.get().getLabel(minecraft.player.getMainHandItem()));
		textField.setFocused(false);
		addRenderableWidget(textField);
	}

	@Override
	protected void initilaizeLedger(Inventory p_97742_) {

	}

	@Override
	protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
		super.renderBg(graphics, f, x, y);

		for (int i = 0; i < 2; i++) {
			if (getMenu().lines[i].isOneStackMode()) {
				graphics.blit(TEXTURE_BASE, leftPos + 6, topPos + 30 + i * 33, 0, 0, 20, 20);
			}
		}

		drawBackgroundSlots(graphics, x, y);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int par1, int par2) {
		super.renderLabels(graphics, par1, par2);

		drawTooltipForSlotAt(graphics, par1, par2);
	}

	private boolean isCarryingNonEmptyList() {
		ItemStack stack = minecraft.player.containerMenu.getCarried();
		return !stack.isEmpty() && stack.getItem() instanceof ListItem && stack.getTag() != null;
	}

	private boolean hasListEquipped() {
		return !minecraft.player.getMainHandItem().isEmpty() && minecraft.player.getMainHandItem().getItem() instanceof ListItem;
	}

	@Override
	public boolean mouseClicked(double x, double y, int b) {
		if (textField.mouseClicked(x, y, b)) {
			return true;
		}

		if (isCarryingNonEmptyList() || !hasListEquipped()) {
			return super.mouseClicked(x, y, b);
		}

		AdvancedSlot slot = getSlotAtLocation((int) x, (int) y);

		if (slot instanceof ListSlot) {
			getMenu().setStack(((ListSlot) slot).lineIndex, ((ListSlot) slot).slotIndex, minecraft.player.containerMenu.getCarried());
			clearExamplesCache(((ListSlot) slot).lineIndex);
			return true;
		}

		return super.mouseClicked(x, y, b);
	}

	@Override
	public void handleButtonClick(IButtonClickEventTrigger sender, int id) {
		int buttonId = id % BUTTON_COUNT;
		int lineId = id / BUTTON_COUNT;

		getMenu().switchButton(lineId, buttonId);
		clearExamplesCache(lineId);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textField.keyPressed(keyCode, scanCode, modifiers)) {
			getMenu().setLabel(textField.getValue());
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char p_94683_, int p_94684_) {
		if (textField.charTyped(p_94683_, p_94684_)) {
			getMenu().setLabel(textField.getValue());
			return true;
		}
		return super.charTyped(p_94683_, p_94684_);
	}
}