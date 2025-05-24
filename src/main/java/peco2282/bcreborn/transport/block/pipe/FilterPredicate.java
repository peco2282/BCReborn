/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.pipe;

import java.util.List;
import java.util.function.Predicate;

@FunctionalInterface
public interface FilterPredicate<E extends Entity> extends Predicate<List<E>> {
  @Override
  boolean test(List<E> entities);
}
