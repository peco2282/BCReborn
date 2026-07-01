/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.nbt;

/**
 * A functional interface that represents a supplier of float-valued results.
 * This is a primitive specialization of {@link java.util.function.Supplier} for float values.
 * <p>
 * This interface is typically used for lazy evaluation or deferred computation of float values,
 * particularly in NBT serialization contexts where float values may need to be retrieved on demand.
 * <p>
 * There is no requirement that a new or distinct result be returned each time the supplier is invoked.
 */
@FunctionalInterface
public interface FloatSupplier {
  /**
   * Gets a float result.
   *
   * @return a float value
   */
  float getAsFloat();
}
