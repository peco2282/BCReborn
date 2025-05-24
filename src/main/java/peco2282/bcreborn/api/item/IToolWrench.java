/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public interface IToolWrench {
  boolean canUseWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit);

  void useWrench(Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit);
}
