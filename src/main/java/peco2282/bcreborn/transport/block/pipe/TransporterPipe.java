package peco2282.bcreborn.transport.block.pipe;

import peco2282.bcreborn.transport.block.entity.pipe.ItemPipeBlockEntity;

import java.util.List;

public interface TransporterPipe extends PipeBlock {
  int SPEED = 5; // tick

  default int speed() {
    return SPEED;
  }

  List<? extends ItemPipeBlockEntity> targetPipes();

  void transportTo(List<ItemEntity> in, ItemPipeBlockEntity pipe);

  boolean canTransport(List<ItemEntity> in, ItemPipeBlockEntity entity);

  default void transport(List<ItemEntity> in) {
    var list = this.targetPipes();
    for (var pipe : list) {
      if (canTransport(in, pipe)) {
        transportTo(in, pipe);
      }
    }
  }
}
