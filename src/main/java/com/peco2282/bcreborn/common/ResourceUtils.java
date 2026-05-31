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
package com.peco2282.bcreborn.common;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.Optional;

public interface ResourceUtils {
  public static boolean resourceExists(String name) {
    return resourceExists(ResourceLocation.parse(name));
  }

  public static boolean resourceExists(ResourceLocation name) {
    Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(name);
    return resource.isPresent();
  }
}
