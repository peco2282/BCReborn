package peco2282.bcreborn.event.internal;

import peco2282.bcreborn.api.event.BCEvent;

import java.lang.reflect.Method;

public class BCEventListener implements EventListener {
  private static final Factory factory = Factory.getFactory();
  private final EventListener listener;

  public BCEventListener(Method method, Object instance) throws ReflectiveOperationException {
    this.listener = factory.create(method, instance);
  }

  @Override
  public void invoke(BCEvent event) {
    listener.invoke(event);
  }
}
