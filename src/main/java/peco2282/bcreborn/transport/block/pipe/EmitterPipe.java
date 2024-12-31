package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


/**
 * Example
 *  <blockquote><pre>
 *    exporter: implement this.
 *    [CONTAINER] -> (exporter) -> ...
 *  </pre></blockquote>
 */
public interface EmitterPipe extends PipeBlock {
  boolean canEmission(Level level, BlockPos from);
  void emission(Level level, BlockPos from);

  default void send(Level level, BlockPos from) {
    if (canEmission(level, from)) {
      emission(level, from);
    }
  }
}
