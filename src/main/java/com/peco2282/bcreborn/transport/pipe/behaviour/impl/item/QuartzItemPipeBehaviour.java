package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;

/**
 * クォーツアイテムパイプの振る舞い。
 * 通常の輸送パイプとして機能し、石・丸石パイプとは接続できる。
 * 配管の分離・交差に使える。
 */
public class QuartzItemPipeBehaviour implements ItemPipeBehaviour {

  public static final QuartzItemPipeBehaviour INSTANCE = new QuartzItemPipeBehaviour();

  private QuartzItemPipeBehaviour() {
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    float speed = item.getSpeed();
    float minSpeed = 0.01f;
    float maxSpeed = 0.15f;
    item.setSpeed(Math.max(minSpeed, Math.min(maxSpeed, speed - 0.001f)));
  }
}
