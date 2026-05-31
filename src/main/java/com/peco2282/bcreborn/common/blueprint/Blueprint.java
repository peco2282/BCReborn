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
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.BuildingPermission;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingNotFoundException;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.blueprints.SchematicBlockBase;
import com.peco2282.bcreborn.api.blueprints.SchematicEntity;
import com.peco2282.bcreborn.api.blueprints.Translation;
import com.peco2282.bcreborn.api.core.BCLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;
import java.util.List;

public class Blueprint extends BlueprintBase {

    public LinkedList<SchematicEntity> entities = new LinkedList<>();

    public Blueprint() {
        super();
        id.extension = "bpt";
    }

    public Blueprint(int sizeX, int sizeY, int sizeZ) {
        super(sizeX, sizeY, sizeZ);
        id.extension = "bpt";
    }

    @Override
    public void rotateLeft(BptContext context) {
        for (SchematicEntity e : entities) {
            e.rotateLeft(context);
        }
        super.rotateLeft(context);
    }

    @Override
    public void translateToBlueprint(Translation transform) {
        super.translateToBlueprint(transform);
        for (SchematicEntity e : entities) {
            e.translateToBlueprint(transform);
        }
    }

    @Override
    public void translateToWorld(Translation transform) {
        super.translateToWorld(transform);
        for (SchematicEntity e : entities) {
            e.translateToWorld(transform);
        }
    }

    @Override
    public void readFromWorld(IBuilderContext context, BlockEntity anchorTile, int x, int y, int z) {
        BptContext bptContext = (BptContext) context;
        BlockPos pos = new BlockPos(x, y, z);

        if (context.world().isEmptyBlock(pos)) {
            return;
        }

        SchematicBlock slot = SchematicRegistry.INSTANCE.createSchematicBlock(
                context.world().getBlockState(pos).getBlock(), 0);

        if (slot == null) {
            return;
        }

        int posX = (int) (x - context.surroundingBox().pMin().x);
        int posY = (int) (y - context.surroundingBox().pMin().y);
        int posZ = (int) (z - context.surroundingBox().pMin().z);

        slot.block = context.world().getBlockState(pos).getBlock();

        if (!SchematicRegistry.INSTANCE.isSupported(slot.block, 0)) {
            return;
        }

        try {
            slot.initializeFromObjectAt(context, x, y, z);
            slot.storeRequirements(context, x, y, z);
            put(posX, posY, posZ, slot);
        } catch (Throwable t) {
            t.printStackTrace();
            BCLog.logger.throwing(t);
        }

        switch (slot.getBuildingPermission()) {
            case ALL:
                break;
            case CREATIVE_ONLY:
                if (bptContext.readConfiguration != null && bptContext.readConfiguration.allowCreative) {
                    if (buildingPermission == BuildingPermission.ALL) {
                        buildingPermission = BuildingPermission.CREATIVE_ONLY;
                    }
                } else {
                    put(posX, posY, posZ, null);
                }
                break;
            case NONE:
                buildingPermission = BuildingPermission.NONE;
                break;
        }
    }

    @Override
    public void readEntitiesFromWorld(IBuilderContext context, BlockEntity anchorTile) {
        BptContext bptContext = (BptContext) context;
        Translation transform = new Translation();
        transform.x = -context.surroundingBox().pMin().x;
        transform.y = -context.surroundingBox().pMin().y;
        transform.z = -context.surroundingBox().pMin().z;

        List<Entity> entitiesInBox = context.world().getEntitiesOfClass(
                Entity.class,
                new net.minecraft.world.phys.AABB(
                        context.surroundingBox().pMin().x,
                        context.surroundingBox().pMin().y,
                        context.surroundingBox().pMin().z,
                        context.surroundingBox().pMax().x,
                        context.surroundingBox().pMax().y,
                        context.surroundingBox().pMax().z
                )
        );

        for (Entity e : entitiesInBox) {
            SchematicEntity s = SchematicRegistry.INSTANCE.createSchematicEntity(e.getClass());
            if (s != null) {
                s.readFromWorld(context, e);
                switch (s.getBuildingPermission()) {
                    case ALL:
                        entities.add(s);
                        break;
                    case CREATIVE_ONLY:
                        if (bptContext.readConfiguration != null && bptContext.readConfiguration.allowCreative) {
                            if (buildingPermission == BuildingPermission.ALL) {
                                buildingPermission = BuildingPermission.CREATIVE_ONLY;
                            }
                            entities.add(s);
                        }
                        break;
                    case NONE:
                        buildingPermission = BuildingPermission.NONE;
                        break;
                }
            }
        }
    }

