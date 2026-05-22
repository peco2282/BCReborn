package com.peco2282.bcreborn.builders.screen;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FillerScreen extends BuildCraftScreen<FillerMenu> {
    private static final ResourceLocation TEXTURE = BCRebornBuilders.location("textures/gui/filler.png");
    private final FillerBlockEntity filler;

    public FillerScreen(FillerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.filler = menu.getFiller();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new FillerButton(leftPos + 115, topPos + 18, 16, 16, 176, 0, b -> {
            changePattern(-1);
        }));

        addRenderableWidget(new FillerButton(leftPos + 151, topPos + 18, 16, 16, 192, 0, b -> {
            changePattern(1);
        }));
    }

    private void changePattern(int delta) {
        int pattern = (menu.getCurrentPattern() + delta + 16) % 16; // Assume 16 patterns
        BCNetworkManager.sendSetFillerPattern(filler.getBlockPos(), pattern);
    }

    @Override
    protected void initilaizeLedger(Inventory inventory) {
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        // Render current pattern icon
        int pattern = menu.getCurrentPattern();
        graphics.blit(TEXTURE, leftPos + 133, topPos + 18, 176 + (pattern % 4) * 16, 16 + (pattern / 4) * 16, 16, 16);

        // Tooltip for pattern
        if (isHovering(133, 18, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.translatable("gui.filler.pattern." + pattern), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("gui.inventory"), 8, imageHeight - 94, 0x404040, false);
    }

    private class FillerButton extends Button {
        private final int u;
        private final int v;

        public FillerButton(int x, int y, int width, int height, int u, int v, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.u = u;
            this.v = v;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            int i = 0;
            if (!this.active) {
                i = 2;
            } else if (this.isHoveredOrFocused()) {
                i = 1;
            }
            graphics.blit(TEXTURE, getX(), getY(), u, v + i * height, width, height);
        }
    }
}
