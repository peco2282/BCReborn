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


import com.peco2282.bcreborn.api.blueprints.MappingNotFoundException;
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.blueprints.SchematicEntity;
import com.peco2282.bcreborn.api.blueprints.SchematicFactory;
import com.peco2282.bcreborn.common.blueprint.SchematicRegistry;
import net.minecraft.nbt.CompoundTag;

public class SchematicFactoryEntity extends SchematicFactory<SchematicEntity> {

  @Override
  protected SchematicEntity loadSchematicFromWorldNBT(CompoundTag nbt, MappingRegistry registry)
    throws MappingNotFoundException {
    int entityId = nbt.getInt("entityId");
    SchematicEntity s = SchematicRegistry.INSTANCE.createSchematicEntity(registry.getEntityForId(entityId));

    if (s != null) {
      s.readSchematicFromNBT(nbt, registry);
    } else {
      return null;
    }

    return s;
  }

  @Override
  public void saveSchematicToWorldNBT(CompoundTag nbt, SchematicEntity object, MappingRegistry registry) {
    super.saveSchematicToWorldNBT(nbt, object, registry);

    nbt.putInt("entityId", registry.getIdForEntity(object.entity));
    object.writeSchematicToNBT(nbt, registry);
  }

}
