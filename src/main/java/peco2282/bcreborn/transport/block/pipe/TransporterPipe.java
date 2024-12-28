package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;

import java.util.List;

public interface TransporterPipe<T extends Entity> {
  <P extends BasePipeBlockEntity> List<P> targetPipes();
  void transportTo(List<T> in, BasePipeBlockEntity pipe);

  boolean canTransport(List<T> in, BasePipeBlockEntity entity);

  default void transport(List<T> in) {
    var list = this.targetPipes();
    for (var pipe : list) {
      if (canTransport(in, pipe)) {
        transportTo(in, pipe);
      }
    }
  }
}
