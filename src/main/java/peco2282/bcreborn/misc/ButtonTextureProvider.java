/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.misc;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.BCReborn;

import java.util.ArrayList;
import java.util.Arrays;

public class ButtonTextureProvider {
  public static final ButtonSet LARGE =
      new ButtonSet(
          new Button(0, 40, 200, 20),
          new Button(0, 20, 200, 20),
          new Button(0, 0, 200, 20),
          new Button(0, 60, 200, 20));
  public static final ButtonSet MIDDLE =
      new ButtonSet(
          new Button(0, 95, 200, 15),
          new Button(0, 110, 200, 15),
          new Button(0, 80, 200, 15),
          new Button(0, 125, 200, 15));
  public static final ButtonSet SMALL =
      new ButtonSet(
          new Button(236, 48, 10, 10),
          new Button(246, 48, 10, 10),
          new Button(226, 48, 10, 10),
          null);
  public static final ButtonSet LEFT_SIDE =
      new ButtonSet(
          new Button(204, 16, 10, 16),
          new Button(204, 32, 10, 16),
          new Button(204, 0, 10, 16),
          null);
  public static final ButtonSet RIGHT_SIDE =
      new ButtonSet(
          new Button(214, 16, 10, 16),
          new Button(214, 32, 10, 16),
          new Button(214, 0, 10, 16),
          null);
  public static final ButtonSet UNLOCK =
      new ButtonSet(
          new Button(224, 16, 16, 16),
          new Button(224, 32, 16, 16),
          new Button(224, 0, 16, 16),
          null);
  public static final ButtonSet LOCK =
      new ButtonSet(
          new Button(240, 16, 16, 16),
          new Button(240, 32, 16, 16),
          new Button(240, 0, 16, 16),
          null);
  private static final ResourceLocation BUTTONS =
      BCReborn.location("textures/gui/sprites/buttons.png");

  public static void renderLargeButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, LARGE, type, x, y);
  }

  public static void renderMiddleButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, MIDDLE, type, x, y);
  }

  public static void renderSmallButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, SMALL, type, x, y);
  }

  public static void renderLeftSideButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, LEFT_SIDE, type, x, y);
  }

  public static void renderRightSideButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, RIGHT_SIDE, type, x, y);
  }

  public static void renderUnlockButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, UNLOCK, type, x, y);
  }

  public static void renderLockSideButton(GuiGraphics graphics, ButtonType type, int x, int y) {
    renderInternal(graphics, LOCK, type, x, y);
  }

  private static void renderInternal(
      GuiGraphics graphics, ButtonSet set, ButtonType type, int x, int y) {
    Rectangle target = LARGE.get(type);
    if (target == null) {
      throw new IllegalArgumentException(
          "Invalid button type: " + type.name() + ". Allows : " + Arrays.toString(set.validType()));
    }
    graphics.blit(BUTTONS, x, y, target.x(), target.y(), target.width(), target.height());
  }

  public enum ButtonType {
    ACTIVE,
    HOVER,
    HIDDEN,
    LOCK
  }

  public interface Rectangle {
    int x();

    int y();

    int width();

    int height();
  }

  public record ButtonSet(Button active, Button hovered, Button hidden, @Nullable Button locked) {
    public @Nullable Rectangle get(ButtonType type) {
      return switch (type) {
        case ACTIVE -> active();
        case HOVER -> hovered();
        case HIDDEN -> hidden();
        case LOCK -> locked();
      };
    }

    public String[] validType() {
      ArrayList<String> validTypes = new ArrayList<>();
      for (ButtonType type : ButtonType.values()) {
        if (get(type) == null) continue;
        validTypes.add(type.name());
      }
      return validTypes.toArray(String[]::new);
    }
  }

  private record Button(int x, int y, int width, int height) implements Rectangle {}
}
