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

import com.mojang.blaze3d.systems.RenderSystem;
import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.menu.BuilderMenu;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.gui.AdvancedSlot;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.screen.AdvancedInterfaceScreen;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BuilderScreen extends AdvancedInterfaceScreen<BuilderMenu> {
  private static final ResourceLocation REGULAR_TEXTURE = BCRebornBuilders.location("textures/gui/builder.png");
  private static final ResourceLocation BLUEPRINT_TEXTURE = BCRebornBuilders.location("textures/gui/builder_blueprint.png");
  private final BuilderBlockEntity builder;

  public BuilderScreen(BuilderMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.builder = menu.getBuilder();
    this.imageWidth = 256;
    this.imageHeight = 225;
    resetNullSlots(6 * 4);

    for (int i = 0; i < 6; ++i) {
      for (int j = 0; j < 4; ++j) {
        slots.set(i * 4 + j, new SlotBuilderRequirement(this, 179 + j * 18, 18 + i * 18));
      }
    }
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  @Override
  protected void init() {
    super.init();
    if (!builder.getInventory().getItem(0).isEmpty()) {
      for (int i = 0; i < 4; i++) {
        addRenderableWidget(new BuilderEraseButton(i, leftPos + 178 + 18 * i, topPos + 197));
      }
    }
  }

  @Override
  protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    boolean isBlueprint = !builder.getInventory().getItem(0).isEmpty();

    graphics.blit(REGULAR_TEXTURE, leftPos, topPos, 0, 0, 176, imageHeight);
    if (isBlueprint) {
      graphics.blit(BLUEPRINT_TEXTURE, leftPos + 169, topPos, 169, 0, 256 - 169, imageHeight);
    }

    List<RequirementItemStack> needs = builder.getNeededItems();

    if (needs != null && !needs.isEmpty()) {
      if (needs.size() > slots.size()) {
        menu.getScrollbarWidget().hidden = false;
        menu.getScrollbarWidget().setLength((needs.size() - slots.size() + 3) / 4);
      } else {
        menu.getScrollbarWidget().hidden = true;
      }

      int offset = menu.getScrollbarWidget().getPosition() * 4;
      for (int s = 0; s < slots.size(); s++) {
        int ts = offset + s;
        if (ts >= needs.size()) {
          ((SlotBuilderRequirement) slots.get(s)).stack = null;
        } else {
          ((SlotBuilderRequirement) slots.get(s)).stack = needs.get(ts);
        }
      }
    } else {
      menu.getScrollbarWidget().hidden = true;
      for (AdvancedSlot slot : slots) {
        if (slot instanceof SlotBuilderRequirement sbr) {
          sbr.stack = null;
        }
      }
    }

    drawWidgets(graphics, mouseX - leftPos, mouseY - topPos);

    if (isBlueprint) {
      drawBackgroundSlots(graphics, mouseX, mouseY);
    }

    if (isBlueprint) {
      for (int i = 0; i < builder.getFluidTanks().size(); i++) {
        Tank tank = builder.getFluidTanks().get(i);
        if (!tank.getFluid().isEmpty() && tank.getFluid().getAmount() > 0) {
          drawFluid(graphics, tank.getFluid(), leftPos + 179 + 18 * i, topPos + 145, 16, 47, tank.getCapacity());
        }
      }

      RenderSystem.setShaderTexture(0, BLUEPRINT_TEXTURE);
      for (int i = 0; i < builder.getFluidTanks().size(); i++) {
        Tank tank = builder.getFluidTanks().get(i);
        if (!tank.getFluid().isEmpty() && tank.getFluid().getAmount() > 0) {
          graphics.blit(BLUEPRINT_TEXTURE, leftPos + 179 + 18 * i, topPos + 145, 0, 54, 16, 47);
        }
      }
    }
  }

  private void drawFluid(GuiGraphics graphics, FluidStack fluid, int x, int y, int width, int height, int maxCapacity) {
    if (fluid.isEmpty()) {
      return;
    }

    IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
    ResourceLocation stillTexture = extensions.getStillTexture(fluid);
    if (stillTexture == null) {
      return;
    }

    TextureAtlasSprite icon = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

    int level = (int) (fluid.getAmount() * height / (float) maxCapacity);
    int color = extensions.getTintColor(fluid);

    float r = ((color >> 16) & 0xFF) / 255f;
    float g = ((color >> 8) & 0xFF) / 255f;
    float b = (color & 0xFF) / 255f;
    float a = ((color >> 24) & 0xFF) / 255f;

    RenderSystem.setShaderColor(r, g, b, a);
    graphics.blit(x, y + height - level, 0, width, level, icon);
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
  }

  @Override
  protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    graphics.drawString(this.font, this.title, 178 / 2 - this.font.width(this.title) / 2, 16, 0x404040, false);
    if (!builder.getInventory().getItem(0).isEmpty()) {
      graphics.drawString(this.font, Component.translatable("gui.building.resources"), 8, 60, 0x404040, false);
      graphics.drawString(this.font, Component.translatable("gui.inventory"), 8, imageHeight - 97, 0x404040, false);
      graphics.drawString(this.font, Component.translatable("gui.needed"), 178, 7, 0x404040, false);
      graphics.drawString(this.font, Component.translatable("gui.building.fluids"), 178, 133, 0x404040, false);
    }
    drawTooltips(graphics, mouseX, mouseY);
  }


  private void drawTooltips(GuiGraphics graphics, int par1, int par2) {
    int top = topPos + 145;
    for (int i = 0; i < builder.getFluidTanks().size(); i++) {
      int left = leftPos + 179 + 18 * i;
      if (par1 >= left && par2 >= top && par1 < (left + 16) && par2 < (top + 47)) {
        List<Component> fluidTip = new ArrayList<>();
        Tank tank = builder.getFluidTanks().get(i);
        if (!tank.getFluid().isEmpty()) {
          fluidTip.add(tank.getFluid().getDisplayName());
          if (!BCRebornCore.hideFluidNumbers) {
            fluidTip.add(Component.literal(tank.getFluid().getAmount() + " mB").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
          }
        } else {
          fluidTip.add(Component.translatable("gui.fluidtank.empty"));
        }
        graphics.renderComponentTooltip(font, fluidTip, par1, par2);
        return;
      }
    }

    drawTooltipForSlotAt(graphics, par1, par2);
  }

  static class SlotBuilderRequirement extends AdvancedSlot {
    public RequirementItemStack stack;

    public SlotBuilderRequirement(BuilderScreen gui, int x, int y) {
      super(gui, x, y);
    }

    @Override
    public ItemStack getItemStack() {
      return stack != null ? stack.stack : ItemStack.EMPTY;
    }

    @Override
    public void drawStack(GuiGraphics guiGraphics, ItemStack item) {
      super.drawStack(guiGraphics, item);

      if (stack != null) {
        // Render real stack size
        String s = String.valueOf(stack.size > 999 ? Math.min(99, stack.size / 1000) + "K" : stack.size);
        int cornerX = (gui.width - gui.getXSize()) / 2;
        int cornerY = (gui.height - gui.getYSize()) / 2;

        RenderSystem.disableDepthTest();
        guiGraphics.drawString(gui.getMinecraft().font, s,
          cornerX + x + 17 - gui.getMinecraft().font.width(s), cornerY + y + 9, 16777215, true);
        RenderSystem.enableDepthTest();
      }
    }
  }

  private class BuilderEraseButton extends Button {
    public BuilderEraseButton(int id, int x, int y) {
      super(x, y, 18, 18, Component.empty(), b -> BCNetworkManager.sendEraseBuilderTank(builder.getBlockPos(), id), DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (!visible) {
        return;
      }
      int u = 0;
      if (isHoveredOrFocused()) {
        u = 36; // 2 * 18
      }
      // Note: 1.7.10 also had a 'clicked' state (u=18), but vanilla Button 1.20.1
      // doesn't expose it easily. Hover state is sufficient for visual feedback.
      graphics.blit(BLUEPRINT_TEXTURE, getX(), getY(), u, 0, 18, 18);
    }
  }
}
