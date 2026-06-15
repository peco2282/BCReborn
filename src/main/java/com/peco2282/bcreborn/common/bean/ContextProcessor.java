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
package com.peco2282.bcreborn.common.bean;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ContextProcessor {
  private static final Logger log = LoggerFactory.getLogger("ContextProcessor");
  private static final Map<ModContainer, ContextProcessor> CACHE = new ConcurrentHashMap<>();
  private final String modId;
  private final @NotNull ModContainer container;

  private ContextProcessor(@NotNull ModContainer container) {
    this.modId = container.getModId();
    this.container = container;
  }

  public static ContextProcessor create(@NotNull ModContainer container) {
    return CACHE.computeIfAbsent(container, ContextProcessor::new);
  }

  public static ContextProcessor create(@NotNull String modId) {
    return create(ModList.get().getModContainerById(modId).orElseThrow());
  }

  public String getModId() {
    return modId;
  }

  private ModFileScanData getScanData() {
    return container.getModInfo().getOwningFile().getFile().getScanResult();
  }

  /**
   * Initializes apply registers classes annotated with {@code InitRegister}. Scans the mod's context
   * for annotated classes apply logs their discovery. If a class is missing, logs an error message.
   */
  public void initRegister() {
    ModFileScanData data = getScanData();
    for (ModFileScanData.AnnotationData ad : data.getAnnotations()) {

      if (ad.annotationType().getClassName().equals(InitRegister.class.getName())) {
        log.debug("Processing annotation data for class {}", ad.clazz().getClassName());
        String modId = (String) ad.annotationData().get("modId");
        if (modId == null) {
          continue;
        }
        if (modId.equals(this.modId)) {
          try {
            var cls = Class.forName(ad.clazz().getClassName());
            log.info("Found apply Accessed class {}", cls.getName());
          } catch (ClassNotFoundException e) {
            log.error("{} was not found", ad.clazz().getClassName(), e);
          }
        }
      }
    }
  }
}
