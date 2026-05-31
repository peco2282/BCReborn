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

import java.util.*;

public abstract class PipeManager {

    public static List<IStripesHandler> stripesHandlers = new ArrayList<>();
    public static ArrayList<Class<? extends PipePluggable>> pipePluggables = new ArrayList<>();
    private static Map<String, Class<? extends PipePluggable>> pipePluggableNames = new HashMap<>();
    private static Map<Class<? extends PipePluggable>, String> pipePluggableByNames = new HashMap<>();
    private static Map<IStripesHandler, Integer> stripesHandlerPriorities = new HashMap<>();

    @Deprecated
    public static boolean canExtractItems(Object extractor, Level world, BlockPos pos) {
        return true;
    }

    @Deprecated
    public static boolean canExtractFluids(Object extractor, Level world, BlockPos pos) {
        return true;
    }

    @Deprecated
    public static void registerStripesHandler(IStripesHandler handler) {
        registerStripesHandler(handler, 0);
    }

    public static void registerStripesHandler(IStripesHandler handler, int priority) {
        stripesHandlers.add(handler);
        stripesHandlerPriorities.put(handler, priority);

        stripesHandlers.sort((o1, o2) -> stripesHandlerPriorities.get(o2) - stripesHandlerPriorities.get(o1));
    }

    public static void registerPipePluggable(Class<? extends PipePluggable> pluggable, String name) {
        pipePluggables.add(pluggable);
        pipePluggableNames.put(name, pluggable);
        pipePluggableByNames.put(pluggable, name);
    }

    public static Class<?> getPluggableByName(String pluggableName) {
        return pipePluggableNames.get(pluggableName);
    }

    public static String getPluggableName(Class<? extends PipePluggable> aClass) {
        return pipePluggableByNames.get(aClass);
    }
}
