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
package com.peco2282.bcreborn.builders.item;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.api.blueprints.BuildingPermission;
import com.peco2282.bcreborn.api.items.IBlueprintItem;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.blueprint.Template;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BlueprintItem extends BuildCraftItem implements IBlueprintItem {
  protected BlueprintItem(Properties properties) {
    super(properties);
  }

  public static boolean isContentReadable(ItemStack stack) {
    return getId(stack) != null;
  }

  public static LibraryId getId(ItemStack stack) {
    CompoundTag nbt = stack.getOrCreateTag();
    LibraryId id = new LibraryId();
    id.read(nbt);

    if (BCRebornBuilders.getServerDB().exists(id)) {
      return id;
    } else {
      return null;
    }
  }

  public static BlueprintBase loadBlueprint(ItemStack stack) {
    if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IBlueprintItem)) {
      return null;
    }

    LibraryId id = getId(stack);
    if (id == null) {
      return null;
    }

    CompoundTag nbt = BCRebornBuilders.getServerDB().load(id);
    BlueprintBase base;
    if (((IBlueprintItem) stack.getItem()).getType(stack) == IBlueprintItem.Type.TEMPLATE) {
      base = new Template();
    } else {
      base = new Blueprint();
    }
    base.readFromNBT(nbt);
    base.id = id;
    return base;
  }

  @Override
  public Component getName(ItemStack stack) {
    return Component.literal(stack.getOrCreateTag().getString("name"));
  }

  @Override
  public boolean setName(ItemStack stack, Component name) {
    stack.getOrCreateTag().putString("name", name.getString());
    return true;
  }

  @Override
  public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
    CompoundTag s = p_41421_.getOrCreateTag();
    if (s.contains("name")) {
      String name = s.getString("name");

      if (name.isEmpty()) {
        p_41423_.add(Component.translatable("item.blueprint.unnamed"));
      } else {
        p_41423_.add(Component.literal(name));
      }

      p_41423_.add(
        Component
          .translatable("item.blueprint.author")
          .append(Component.literal(" "))
          .append(s.getString("author"))
      );
    } else {
      p_41423_.add(Component.translatable("item.blueprint.blank"));
    }

    if (s.contains("permission")) {
      BuildingPermission p = BuildingPermission.values()[s.getByte("permission")];

      if (p == BuildingPermission.CREATIVE_ONLY) {
        p_41423_.add(Component.translatable("item.blueprint.creative_only"));
      } else if (p == BuildingPermission.NONE) {
        p_41423_.add(Component.translatable("item.blueprint.no_build"));
      }
    }

    if (s.contains("isComplete")) {
      boolean isComplete = s.getBoolean("isComplete");

      if (!isComplete) {
        p_41423_.add(Component.translatable("item.blueprint.incomplete"));
      }
    }
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return stack.getOrCreateTag().contains("name") ? 1 : 16;
  }

  public abstract String getIconType();
}
