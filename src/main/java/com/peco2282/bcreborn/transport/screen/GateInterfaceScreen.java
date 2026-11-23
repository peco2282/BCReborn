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
package com.peco2282.bcreborn.transport.screen;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gui.screens.Screen;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.common.gui.AdvancedSlot;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.common.gui.StatementParameterSlot;
import com.peco2282.bcreborn.common.gui.StatementSlot;
import com.peco2282.bcreborn.common.screen.AdvancedInterfaceScreen;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.menu.GateInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Function;
import net.minecraft.client.gui.components.Button;

/**
 * Port of the original {@code GuiGateInterface}: icon slots for the triggers and actions and their parameters,
 * laid out per gate material as in the original.
 * <ul>
 *   <li>left click / wheel up: the next candidate (an empty slot takes the first one);</li>
 *   <li>right click / wheel down: the previous candidate;</li>
 *   <li>shift click: clear the slot;</li>
 *   <li>a parameter slot is clicked with the item to use (an empty hand clears an item parameter).</li>
 * </ul>
 * The lamp next to each trigger shows the row's state (original actionsState). The clicks are decided on the
 * server ({@link GateInterfaceMenu#clickMenuButton}).
 */
public class GateInterfaceScreen extends AdvancedInterfaceScreen<GateInterfaceMenu> {
  private Gate gate;
  @Nullable
  private GateDefinition.GateMaterial laidOutFor;

  public GateInterfaceScreen(GateInterfaceMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.imageWidth = 176;
    this.imageHeight = 166;
  }

  @Override
  protected void init() {
    gate = menu.getGate();
    if (gate != null) {
      imageHeight = gate.material.guiHeight;
    }
    super.init();
    layout();
  }

  /** Original GuiGateInterface.init(): the slot positions of each material. */
  private void layout() {
    slots.clear();
    laidOutFor = gate == null ? null : gate.material;
    if (gate == null) return;
    switch (gate.material) {
      case REDSTONE -> {
        slots.add(new TriggerSlot(62, 26, 0));
        slots.add(new ActionSlot(98, 26, 0));
      }
      case IRON -> {
        slots.add(new TriggerSlot(62, 26, 0));
        slots.add(new TriggerSlot(62, 44, 1));
        slots.add(new ActionSlot(98, 26, 0));
        slots.add(new ActionSlot(98, 44, 1));
      }
      case QUARTZ -> {
        for (int i = 0; i < 2; i++) {
          TriggerSlot ts = new TriggerSlot(44, 26 + i * 18, i);
          ActionSlot as = new ActionSlot(98, 26 + i * 18, i);
          slots.add(ts);
          slots.add(as);
          slots.add(new TriggerParameterSlot(62, 26 + i * 18, 0, ts));
          slots.add(new ActionParameterSlot(116, 26 + i * 18, 0, as));
        }
      }
      case GOLD -> {
        for (int k = 0; k < 4; k++) {
          TriggerSlot ts = new TriggerSlot(53, 26 + 18 * k, k);
          slots.add(ts);
          slots.add(new ActionSlot(107, 26 + 18 * k, k));
          slots.add(new TriggerParameterSlot(71, 26 + 18 * k, 0, ts));
        }
      }
      case DIAMOND -> {
        for (int k = 0; k < 4; k++) {
          TriggerSlot left = new TriggerSlot(8, 26 + 18 * k, k * 2);
          TriggerSlot right = new TriggerSlot(98, 26 + 18 * k, k * 2 + 1);
          slots.add(left);
          slots.add(right);
          slots.add(new ActionSlot(62, 26 + 18 * k, k * 2));
          slots.add(new ActionSlot(152, 26 + 18 * k, k * 2 + 1));
          slots.add(new TriggerParameterSlot(26, 26 + 18 * k, 0, left));
          slots.add(new TriggerParameterSlot(116, 26 + 18 * k, 0, right));
        }
      }
      case EMERALD -> {
        for (int y = 0; y < 4; y++) {
          TriggerSlot ts = new TriggerSlot(8, 26 + 18 * y, y);
          slots.add(ts);
          for (int x = 0; x < 3; x++) {
            slots.add(new TriggerParameterSlot(8 + 18 * (x + 1), 26 + 18 * y, x, ts));
          }
          ActionSlot as = new ActionSlot(98, 26 + 18 * y, y);
          slots.add(as);
          for (int x = 0; x < 3; x++) {
            slots.add(new ActionParameterSlot(98 + 18 * (x + 1), 26 + 18 * y, x, as));
          }
        }
      }
    }
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    if (gate == null) return;
    ResourceLocation texture = gate.material.guiFile;
    graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

    for (AdvancedSlot slot : slots) {
      if (slot instanceof TriggerSlot trigger) {
        // Original: the lamp right of the trigger (and its parameters) shows the row's state.
        Gate.ActionActiveState state = menu.getActionState(trigger.slot);
        if (state != Gate.ActionActiveState.Deactivated) {
          boolean halfWidth = state == Gate.ActionActiveState.Partial;
          graphics.blit(texture, leftPos + slot.x + 17 + 18 * gate.material.numTriggerParameters, topPos + slot.y + 6,
            176, 18, halfWidth ? 9 : 18, 4);
        }
      } else if (slot instanceof StatementParameterSlot param) {
        // Original: a parameter slot the statement does not use is greyed out; a required, empty one is marked.
        if (param.statementSlot.isDefined()) {
          if (!param.isAllowed()) {
            graphics.blit(texture, leftPos + slot.x - 1, topPos + slot.y - 1, 176, 0, 18, 18);
          } else if (param.isRequired() && param.getItemStack().isEmpty() && param.getParameter() == null) {
            graphics.blit(texture, leftPos + slot.x - 1, topPos + slot.y - 1, 176, 22, 18, 18);
          }
        } else {
          graphics.blit(texture, leftPos + slot.x - 1, topPos + slot.y - 1, 176, 0, 18, 18);
        }
      }
    }

    drawBackgroundSlots(graphics, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    if (gate == null) return;
    String name = GateDefinition.getLocalizedName(gate.material, gate.logic);
    graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 10, 0x404040, false);
    graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 97, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    // The gate object is replaced whenever the pipe's data arrives again.
    Gate current = menu.getGate();
    if (current != null) {
      gate = current;
      if (gate.material != laidOutFor) {
        imageHeight = gate.material.guiHeight;
        layout();
      }
    }
    renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    if (menu.getCarried().isEmpty()) {
      AdvancedSlot slot = getSlotAtLocation(mouseX, mouseY);
      if (slot != null && slot.isDefined()) {
        drawTooltipForSlotAt(graphics, mouseX, mouseY);
      } else {
        renderTooltip(graphics, mouseX, mouseY);
      }
    }
  }

