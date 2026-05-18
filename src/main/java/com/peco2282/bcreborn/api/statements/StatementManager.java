/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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

import java.util.*;
import java.util.function.Function;

public final class StatementManager {

    public static Map<String, IStatement> statements = new HashMap<>();
    public static Map<String, Class<? extends IStatementParameter>> parameters = new HashMap<>();
    public static Map<String, Codec<? extends IStatementParameter>> parameterCodecs = new HashMap<>();
    private static List<ITriggerProvider> triggerProviders = new LinkedList<>();
    private static List<IActionProvider> actionProviders = new LinkedList<>();

    private StatementManager() {
    }

    public static void registerTriggerProvider(ITriggerProvider provider) {
        if (provider != null && !triggerProviders.contains(provider)) {
            triggerProviders.add(provider);
        }
    }

    public static void registerActionProvider(IActionProvider provider) {
        if (provider != null && !actionProviders.contains(provider)) {
            actionProviders.add(provider);
        }
    }

    public static void registerStatement(IStatement statement) {
        statements.put(statement.getUniqueTag(), statement);
    }

    public static void registerParameterClass(Class<? extends IStatementParameter> param) {
        String tag = createParameter(param).getUniqueTag();
        parameters.put(tag, param);
        try {
            parameterCodecs.put(tag, (Codec<? extends IStatementParameter>) param.getField("CODEC").get(null));
        } catch (Exception e) {
            // No codec
        }
    }

    public static DataResult<? extends Codec<? extends IStatementParameter>> getParameterCodec(String tag) {
        if (parameterCodecs.containsKey(tag)) {
            return DataResult.success(parameterCodecs.get(tag));
        }
        return DataResult.error(() -> "Unknown parameter tag: " + tag);
    }

    @Deprecated
    public static void registerParameterClass(String name, Class<? extends IStatementParameter> param) {
        parameters.put(name, param);
    }

    public static List<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity entity) {
        List<ITriggerExternal> result;

        if (entity instanceof IOverrideDefaultStatements) {
            result = ((IOverrideDefaultStatements) entity).overrideTriggers();
            if (result != null) {
                return result;
            }
        }

        result = new LinkedList<>();

        for (ITriggerProvider provider : triggerProviders) {
            Collection<ITriggerExternal> toAdd = provider.getExternalTriggers(side, entity);

            if (toAdd != null) {
                for (ITriggerExternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static List<IActionExternal> getExternalActions(Direction side, BlockEntity entity) {
        List<IActionExternal> result;

        if (entity instanceof IOverrideDefaultStatements) {
            result = ((IOverrideDefaultStatements) entity).overrideActions();
            if (result != null) {
                return result;
            }
        }

        result = new LinkedList<>();

        for (IActionProvider provider : actionProviders) {
            Collection<IActionExternal> toAdd = provider.getExternalActions(side, entity);

            if (toAdd != null) {
                for (IActionExternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static List<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
        List<ITriggerInternal> result = new LinkedList<>();

        for (ITriggerProvider provider : triggerProviders) {
            Collection<ITriggerInternal> toAdd = provider.getInternalTriggers(container);

            if (toAdd != null) {
                for (ITriggerInternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static List<IActionInternal> getInternalActions(IStatementContainer container) {
        List<IActionInternal> result = new LinkedList<>();

        for (IActionProvider provider : actionProviders) {
            Collection<IActionInternal> toAdd = provider.getInternalActions(container);

            if (toAdd != null) {
                for (IActionInternal t : toAdd) {
                    if (!result.contains(t)) {
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public static IStatementParameter createParameter(String kind) {
        return createParameter(parameters.get(kind));
    }

    private static IStatementParameter createParameter(Class<? extends IStatementParameter> param) {
        try {
            return param.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        for (IStatement statement : statements.values()) {
            statement.registerIcons(textureGetter);
        }

        for (Class<? extends IStatementParameter> parameter : parameters.values()) {
            createParameter(parameter).registerIcons(textureGetter);
        }
    }
}
