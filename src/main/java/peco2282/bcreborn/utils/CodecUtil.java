/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CodecUtil {

  public static <T, V> T encodeStart(Codec<V> codec, V input, DynamicOps<T> ops) {
    return codec.encodeStart(ops, input).getOrThrow();
  }

  public static <T, V> T encode(Codec<V> codec, V input, DynamicOps<T> ops, T prefix) {
    return codec.encode(input, ops, prefix).getOrThrow();
  }

  public static <T, V> V decode(Codec<V> codec, DynamicOps<T> ops, T input) {
    return codec.decode(ops, input).getOrThrow().getFirst();
  }

  public static <T> T encodeBlockPos(BlockPos pos, DynamicOps<T> ops, T prefix) {
    return encode(BlockPos.CODEC, pos, ops, prefix);
  }

  public static <T> BlockPos decodeBlockPos(DynamicOps<T> ops, T tag) {
    return decode(BlockPos.CODEC, ops, tag);
  }

  public static <T> T encodeBlockState(BlockState pos, DynamicOps<T> ops, T value) {
    return encode(BlockState.CODEC, pos, ops, value);
  }

  public static <T> BlockState decodeBlockState(DynamicOps<T> ops, T tag) {
    return decode(BlockState.CODEC, ops, tag);
  }
}
