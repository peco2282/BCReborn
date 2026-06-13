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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global manager for pipe-related registries, such as stripes handlers and pipe pluggables.
 */
public abstract class PipeManager {

  private static final Map<String, Class<? extends PipePluggable>> pipePluggableNames = new HashMap<>();
  private static final Map<Class<? extends PipePluggable>, String> pipePluggableByNames = new HashMap<>();
  private static final Map<IStripesHandler, Integer> stripesHandlerPriorities = new HashMap<>();
  /**
   * List of registered {@link IStripesHandler}s, sorted by priority.
   */
  public static List<IStripesHandler> stripesHandlers = new ArrayList<>();
  /**
   * List of all registered {@link PipePluggable} classes.
   */
  public static ArrayList<Class<? extends PipePluggable>> pipePluggables = new ArrayList<>();

  /**
   * Checks if items can be extracted from the specified position.
   *
   * @param extractor The object doing the extraction.
   * @param world     The world.
   * @param pos       The position.
   * @return True if extraction is allowed.
   * @deprecated Use capability checks or specific transport module methods.
   */
  @Deprecated
  public static boolean canExtractItems(Object extractor, Level world, BlockPos pos) {
    return true;
  }

  /**
   * Checks if fluids can be extracted from the specified position.
   *
   * @param extractor The object doing the extraction.
   * @param world     The world.
   * @param pos       The position.
   * @return True if extraction is allowed.
   * @deprecated Use capability checks or specific transport module methods.
   */
  @Deprecated
  public static boolean canExtractFluids(Object extractor, Level world, BlockPos pos) {
    return true;
  }

  /**
   * Registers a stripes handler with default priority (0).
   *
   * @param handler The handler to register.
   * @deprecated Use {@link #registerStripesHandler(IStripesHandler, int)} instead.
   */
  @Deprecated
  public static void registerStripesHandler(IStripesHandler handler) {
    registerStripesHandler(handler, 0);
  }

  /**
   * Registers a stripes handler with the specified priority.
   * Higher priority handlers are checked first.
   *
   * @param handler  The handler to register.
   * @param priority The priority value.
   */
  public static void registerStripesHandler(IStripesHandler handler, int priority) {
    stripesHandlers.add(handler);
    stripesHandlerPriorities.put(handler, priority);

    stripesHandlers.sort((o1, o2) -> stripesHandlerPriorities.get(o2) - stripesHandlerPriorities.get(o1));
  }

  /**
   * Registers a pipe pluggable class with a unique name.
   *
   * @param pluggable The pluggable class.
   * @param name      The unique name for registration.
   */
  public static void registerPipePluggable(Class<? extends PipePluggable> pluggable, String name) {
    pipePluggables.add(pluggable);
    pipePluggableNames.put(name, pluggable);
    pipePluggableByNames.put(pluggable, name);
  }

  /**
   * Gets a pluggable class by its registered name.
   *
   * @param pluggableName The registered name.
   * @return The pluggable class, or null if not found.
   */
  public static Class<?> getPluggableByName(String pluggableName) {
    return pipePluggableNames.get(pluggableName);
  }

  /**
   * Gets the registered name of a pluggable class.
   *
   * @param aClass The pluggable class.
   * @return The registered name, or null if not found.
   */
  public static String getPluggableName(Class<? extends PipePluggable> aClass) {
    return pipePluggableByNames.get(aClass);
  }
}
