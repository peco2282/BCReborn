package peco2282.bcreborn.api.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public interface IToolWrench {
  boolean canUseWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit);

  void useWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit);
}
