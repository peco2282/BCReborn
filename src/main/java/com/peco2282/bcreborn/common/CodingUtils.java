package com.peco2282.bcreborn.common;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface CodingUtils {
  static <T> T apply(T obj, Consumer<T> consumer) {
    consumer.accept(obj);
    return obj;
  }

  static <T, R> List<R> map2List(Collection<T> list, Function<T, R> function) {
    return list.stream().map(function).collect(Collectors.toList());
  }

  static <T, R> Set<R> map2Set(Collection<T> list, Function<T, R> function) {
    return list.stream().map(function).collect(Collectors.toSet());
  }
}
