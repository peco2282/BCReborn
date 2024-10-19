package peco2282.bcreborn.lib.block.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.lib.block.menu.BCMenu;

public abstract class BCContainerScreen<T extends BCMenu> extends AbstractContainerScreen<T> {
  public BCContainerScreen(T p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
  }

  @NotNull
  protected abstract ResourceLocation getTexture();
}
