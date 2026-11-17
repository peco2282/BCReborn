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
package com.peco2282.bcreborn.core.item;

import com.peco2282.bcreborn.api.items.IList;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.CoreMenuTypes;
import com.peco2282.bcreborn.core.list.ListHandlerNew;
import com.peco2282.bcreborn.core.list.ListHandlerOld;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListItem extends BuildCraftItem implements IList {
  public static final String TAG_LABEL = "label";
  public static final String TAG_WRITTEN = "written";


  public ListItem() {
    super(new Properties().stacksTo(1));
  }

  public static void saveLabel(ItemStack stack, Component text) {
    CompoundTag tag = stack.getOrCreateTag();
    tag.putString(TAG_LABEL, text.getString());
    tag.putBoolean(TAG_WRITTEN, true);
  }

  /**
   * Original: damage 0 was the deprecated list and damage 1 the current one. 1.20 has one item, so a list that
   * still holds data of the old format ({@code line[i]}) opens the old GUI and every other list the new one. The
   * menus read the main hand, so only the main hand opens a list.
   */
  public static boolean isOldList(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    return tag != null && tag.contains("line[0]") && !tag.contains("lines");
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (hand != InteractionHand.MAIN_HAND) {
      return InteractionResultHolder.pass(stack);
    }
    if (!world.isClientSide && stack.is(CoreItems.LIST.get())) {
      var menuType = (isOldList(stack) ? CoreMenuTypes.LIST_OLD : CoreMenuTypes.LIST_NEW).get();
      NetworkHooks.openScreen(
        (ServerPlayer) player,
        new SimpleMenuProvider(
          (p_39954_, p_39955_, p_39956_) -> menuType.create(p_39954_, p_39955_),
          getName(stack)
        )
      );
    }
    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
  }

  @Override
  public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
    if (isOldList(p_41421_)) {
      p_41423_.add(Component.translatable("tip.deprecated").withStyle(ChatFormatting.DARK_RED));
    }

    String label = getLabel(p_41421_);
    if (!label.isEmpty()) {
      p_41423_.add(Component.literal(label));
    }
  }

  @Override
  public Component getName(ItemStack p_41458_) {
    String label = getLabel(p_41458_);
    return label.isEmpty() ? super.getName(p_41458_) : Component.literal(label);
  }

  @Override
  public String getLabel(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    return tag != null ? tag.getString(TAG_LABEL) : "";
  }

  @Override
  public boolean matches(ItemStack stackList, ItemStack item) {
    if (stackList.isEmpty() || item.isEmpty()) {
      return false;
    }

    if (ListHandlerNew.matches(stackList, item)) {
      return true;
    }

    return ListHandlerOld.matches(stackList, item);
  }

  @Override
  public boolean setName(ItemStack stack, Component name) {
    saveLabel(stack, name);
    return true;
  }
}
