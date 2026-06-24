package com.peco2282.bcreborn.core.item;

import com.peco2282.bcreborn.api.tiles.IDebuggable;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DebuggerItem extends BuildCraftItem {
  public DebuggerItem() {
    super(new Item.Properties());
  }

  @Override
  public InteractionResult useOn(UseOnContext p_41427_) {
    if (p_41427_.getLevel().isClientSide()) {
      return InteractionResult.sidedSuccess(p_41427_.getLevel().isClientSide());
    }

    BlockEntity tile = p_41427_.getLevel().getBlockEntity(p_41427_.getClickedPos());
    if (tile instanceof IDebuggable debuggable) {
      ArrayList<String> info = new ArrayList<>();
      debuggable.getDebugInfo(info, p_41427_.getClickedFace(), p_41427_.getItemInHand(), p_41427_.getPlayer());
      for (String s : info) {
        p_41427_.getPlayer().sendSystemMessage(Component.literal(s));
      }
      return InteractionResult.sidedSuccess(p_41427_.getLevel().isClientSide());
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
    return true;
  }

  @Override
  public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
    p_41423_.add(Component.literal("Use only for testing! Leaks secrets."));
  }
}
