package peco2282.bcreborn.event.internal;

import peco2282.bcreborn.api.event.BCEvent;

public interface EventBus {
  void register(Object listener);
  void registerAnnotated(Object listener);
  void unregister(Object listener);
  boolean post(BCEvent event);
  boolean post(BCEvent event, EventDispatcher dispatcher);
  void shutdown();
  void start();
}
