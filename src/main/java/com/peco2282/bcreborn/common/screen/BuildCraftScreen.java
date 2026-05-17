package com.peco2282.bcreborn.common.screen;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class BuildCraftScreen<M extends BuildCraftMenu<M>> extends AbstractContainerScreen<M> {
  public static final ResourceLocation LEDGER_TEXTURE = ResourceLocation.fromNamespaceAndPath(BCRebornCore.MODID, "textures/gui/ledger.png");

  protected final LedgerManager ledgerManager = new LedgerManager(this);

  public BuildCraftScreen(M p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    this.initilaizeLedger(p_97742_);
  }

  protected abstract void initilaizeLedger(Inventory p_97742_);
}
