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
package com.peco2282.bcreborn.api.statements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public final class StatementManager {

  private static final List<ITriggerProvider> triggerProviders = new LinkedList<>();
  private static final List<IActionProvider> actionProviders = new LinkedList<>();
  private static final Map<ResourceLocation, IStatement> STATEMENTS = new HashMap<>();
  private static final Map<ResourceLocation, Parameter<?>> PARAMETERS = new HashMap<>();
  private record Parameter<P extends IStatementParameter>(P parameter, Codec<P> codec) {
  }

  private StatementManager() {
  }

  public static void registerTriggerProvider(ITriggerProvider provider) {
    if (!triggerProviders.contains(provider)) {
      triggerProviders.add(provider);
    }
  }

  public static void registerActionProvider(IActionProvider provider) {
    if (!actionProviders.contains(provider)) {
      actionProviders.add(provider);
    }
  }

  public static void registerStatement(IStatement statement) {
    STATEMENTS.put(statement.getUniqueTag(), statement);
  }

  @Nullable
  public static IStatement getStatement(String tag) {
    return getStatement(ResourceLocation.parse(tag));
  }

  @Nullable
  public static IStatement getStatement(ResourceLocation tag) {
    return STATEMENTS.get(tag);
  }

  public static <P extends IStatementParameter> void registerParameter(P parameter, Codec<P> codec) {
    PARAMETERS.put(parameter.getUniqueTag(), new Parameter<>(parameter, codec));
  }

  public static DataResult<Codec<? extends IStatementParameter>> getParameterCodec(ResourceLocation tag) {
    if (PARAMETERS.containsKey(tag)) {
      return DataResult.success(PARAMETERS.get(tag).codec());
    }
    return DataResult.error(() -> "Unknown parameter tag: " + tag);
  }

  public static List<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity entity) {
    List<ITriggerExternal> result;

    if (entity instanceof IOverrideDefaultStatements) {
      result = ((IOverrideDefaultStatements) entity).overrideTriggers();
      return result;
    }

    result = new LinkedList<>();

    for (ITriggerProvider provider : triggerProviders) {
      Collection<ITriggerExternal> toAdd = provider.getExternalTriggers(side, entity);

      for (ITriggerExternal t : toAdd) {
        if (!result.contains(t)) {
          result.add(t);
        }
      }
    }

    return result;
  }

  public static List<IActionExternal> getExternalActions(Direction side, BlockEntity entity) {
    List<IActionExternal> result;

    if (entity instanceof IOverrideDefaultStatements) {
      result = ((IOverrideDefaultStatements) entity).overrideActions();
      return result;
    }

    result = new LinkedList<>();

    for (IActionProvider provider : actionProviders) {
      Collection<IActionExternal> toAdd = provider.getExternalActions(side, entity);

      for (IActionExternal t : toAdd) {
        if (!result.contains(t)) {
          result.add(t);
        }
      }
    }

    return result;
  }

  public static List<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
    List<ITriggerInternal> result = new LinkedList<>();

    for (ITriggerProvider provider : triggerProviders) {
      Collection<ITriggerInternal> toAdd = provider.getInternalTriggers(container);

      for (ITriggerInternal t : toAdd) {
        if (!result.contains(t)) {
          result.add(t);
        }
      }
    }

    return result;
  }

  public static List<IActionInternal> getInternalActions(IStatementContainer container) {
    List<IActionInternal> result = new LinkedList<>();

    for (IActionProvider provider : actionProviders) {
      Collection<IActionInternal> toAdd = provider.getInternalActions(container);

      for (IActionInternal t : toAdd) {
        if (!result.contains(t)) {
          result.add(t);
        }
      }
    }

    return result;
  }

  public static IStatementParameter createParameter(String kind) {
    return createParameter(ResourceLocation.parse(kind));
  }
  public static IStatementParameter createParameter(ResourceLocation kind) {
    var parameter = PARAMETERS.get(kind);
    if (parameter != null) return parameter.parameter();
    throw new IllegalArgumentException("Unknown parameter kind: " + kind);
  }

  @OnlyIn(Dist.CLIENT)
  public static void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    for (IStatement statement : STATEMENTS.values()) {
      statement.registerIcons(textureGetter);
    }

    for (Parameter<?> parameter : PARAMETERS.values()) {
      parameter.parameter().registerIcons(textureGetter);
    }
  }
}
