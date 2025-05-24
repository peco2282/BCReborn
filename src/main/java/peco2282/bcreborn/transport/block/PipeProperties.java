/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block;

import peco2282.bcreborn.utils.PropertyBuilder;

/**
 * A utility class that defines various {@link PropertyBuilder} instances for pipe properties. These
 * properties are grouped based on material types (e.g., Wooden, Stone, etc.) and categorized by
 * their use (Item transportation, Fluid transportation, and Energy transportation).
 */
public class PipeProperties {
  /** Pipe properties for transporting items through wooden pipes. */
  public static final PropertyBuilder WOODEN_ITEM = builder();

  /** Pipe properties for transporting items through stone pipes. */
  public static final PropertyBuilder STONE_ITEM = builder();

  /** Pipe properties for transporting items through cobblestone pipes. */
  public static final PropertyBuilder COBBLESTONE_ITEM = builder();

  /** Pipe properties for transporting items through iron pipes. */
  public static final PropertyBuilder IRON_ITEM = builder();

  /** Pipe properties for transporting items through gold pipes. */
  public static final PropertyBuilder GOLD_ITEM = builder();

  /** Pipe properties for transporting items through diamond pipes. */
  public static final PropertyBuilder DIAMOND_ITEM = builder();

  /** Pipe properties for transporting fluids through wooden pipes. */
  public static final PropertyBuilder WOODEN_FLUID = builder();

  /** Pipe properties for transporting fluids through stone pipes. */
  public static final PropertyBuilder STONE_FLUID = builder();

  /** Pipe properties for transporting fluids through cobblestone pipes. */
  public static final PropertyBuilder COBBLESTONE_FLUID = builder();

  /** Pipe properties for transporting fluids through iron pipes. */
  public static final PropertyBuilder IRON_FLUID = builder();

  /** Pipe properties for transporting fluids through gold pipes. */
  public static final PropertyBuilder GOLD_FLUID = builder();

  /** Pipe properties for transporting fluids through diamond pipes. */
  public static final PropertyBuilder DIAMOND_FLUID = builder();

  /** Pipe properties for transporting energy through wooden pipes. */
  public static final PropertyBuilder WOODEN_ENERGY = builder();

  /** Pipe properties for transporting energy through stone pipes. */
  public static final PropertyBuilder STONE_ENERGY = builder();

  /** Pipe properties for transporting energy through cobblestone pipes. */
  public static final PropertyBuilder COBBLESTONE_ENERGY = builder();

  /** Pipe properties for transporting energy through iron pipes. */
  public static final PropertyBuilder IRON_ENERGY = builder();

  /** Pipe properties for transporting energy through gold pipes. */
  public static final PropertyBuilder GOLD_ENERGY = builder();

  /** Pipe properties for transporting energy through diamond pipes. */
  public static final PropertyBuilder DIAMOND_ENERGY = builder();

  /**
   * Creates a new {@link PropertyBuilder} instance.
   *
   * @return A new instance of {@link PropertyBuilder}.
   */
  private static PropertyBuilder builder() {
    return PropertyBuilder.builder();
  }
}
