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
package com.peco2282.bcreborn.common.config;

import com.peco2282.bcreborn.common.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigSelectionList extends ContainerObjectSelectionList<ConfigSelectionList.Entry> {
  public ConfigSelectionList(Minecraft minecraft, int width, int height, int y0, int y1) {
    super(minecraft, width, height, y0, y1, 28);
  }

  private static MutableComponent sectionTitle(ConfigSection section) {
    return section.title().withStyle(style -> style.withFont(Fonts.OXANIUM_SEMIBOLD));
  }

  private static MutableComponent entryTitle(ConfigEntry<?> entry) {
    return entry.title().withStyle(style -> style.withFont(Fonts.OXANIUM));
  }

  private static MutableComponent entryTooltip(ConfigEntry<?> entry) {
    return entry.tooltip().withStyle(style -> style.withFont(Fonts.RAJDHANI));
  }

  private static Style valueStyle() {
    return Style.EMPTY.withFont(Fonts.SHARE_TECH_MONO);
  }

  public void updateSize(int width, int height, int y0, int y1) {
    this.width = width;
    this.height = height;
    this.y0 = y0;
    this.y1 = y1;
  }

  public void addSection(ConfigSection section) {
    this.addEntry(new SectionEntry(section));
    for (ConfigEntry<?> entry : section.entries()) {
      this.addEntry(new ConfigEntryRow<>(entry));
    }
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTick);
  }

  @Override
  public int getRowWidth() {
    return 360;
  }

  @Override
  protected int getScrollbarPosition() {
    return this.width / 2 + 200;
  }

  public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    protected Font font = Minecraft.getInstance().font;
  }

  public static class SectionEntry extends Entry {
    private final Component title;

    public SectionEntry(ConfigSection title) {
      this.title = sectionTitle(title);
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
      graphics.drawString(this.font, this.title, left + width / 2 - this.font.width(this.title) / 2, top + height - 14, 0xFFFFFF);
    }

    @Override
    public List<? extends GuiEventListener> children() {
      return Collections.emptyList();
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
      return Collections.emptyList();
    }
  }

  public static class ConfigEntryRow<T> extends Entry {
    private final ConfigEntry<T> entry;
    private final AbstractWidget widget;
    private final List<AbstractWidget> children = new ArrayList<>();

    public ConfigEntryRow(ConfigEntry<T> entry) {
      this.entry = entry;
      this.widget = createWidget();
      if (this.widget != null) {
        this.children.add(this.widget);
      }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private AbstractWidget createWidget() {
      int widgetWidth = 120;
      int x = 0; // Will be set in render
      int y = 0;

      switch (entry.type()) {
        case BOOLEAN -> {
          return CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO)
            .withInitialValue((Boolean) entry.getter().get())
            .withTooltip(t -> Tooltip.create(entryTooltip(entry)))
            .displayOnlyValue()
            .create(x, y, widgetWidth, 20, entryTitle(entry), (b, value) -> entry.setter().accept((T) value));
        }
        case INTEGER, DOUBLE, STRING, STRING_LIST -> {
          EditBox editBox = new EditBox(this.font, x, y, widgetWidth, 20, entryTitle(entry));
          editBox.setValue(String.valueOf(entry.getter().get()));
          editBox.setFormatter((value, offset) -> FormattedCharSequence.forward(value, valueStyle()));
          editBox.setTooltip(Tooltip.create(entryTooltip(entry)));
          editBox.setResponder(s -> {
            try {
              if (entry.type() == ConfigEntry.EntryType.INTEGER) {
                @SuppressWarnings("WrapperTypeMayBePrimitive")
                Integer val = Integer.valueOf(s);
                if (entry.validator().test((T) val)) entry.setter().accept((T) val);
              } else if (entry.type() == ConfigEntry.EntryType.DOUBLE) {
                @SuppressWarnings("WrapperTypeMayBePrimitive")
                Double val = Double.parseDouble(s);
                if (entry.validator().test((T) val)) entry.setter().accept((T) val);
              } else if (entry.type() == ConfigEntry.EntryType.STRING) {
                if (entry.validator().test((T) s)) entry.setter().accept((T) s);
              } else {
                List<String> list = List.of(s.split(","));
                if (entry.validator().test((T) list)) entry.setter().accept((T) list);
              }
            } catch (NumberFormatException ignored) {
            }
          });
          return editBox;
        }
        case ENUM -> {
          T current = entry.getter().get();
          if (current instanceof Enum<?> e) {
            //noinspection RedundantCast
            return CycleButton.builder(o -> Component.literal(o.toString()))
              .withValues((T[]) e.getDeclaringClass().getEnumConstants())
              .withInitialValue(current)
              .withTooltip(t -> Tooltip.create(entryTooltip(entry)))
              .displayOnlyValue()
              .create(x, y, widgetWidth, 20, entryTitle(entry), (b, value) -> entry.setter().accept((T) value));
          }
        }
        // STRING_LIST is complex for a simple row, maybe implement later or use a button to open another screen
      }
      return null;
    }

    @Override
    public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
      graphics.drawString(this.font, this.entry.title(), left, top + 7, 0xFFFFFF);
      if (this.widget != null) {
        this.widget.setX(left + width - this.widget.getWidth());
        this.widget.setY(top + 4);
        this.widget.render(graphics, mouseX, mouseY, partialTick);
      }
    }

    @Override
    public List<? extends GuiEventListener> children() {
      return this.children;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
      return this.children;
    }
  }
}
