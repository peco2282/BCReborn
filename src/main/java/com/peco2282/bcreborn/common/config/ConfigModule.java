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
package com.peco2282.bcreborn.common.config;

import com.peco2282.bcreborn.builders.BuildersConfig;
import com.peco2282.bcreborn.common.GeneralConfig;
import com.peco2282.bcreborn.core.CoreConfig;
import com.peco2282.bcreborn.energy.EnergyConfig;
import com.peco2282.bcreborn.factory.FactoryConfig;
import com.peco2282.bcreborn.robotics.RoboticsConfig;
import com.peco2282.bcreborn.silicon.SiliconConfig;
import com.peco2282.bcreborn.transport.TransportConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ConfigModule(
  MutableComponent module,
  MutableComponent description,
  List<ConfigSection> sections
) {
  public static final ConfigModule GENERAL = builder(
    Component.translatable("screen.config.module.general.title"),
    Component.translatable("screen.config.module.general.description")
  )
    .addSections(GeneralConfig.entries())
    .build();
  public static final ConfigModule CORE = builder(
    Component.translatable("screen.config.module.core.title"),
    Component.translatable("screen.config.module.core.description")
  )
    .addSections(CoreConfig.entries())
    .build();
  public static final ConfigModule BUILDER = builder(
    Component.translatable("screen.config.module.builder.title"),
    Component.translatable("screen.config.module.builder.description")
  )
    .addSections(BuildersConfig.entries())
    .build();
  public static final ConfigModule ENERGY = builder(
    Component.translatable("screen.config.module.energy.title"),
    Component.translatable("screen.config.module.energy.description")
  )
    .addSections(EnergyConfig.entries())
    .build();
  public static final ConfigModule FACTORY = builder(
    Component.translatable("screen.config.module.factory.title"),
    Component.translatable("screen.config.module.factory.description")
  )
    .addSections(FactoryConfig.entries())
    .build();
  public static final ConfigModule ROBOTICS = builder(
    Component.translatable("screen.config.module.robotics.title"),
    Component.translatable("screen.config.module.robotics.description")
  )
    .addSections(RoboticsConfig.entries())
    .build();
  public static final ConfigModule SILICON = builder(
    Component.translatable("screen.config.module.silicon.title"),
    Component.translatable("screen.config.module.silicon.description")
  )
    .addSections(SiliconConfig.entries())
    .build();
  public static final ConfigModule TRANSPORT = builder(
    Component.translatable("screen.config.module.transport.title"),
    Component.translatable("screen.config.module.transport.description")
  )
    .addSections(TransportConfig.entries())
    .build();

  public static Builder builder(MutableComponent module, MutableComponent description) {
    return new Builder(module, description);
  }

  public static class Builder {
    private final MutableComponent module;
    private final MutableComponent description;
    private final List<ConfigSection> sections;

    public Builder(MutableComponent module, MutableComponent description) {
      this.module = module;
      this.description = description;
      this.sections = new ArrayList<>();
    }

    public Builder addSections(ConfigSection... sections) {
      Collections.addAll(this.sections, sections);
      return this;
    }

    public ConfigModule build() {
      return new ConfigModule(this.module, this.description, this.sections);
    }
  }
}
