/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.capability.pipe;

import net.minecraft.core.Direction;
import peco2282.bcreborn.transport.block.pipe.ItemEntity;
import peco2282.bcreborn.transport.block.pipe.PipeStorage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ItemPipe implements IPipe<ItemEntity> {
  private final Set<Direction> connections = EnumSet.noneOf(Direction.class); // 接続方向
  private final PipeStorage.ItemStorage storage;
  private List<Direction> sendOrder = new ArrayList<>(); // 搬送順序
  private Direction lastInput = null;

  public ItemPipe(PipeStorage.ItemStorage storage) {
    this.storage = storage;
  }

  @Override
  public boolean isConnected(Direction direction) {
    return connections.contains(direction);
  }

  @Override
  public void setConnection(Direction direction, boolean connected) {
    if (connected) {
      connections.add(direction);
    } else {
      connections.remove(direction);
    }
  }

  @Override
  public List<Direction> getSendOrder() {
    return sendOrder;
  }

  @Override
  public void setSendOrder(List<Direction> order) {
    this.sendOrder = order;
  }

  @Override
  public boolean insertItem(ItemEntity stack, Direction from) {
    if (!isConnected(from)) return false;

    // 入力元を記録
    this.lastInput = from;

    // 搬送先へアイテムを送る
    for (Direction direction : sendOrder) {
      if (direction != from && isConnected(direction)) {
        boolean success = sendToNeighbor(stack, direction);
        if (success) return true;
      }
    }

    return false; // 送信失敗
  }

  @Override
  public ItemEntity extractItem(Direction to) {
    // アイテムの取り出し処理（必要に応じて実装）
    // ここではデフォルトで空を返す例
    return ItemEntity.EMPTY;
  }

  @Override
  public Direction getLastInput() {
    return lastInput;
  }

  @Override
  public void setLastInput(Direction direction) {
    this.lastInput = direction;
  }

  // 隣接パイプへの送信処理（例: Capabilityを介した送信）
  private boolean sendToNeighbor(ItemEntity stack, Direction direction) {
    // 隣接するパイプのCapabilityを取得して渡す処理
    // 実際の実装ではワールドや座標を参照
    return false; // この例では未実装
  }
}
