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
package com.peco2282.bcreborn.silicon.block.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageFontRenderer extends Font {
  private final CompoundTag pkgTag;
  private final Pattern stringPattern = Pattern.compile("^\\{\\{BC_PACKAGE_SPECIAL:([0-2])}}$");

  public PackageFontRenderer(ItemStack packageStack) {
    super(text -> {
      try {
        var field = Font.class.getDeclaredField("fonts");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") Function<ResourceLocation, FontSet> func = (Function<ResourceLocation, FontSet>) field.get(Minecraft.getInstance().font);
        return func.apply(text);
      } catch (Exception e) {
        return null;
      }
    }, false);
    this.pkgTag = packageStack.getOrCreateTag();
  }

  @Override
  public int width(String text) {
    Matcher m = stringPattern.matcher(text);
    if (!m.find()) {
      return super.width(text);
    }
    return 21;
  }

  // 1.20.1では drawString ではなく GuiGraphics を通じて描画されることが多いが、
  // Fontクラスをオーバーライドして特殊な描画を行う場合は、内部のレンダリングメソッドをフックする。
  // ここでは簡易化のため、widthのみを調整し、実際の描画はScreen側で行うか、
  // あるいは特定の描画コンテキストでこのFontが使われた際に特殊処理を行う。

  public void drawPackageItems(GuiGraphics guiGraphics, String text, int x, int y) {
    Matcher m = stringPattern.matcher(text);
    if (!m.find()) {
      guiGraphics.drawString(this, text, x, y, 0xFFFFFFFF);
      return;
    }

    int index = Integer.parseInt(m.group(1)) * 3;
    for (int i = 0; i < 3; i++) {
      int slot = index + i;
      if (pkgTag.contains("item" + slot)) {
        ItemStack stack = ItemStack.of(pkgTag.getCompound("item" + slot));
        if (!stack.isEmpty()) {
          guiGraphics.pose().pushPose();
          guiGraphics.pose().translate(x + i * 7, y, 0);
          guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
          guiGraphics.renderItem(stack, 0, 0);
          guiGraphics.pose().popPose();
        }
      }
    }
  }
}
