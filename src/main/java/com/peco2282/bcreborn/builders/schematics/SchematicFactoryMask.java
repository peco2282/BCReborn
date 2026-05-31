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
package com.peco2282.bcreborn.builders.schematics;

import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.blueprints.SchematicFactory;
import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import net.minecraft.nbt.CompoundTag;

public class SchematicFactoryMask extends SchematicFactory<SchematicMask> {

	@Override
	protected SchematicMask loadSchematicFromWorldNBT(CompoundTag nbt, MappingRegistry registry) {
		SchematicMask s = new SchematicMask();
		s.readSchematicFromNBT(nbt, registry);

		return s;
	}

	@Override
	public void saveSchematicToWorldNBT(CompoundTag nbt, SchematicMask object, MappingRegistry registry) {
		super.saveSchematicToWorldNBT(nbt, object, registry);

		object.writeSchematicToNBT(nbt, registry);
	}

}
