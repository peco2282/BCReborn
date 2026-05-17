package com.peco2282.bcreborn.common.bean;

import org.jetbrains.annotations.NotNull;

public interface DataHolder extends Comparable<Integer> {
  @Override
  int compareTo(@NotNull Integer o);
}