    @Override
    public void saveContents(CompoundTag nbt) {
        ListTag nbtContents = new ListTag();

        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    SchematicBlockBase schematic = get(x, y, z);
                    CompoundTag cpt = new CompoundTag();
                    if (schematic != null) {
                        schematic.idsToBlueprint(mapping);
                        schematic.writeSchematicToNBT(cpt, mapping);
                    }
                    nbtContents.add(cpt);
                }
            }
        }

        nbt.put("contents", nbtContents);

        ListTag entitiesNBT = new ListTag();
        for (SchematicEntity s : entities) {
            CompoundTag subNBT = new CompoundTag();
            s.idsToBlueprint(mapping);
            s.writeSchematicToNBT(subNBT, mapping);
            entitiesNBT.add(subNBT);
        }
        nbt.put("entities", entitiesNBT);

        CompoundTag contextNBT = new CompoundTag();
        mapping.write(contextNBT);
        nbt.put("idMapping", contextNBT);
    }

    @Override
    public void loadContents(CompoundTag nbt) throws BptError {
        mapping.read(nbt.getCompound("idMapping"));

        ListTag nbtContents = nbt.getList("contents", Tag.TAG_COMPOUND);
        int index = 0;

        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    CompoundTag cpt = nbtContents.getCompound(index);
                    index++;

                    if (cpt.contains("blockId")) {
                        net.minecraft.world.level.block.Block block;
                        try {
                            block = mapping.getBlockForId(cpt.getInt("blockId"));
                        } catch (MappingNotFoundException e) {
                            block = null;
                            isComplete = false;
                        }

                        if (block != null) {
                            SchematicBlockBase schematic = SchematicRegistry.INSTANCE.createSchematicBlock(block, 0);
                            if (schematic != null) {
                                schematic.readSchematicFromNBT(cpt, mapping);
                                if (!((SchematicBlock) schematic).doNotUse()) {
                                    schematic.idsToWorld(mapping);
                                    switch (schematic.getBuildingPermission()) {
                                        case ALL:
                                            break;
                                        case CREATIVE_ONLY:
                                            if (buildingPermission == BuildingPermission.ALL) {
                                                buildingPermission = BuildingPermission.CREATIVE_ONLY;
                                            }
                                            break;
                                        case NONE:
                                            buildingPermission = BuildingPermission.NONE;
                                            break;
                                    }
                                } else {
                                    schematic = null;
                                    isComplete = false;
                                }
                            }
                            put(x, y, z, schematic);
                        } else {
                            put(x, y, z, null);
                            isComplete = false;
                        }
                    } else {
                        put(x, y, z, null);
                    }
                }
            }
        }

        ListTag entitiesNBT = nbt.getList("entities", Tag.TAG_COMPOUND);
        for (int i = 0; i < entitiesNBT.size(); ++i) {
            CompoundTag cpt = entitiesNBT.getCompound(i);
            if (cpt.contains("entityId")) {
                Class<? extends Entity> entityClass;
                try {
                    entityClass = mapping.getEntityForId(cpt.getInt("entityId"));
                } catch (MappingNotFoundException e) {
                    entityClass = null;
                    isComplete = false;
                }

                if (entityClass != null) {
                    SchematicEntity s = SchematicRegistry.INSTANCE.createSchematicEntity(entityClass);
                    if (s != null) {
                        s.readSchematicFromNBT(cpt, mapping);
                        s.idsToWorld(mapping);
                        entities.add(s);
                    }
                } else {
                    isComplete = false;
                }
            }
        }
    }

    @Override
    public ItemStack getStack() {
        // Item reference will be resolved at runtime via registry
        return ItemStack.EMPTY;
    }
}
