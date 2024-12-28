package peco2282.bcreborn.transport.block.pipe;

import java.util.List;
import java.util.function.Predicate;

@FunctionalInterface
public interface FilterPredicate<E extends Entity> extends Predicate<List<E>> {
  @Override
  boolean test(List<E> entities);
}
