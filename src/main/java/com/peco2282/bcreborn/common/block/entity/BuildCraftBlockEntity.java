package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.api.IControllable;
import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.energy.IEnergyHandler;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.TileBuffer;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public abstract class BuildCraftBlockEntity extends BlockEntity implements IEnergyHandler, ISerializable {
  protected boolean init = false;
  protected IControllable.Mode mode;

  private int receivedTick, extractedTick;
  private long worldTimeEnergyReceive;
  private EnergyStorage battery;


  protected TileBuffer[] cache;
  protected HashSet<Player> guiWatchers = new HashSet<>();
  private final String owner = "[BuildCraft]";


  public BuildCraftBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
  }

  public void setBattery(EnergyStorage battery) {
    this.battery = battery;
  }

  public EnergyStorage getBattery() {
    return battery;
  }

  public @NotNull BlockPos getBlockPos() {
    return this.worldPosition;
  }

  public @NotNull Level getLevel() {
    //noinspection DataFlowIssue
    return super.level;
  }

  public void initialize() {
  }

  protected BlockState getBlockState(BlockPos pos) {
    //noinspection DataFlowIssue
    return level.getBlockState(pos);
  }

  protected BlockEntity getBlockEntity(BlockPos pos) {
    //noinspection DataFlowIssue
    return level.getBlockEntity(pos);
  }

  protected boolean hasNeighborSignal(BlockPos pos) {
    //noinspection DataFlowIssue
    return level.hasNeighborSignal(pos);
  }

  @Override
  public void setRemoved() {
    init = false;
    super.setRemoved();
  }

  protected abstract void tick(Level level, BlockPos pos, BlockState state);

  public boolean stillValid(Player player) {
    if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
      return false;
    }
    return player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    if (battery != null) {
      CompoundTag batteryNBT = new CompoundTag();
      battery.write(batteryNBT);
      nbt.put("battery", batteryNBT);
    }
    if (mode != null) {
      nbt.putByte("lastMode", (byte) mode.ordinal());
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    if (battery != null) {
      battery.read(nbt.getCompound("battery"));
    }
    if (nbt.contains("lastMode")) {
      mode = IControllable.Mode.values()[nbt.getByte("lastMode")];
    }
  }

  protected int getTicksSinceEnergyReceived() {
    return (int) (level.getGameTime() - worldTimeEnergyReceive);
  }


  @Override
  public int hashCode() {
    var pos = getBlockPos();
    return (pos.getX() * 37 + pos.getY()) * 37 + pos.getZ();
  }

  @Override
  public void readData(FriendlyByteBuf data) {
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void setChanged() {
    super.setChanged();
    if (level != null) {
      if (!level.isClientSide) {
        BCNetworkManager.sendBlockEntityUpdate(this, getBlockPos(), this::writeData);
      }
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  public static <T extends BuildCraftBlockEntity> BlockEntityTicker<T> ticker() {
    return (level, blockPos, blockState, blockEntity) -> {
      if (!blockEntity.init && !blockEntity.isRemoved()) {
        blockEntity.initialize();
        blockEntity.init = true;
      }
      blockEntity.tick(level, blockPos, blockState);
    };
  }

  protected static ResourceBuilder getResource(String path) {
    return ResourceBuilder.create(path);
  }

  protected static ResourceBuilder getResource(String ns, String path) {
    return ResourceBuilder.create(ns, path);
  }

  protected static ResourceBuilder getChildResource(ResourceLocation root, String path) {
    return ResourceBuilder.create(root.getNamespace(), root.getPath()).addPath(path);
  }

  protected static ResourceBuilder getChildResource(ResourceLocation root, ResourceLocation child) {
    if (root.getNamespace().equals(child.getNamespace())) {
      return getChildResource(root, child.getPath());
    } else {
      throw new IllegalArgumentException("Cannot get child resource from different namespaces: " + root.getNamespace() + " apply " + child.getNamespace() + "!");
    }
  }

}
