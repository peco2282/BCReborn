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
package com.peco2282.bcreborn.api.transport;

import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.api.transport.pluggable.PluggableType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Supplier;

/**
 * Global manager for pipe-related registries, such as stripes handlers and pipe pluggables.
 */
public abstract class PipeManager {
  private static final Map<ResourceLocation, PluggableType<? extends PipePluggable<?>>> PLUGGABLE_TYPE = new HashMap<>();
  private static final List<StripeHandlerEntry> STRIPES_HANDLERS = new ArrayList<>();

  public static void registerStripesHandler(StripeHandlerEntry entry) {
    STRIPES_HANDLERS.add(entry);
    STRIPES_HANDLERS.sort(Comparator.comparingInt(StripeHandlerEntry::priority).reversed());
  }

  @Contract(pure = true)
  @UnmodifiableView
  public static List<StripeHandlerEntry> getStripeHandlerEntries() {
    return Collections.unmodifiableList(STRIPES_HANDLERS);
  }

  @UnmodifiableView
  public static List<IStripesHandler> getStripesHandlers() {
    return getStripeHandlerEntries().stream()
      .map(StripeHandlerEntry::handler)
      .toList();
  }

  public static void registerStripesHandler(
    IStripesHandler handler,
    int priority
  ) {
    registerStripesHandler(StripeHandlerEntry.of(handler, priority));
  }

  public static void registerStripesHandler(
    IStripesHandler handler
  ) {
    registerStripesHandler(StripeHandlerEntry.of(handler));
  }

  @Contract("_ -> param1")
  public static <T extends PipePluggable<T>> PluggableType<T> registerPipePluggable(PluggableType<T> type) {
    if (PLUGGABLE_TYPE.containsKey(type.id())) {
      throw new IllegalArgumentException("Pipe pluggable type with ID " + type.id() + " already registered");
    }
    PLUGGABLE_TYPE.put(type.id(), type);
    return type;
  }

  public static <T extends PipePluggable<T>> PluggableType<T> registerPipePluggable(ResourceLocation id, Supplier<T> factory) {
    return registerPipePluggable(PluggableType.of(id, factory));
  }

  public static @Nullable PluggableType<?> getPipePluggable(ResourceLocation id) {
    return PLUGGABLE_TYPE.get(id);
  }

  public static boolean hasPipePluggable(ResourceLocation id) {
    return PLUGGABLE_TYPE.containsKey(id);
  }

  @SuppressWarnings("unchecked")
  public static <T extends PipePluggable<T>> T createPipePluggable(ResourceLocation id) {
    var type = getPipePluggable(id);
    if (type == null) {
      throw new IllegalArgumentException("Pipe pluggable type with ID " + id + " not found");
    }
    return (T) type.create();
  }

  public static <T extends PipePluggable<T>> T createPipePluggable(String id, CompoundTag tag) {
    return createPipePluggable(ResourceLocation.parse(id), tag);
  }

  public static <T extends PipePluggable<T>> T createPipePluggable(ResourceLocation id, CompoundTag tag) {
    T pluggable = createPipePluggable(id);
    pluggable.readTag(tag);
    return pluggable;
  }
}
