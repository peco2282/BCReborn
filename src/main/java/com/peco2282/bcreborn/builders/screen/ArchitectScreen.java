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
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import com.peco2282.bcreborn.builders.menu.ArchitectMenu;
import com.peco2282.bcreborn.common.blueprint.BlueprintReadConfiguration;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ArchitectScreen extends BuildCraftScreen<ArchitectMenu> {
    private static final ResourceLocation TEXTURE = BCRebornBuilders.location("textures/gui/architect_gui.png");
    private final ArchitectBlockEntity architect;

    private Button optionRotate;
    private Button optionExcavate;
    private Button optionAllowCreative;
    private EditBox nameField;

    public ArchitectScreen(ArchitectMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.architect = menu.getArchitect();
        this.imageWidth = 256;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        optionRotate = addRenderableWidget(Button.builder(Component.empty(), b -> {
            architect.readConfiguration.rotate = !architect.readConfiguration.rotate;
            updateConfiguration();
        }).bounds(leftPos + 5, topPos + 30, 79, 20).build());

        optionExcavate = addRenderableWidget(Button.builder(Component.empty(), b -> {
            architect.readConfiguration.excavate = !architect.readConfiguration.excavate;
            updateConfiguration();
        }).bounds(leftPos + 5, topPos + 55, 79, 20).build());

        optionAllowCreative = addRenderableWidget(Button.builder(Component.empty(), b -> {
            architect.readConfiguration.allowCreative = !architect.readConfiguration.allowCreative;
            updateConfiguration();
        }).bounds(leftPos + 5, topPos + 80, 79, 20).build());

        nameField = addRenderableWidget(new EditBox(this.font, leftPos + 90, topPos + 62, 156, 12, Component.empty()));
        nameField.setMaxLength(32);
        nameField.setValue(architect.getName().getString());
        nameField.setResponder(this::onNameChanged);

        updateButtonLabels();
    }

    private void onNameChanged(String newName) {
        BCNetworkManager.sendSetArchitectName(architect.getBlockPos(), newName);
    }

    private void updateConfiguration() {
        BCNetworkManager.sendSetReadArchitectConfiguration(architect.getBlockPos(), architect.readConfiguration);
        updateButtonLabels();
    }

    private void updateButtonLabels() {
        BlueprintReadConfiguration conf = architect.readConfiguration;
        optionRotate.setMessage(Component.translatable(conf.rotate ? "tile.architect.rotate" : "tile.architect.norotate"));
        optionExcavate.setMessage(Component.translatable(conf.excavate ? "tile.architect.excavate" : "tile.architect.noexcavate"));
        optionAllowCreative.setMessage(Component.translatable(conf.allowCreative ? "tile.architect.allowCreative" : "tile.architect.noallowCreative"));
    }

    @Override
    protected void initilaizeLedger(Inventory inventory) {
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int progress = menu.getComputingProgress();
        graphics.blit(TEXTURE, leftPos + 159, topPos + 34, 0, 166, progress + 1, 16);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
        // Inventory label position adjusted for 256 width
        graphics.drawString(this.font, Component.translatable("gui.inventory"), 88, imageHeight - 94, 0x404040, false);
    }
}
