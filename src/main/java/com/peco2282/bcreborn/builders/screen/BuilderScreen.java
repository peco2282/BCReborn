package com.peco2282.bcreborn.builders.screen;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.menu.BuilderMenu;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuilderScreen extends BuildCraftScreen<BuilderMenu> {
    private static final ResourceLocation REGULAR_TEXTURE = BCRebornBuilders.location("textures/gui/builder.png");
    private static final ResourceLocation BLUEPRINT_TEXTURE = BCRebornBuilders.location("textures/gui/builder_blueprint.png");
    private final BuilderBlockEntity builder;

    public BuilderScreen(BuilderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.builder = menu.getBuilder();
        this.imageWidth = 256;
        this.imageHeight = 225;
    }

    @Override
    protected void initilaizeLedger(Inventory inventory) {
    }

    @Override
    protected void init() {
        super.init();
        if (!builder.getInventory().getItem(0).isEmpty()) {
            addRenderableWidget(new BuilderEraseButton(leftPos + 233, topPos + 160));
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        boolean isBlueprint = !builder.getInventory().getItem(0).isEmpty();

        graphics.blit(REGULAR_TEXTURE, leftPos, topPos, 0, 0, 176, imageHeight);
        if (isBlueprint) {
            graphics.blit(BLUEPRINT_TEXTURE, leftPos + 169, topPos, 169, 0, 256 - 169, imageHeight);
            
            // Render required items
            List<RequirementItemStack> requirements = builder.getRequiredItems();
            int off = menu.getScrollbarWidget().getPosition();
            for (int i = 0; i < 6; i++) {
                int index = i + off;
                if (index < requirements.size()) {
                    RequirementItemStack req = requirements.get(index);
                    ItemStack stack = req.stack;
                    graphics.renderFakeItem(stack, leftPos + 179, topPos + 18 + i * 18);
                    graphics.drawString(this.font, "x" + req.size, leftPos + 200, topPos + 22 + i * 18, 0x404040, false);
                }
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 178 / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
        if (!builder.getInventory().getItem(0).isEmpty()) {
            graphics.drawString(this.font, Component.translatable("gui.building.resources"), 8, 60, 0x404040, false);
            graphics.drawString(this.font, Component.translatable("gui.inventory"), 8, imageHeight - 97, 0x404040, false);
            graphics.drawString(this.font, Component.translatable("gui.needed"), 178, 7, 0x404040, false);
            graphics.drawString(this.font, Component.translatable("gui.building.fluids"), 178, 133, 0x404040, false);
        }
    }

    private class BuilderEraseButton extends Button {
        public BuilderEraseButton(int x, int y) {
            super(x, y, 16, 16, Component.empty(), b -> {
                BCNetworkManager.sendEraseBuilderTank(builder.getBlockPos());
            }, DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            // Render custom erase icon from texture
            graphics.blit(BLUEPRINT_TEXTURE, getX(), getY(), 234, 160, 16, 16);
        }
    }
}
