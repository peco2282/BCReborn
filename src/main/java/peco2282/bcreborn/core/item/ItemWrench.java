package peco2282.bcreborn.core.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import peco2282.bcreborn.api.item.IToolWrench;
import peco2282.bcreborn.lib.item.ItemBaseNeptune;

public class ItemWrench extends ItemBaseNeptune implements IToolWrench {
  public ItemWrench(String id) {
    super(new Properties().stacksTo(1), id);
  }

  @Override
  public InteractionResult useOn(UseOnContext p_41427_) {
    useWrench(p_41427_.getPlayer(), p_41427_.getHand(), p_41427_.getItemInHand(), p_41427_.getHitResult());
    return super.useOn(p_41427_);
  }

  @Override
  public boolean canUseWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit) {
    return true;
  }

  @Override
  public void useWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit) {
    player.swing(hand);
  }
}
