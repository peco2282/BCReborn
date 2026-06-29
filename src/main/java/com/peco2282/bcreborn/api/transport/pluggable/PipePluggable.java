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
package com.peco2282.bcreborn.api.transport.pluggable;

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.api.transport.IPipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all pipe pluggables (e.g., facades, gates, etc.).
 * Implementations must have a public no-args constructor for synchronization and serialization.
 */
public abstract class PipePluggable<T extends PipePluggable<T>> implements INBTSerializable, IBufferSerializable {
  protected PluggableType<T> type;

  public PipePluggable(PluggableType<T> type) {
    this.type = type;
  }

  public final PluggableType<T> getType() {
    return type;
  }

  /**
   * Gets the items that should be dropped when this pluggable is removed.
   *
   * @param pipe The pipe this pluggable is attached to.
   * @return An array of {@link ItemStack}s.
   */
  public abstract ItemStack[] getDropItems(IPipeBlockEntity pipe);

  /**
   * Called every tick to update the pluggable's state.
   *
   * @param pipe      The pipe this pluggable is attached to.
   * @param direction The side of the pipe this pluggable is on.
   */
  public void update(IPipeBlockEntity pipe, Direction direction) {
  }

  /**
   * Called when the pluggable is attached to a pipe.
   *
   * @param pipe      The pipe.
   * @param direction The side.
   */
  public void onAttachedPipe(IPipeBlockEntity pipe, Direction direction) {
    validate(pipe, direction);
  }

  /**
   * Called when the pluggable is detached from a pipe.
   *
   * @param pipe      The pipe.
   * @param direction The side.
   */
  public void onDetachedPipe(IPipeBlockEntity pipe, Direction direction) {
    invalidate();
  }

  /**
   * Checks if this pluggable blocks pipe connections on the specified side.
   *
   * @param pipe      The pipe.
   * @param direction The side.
   * @return True if it blocks connections.
   */
  public abstract boolean isBlocking(IPipeBlockEntity pipe, Direction direction);

  /**
   * Invalidates the pluggable state.
   */
  public void invalidate() {
  }

  /**
   * Validates the pluggable state with the given pipe and direction.
   *
   * @param pipe      The pipe.
   * @param direction The side.
   */
  public void validate(IPipeBlockEntity pipe, Direction direction) {
  }

  /**
   * Checks if this pluggable should be considered solid on the specified side.
   *
   * @param pipe      The pipe.
   * @param direction The side.
   * @return True if solid.
   */
  public boolean isSolidOnSide(IPipeBlockEntity pipe, Direction direction) {
    return false;
  }

  /**
   * Gets the bounding box for this pluggable on the specified side.
   *
   * @param side The side.
   * @return The {@link AABB} bounding box.
   */
  public abstract AABB getBoundingBox(Direction side);

  /**
   * Gets the static renderer for this pluggable.
   *
   * @return The {@link IPipePluggableRenderer}.
   */
  @OnlyIn(Dist.CLIENT)
  public abstract IPipePluggableRenderer getRenderer();

  /**
   * Gets the dynamic renderer for this pluggable.
   *
   * @return The {@link IPipePluggableDynamicRenderer}, or null if not needed.
   */
  @Nullable
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableDynamicRenderer getDynamicRenderer() {
    return null;
  }

  /**
   * Checks if a rendering update is required when this pluggable replaces another.
   *
   * @param old The old pluggable.
   * @return True if a render update is needed.
   */
  public boolean requiresRenderUpdate(PipePluggable<?> old) {
    return true;
  }
}
