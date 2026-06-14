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
package com.peco2282.bcreborn;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BCReborn {
  boolean DEVELOPER_MODE = true;

  String MOD_ID_BASE = "bcreborn";

  StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  static Logger createLogger() {
    String pack = STACK_WALKER.getCallerClass().getPackageName().replace("com.peco2282.bcreborn.", "").split("\\.")[0];
    return LoggerFactory.getLogger((MOD_ID_BASE + "." + pack).toUpperCase());
  }

  static Logger commonLogger() {
    return LoggerFactory.getLogger(MOD_ID_BASE.toUpperCase());
  }

  static String getModId(Type type) {
    return MOD_ID_BASE + type.modId;
  }

  static ResourceLocation getBasedLocation(String path) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID_BASE, path);
  }

  static ResourceLocation getLocation(String path) {
    return ResourceLocation.fromNamespaceAndPath(getModId(Type.CORE), path);
  }

  static ResourceLocation getLocation(Type type, String path) {
    return ResourceLocation.fromNamespaceAndPath(getModId(type), path);
  }

  enum Type {
    CORE,
    BUILDERS,
    TRANSPORT,
    ENERGY,
    FACTORY,
    SILICON,
    ROBOTICS,
    ;
    final String modId;

    Type() {
      this.modId = name().toLowerCase();
    }
  }
}
