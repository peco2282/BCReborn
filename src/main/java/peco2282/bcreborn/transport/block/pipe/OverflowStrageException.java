/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.pipe;

public class OverflowStrageException extends RuntimeException {
  public OverflowStrageException(int capacity, int size, String type) {
    super(String.format("Overflow %s : %d capacity %d size", type, capacity, size));
  }
}
