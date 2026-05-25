/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.list;


import com.mojang.blaze3d.systems.RenderSystem;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.gui.AdvancedSlot;
import com.peco2282.bcreborn.common.gui.GuiAdvancedInterface;
import com.peco2282.bcreborn.core.ItemsCore;
import com.peco2282.bcreborn.core.item.ListItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ListOldScreen extends GuiAdvancedInterface<ListOldMenu> {

	private static final ResourceLocation TEXTURE_BASE = BCRebornCore.location("textures/gui/list.png");

	private EditBox textField;
	private Player player;

	private static class MainSlot extends AdvancedSlot {
		public int lineIndex;

		public MainSlot(ListOldScreen gui, int x, int y, int iLineIndex) {
			super(gui, x, y);

			lineIndex = iLineIndex;
		}

		@Override
		public ItemStack getItemStack() {
			ListOldMenu container = (ListOldMenu) gui.getMenu();

			return container.lines[lineIndex].getStack(0);
		}
	}

	private static class SecondarySlot extends AdvancedSlot {
		public int lineIndex;
		public int slotIndex;

		public SecondarySlot(ListOldScreen gui, int x, int y, int iLineIndex, int iSlotIndex) {
			super(gui, x, y);

			lineIndex = iLineIndex;
			slotIndex = iSlotIndex;
		}

		@Override
		public ItemStack getItemStack() {
			ListOldMenu container = (ListOldMenu) gui.getMenu();

			if (slotIndex == 6 && container.lines[lineIndex].getStack(7) != null) {
				return null;
			}

			return container.lines[lineIndex].getStack(slotIndex);
		}
	}

	private static class Button extends AdvancedSlot {

		public int line;
		public int kind;
		private String desc;

		public Button(ListOldScreen gui, int x, int y, int iLine, int iKind, String iDesc) {
			super(gui, x, y);

			line = iLine;
			kind = iKind;
			desc = iDesc;
		}

		@Override
		public String getDescription() {
			return desc;
		}
	}

	public ListOldScreen(ListOldMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);

		width = 176;
		height = 241;

		for (int sy = 0; sy < 6; sy++) {
			slots.add(new MainSlot(this, 44, 31 + sy * 18, sy));

			for (int sx = 1; sx < 7; sx++) {
				slots.add(new SecondarySlot(this, 44 + sx * 18, 31 + sy * 18, sy, sx));
			}

			slots.add(new Button(this, 8, 31 + sy * 18, sy, 0, "gui.list.metadata"));
			slots.add(new Button(this, 26, 31 + sy * 18, sy, 1, "gui.list.oredict"));
		}

		player = inventory.player;
	}

	@Override
	public void init() {
		super.init();

		textField = new EditBox(this.font, 10, 10, 156, 12, Component.empty());
		textField.setMaxLength(32);
		textField.setValue(ItemsCore.LIST.get().getLabel(player.getMainHandItem()));
		textField.setFocused(false);
	}

	@Override
	protected void initilaizeLedger(Inventory p_97742_) {

	}

	@Override
	protected void renderBg(GuiGraphics graphics, float f, int x, int y) {
		super.renderBg(graphics, f, x, y);

		ListOldMenu container = getMenu();

		RenderSystem.setShaderTexture(0, TEXTURE_BASE);

		for (int i = 0; i < 6; ++i) {
			if (container.lines[i].subitemsWildcard) {
				drawTexturedModalRect(graphics, getGuiLeft() + 7, getGuiTop() + 30 + 18 * i, 194, 18, 18, 18);
			} else {
				drawTexturedModalRect(graphics, getGuiLeft() + 7, getGuiTop() + 30 + 18 * i, 194, 0, 18, 18);
			}

			if (container.lines[i].isOre) {
				if (container.lines[i].oreWildcard) {
					drawTexturedModalRect(graphics, getGuiLeft() + 25, getGuiTop() + 30 + 18 * i, 176, 18, 18, 18);
				} else {
					drawTexturedModalRect(graphics, getGuiLeft() + 25, getGuiTop() + 30 + 18 * i, 176, 0, 18, 18);
				}
			}

			if (container.lines[i].subitemsWildcard || container.lines[i].oreWildcard) {
				for (int j = 0; j < 6; ++j) {
					drawTexturedModalRect(graphics, getGuiLeft() + 62 + 18 * j, getGuiTop() + 31 + 18 * i, 195, 37, 16, 16);
				}
			}
		}

		drawBackgroundSlots(graphics, x, y);

		RenderSystem.setShaderTexture(0, TEXTURE_BASE);

		for (int i = 0; i < 6; ++i) {
			if (container.lines[i].getStack(7) != null) {
				drawTexturedModalRect(graphics, getGuiLeft() + 152, getGuiTop() + 31 + 18 * i, 177, 37, 16, 16);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int par1, int par2) {
		super.renderLabels(graphics, par1, par2);

		textField.render(graphics, par1, par2, 0);

		drawTooltipForSlotAt(graphics, par1, par2);
	}

	private boolean isCarryingList() {
		ItemStack stack = minecraft.player.getInventory().getSelected();
		return !stack.isEmpty() && stack.getItem() instanceof ListItem;
	}

	private boolean hasListEquipped() {
		return !minecraft.player.getMainHandItem().isEmpty() && minecraft.player.getMainHandItem().getItem() instanceof ListItem;
	}

	@Override
	public boolean mouseClicked(double x, double y, int b) {
		super.mouseClicked(x, y, b);

		if (isCarryingList() || !hasListEquipped()) {
			return false;
		}

		AdvancedSlot slot = getSlotAtLocation(x, y);
		ListOldMenu container = getMenu();

		if (slot instanceof MainSlot) {
			container.setStack(((MainSlot) slot).lineIndex, 0, minecraft.player.getMainHandItem());
		} else if (slot instanceof SecondarySlot) {
			container.setStack(((SecondarySlot) slot).lineIndex, ((SecondarySlot) slot).slotIndex,
					minecraft.player.getMainHandItem());
		} else if (slot instanceof Button) {
			Button button = (Button) slot;

			container.switchButton(button.line, button.kind);
		}

		textField.mouseClicked(x - getGuiLeft(), y - getGuiTop(), b);
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (textField.isFocused()) {
			if (keyCode == 28 || keyCode == 27) {
				textField.setFocused(false);
			} else {
				textField.keyPressed(keyCode, scanCode, modifiers);
				((ListOldMenu) getMenu()).setLabel(textField.getValue());
			}
		} else {
			super.keyPressed(keyCode, scanCode, modifiers);
		}
		return true;
	}
}