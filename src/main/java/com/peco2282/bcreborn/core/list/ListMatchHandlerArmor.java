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
package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ListMatchHandlerArmor extends ListMatchHandler {
  private int getArmorTypeID(ItemStack stack) {
    if (!(stack.getItem() instanceof ArmorItem armor)) {
      return 0;
    }
    // In 1.20.1, we can just use the EquipmentSlot
    return 1 << armor.getEquipmentSlot().getIndex();
  }

  @Override
  public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
    if (type == Type.TYPE) {
      int armorTypeIDSource = getArmorTypeID(stack);
      if (armorTypeIDSource > 0) {
        int armorTypeIDTarget = getArmorTypeID(target);
        if (precise) {
          return armorTypeIDSource == armorTypeIDTarget;
        } else {
          return (armorTypeIDSource & armorTypeIDTarget) != 0;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isValidSource(Type type, ItemStack stack) {
    return getArmorTypeID(stack) > 0;
  }
}
