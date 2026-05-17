package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 砂岩アイテムパイプの振る舞い
 * 他のパイプとは接続するが、チェストや機械などのインベントリには接続しない。
 * 配管だけを通したい場所に使う中継専用パイプ。
 */
public class SandstoneItemPipeBehaviour implements ItemPipeBehaviour {

  public static final SandstoneItemPipeBehaviour INSTANCE = new SandstoneItemPipeBehaviour();

  private SandstoneItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    // パイプ同士の接続のみ許可。インベントリ（機械・チェスト等）には接続しない。
    return neighbor.getBlock() instanceof PipeBlock;
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    float speed = item.getSpeed();
    float minSpeed = 0.01f;
    float maxSpeed = 0.15f;
    item.setSpeed(Math.max(minSpeed, Math.min(maxSpeed, speed - 0.001f)));
  }
}
