package peco2282.bcreborn.event;

import peco2282.bcreborn.api.event.BCEvent;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;

public class PipeCreationEvent extends BCEvent {
  private final BasePipeBlockEntity pipe;
  public PipeCreationEvent(BasePipeBlockEntity entity) {
    pipe = entity;
  }

  public BasePipeBlockEntity getPipe() {
    return pipe;
  }
}
