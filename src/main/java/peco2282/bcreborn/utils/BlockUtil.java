package peco2282.bcreborn.utils;

import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class BlockUtil {
  public static Tuple<Boolean, @Nullable BlockState> firstMatch(Predicate<Block> predicate, BlockState... states) {
    var map = allMatch(predicate, states);
    //noinspection DataFlowIssue
    return map.entrySet().stream().findFirst().map(e -> new Tuple<>(true, e.getValue())).orElse(new Tuple<>(false, null));
  }

  @NotNull
  public static Map<Block, BlockState> allMatch(@NotNull Predicate<Block> predicate, BlockState... states) {
    Map<Block, BlockState> map = new HashMap<>();
    for (BlockState state : states) {
      if (predicate.test(state.getBlock())) map.put(state.getBlock(), state);
    }
    return map;
  }
}
