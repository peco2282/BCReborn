/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.enums;

public enum EnumSnapshotType {
  TEMPLATE(900),
  BLUEPRINT(300);

  public final int maxPerTick;

  EnumSnapshotType(int maxPerTick) {
    this.maxPerTick = maxPerTick;
  }
}
