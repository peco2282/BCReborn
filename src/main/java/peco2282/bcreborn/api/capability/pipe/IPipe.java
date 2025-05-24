/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.capability.pipe;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import peco2282.bcreborn.transport.block.pipe.Entity;

import java.util.List;

@AutoRegisterCapability
public interface IPipe<E extends Entity> {
  boolean isConnected(Direction direction);

  void setConnection(Direction direction, boolean connected);

  List<Direction> getSendOrder();

  void setSendOrder(List<Direction> sendOrder);

  E extractItem(Direction direction);

  boolean insertItem(E stack, Direction from);

  Direction getLastInput();

  void setLastInput(Direction direction);
}
