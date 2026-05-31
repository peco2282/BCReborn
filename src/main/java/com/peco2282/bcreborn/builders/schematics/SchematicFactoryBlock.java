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
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.blueprints.SchematicFactory;
import com.peco2282.bcreborn.common.blueprint.SchematicRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SchematicFactoryBlock extends SchematicFactory<SchematicBlock> {

  @Override
  protected SchematicBlock loadSchematicFromWorldNBT(CompoundTag nbt, MappingRegistry registry)
    throws MappingNotFoundException {
    int blockId = nbt.getInt("blockId");
    Block b = registry.getBlockForId(blockId);

    if (b == Blocks.AIR) {
      SchematicBlock s = new SchematicBlock();
      s.state = Blocks.AIR.defaultBlockState();

      return s;
    } else {
      SchematicBlock s = SchematicRegistry.INSTANCE.createSchematicBlock(b, nbt.getInt("blockMeta"));

      if (s != null) {
        s.readSchematicFromNBT(nbt, registry);
        return s;
      }
    }

    return null;
  }

  @Override
  public void saveSchematicToWorldNBT(CompoundTag nbt, SchematicBlock object, MappingRegistry registry) {
    super.saveSchematicToWorldNBT(nbt, object, registry);

    Block b = object.state != null ? object.state.getBlock() : object.block;
    nbt.putInt("blockId", registry.getIdForBlock(b));
    object.writeSchematicToNBT(nbt, registry);
  }

}
