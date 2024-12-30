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
