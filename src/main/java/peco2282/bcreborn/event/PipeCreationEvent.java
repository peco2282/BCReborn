/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
