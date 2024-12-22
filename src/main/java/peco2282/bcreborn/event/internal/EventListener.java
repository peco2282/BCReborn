package peco2282.bcreborn.event.internal;

import peco2282.bcreborn.api.event.BCEvent;

public interface EventListener {
  void invoke(BCEvent event);
}
