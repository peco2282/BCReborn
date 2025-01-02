package peco2282.bcreborn.api.capability.pipe;

import net.minecraft.core.Direction;
import peco2282.bcreborn.transport.block.pipe.Entity;

import java.util.List;

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
