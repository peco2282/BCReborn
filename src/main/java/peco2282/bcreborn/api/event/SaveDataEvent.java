package peco2282.bcreborn.api.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import peco2282.bcreborn.builder.saved.RangeCache;

import java.util.function.Consumer;

@Cancelable
public class SaveDataEvent extends Event {
  private final RangeCache cache;

  public SaveDataEvent(RangeCache cache) {
    this.cache = cache;
  }

  public void addTag(Consumer<RangeCache> consumer) {
    consumer.accept(cache);
  }
}
