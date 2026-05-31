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
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Locale;
import java.util.function.Function;

public class TriggerInventory extends BCStatement implements ITriggerExternal {

  public State state;

  public TriggerInventory(State state) {
    super("buildcraft:inventory." + state.name().toLowerCase(Locale.ENGLISH), "buildcraft.inventory." + state.name().toLowerCase(Locale.ENGLISH));

    this.state = state;
  }

  @Override
  public int maxParameters() {
    return state == State.Contains || state == State.Space ? 1 : 0;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.trigger.inventory." + state.name().toLowerCase(Locale.ENGLISH));
  }

  @Override
  public boolean isTriggerActive(BlockEntity tile, Direction side, IStatementContainer container, IStatementParameter[] parameters) {
    if (tile == null) return false;

    return tile.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).map(handler -> {
      ItemStack searchedStack = ItemStack.EMPTY;

      if (parameters != null && parameters.length >= 1 && parameters[0] != null) {
        searchedStack = parameters[0].getItemStack();
      }

      boolean foundItems = false;
      boolean foundSpace = false;
      boolean hasSlots = handler.getSlots() > 0;

      for (int i = 0; i < handler.getSlots(); i++) {
        ItemStack stack = handler.getStackInSlot(i);

        if (!stack.isEmpty()) {
          if (searchedStack.isEmpty() || (ItemStack.isSameItemSameTags(stack, searchedStack))) {
            foundItems = true;
          }
        }

        if (handler.insertItem(i, searchedStack.isEmpty() ? new ItemStack(net.minecraft.world.item.Items.STONE) : searchedStack, true).getCount() < (searchedStack.isEmpty() ? 1 : searchedStack.getCount())) {
          // This is a bit rough for "space" but it's a start
          foundSpace = true;
        } else if (stack.isEmpty()) {
          foundSpace = true;
        }
      }

      if (!hasSlots) {
        return false;
      }

      switch (state) {
        case Empty:
          return !foundItems;
        case Contains:
          return foundItems;
        case Space:
          return foundSpace;
        default:
          return !foundSpace;
      }
    }).orElse(false);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_inventory_" + state.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return new StatementParameterItemStack(ItemStack.EMPTY);
  }

  public enum State {

    Empty, Contains, Space, Full
  }
}
