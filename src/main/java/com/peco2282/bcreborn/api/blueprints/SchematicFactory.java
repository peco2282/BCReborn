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
package com.peco2282.bcreborn.api.blueprints;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;

public abstract class SchematicFactory<S extends Schematic> {

	private static final HashMap<String, SchematicFactory<?>> factories = new HashMap<String, SchematicFactory<?>>();

	private static final HashMap<Class<? extends Schematic>, SchematicFactory<?>> schematicToFactory = new HashMap<Class<? extends Schematic>, SchematicFactory<?>>();

	protected abstract S loadSchematicFromWorldNBT(CompoundTag nbt, MappingRegistry registry)
			throws MappingNotFoundException;

	public void saveSchematicToWorldNBT(CompoundTag nbt, S object, MappingRegistry registry) {
		nbt.putString("factoryID", getClass().getCanonicalName());
	}

	public static Schematic createSchematicFromWorldNBT(CompoundTag nbt, MappingRegistry registry)
			throws MappingNotFoundException {
		String factoryName = nbt.getString("factoryID");

		if (factories.containsKey(factoryName)) {
			return factories.get(factoryName).loadSchematicFromWorldNBT(nbt, registry);
		} else {
			return null;
		}
	}

	public static void registerSchematicFactory(Class<? extends Schematic> clas, SchematicFactory<?> factory) {
		schematicToFactory.put(clas, factory);
		factories.put(factory.getClass().getCanonicalName(), factory);
	}

	public static SchematicFactory getFactory(Class<? extends Schematic> clas) {
		Class superClass = clas.getSuperclass();

		if (schematicToFactory.containsKey(clas)) {
			return schematicToFactory.get(clas);
		} else if (superClass != null) {
			return getFactory(superClass);
		} else {
			return null;
		}
	}

}
