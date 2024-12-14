package peco2282.bcreborn.utils;

import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;


/**
 * Utility class for working with blocks and block states in Minecraft.
 * Provides methods to filter and match block states based on specific predicates.
 *
 * @author peco2282
 */
public class BlockUtil {
  /**
   * Finds the first {@link BlockState} that matches the given predicate.
   *
   * @param predicate the condition to test for a matching block
   * @param states    an array of {@link BlockState} to search through
   * @return a {@link Tuple} where the first value is {@code true} if a match is found, 
   *         and the second value is the matched {@link BlockState}, or {@code null} if no match is found
   */
  public static Tuple<Boolean, @Nullable BlockState> firstMatch(Predicate<Block> predicate, BlockState... states) {
    var map = allMatch(predicate, states);
    //noinspection DataFlowIssue
    return map.entrySet().stream().findFirst().map(e -> new Tuple<>(true, e.getValue())).orElse(new Tuple<>(false, null));
  }

  /**
   * Retrieves a map of all {@link BlockState}s that match the given predicate.
   *
   * @param predicate the condition to test for matching blocks
   * @param states    an array of {@link BlockState} to search through
   * @return a map where the keys are matching {@link Block Blocks} and 
   *         the values are their corresponding {@link BlockState}s
   */
  @NotNull
  public static Map<Block, BlockState> allMatch(@NotNull Predicate<Block> predicate, BlockState... states) {
    Map<Block, BlockState> map = new HashMap<>();
    for (BlockState state : states) {
      if (predicate.test(state.getBlock())) map.put(state.getBlock(), state);
    }
    return map;
  }
}
