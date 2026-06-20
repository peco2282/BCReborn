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
package com.peco2282.bcreborn.common.builder.patterns;

import com.mojang.serialization.Codec;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.SchematicRegistry;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public abstract class FillerPattern implements IFillerPattern {
  public static final Map<ResourceLocation, FillerPattern> patterns = new TreeMap<>();
  public static final Codec<FillerPattern> CODEC = ResourceLocation.CODEC.dispatch(FillerPattern::getUniqueTag, tag -> {
    FillerPattern pattern = patterns.get(tag);
    return pattern != null ? pattern.getCodec() : null;
  });
  private final String tag;
  protected TextureAtlasSprite icon;

  public FillerPattern(String tag) {
    this.tag = tag;
    patterns.put(getUniqueTag(), this);
  }

  /**
   * Generates a filling in a given area
   */
  public static void fill(int xMin, int yMin, int zMin, int xMax, int yMax,
                          int zMax, Template template) {
    for (int y = yMin; y <= yMax; ++y) {
      for (int x = xMin; x <= xMax; ++x) {
        for (int z = zMin; z <= zMax; ++z) {
          if (isValid(x, y, z, template)) {
            template.put(x, y, z, new SchematicMask(true));
          }
        }
      }
    }
  }

  /**
   * Generates an empty in a given area
   */
  public static void empty(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, Template template) {
    for (int y = yMax; y >= yMin; y--) {
      for (int x = xMin; x <= xMax; ++x) {
        for (int z = zMin; z <= zMax; ++z) {
          if (isValid(x, y, z, template)) {
            template.put(x, y, z, null);
          }
        }
      }
    }
  }

  /**
   * Generates a flatten in a given area
   */
  public static void flatten(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, Template template) {
    for (int x = xMin; x <= xMax; ++x) {
      for (int z = zMin; z <= zMax; ++z) {
        for (int y = yMax; y >= yMin; --y) {
          if (isValid(x, y, z, template)) {
            template.put(x, y, z, new SchematicMask(true));
          }
        }
      }
    }
  }

  private static boolean isValid(int x, int y, int z, BlueprintBase bpt) {
    return x >= 0 && y >= 0 && z >= 0 && x < bpt.sizeX && y < bpt.sizeY && z < bpt.sizeZ;
  }

  public abstract Codec<? extends FillerPattern> getCodec();

  @Override
  public String getDescription() {
    return "fillerpattern." + tag;
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return null;
  }

  @Override
  public IStatement rotateLeft() {
    return this;
  }

  @Override
  public ResourceLocation getUniqueTag() {
    return BCReborn.getBasedLocation(tag);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    this.icon = textureGetter.apply(BCRebornCore.location("item/filler_pattern/" + tag));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public TextureAtlasSprite getIcon() {
    return this.icon;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public TextureAtlasSprite getBlockOverlay() {
    return null;
  }

  @Override
  public int maxParameters() {
    return 0;
  }

  @Override
  public int minParameters() {
    return 0;
  }

  @Override
  public String toString() {
    return "Pattern: " + getUniqueTag();
  }

  public abstract Template getTemplate(Box box, Level world, IStatementParameter[] parameters);

  public Blueprint getBlueprint(Box box, Level world, IStatementParameter[] parameters, BlockState meta) {
    Blueprint result = new Blueprint(box.sizeX(), box.sizeY(), box.sizeZ());

    try {
      Template tmpl = getTemplate(box, world, parameters);

      for (int x = 0; x < box.sizeX(); ++x) {
        for (int y = 0; y < box.sizeY(); ++y) {
          for (int z = 0; z < box.sizeZ(); ++z) {
            if (tmpl.get(x, y, z) != null) {
              result.put(x, y, z, SchematicRegistry.INSTANCE.createSchematicBlock(meta));
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return result;
  }
}
