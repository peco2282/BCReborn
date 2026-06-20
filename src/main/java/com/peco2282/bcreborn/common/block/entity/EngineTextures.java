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
package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.utils.ResourceUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class EngineTextures {
  // TEMP
  public static final ResourceLocation TRUNK_BLUE_TEXTURE = BCRebornCore.location("textures/block/engine/trunk_blue.png");
  public static final ResourceLocation TRUNK_GREEN_TEXTURE = BCRebornCore.location("textures/block/engine/trunk_green.png");
  public static final ResourceLocation TRUNK_YELLOW_TEXTURE = BCRebornCore.location("textures/block/engine/trunk_yellow.png");
  public static final ResourceLocation TRUNK_RED_TEXTURE = BCRebornCore.location("textures/block/engine/trunk_red.png");
  public static final ResourceLocation TRUNK_OVERHEAT_TEXTURE = BCRebornCore.location("textures/block/engine/trunk_overheat.png");

  // クライアント専用APIを避けるため、MISSINGはフォールバック用の固定パスを使用
  public static final ResourceLocation MISSING = ResourceLocation.withDefaultNamespace("missingno");

  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

  public static ResourceLocation getBaseTexture(EngineBlockEntity<?> engine) {
    return engine.getEngineResource().addPath("base.png").build(ResourceBuilder.ResourceType.BLOCK);
  }

  public static ResourceLocation getChamberTexture(EngineBlockEntity<?> engine) {
    return engine.getEngineResource().addPath("chamber.png").build(ResourceBuilder.ResourceType.BLOCK);
  }

  public static ResourceLocation getTrunkTexture(EngineBlockEntity<?> engine) {
    var trunk = engine.getEngineResource().addPath("trunk.png").build(ResourceBuilder.ResourceType.BLOCK);

    if (ResourceUtils.resourceExists(trunk)) {
      return trunk;
    }

    return getTrunkTextureLocation(engine.getEnergyStage());
  }

  public static ResourceLocation getTrunkTextureLocation(EngineBlockEntity.EnergyStage stage) {
    return switch (stage) {
      case BLUE -> TRUNK_BLUE_TEXTURE;
      case GREEN -> TRUNK_GREEN_TEXTURE;
      case YELLOW -> TRUNK_YELLOW_TEXTURE;
      case RED -> TRUNK_RED_TEXTURE;
      default -> TRUNK_OVERHEAT_TEXTURE;
    };
  }
}
