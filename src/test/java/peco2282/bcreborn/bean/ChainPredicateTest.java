package peco2282.bcreborn.bean;

import org.junit.jupiter.api.Test;

public class ChainPredicateTest {
  @interface Ann {}
  @Test
  void testChainPredicate() {
    ContextProcessor.ChainPredicate<Ann> predicate = new ContextProcessor.ChainPredicate<>(Ann.class);
  }
}
