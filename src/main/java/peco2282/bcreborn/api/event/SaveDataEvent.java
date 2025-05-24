/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.event;

import peco2282.bcreborn.builder.saved.RangeCache;

import java.util.function.Consumer;

public class SaveDataEvent extends BCEvent {
  private final RangeCache cache;

  public SaveDataEvent(RangeCache cache) {
    this.cache = cache;
  }

  public void addTag(Consumer<RangeCache> consumer) {
    consumer.accept(cache);
  }
}
