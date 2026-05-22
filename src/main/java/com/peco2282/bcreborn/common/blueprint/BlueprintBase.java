/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.BuildingPermission;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.blueprints.SchematicBlockBase;
import com.peco2282.bcreborn.api.blueprints.Translation;
import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.common.Box;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

public abstract class BlueprintBase {

    public ArrayList<CompoundTag> subBlueprintsNBT = new ArrayList<>();

    public int anchorX, anchorY, anchorZ;
    public int sizeX, sizeY, sizeZ;
    public LibraryId id = new LibraryId();
    public String author;
    public boolean rotate = true;
    public boolean excavate = true;
    public BuildingPermission buildingPermission = BuildingPermission.ALL;
    public boolean isComplete = true;

    protected MappingRegistry mapping = new MappingRegistry();
    protected SchematicBlockBase[] contents;

    private CompoundTag cachedNbt;
    private Direction mainDir = Direction.EAST;

    public BlueprintBase() {
    }

    public BlueprintBase(int sizeX, int sizeY, int sizeZ) {
        contents = new SchematicBlockBase[sizeX * sizeY * sizeZ];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        anchorX = 0;
        anchorY = 0;
        anchorZ = 0;
    }

    private int toArrayPos(int x, int y, int z) {
        return (y * sizeZ + z) * sizeX + x;
    }

    public SchematicBlockBase get(int x, int y, int z) {
        return contents[(y * sizeZ + z) * sizeX + x];
    }

    public void put(int x, int y, int z, SchematicBlockBase s) {
        contents[(y * sizeZ + z) * sizeX + x] = s;
    }

    public void translateToBlueprint(Translation transform) {
        for (SchematicBlockBase content : contents) {
            if (content != null) {
                content.translateToBlueprint(transform);
            }
        }
    }

    public void translateToWorld(Translation transform) {
        for (SchematicBlockBase content : contents) {
            if (content != null) {
                content.translateToWorld(transform);
            }
        }
    }

