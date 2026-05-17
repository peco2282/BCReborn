/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.ISchematicRegistry;
import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.blueprints.SchematicEntity;
import com.peco2282.bcreborn.api.core.BCLog;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

public final class SchematicRegistry implements ISchematicRegistry {

    public static final SchematicRegistry INSTANCE = new SchematicRegistry();

    private static final HashMap<Class<? extends Schematic>, Constructor<?>> emptyConstructorMap = new HashMap<>();

    public final HashMap<String, SchematicConstructor> schematicBlocks = new HashMap<>();
    public final HashMap<Class<? extends Entity>, SchematicConstructor> schematicEntities = new HashMap<>();

    private final HashSet<String> modsForbidden = new HashSet<>();
    private final HashSet<String> blocksForbidden = new HashSet<>();

    private SchematicRegistry() {
    }

    public static class SchematicConstructor {
        private static final Object[] EMPTY_PARAMS = new Object[0];

        public final Class<? extends Schematic> clazz;
        public final Object[] params;
        private final Constructor<?> constructor;

        SchematicConstructor(Class<? extends Schematic> clazz, Object[] params) throws IllegalArgumentException {
            this.clazz = clazz;
            this.params = params != null && params.length > 0 ? params : EMPTY_PARAMS;
            this.constructor = findConstructor();
        }

        public Schematic newInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return (Schematic) constructor.newInstance(this.params);
        }

        private Constructor<?> findConstructor() throws IllegalArgumentException {
            if (params.length == 0 && emptyConstructorMap.containsKey(clazz)) {
                return emptyConstructorMap.get(clazz);
            }

            for (Constructor<?> c : clazz.getConstructors()) {
                if (c == null) continue;
                Class<?>[] typesSignature = c.getParameterTypes();
                if (typesSignature.length != params.length) continue;

                boolean valid = true;
                for (int i = 0; i < params.length; i++) {
                    if (params[i] == null) continue;
                    Class<?> paramClass = params[i].getClass();
                    if (!(typesSignature[i].isAssignableFrom(paramClass)
                            || (typesSignature[i] == int.class && paramClass == Integer.class)
                            || (typesSignature[i] == double.class && paramClass == Double.class)
                            || (typesSignature[i] == boolean.class && paramClass == Boolean.class))) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) continue;

                if (params.length == 0) {
                    emptyConstructorMap.put(clazz, c);
                }
                return c;
            }
            throw new IllegalArgumentException("Builder: Could not find matching constructor for class " + clazz);
        }
    }

    @Override
    public void registerSchematicBlock(Block block, Class<? extends Schematic> clazz, Object... params) {
        registerSchematicBlock(block, 0, clazz, params);
    }

    @Override
    public void registerSchematicBlock(Block block, int meta, Class<? extends Schematic> clazz, Object... params) {
        if (block == null) {
            BCLog.logger.warn("Builder: Mod tried to register a null block schematic! Ignoring.");
            return;
        }
        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
        if (name == null) {
            BCLog.logger.warn("Builder: Mod tried to register block '" + block.getClass().getName() + "' schematic with a null name! Ignoring.");
            return;
        }
        String key = toStringKey(block, meta);
        if (schematicBlocks.containsKey(key)) {
            BCLog.logger.warn("Builder: Block " + name + " is already associated with a schematic. Skipping.");
            return;
        }
        try {
            schematicBlocks.put(key, new SchematicConstructor(clazz, params));
        } catch (IllegalArgumentException e) {
            BCLog.logger.warn("Builder: Could not register schematic for block " + name + ": " + e.getMessage());
        }
    }

    @Override
    public void registerSchematicEntity(
            Class<? extends Entity> entityClass,
            Class<? extends SchematicEntity> schematicClass, Object... params) {
        if (schematicEntities.containsKey(entityClass)) {
            BCLog.logger.warn("Builder: Entity " + entityClass.getName() + " is already associated with a schematic. Skipping.");
            return;
        }
        try {
            schematicEntities.put(entityClass, new SchematicConstructor(schematicClass, params));
        } catch (IllegalArgumentException e) {
            BCLog.logger.warn("Builder: Could not register schematic for entity " + entityClass.getName() + ": " + e.getMessage());
        }
    }

    public SchematicBlock createSchematicBlock(Block block, int metadata) {
        if (block == null) return null;

        SchematicConstructor c = schematicBlocks.get(toStringKey(block, metadata));
        if (c == null) {
            // Fall back to meta 0
            c = schematicBlocks.get(toStringKey(block, 0));
        }
        if (c == null) return null;

        try {
            SchematicBlock s = (SchematicBlock) c.newInstance();
            s.block = block;
            return s;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SchematicEntity createSchematicEntity(Class<? extends Entity> entityClass) {
        if (!schematicEntities.containsKey(entityClass)) return null;

        try {
            SchematicConstructor c = schematicEntities.get(entityClass);
            SchematicEntity s = (SchematicEntity) c.newInstance();
            s.entity = entityClass;
            return s;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isSupported(Block block, int metadata) {
        return schematicBlocks.containsKey(toStringKey(block, metadata))
                || schematicBlocks.containsKey(toStringKey(block, 0));
    }

    public boolean isAllowedForBuilding(Block block, int metadata) {
        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
        if (name == null) return false;
        String nameStr = name.toString();
        String modId = name.getNamespace();
        return isSupported(block, metadata)
                && !blocksForbidden.contains(nameStr)
                && !modsForbidden.contains(modId);
    }

    public void addForbiddenMod(String modId) {
        modsForbidden.add(modId);
    }

    public void addForbiddenBlock(String blockName) {
        blocksForbidden.add(blockName);
    }

    private String toStringKey(Block block, int meta) {
        ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
        return (name != null ? name.toString() : block.getClass().getName()) + ":" + meta;
    }
}
