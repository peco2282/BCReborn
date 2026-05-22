/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
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
