package peco2282.bcreborn.event.internal;

import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.event.BCEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerContainer {
  private static final Map<Class<? extends BCEvent>, List<EventListener>> listeners = new HashMap<>();

  public static <T extends BCEvent> List<EventListener> registerListener(Class<T> clazz, EventListener listener) {
    return listeners.compute(clazz, (k, v) -> {
      if (v != null) {
        v.add(listener);
      } else {
        v = new ArrayList<>();
        v.add(listener);
      }
      return v;
    });
  }

  public static <T extends BCEvent> @Nullable List<EventListener> getListeners(Class<T> clazz) {
    return listeners.get(clazz);
  }
}
