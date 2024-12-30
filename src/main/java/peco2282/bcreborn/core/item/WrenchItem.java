package peco2282.bcreborn.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.item.IToolWrench;
import peco2282.bcreborn.lib.item.BaseNeptuneItem;

public class WrenchItem extends BaseNeptuneItem implements IToolWrench {
  public WrenchItem(String id) {
    super(new Properties().stacksTo(1), id);
  }

  @Override
  public InteractionResult useOn(UseOnContext p_41427_) {
    if (p_41427_.getPlayer() == null) return super.useOn(p_41427_);
    if (canUseWrench(p_41427_.getPlayer(), p_41427_.getHand(), p_41427_.getItemInHand(), p_41427_.getHitResult()))
      useWrench(p_41427_.getPlayer(), p_41427_.getHand(), p_41427_.getItemInHand(), p_41427_.getHitResult());
    return super.useOn(p_41427_);
  }

  @Override
  public boolean canUseWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit) {
    IToolWrench w = wrench(player, hit.getBlockPos());
    if (w == null) return false;
    return w.canUseWrench(player, hand, wrench, hit);
  }

  @Override
  public void useWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit) {
    player.swing(hand);
    IToolWrench w = wrench(player, hit.getBlockPos());
    if (w != null) w.useWrench(player, hand, wrench, hit);
  }

  @SuppressWarnings("resource")
  private static @Nullable IToolWrench wrench(Player player, BlockPos pos) {
    final Level level = player.level();
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof IToolWrench) return (IToolWrench) be;
    return null;
  }
}
