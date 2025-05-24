/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.bean;

import org.junit.jupiter.api.Test;

public class ChainPredicateTest {
  @interface Ann {}

  @Test
  void testChainPredicate() {
    ContextProcessor.ChainPredicate<Ann> predicate =
        new ContextProcessor.ChainPredicate<>(Ann.class);
  }
}
