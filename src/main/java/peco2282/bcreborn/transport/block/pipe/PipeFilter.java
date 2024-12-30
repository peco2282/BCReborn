package peco2282.bcreborn.transport.block.pipe;

import java.util.List;

public interface PipeFilter<T extends Entity> {
  int size();
  boolean accept(List<T> entity);
  List<List<T>> getAll();
}
