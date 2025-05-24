/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.event.internal;

import peco2282.bcreborn.api.event.BCEvent;

public interface EventDispatcher {
  void dispatch(EventListener listener, BCEvent event);
}
