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
