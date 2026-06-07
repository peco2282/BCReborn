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

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.common.gui.AdvancedInterfaceScreen;
import com.peco2282.bcreborn.common.gui.StatementParameterSlot;
import com.peco2282.bcreborn.common.gui.StatementSlot;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.menu.GateInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Function;

public class GateInterfaceScreen extends AdvancedInterfaceScreen<GateInterfaceMenu> {
  private static final Function<String, ResourceLocation> TEXTURE = s -> {
    assert s.length() == 1;
    char it = s.charAt(0);
    String index;
    if (it < '1') {
      index = "1";
    } else if ('6' < it) {
      index = "6";
    } else {
      index = s;
    }
    return BCRebornTransport.location("textures/gui/gate_interface_" + index + ".png");
  };
  private IPipe pipe;
  private Gate gate;

  public GateInterfaceScreen(GateInterfaceMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    this.imageWidth = 176;
    this.imageHeight = 166;
    this.inventoryLabelY = this.imageHeight - 93;
  }

  @Override
  protected void initilaizeLedger(Inventory p_97742_) {
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(TEXTURE.apply("1"), x, y, 0, 0, this.imageWidth, this.imageHeight);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
    guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  private class TriggerSlot extends StatementSlot {
    public TriggerSlot(int x, int y, IPipe pipe, int slot) {
      super(GateInterfaceScreen.this, x, y, slot);
    }

    @Override
    public IStatement getStatement() {
      return gate.getTrigger(slot);
    }
  }

  private class ActionSlot extends StatementSlot {
    public ActionSlot(int x, int y, IPipe pipe, int slot) {
      super(GateInterfaceScreen.this, x, y, slot);
    }

    @Override
    public IStatement getStatement() {
      return gate.getAction(slot);
    }
  }

  class TriggerParameterSlot extends StatementParameterSlot {
    public TriggerParameterSlot(int x, int y, IPipe pipe, int slot, StatementSlot iStatementSlot) {
      super(GateInterfaceScreen.this, x, y, slot, iStatementSlot);
    }

    @Override
    public IStatementParameter getParameter() {
      return gate.getTriggerParameter(statementSlot.slot, slot);
    }

    @Override
    public void setParameter(IStatementParameter param, boolean notifyServer) {
      gate.setTriggerParameter(statementSlot.slot, slot, param);
      // TODO: notifyServer logic if needed, or handle in Gate
    }
  }

  class ActionParameterSlot extends StatementParameterSlot {
    public ActionParameterSlot(int x, int y, IPipe pipe, int slot, StatementSlot iStatementSlot) {
      super(GateInterfaceScreen.this, x, y, slot, iStatementSlot);
    }

    @Override
    public IStatementParameter getParameter() {
      return GateInterfaceScreen.this.gate.getActionParameter(statementSlot.slot, slot);
    }

    @Override
    public void setParameter(IStatementParameter param, boolean notifyServer) {
      GateInterfaceScreen.this.gate.setActionParameter(statementSlot.slot, slot, param);
    }
  }
}