  private void send(int kind, int row, int parameter, int click, boolean shift) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, GateInterfaceMenu.buttonId(kind, row, parameter, click, shift));
    }
  }

  /** Original doSlotClick(). */
  private boolean slotClick(@Nullable AdvancedSlot slot, int button) {
    if (gate == null || slot == null) return false;
    boolean shift = Screen.hasShiftDown();
    if (slot instanceof TriggerSlot trigger) {
      if (!shift && menu.getPotentialTriggers().isEmpty()) return true;
      send(GateInterfaceMenu.KIND_TRIGGER, trigger.slot, 0, statementClick(button, shift), false);
      return true;
    }
    if (slot instanceof ActionSlot action) {
      if (!shift && menu.getPotentialActions().isEmpty()) return true;
      send(GateInterfaceMenu.KIND_ACTION, action.slot, 0, statementClick(button, shift), false);
      return true;
    }
    if (slot instanceof StatementParameterSlot param && param.statementSlot.isDefined()
      && param.statementSlot.getStatement().maxParameters() != 0) {
      int kind = param instanceof TriggerParameterSlot ? GateInterfaceMenu.KIND_TRIGGER_PARAMETER : GateInterfaceMenu.KIND_ACTION_PARAMETER;
      send(kind, param.statementSlot.slot, param.slot, Math.min(button, 2), shift);
      return true;
    }
    return false;
  }

  private static int statementClick(int button, boolean shift) {
    if (shift) return GateInterfaceMenu.CLICK_CLEAR;
    return button == GLFW.GLFW_MOUSE_BUTTON_LEFT ? GateInterfaceMenu.CLICK_NEXT : GateInterfaceMenu.CLICK_PREVIOUS;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (slotClick(getSlotAtLocation(mouseX, mouseY), button)) return true;
    return super.mouseClicked(mouseX, mouseY, button);
  }

  /** Original handleMouseInput(): the wheel cycles a statement slot (up = next, down = previous). */
  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    AdvancedSlot slot = getSlotAtLocation(mouseX, mouseY);
    if (delta != 0 && (slot instanceof TriggerSlot || slot instanceof ActionSlot)) {
      return slotClick(slot, delta > 0 ? GLFW.GLFW_MOUSE_BUTTON_LEFT : GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  private class TriggerSlot extends StatementSlot {
    TriggerSlot(int x, int y, int slot) {
      super(GateInterfaceScreen.this, x, y, slot);
    }

    @Override
    public IStatement getStatement() {
      return gate == null ? null : gate.getTrigger(slot);
    }
  }

  private class ActionSlot extends StatementSlot {
    ActionSlot(int x, int y, int slot) {
      super(GateInterfaceScreen.this, x, y, slot);
    }

    @Override
    public IStatement getStatement() {
      return gate == null ? null : gate.getAction(slot);
    }
  }

  private class TriggerParameterSlot extends StatementParameterSlot {
    TriggerParameterSlot(int x, int y, int slot, StatementSlot statementSlot) {
      super(GateInterfaceScreen.this, x, y, slot, statementSlot);
    }

    @Override
    public IStatementParameter getParameter() {
      return gate == null ? null : gate.getTriggerParameter(statementSlot.slot, slot);
    }

    @Override
    public void setParameter(IStatementParameter param, boolean notifyServer) {
      // Decided on the server; the gate arrives with the pipe's update.
    }
  }

  private class ActionParameterSlot extends StatementParameterSlot {
    ActionParameterSlot(int x, int y, int slot, StatementSlot statementSlot) {
      super(GateInterfaceScreen.this, x, y, slot, statementSlot);
    }

    @Override
    public IStatementParameter getParameter() {
      return gate == null ? null : gate.getActionParameter(statementSlot.slot, slot);
    }

    @Override
    public void setParameter(IStatementParameter param, boolean notifyServer) {
    }
  }
}
