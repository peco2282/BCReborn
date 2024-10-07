package peco2282.bcreborn.misc;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.BCReborn;

import java.util.function.UnaryOperator;

public class ButtonTextureProvider {
  private static final ResourceLocation BUTTONS = BCReborn.location("buttons.png");

  private static final WidgetSprites LEFT_BUTTON = new WidgetSprites(
      BCReborn.location("off_left_button.png"),
      BCReborn.location("on_left_button.png"),
      BCReborn.location("selected_left_button.png")
  );
  private static final WidgetSprites RIGHT_BUTTON = new WidgetSprites(
      BCReborn.location("off_right_button.png"),
      BCReborn.location("on_right_button.png"),
      BCReborn.location("selected_right_button.png")
  );
  private static final WidgetSprites LOCK_BUTTON = new WidgetSprites(
      BCReborn.location("off_lock.png"),
      BCReborn.location("on_lock.png"),
      BCReborn.location("selected_lock.png")
  );
  private static final WidgetSprites UNLOCK_BUTTON = new WidgetSprites(
      BCReborn.location("off_unlock.png"),
      BCReborn.location("on_unlock.png"),
      BCReborn.location("selected_unlock.png")
  );

  public static void leftButton(GuiGraphics graphics, Mode mode, int startX, int startY, boolean active, boolean focused) {
    var sp = LEFT_BUTTON.get(active, focused);
    graphics.blitSprite(LEFT_BUTTON.get(active, focused), startX, startY, 10, 16);
  }

  public static void rightButton(GuiGraphics graphics, Mode mode, int startX, int startY) {
    int y = switch (mode) {
      case OFF -> 0;
      case ON -> 16;
      case SELECTED -> 32;
    };
    graphics.blit(
        BUTTONS,
        startX,
        startY,
        214,
        y,
        10,
        16
    );
  }  public static void lockButton(GuiGraphics graphics, Mode mode, int startX, int startY) {
    int y = switch (mode) {
      case OFF -> 0;
      case ON -> 16;
      case SELECTED -> 32;
    };
    graphics.blit(
        BUTTONS,
        startX,
        startY,
        240,
        y,
        16,
        16
    );
  }

  public static void unlockButton(GuiGraphics graphics, Mode mode, int startX, int startY) {
    int y = switch (mode) {
      case OFF -> 0;
      case ON -> 16;
      case SELECTED -> 32;
    };
    graphics.blit(
        BUTTONS,
        startX,
        startY,
        224,
        y,
        16,
        16
    );
  }

  public enum Mode {
    OFF, ON, SELECTED;
    String path(@NotNull UnaryOperator<String> operator) {
      return operator.apply(name().toLowerCase());
    }
  }
}
