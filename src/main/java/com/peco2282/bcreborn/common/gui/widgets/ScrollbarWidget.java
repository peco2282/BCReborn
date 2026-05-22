package com.peco2282.bcreborn.common.gui.widgets;


import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.common.utils.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScrollbarWidget extends Widget {
	private static final int HEIGHT = 14;
	private int pos, len;
	private boolean isClicking;

	public ScrollbarWidget(int x, int y, int u, int v, int h) {
		super(x, y, u, v, 6, h);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw(net.minecraft.client.gui.GuiGraphics graphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
		gui.drawTexturedModalRect(graphics, guiX + x, guiY + y, u, v, w, h);
		int posPx = pos * (h - HEIGHT + 2) / len;
		gui.drawTexturedModalRect(graphics, guiX + x, guiY + y + posPx, u + 6, v, w, HEIGHT);
	}

	private void updateLength(int mouseY) {
		setPosition(((mouseY - y) * len + (h / 2)) / h);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			isClicking = true;
			updateLength(mouseY);
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		if (isClicking && mouseButton == 0) {
			updateLength(mouseY);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		if (isClicking && eventType == 0) {
			updateLength(mouseY);
			isClicking = false;
		}
	}

	public int getPosition() {
		return pos;
	}

	public void setPosition(int pos) {
		this.pos = MathUtils.clamp(pos, 0, len);
	}

	public void setLength(int len) {
		this.len = len;
		setPosition(this.pos);
	}
}