	public void rotateLeft(BptContext context) {
		SchematicBlockBase[] newContents = new SchematicBlockBase[sizeZ * sizeY * sizeX];

        for (int x = 0; x < sizeZ; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeX; ++z) {
                    int pos = (y * sizeX + z) * sizeZ + x;
                    newContents[pos] = contents[toArrayPos(z, y, (sizeZ - 1) - x)];

                    if (newContents[pos] != null) {
                        try {
                            newContents[pos].rotateLeft(context);
                        } catch (Throwable t) {
                            t.printStackTrace();
                            BCLog.logger.throwing(t);
                        }
                    }
                }
            }
        }

        int newAnchorX = (sizeZ - 1) - anchorZ;
        int newAnchorY = anchorY;
        int newAnchorZ = anchorX;

        for (CompoundTag sub : subBlueprintsNBT) {
            Direction dir = Direction.values()[sub.getByte("dir")];
            dir = rotateDirectionLeft(dir);

            int px = sub.getInt("x");
            int pz = sub.getInt("z");
            int newPx = (context.box.sizeZ() - 1) - pz;
            int newPz = px;

            sub.putInt("x", newPx);
            sub.putInt("z", newPz);
            sub.putByte("dir", (byte) dir.ordinal());
        }

        context.rotateLeft();

        anchorX = newAnchorX;
        anchorY = newAnchorY;
        anchorZ = newAnchorZ;

        contents = newContents;
        int tmp = sizeX;
        sizeX = sizeZ;
        sizeZ = tmp;

        mainDir = rotateDirectionLeft(mainDir);
    }

    private static Direction rotateDirectionLeft(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.WEST;
            case WEST -> Direction.SOUTH;
            case SOUTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
            default -> dir;
        };
    }

    private void writeToNBTInternal(CompoundTag nbt) {
        nbt.putString("version", "1.20.1");

        if (this instanceof Template) {
            nbt.putString("kind", "template");
        } else {
            nbt.putString("kind", "blueprint");
        }

        nbt.putInt("sizeX", sizeX);
        nbt.putInt("sizeY", sizeY);
        nbt.putInt("sizeZ", sizeZ);
        nbt.putInt("anchorX", anchorX);
        nbt.putInt("anchorY", anchorY);
        nbt.putInt("anchorZ", anchorZ);
        nbt.putBoolean("rotate", rotate);
        nbt.putBoolean("excavate", excavate);

        if (author != null) {
            nbt.putString("author", author);
        }

        saveContents(nbt);

        ListTag subBptList = new ListTag();
        for (CompoundTag subBpt : subBlueprintsNBT) {
            subBptList.add(subBpt);
        }
        nbt.put("subBpt", subBptList);
    }

    public static BlueprintBase loadBluePrint(CompoundTag nbt) {
        String kind = nbt.getString("kind");

        BlueprintBase bpt;
        if ("template".equals(kind)) {
            bpt = new Template();
        } else {
            bpt = new Blueprint();
        }

        bpt.readFromNBT(nbt);
        return bpt;
    }

    public void readFromNBT(CompoundTag nbt) {
        sizeX = nbt.getInt("sizeX");
        sizeY = nbt.getInt("sizeY");
        sizeZ = nbt.getInt("sizeZ");
        anchorX = nbt.getInt("anchorX");
        anchorY = nbt.getInt("anchorY");
        anchorZ = nbt.getInt("anchorZ");

        author = nbt.getString("author");

        rotate = !nbt.contains("rotate") || nbt.getBoolean("rotate");
        excavate = !nbt.contains("excavate") || nbt.getBoolean("excavate");

        contents = new SchematicBlockBase[sizeX * sizeY * sizeZ];

        try {
            loadContents(nbt);
        } catch (BptError e) {
            e.printStackTrace();
        }

        if (nbt.contains("subBpt")) {
            ListTag subBptList = nbt.getList("subBpt", Tag.TAG_COMPOUND);
            for (int i = 0; i < subBptList.size(); ++i) {
                subBlueprintsNBT.add(subBptList.getCompound(i));
            }
        }
    }

    public Box getBoxForPos(int x, int y, int z) {
        int xMin = x - anchorX;
        int yMin = y - anchorY;
        int zMin = z - anchorZ;
        int xMax = x + sizeX - anchorX - 1;
        int yMax = y + sizeY - anchorY - 1;
        int zMax = z + sizeZ - anchorZ - 1;

        Box res = new Box();
        res.initialize(xMin, yMin, zMin, xMax, yMax, zMax);
        res.reorder();
        return res;
    }

    public BptContext getContext(Level world, Box box) {
        return new BptContext(world, box, mapping);
    }

    public void addSubBlueprint(BlueprintBase subBpt, int x, int y, int z, Direction dir) {
        CompoundTag subNBT = new CompoundTag();
        subNBT.putInt("x", x);
        subNBT.putInt("y", y);
        subNBT.putInt("z", z);
        subNBT.putByte("dir", (byte) dir.ordinal());
        subNBT.put("bpt", getNBT());
        subBlueprintsNBT.add(subNBT);
    }

    public CompoundTag getNBT() {
        if (cachedNbt == null) {
            cachedNbt = new CompoundTag();
            writeToNBTInternal(cachedNbt);
        }
        return cachedNbt;
    }

    public BlueprintBase adjustToWorld(Level world, int x, int y, int z, Direction o) {
        if (buildingPermission == BuildingPermission.NONE
                || (buildingPermission == BuildingPermission.CREATIVE_ONLY
                && !world.isClientSide())) {
            return null;
        }

        BptContext context = getContext(world, getBoxForPos(x, y, z));

        if (rotate) {
            if (o == Direction.EAST) {
                // Do nothing
            } else if (o == Direction.SOUTH) {
                rotateLeft(context);
            } else if (o == Direction.WEST) {
                rotateLeft(context);
                rotateLeft(context);
            } else if (o == Direction.NORTH) {
                rotateLeft(context);
                rotateLeft(context);
                rotateLeft(context);
            }
        }

        Translation transform = new Translation();
        transform.x = x - anchorX;
        transform.y = y - anchorY;
        transform.z = z - anchorZ;

        translateToWorld(transform);
        return this;
    }

    public abstract void loadContents(CompoundTag nbt) throws BptError;

    public abstract void saveContents(CompoundTag nbt);

    public abstract void readFromWorld(IBuilderContext context, BlockEntity anchorTile, int x, int y, int z);

    public abstract ItemStack getStack();

    public void readEntitiesFromWorld(IBuilderContext context, BlockEntity anchorTile) {
    }
}
