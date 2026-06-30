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
package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.blueprints.RecursiveBlueprintReader;
import com.peco2282.bcreborn.builders.menu.ArchitectMenu;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.IBlockEntityContainer;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.BlueprintReadConfiguration;
import com.peco2282.bcreborn.common.internal.IBoxProvider;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ArchitectBlockEntity extends BuildCraftBlockEntity implements MenuProvider, ILEDProvider, Container, IBlockEntityContainer, IBoxProvider {

  private final SimpleInventory inv = new SimpleInventory(2, "Architect", 1);
  public String currentAuthorName = "";
  public Mode mode = Mode.NONE;
  public Box box = new Box();
  public String name = "";
  public BlueprintReadConfiguration readConfiguration = new BlueprintReadConfiguration();
  public ArrayList<LaserData> subLasers = new ArrayList<>();
  public ArrayList<BlockPos> subBlueprints = new ArrayList<>();
  private RecursiveBlueprintReader reader;
  private boolean clientIsWorking, initialized;

  public ArchitectBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.ARCHITECT.get(), pos, state);
  }

  public Box getBox() {
    return box;
  }

  public ArrayList<BlockPos> getSubBlueprints() {
    return subBlueprints;
  }

  public Container getInventory() {
    return inv;
  }

  @Override
  public void initialize() {
    super.initialize();
    if (!level.isClientSide && !initialized) {
      if (!box.isInitialized()) {
        for (Direction dir : Direction.values()) {
          BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
          if (tile instanceof IAreaProvider a) {
            mode = Mode.COPY;
            box.initialize(a.xMin(), a.yMin(), a.zMin(), a.xMax(), a.yMax(), a.zMax());
            if (box.isInitialized()) {
              box.createLaserData();
            }
            a.removeFromWorld();
            break;
          }
        }
        if (!box.isInitialized()) {
          if (BCRebornCore.DEVELOPER_MODE) {
            mode = Mode.EDIT;
          } else {
            mode = Mode.NONE;
          }
        }
      } else {
        mode = Mode.COPY;
        box.createLaserData();
      }
      initialized = true;
    } else if (level.isClientSide) {
      if (box.isInitialized()) {
        box.createLaserData();
      }
    }
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (!level.isClientSide) {
      if (mode == Mode.COPY) {
        if (reader == null && !getItem(0).isEmpty()) {
          initializeBlueprint();
        }

        if (reader != null) {
          reader.iterate();

          if (reader.isDone()) {
            storeBlueprintStack(getItem(0));
            reader = null;
            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
          }
        }
      }
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);

    if (nbt.contains("box")) {
      box.initialize(nbt.getCompound("box"));
    }

    inv.readFromNBT(nbt, "Items");

    mode = Mode.values()[nbt.getByte("mode")];
    name = nbt.getString("name");
    currentAuthorName = nbt.getString("lastAuthor");

    if (nbt.contains("readConfiguration")) {
      readConfiguration.readTag(nbt.getCompound("readConfiguration"));
    }

    long[] subBptList = nbt.getLongArray("subBlueprints");
    subBlueprints.clear();
    subLasers.clear();
    for (long tag : subBptList) {
      addSubBlueprint(BlockPos.of(tag));
    }
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);

    if (box.isInitialized()) {
      CompoundTag boxStore = new CompoundTag();
      box.writeTag(boxStore);
      nbt.put("box", boxStore);
    }

    inv.writeToNBT(nbt, "Items");

    nbt.putByte("mode", (byte) mode.ordinal());
    nbt.putString("name", name);
    nbt.putString("lastAuthor", currentAuthorName);

    CompoundTag readConf = new CompoundTag();
    readConfiguration.writeTag(readConf);
    nbt.put("readConfiguration", readConf);

    LongArrayTag subBptList = new LongArrayTag(subBlueprints.stream().mapToLong(BlockPos::asLong).toArray());
    nbt.put("subBlueprints", subBptList);
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    box.writeData(data);
    data.writeUtf(name);
    data.writeBoolean(getIsWorking());
    data.writeByte(mode.ordinal());
    if (mode == Mode.COPY) {
      readConfiguration.writeData(data);
      data.writeShort(subLasers.size());
      for (LaserData ld : subLasers) {
        ld.writeData(data);
      }
    }
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    box.readData(stream);
    name = stream.readUtf();
    clientIsWorking = stream.readBoolean();
    mode = Mode.values()[stream.readByte()];

    if (level != null && level.isClientSide) {
      if (box.isInitialized()) {
        box.createLaserData();
      }
    }

    if (mode == Mode.COPY) {
      readConfiguration.readData(stream);
      int size = stream.readUnsignedShort();
      subLasers.clear();
      for (int i = 0; i < size; i++) {
        LaserData ld = new LaserData();
        ld.readData(stream);
        subLasers.add(ld);
      }
    }
  }

  private boolean getIsWorking() {
    return mode == Mode.COPY && reader != null;
  }

  public int getComputingProgressScaled(int scale) {
    if (reader != null) {
      return (int) (reader.getComputingProgressScaled() * scale);
    } else {
      return 0;
    }
  }

  public void setReadConfiguration(BlueprintReadConfiguration config) {
    this.readConfiguration = config;
  }

  public void rpcSetConfiguration(BlueprintReadConfiguration conf) {
    readConfiguration = conf;
    BCNetworkManager.sendSetReadArchitectConfiguration(getBlockPos(), readConfiguration);
  }

  public void addSubBlueprint(BlockEntity sub) {
    if (mode == Mode.COPY) {
      addSubBlueprint(sub.getBlockPos());
      setChanged();
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  private void addSubBlueprint(BlockPos pos) {
    subBlueprints.add(pos);

    LaserData laser = new LaserData(new Position(pos.getX(), pos.getY(), pos.getZ()), new Position(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()));

    laser.head.x += 0.5F;
    laser.head.y += 0.5F;
    laser.head.z += 0.5F;

    laser.tail.x += 0.5F;
    laser.tail.y += 0.5F;
    laser.tail.z += 0.5F;

    subLasers.add(laser);
  }

  public void storeBlueprintStack(ItemStack blueprintStack) {
    setItem(1, blueprintStack);
    removeItem(0, 1);
  }

  private void initializeBlueprint() {
    if (getLevel().isClientSide) {
      return;
    }

    if (mode == Mode.COPY) {
      reader = new RecursiveBlueprintReader(this);
    }
  }

  @Override
  public Component getName() {
    return getDisplayName();
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Component getDisplayName() {
    return name.isEmpty() ? Component.translatable("menu.bcrebornbuilders.architect") : Component.literal(name);
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
    return new ArchitectMenu(windowId, inventory, this);
  }

  @Override
  public AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new ArchitectMenu(p_58627_, p_58628_, this);
  }

  @Override
  public int getContainerSize() {
    return 2;
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int p_18942_) {
    return inv.getItem(p_18942_);
  }

  @Override
  public ItemStack removeItem(int p_18942_, int p_18943_) {
    ItemStack result = inv.removeItem(p_18942_, p_18943_);
    if (p_18942_ == 0) {
      initializeBlueprint();
    }
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18951_) {
    return inv.removeItemNoUpdate(p_18951_);
  }

  @Override
  public void setItem(int p_18944_, ItemStack p_18945_) {
    inv.setItem(p_18944_, p_18945_);
    if (p_18944_ == 0) {
      initializeBlueprint();
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return mode != Mode.NONE && super.stillValid(player);
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public int getLEDLevel(int led) {
    boolean condition = switch (led) {
      case 0 -> clientIsWorking;
      case 1 -> mode == Mode.COPY && box != null && box.isInitialized();
      case 2 -> mode == Mode.EDIT;
      default -> false;
    };
    return condition ? 15 : 0;
  }

  @Override
  public AABB getRenderBoundingBox() {
    Box completeBox = new Box(this).extendToEncompass(box);
    for (LaserData d : subLasers) {
      completeBox.extendToEncompass(d.tail);
    }
    return completeBox.getBoundingBox();
  }

  public enum Mode {
    NONE, EDIT, COPY
  }
}
