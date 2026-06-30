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

import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.IBlockEntityContainer;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.BlockEntityBuffer;
import com.peco2282.bcreborn.common.blueprint.BptBuilderTemplate;
import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import com.peco2282.bcreborn.common.builder.patterns.FillerPattern;
import com.peco2282.bcreborn.common.internal.IBoxProvider;
import com.peco2282.bcreborn.common.internal.IBoxesProvider;
import com.peco2282.bcreborn.common.internal.IDropControlInventory;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.registry.BCFillerPatterns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import java.util.LinkedList;
import java.util.List;

public class FillerBlockEntity extends AbstractBuilderBlockEntity implements MenuProvider, IBlockEntityContainer, ILEDProvider, IAreaProvider, IBoxProvider, IBoxesProvider, IDropControlInventory {
  public static final int POWER_ACTIVATION = 25;
  private final SimpleInventory inv = new SimpleInventory(28, "Filler", 64);
  public int currentPattern = 0;
  public Box box = new Box();
  public boolean excavate = true;
  private boolean isWorking = false;
  private boolean done = true;
  private BptBuilderTemplate currentTemplate;
  private int lastPattern = -1;

  public FillerBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.FILLER.get(), pos, state);
    setBattery(new EnergyStorage(10000, 1000, 1000));
  }

  @Override
  public void initialize() {
    super.initialize();
    cache = BlockEntityBuffer.makeBuffer(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), false);
    if (!box.isInitialized()) {
      for (Direction dir : Direction.values()) {
        BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
        if (tile instanceof IAreaProvider provider) {
          box.initialize(provider.xMin(), provider.yMin(), provider.zMin(), provider.xMax(), provider.yMax(), provider.zMax());
          if (box.isInitialized()) {
            box.createLaserData();
            provider.removeFromWorld();
            break;
          }
        }
      }
    } else {
      box.createLaserData();
    }
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) return;

    if (mode == IControllable.Mode.Off) {
      isWorking = false;
      return;
    }

    if (!box.isInitialized()) {
      isWorking = false;
      return;
    }

    if (getBattery().getEnergyStored() < POWER_ACTIVATION) {
      isWorking = false;
      return;
    }

    if (lastPattern != currentPattern) {
      currentTemplate = null;
      lastPattern = currentPattern;
      done = false;
    }

    if (done) {
      if (mode == IControllable.Mode.Loop) {
        done = false;
      } else {
        isWorking = false;
        return;
      }
    }

    if (currentTemplate == null) {
      initTemplate();
    }

    if (currentTemplate != null) {
      isWorking = currentTemplate.buildNextSlot(level, this, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
      if (isWorking) {
        getBattery().useEnergy(POWER_ACTIVATION, POWER_ACTIVATION, false);
      }
      if (currentTemplate.isDone(this)) {
        done = true;
        currentTemplate = null;
      }
    } else {
      isWorking = false;
    }
  }

  private void initTemplate() {
    var patterns = BCFillerPatterns.collection();
    if (currentPattern < 0 || currentPattern >= patterns.size()) return;
    IFillerPattern pattern = patterns.get(currentPattern).getValue();
    if (pattern instanceof FillerPattern fp) {
      currentTemplate = new BptBuilderTemplate(fp.getTemplate(box, level, null), level, box.xMin, box.yMin, box.zMin);
      done = false;
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readFromNBT(nbt, "Items");
    currentPattern = nbt.getInt("delta");
    if (nbt.contains("box")) {
      box.initialize(nbt.getCompound("box"));
    }
    done = nbt.getBoolean("done");
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeToNBT(nbt, "Items");
    nbt.putInt("delta", currentPattern);
    if (box.isInitialized()) {
      CompoundTag boxNbt = new CompoundTag();
      box.writeTag(boxNbt);
      nbt.put("box", boxNbt);
    }
    nbt.putBoolean("done", done);
  }

  public Container getInventory() {
    return inv;
  }

  public List<ItemStack> getInventoryList() {
    List<ItemStack> list = new LinkedList<>();
    for (int i = 0; i < inv.getContainerSize(); i++) {
      list.add(inv.getItem(i));
    }
    return list;
  }

  public SimpleInventory getFillerInventory() {
    return inv;
  }

  @Override
  public int xMin() {
    return box.xMin;
  }

  @Override
  public int yMin() {
    return box.yMin;
  }

  @Override
  public int zMin() {
    return box.zMin;
  }

  @Override
  public int xMax() {
    return box.xMax;
  }

  @Override
  public int yMax() {
    return box.yMax;
  }

  @Override
  public int zMax() {
    return box.zMax;
  }

  @Override
  public void removeFromWorld() {
  }

  @Override
  public Box getBox() {
    return box;
  }

  @Override
  public ArrayList<Box> getBoxes() {
    ArrayList<Box> list = new ArrayList<>();
    list.add(box);
    return list;
  }

  @Override
  public boolean doDrop() {
    return true;
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    super.writeData(data);
    data.writeInt(currentPattern);
    data.writeBoolean(isWorking);
    box.writeData(data);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    super.readData(data);
    currentPattern = data.readInt();
    isWorking = data.readBoolean();
    box.readData(data);
    if (level != null && level.isClientSide) {
      if (box.isInitialized()) {
        box.createLaserData();
      }
    }
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornbuilders.filler");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
    return new FillerMenu(windowId, inventory, this);
  }

  @Override
  public AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new FillerMenu(p_58627_, p_58628_, this);
  }

  @Override
  public int getContainerSize() {
    return inv.getContainerSize();
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
    return inv.removeItem(p_18942_, p_18943_);
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18951_) {
    return inv.removeItemNoUpdate(p_18951_);
  }

  @Override
  public void setItem(int p_18944_, ItemStack p_18945_) {
    inv.setItem(p_18944_, p_18945_);
  }

  @Override
  public boolean stillValid(Player player) {
    return super.stillValid(player);
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public Component getName() {
    return getDisplayName();
  }

  @Override
  public int getLEDLevel(int led) {
    return isWorking ? 15 : 0;
  }

  @Override
  public AABB getRenderBoundingBox() {
    return new Box(this).extendToEncompass(box).getBoundingBox();
  }

  public IFillerPattern getPattern() {
    return BCFillerPatterns.collection().get(currentPattern).getValue();
  }

  public void nextPattern() {
    currentPattern = (currentPattern + 1) % BCFillerPatterns.collection().size();
  }

  public void previousPattern() {
    currentPattern = (currentPattern - 1 + BCFillerPatterns.collection().size()) % BCFillerPatterns.collection().size();
  }
}
