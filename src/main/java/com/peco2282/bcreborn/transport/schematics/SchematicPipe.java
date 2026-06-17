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
package com.peco2282.bcreborn.transport.schematics;

import com.peco2282.bcreborn.api.blueprints.*;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementManager;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class SchematicPipe extends SchematicTile {

  public static final int MAX_STATEMENTS = 8;
  public static final int MAX_PARAMETERS = 3;

  private final BuildingPermission permission = BuildingPermission.ALL;

  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    PipeBlockEntity pipe = PipeBlock.getPipe(context.world(), pos);

    if (PipeBlock.isValid(pipe)) {
      int pipeId = tileNBT.getInt("pipeId");
      Item item = Item.byId(pipeId);
      return getPipeItem(pipe) == item;
    } else {
      return false;
    }
  }

  private Item getPipeItem(PipeBlockEntity pipe) {
    try {
      Field matField = PipeBlockEntity.class.getDeclaredField("pipeMaterial");
      matField.setAccessible(true);
      PipeMaterial material = (PipeMaterial) matField.get(pipe);

      Field typeField = PipeBlockEntity.class.getDeclaredField("transportType");
      typeField.setAccessible(true);
      PipeType type = (PipeType) typeField.get(pipe);

      RegistryObject<PipeBlock> block = TransportBlocks.get(type, material);
      if (block != null) {
        return block.get().asItem();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Items.AIR;
  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    PipeBlockEntity.SideProperties props = new PipeBlockEntity.SideProperties();

    props.readFromNBT(tileNBT);
    props.rotateLeft();
    props.writeToNBT(tileNBT);

    Item pipeItem = Item.byId(tileNBT.getInt("pipeId"));

    if (BptPipeExtension.contains(pipeItem)) {
      BptPipeExtension.get(pipeItem).rotateLeft(this, context);
    }

    if (tileNBT.contains("Gate")) {
      CompoundTag gateNBT = tileNBT.getCompound("Gate");
      rotateGateLeft(gateNBT);
    } else {
      CompoundTag[] gatesNBT = new CompoundTag[6];

      for (int i = 0; i < 6; ++i) {
        if (tileNBT.contains("Gate[" + i + "]")) {
          gatesNBT[i] = tileNBT.getCompound("Gate[" + i + "]");
        }
      }

      for (int i = 0; i < 6; ++i) {
        int newI = Direction.values()[i].getClockWise().ordinal();

        if (gatesNBT[i] != null) {
          rotateGateLeft(gatesNBT[i]);
          tileNBT.put("Gate[" + newI + "]", gatesNBT[i]);
        } else {
          tileNBT.remove("Gate[" + newI + "]");
        }
      }
    }
  }

  private void rotateGateLeft(CompoundTag gateNBT) {
    for (int i = 0; i < MAX_STATEMENTS; ++i) {
      if (gateNBT.contains("trigger[" + i + "]")) {
        IStatement t = StatementManager.getStatement(ResourceLocation.parse(gateNBT.getString("trigger[" + i + "]")));
        if (t != null) {
          t = t.rotateLeft();
          gateNBT.putString("trigger[" + i + "]", t.getUniqueTag().toString());
        }
      }

      if (gateNBT.contains("action[" + i + "]")) {
        IStatement a = StatementManager.getStatement(ResourceLocation.parse(gateNBT.getString("action[" + i + "]")));
        if (a != null) {
          a = a.rotateLeft();
          gateNBT.putString("action[" + i + "]", a.getUniqueTag().toString());
        }
      }

      for (int j = 0; j < MAX_PARAMETERS; ++j) {
        if (gateNBT.contains("triggerParameters[" + i + "][" + j + "]")) {
          CompoundTag cpt = gateNBT.getCompound("triggerParameters[" + i + "][" + j + "]");
          IStatementParameter parameter = StatementManager.createParameter(cpt.getString("kind"));
          if (parameter != null) {
            parameter.readFromNBT(cpt);
            parameter = parameter.rotateLeft();
            parameter.writeToNBT(cpt);
            gateNBT.put("triggerParameters[" + i + "][" + j + "]", cpt);
          }
        }

        if (gateNBT.contains("actionParameters[" + i + "][" + j + "]")) {
          CompoundTag cpt = gateNBT.getCompound("actionParameters[" + i + "][" + j + "]");
          IStatementParameter parameter = StatementManager.createParameter(cpt.getString("kind"));
          if (parameter != null) {
            parameter.readFromNBT(cpt);
            parameter = parameter.rotateLeft();
            parameter.writeToNBT(cpt);
            gateNBT.put("actionParameters[" + i + "][" + j + "]", cpt);
          }
        }
      }
    }

    if (gateNBT.contains("direction")) {
      int dir = gateNBT.getInt("direction");
      gateNBT.putInt("direction", Direction.values()[dir].getClockWise().ordinal());
    }
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    BlockPos pos = new BlockPos(x, y, z);
    context.world().setBlock(pos, state, 3);

    BlockEntity tile = context.world().getBlockEntity(pos);
    if (tile != null) {
      tileNBT.putInt("x", x);
      tileNBT.putInt("y", y);
      tileNBT.putInt("z", z);
      tile.load(tileNBT);
    }
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    BlockEntity tile = context.world().getBlockEntity(pos);
    PipeBlockEntity pipe = PipeBlock.getPipe(context.world(), pos);

    if (PipeBlock.isValid(pipe)) {
      tileNBT = tile.saveWithFullMetadata();

      // remove all pipe contents
      tileNBT.remove("travelingEntities");

      for (Direction direction : Direction.values()) {
        tileNBT.remove("tank[" + direction.ordinal() + "]");
        tileNBT.remove("transferState[" + direction.ordinal() + "]");
      }

      for (int i = 0; i < 6; ++i) {
        tileNBT.remove("powerQuery[" + i + "]");
        tileNBT.remove("nextPowerQuery[" + i + "]");
        tileNBT.remove("internalPower[" + i + "]");
        tileNBT.remove("internalNextPower[" + i + "]");
      }
    }
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    PipeBlockEntity pipe = PipeBlock.getPipe(context.world(), pos);

    if (PipeBlock.isValid(pipe)) {
      ArrayList<ItemStack> items = computeItemDrop(pipe);
      storedRequirements = new ItemStack[items.size() + 1];
      items.toArray(storedRequirements);
      storedRequirements[storedRequirements.length - 1] = new ItemStack(getPipeItem(pipe));
    }
  }

  private ArrayList<ItemStack> computeItemDrop(PipeBlockEntity pipe) {
    ArrayList<ItemStack> list = new ArrayList<>();
    try {
      PipeBlockEntity.SideProperties props = pipe.sideProperties;

      for (PipePluggable pluggable : props.pluggables) {
        if (pluggable != null) {
          // NOTE: This might still fail if pluggable.getDropItems expects IPipeTile
          // We may need to pass a proxy or a dummy IPipeTile if PipeBlockEntity doesn't implement it yet.
          if (pipe instanceof IPipeTile) {
            Collections.addAll(list, pluggable.getDropItems((IPipeTile) pipe));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  @Override
  public void postProcessing(IBuilderContext context, int x, int y, int z) {
    Item pipeItem = Item.byId(tileNBT.getInt("pipeId"));

    if (BptPipeExtension.contains(pipeItem)) {
      BptPipeExtension.get(pipeItem).postProcessing(this, context);
    }
  }

  @Override
  public BuildingStage getBuildStage() {
    return BuildingStage.STANDALONE;
  }

  @Override
  public void idsToBlueprint(MappingRegistry registry) {
    super.idsToBlueprint(registry);

    if (tileNBT.contains("pipeId")) {
      Item item = Item.byId(tileNBT.getInt("pipeId"));
      tileNBT.putInt("pipeId", registry.getIdForItem(item));
    }
  }

  @Override
  public void idsToWorld(MappingRegistry registry) {
    super.idsToWorld(registry);

    if (tileNBT.contains("pipeId")) {
      try {
        Item item = registry.getItemForId(tileNBT.getInt("pipeId"));
        tileNBT.putInt("pipeId", Item.getId(item));
      } catch (MappingNotFoundException e) {
        tileNBT.remove("pipeId");
      }
    }
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    super.writeSchematicToNBT(nbt, registry);
    nbt.putInt("version", 2);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    super.readSchematicFromNBT(nbt, registry);

    if (!nbt.contains("version") || nbt.getInt("version") < 2) {
      // Schematics previous to the fixes in version 2 had item id
      // translation badly broken. We need to flush out information that
      // would be otherwise corrupted - that is the inventory (with the
      // old formalism "items") and gate parameters.
      tileNBT.remove("items");

      if (tileNBT.contains("Gate")) {
        CompoundTag gateNBT = tileNBT.getCompound("Gate");

        for (int i = 0; i < 8; ++i) {
          if (gateNBT.contains("triggerParameters[" + i + "]")) {
            CompoundTag parameterNBT = gateNBT.getCompound("triggerParameters[" + i + "]");

            if (parameterNBT.contains("stack")) {
              parameterNBT.remove("stack");
            }
          }
        }
      }
    }
  }

  @Override
  public BuildingPermission getBuildingPermission() {
    return permission;
  }
}
