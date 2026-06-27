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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation used to mark classes for automatic registration during mod initialization.
 * <p>
 * Classes annotated with {@code @InitRegister} are discovered and processed by the
 * {@link ContextProcessor} during the mod loading phase. This annotation is typically
 * used to register components, handlers, or other initialization logic that needs to
 * be executed when the mod is loaded.
 * </p>
 *
 * @see ContextProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InitRegister {
  /**
   * The mod ID that this registration belongs to.
   * <p>
   * This should match the mod ID defined in the mod's main class or configuration.
   * </p>
   *
   * @return the mod ID for this registration
   */
  String modId();

  /**
   * The priority of this registration relative to other registrations.
   * <p>
   * Lower values indicate higher priority and will be processed first.
   * The default priority is 0.
   * </p>
   *
   * @return the priority value, default is 0
   */
  int priority() default 0;
}
