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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;

public class VoidItemPipeBehaviour implements ItemPipeBehaviour {

  public static final VoidItemPipeBehaviour INSTANCE = new VoidItemPipeBehaviour();

  private VoidItemPipeBehaviour() {
  }

  @Override
  public void onReachedCenter(PipeBlockEntity pipe, TravelingItem item) {
    // originalと同様に、パイプ中央到達時にアイテムを消滅させる
    item.getStack().setCount(0);
  }
}
