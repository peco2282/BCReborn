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
package com.peco2282.bcreborn.factory.block.entity;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.block.BlockEntityBuffer;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.core.ConfigCore;
import com.peco2282.bcreborn.energy.EnergyConfig;
import com.peco2282.bcreborn.energy.fluids.SingleUseTank;
import com.peco2282.bcreborn.energy.fluids.Tank;
import com.peco2282.bcreborn.energy.fluids.TankUtils;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PumpBlockEntity extends BuildCraftBlockEntity implements IHasWork, IFluidHandler, IRedstoneEngineReceiver, ILEDProvider, IEnergyStorage {

  public static final int REBUID_DELAY = 512;
  public static int MAX_LIQUID = 16000;
  public Tank tank = new SingleUseTank("tank", MAX_LIQUID);

//  private EntityBlock tube;
  private final TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues = new TreeMap<>();
  private double tubeY = Double.NaN;
  private int aimY = -1;

  private SafeTimeTracker timer = new SafeTimeTracker(REBUID_DELAY);
  private int tick = Utils.RANDOM.nextInt(32);
  private int tickPumped = tick - 20;
  private int numFluidBlocksFound = 0;
  private boolean powered = false;

  private int ledState;
  // tick % 16 => min. 16 ticks per network update
  private final SafeTimeTracker updateTracker = new SafeTimeTracker(Math.max(16, ConfigCore.getUpdateFactor()));


  public PumpBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.PUMP.get(), pos, state);
    this.setBattery(new EnergyStorage(1000, 150, 0));
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (powered) {
      pumpLayerQueues.clear();
      tubeY = Double.NaN;
      aimY = -1;
      if (updateTracker.markTimeIfDelay(level)) {
        setChanged();
      }
      return;
    }

    if (Double.isNaN(tubeY)) {
      tubeY = pos.getY();
      aimY = pos.getY();
      setChanged();
    }

    pushToConsumers();

    // 隣接する液体コンテナがあるかチェック（BuildCraft仕様：出力先がないと動作しない）

    IFluidHandler handler = getFluidCapability();
    if (handler == null) { // No Fluid Capability
      return;
    }
    boolean hasConsumer = false;
    if (cache == null) {
      cache = BlockEntityBuffer.makeBuffer(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), false);
    }
    for (Direction side : Direction.values()) {
      BlockEntity tile = cache[side.ordinal()].getTile();
      if (tile != null && tile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).isPresent()) {
        hasConsumer = true;
        break;
      }
    }

    if (!hasConsumer && tank.getFluidAmount() >= tank.getCapacity()) {
      // タンクが満タンで、かつ出力先がない場合は何もしない
      return;
    }

    // Update tube height
    double targetTubeY = aimY;
    double tubeSpeed = 0.05; // 通常の下降速度
    
    // 液体面までの下降ロジック
    // 下に液体があれば、一定のエネルギーで下降する
    if (Math.abs(tubeY - aimY) < 0.01) {
      BlockPos tubePos = pos.atY(aimY);
      if (isBlocked(tubePos)) {
        // ブロックがある場合はそれ以上伸ばせないが、液体があればそこを基準にする
        if (!isPumpableFluid(tubePos)) {
          // 液体でもなく、ブロックされているなら終了
        }
      } else if (!isPumpableFluid(tubePos)) {
        // ブロックされておらず、液体でもないなら下に伸ばす
        // エネルギーを消費して下降（一定のエネルギー量 10RF/tick 程度を想定）
        if (getBattery().useEnergy(10, 10, false) > 0) {
          if (aimY > level.getMinBuildHeight()) {
            aimY--;
            setChanged();
          }
        }
      } else {
        // 現在の場所が液体面。
        System.out.println("Fluid!");
        // TODO: Impl Fill fluid!
      }
    }

    if (tubeY > targetTubeY + 0.01) {
      tubeY -= tubeSpeed;
      if (tubeY < targetTubeY) {
        tubeY = targetTubeY;
      }
    } else if (tubeY < targetTubeY - 0.01) {
      tubeY += tubeSpeed;
      if (tubeY > targetTubeY) {
        tubeY = targetTubeY;
      }
    }

    tick++;

    // 吸引処理
    // 液体面についたら少しずつ吸う（毎チック判定するが、エネルギーとタンク容量に依存）
    if (Math.abs(tubeY - aimY) < 0.01) {
      BlockIndex index = getNextIndexToPump(false);
      FluidStack fluidToPump = null;

      if (index != null && index.getY() != -1) {
        fluidToPump = BlockUtils.drainBlock(level, index, false);
        if (fluidToPump.isEmpty() || !isFluidAllowed(fluidToPump.getFluid())) {
          fluidToPump = null;
          getNextIndexToPump(true); // 汲み出せないのでスキップ
        }
      }

      if (fluidToPump != null && !fluidToPump.isEmpty()) {
        // 1回あたり100mBずつ吸う（少しずつ吸う感じを出す）
        int amountToDrain = Math.min(fluidToPump.getAmount(), 100);
        FluidStack smallFluid = new FluidStack(fluidToPump.getFluid(), amountToDrain);
        
        if (tank.fill(smallFluid, FluidAction.SIMULATE) == smallFluid.getAmount()) {
          // エネルギー消費（100RF/1000mB相当なら、10RF/100mB）
          if (getBattery().useEnergy(10, 10, false) > 0) {
            boolean consumeWater = EnergyConfig.isPumpsConsumeWater();
            // 無限水源チェック: 水であり、かつ9ブロック以上の水源がある場合
            boolean infiniteWater = smallFluid.getFluid() == Fluids.WATER && numFluidBlocksFound >= 9 && !consumeWater;

            if (infiniteWater) {
              tank.fill(smallFluid, FluidAction.EXECUTE);
              tickPumped = tick;
            } else {
              // 1ブロック吸える時に吸う。
              // タンクに余裕がある時に1ブロック丸ごと消す。
              if (tank.getCapacity() - tank.getFluidAmount() >= 1000) {
                if (getBattery().useEnergy(90, 90, false) > 0) { // 残り900RF
                  index = getNextIndexToPump(true);
                  if (index != null) {
                    BlockUtils.drainBlock(level, index, true);
                    tank.fill(fluidToPump, FluidAction.EXECUTE);
                    tickPumped = tick;
                  }
                }
              }
            }
          }
        }
      } else {
        // 吸引対象がない場合、またはキューが空の場合
        if (tick % 16 == 0) {
          rebuildQueue();

          BlockIndex next = getNextIndexToPump(false);
          if (next == null || next.getY() == -1) {
            // 現在の層および接続されている全層に液体がないなら管をさらに下に伸ばす
            BlockPos p = pos.atY(aimY - 1);
            if (aimY > level.getMinBuildHeight() && !isBlocked(p) && isPumpableFluid(p)) {
              aimY--;
              setChanged();
            }
          }
        }
      }
    }
  }

  @Nullable
  private IFluidHandler getFluidCapability() {
    AtomicReference<IFluidHandler> handler = new AtomicReference<>(null);
    for (Direction dir : Direction.values()) {
      var be = getLevel().getBlockEntity(getBlockPos().relative(dir));
      if (be != null) {
        be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir).ifPresent(handler::set);
      }
    }
    return handler.get();
  }

  public void onNeighborBlockChange(Block block) {
    boolean p = level.hasNeighborSignal(getBlockPos());

    if (powered != p) {
      powered = p;
      setChanged();
    }
  }

  private boolean isBlocked(BlockPos pos) {
    BlockState state = level.getBlockState(pos);

    return state.blocksMotion();
  }

  private void pushToConsumers() {
    if (cache == null) {
      cache = BlockEntityBuffer.makeBuffer(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), false);
    }

    TankUtils.pushFluidToConsumers(tank, 400, cache);
  }

  @Nullable
  private BlockIndex getNextIndexToPump(boolean remove) {
    if (pumpLayerQueues.isEmpty()) {
      if (timer.markTimeIfDelay(level)) {
        rebuildQueue();
      }

      return null;
    }

    Deque<BlockIndex> topLayer = pumpLayerQueues.lastEntry().getValue();

    if (topLayer != null) {
      if (topLayer.isEmpty()) {
        pumpLayerQueues.pollLastEntry();
        return getNextIndexToPump(remove);
      }

      if (remove) {
        return topLayer.pollLast();
      } else {
        return topLayer.peekLast();
      }
    } else {
      return null;
    }
  }

  private Deque<BlockIndex> getLayerQueue(int layer) {
    return pumpLayerQueues.computeIfAbsent(layer, k -> new LinkedList<>());
  }

  public void rebuildQueue() {
    numFluidBlocksFound = 0;
    pumpLayerQueues.clear();

    if (aimY < getLevel().getMinBuildHeight()) {
      return;
    }

    BlockPos tubePos = getBlockPos().atY(aimY);
    Block block = getLevel().getBlockState(tubePos).getBlock();
    Fluid pumpingFluid = BlockUtils.getFluid(block);

    if (pumpingFluid == Fluids.EMPTY) {
      return;
    }

    if (tank instanceof SingleUseTank sut) {
      if (pumpingFluid != sut.getAcceptedFluid()) {
        return;
      }
    }

    Set<BlockIndex> visitedBlocks = new HashSet<>();
    Deque<BlockIndex> fluidsToExpand = new LinkedList<>();

    // 初回のチェック（無限水源保護のためのカウント）
    if (pumpingFluid == Fluids.WATER) {
      checkInfiniteWater(tubePos, pumpingFluid);
    }

    queueForPumping(tubePos, visitedBlocks, fluidsToExpand, pumpingFluid);

    while (!fluidsToExpand.isEmpty()) {
      BlockIndex index = fluidsToExpand.pollFirst();

      // 6方向を探索
      queueForPumping(index.above(), visitedBlocks, fluidsToExpand, pumpingFluid);
      queueForPumping(index.below(), visitedBlocks, fluidsToExpand, pumpingFluid);
      queueForPumping(index.east(), visitedBlocks, fluidsToExpand, pumpingFluid);
      queueForPumping(index.west(), visitedBlocks, fluidsToExpand, pumpingFluid);
      queueForPumping(index.south(), visitedBlocks, fluidsToExpand, pumpingFluid);
      queueForPumping(index.north(), visitedBlocks, fluidsToExpand, pumpingFluid);

      // 探索上限（安全のため。半径64の円内に収まるはずだが、念のため多めに設定）
      if (visitedBlocks.size() > 64 * 64 * 4) {
        break;
      }
    }
  }

  private void checkInfiniteWater(BlockPos center, Fluid water) {
    numFluidBlocksFound = 0;
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        BlockPos p = center.offset(x, 0, z);
        if (BlockUtils.getFluid(getLevel().getBlockState(p).getBlock()) == water && level.getFluidState(p).isSource()) {
          numFluidBlocksFound++;
        }
      }
    }
  }

  public void queueForPumping(BlockPos pos, Set<BlockIndex> visitedBlocks, Deque<BlockIndex> fluidsToExpand, Fluid pumpingFluid) {
    // 高さが管の先端より高い場所は吸わない（仕様：高い順に吸うが、管の先端が基準点）
    // 元のBuildCraftでは管の先端の層から開始し、上の層も地続きなら吸う。
    // ただし、現在の実装では aimY を基準点としている。
    if (pos.getY() < getLevel().getMinBuildHeight() || pos.getY() > getLevel().getMaxBuildHeight()) {
      return;
    }

    BlockIndex index = new BlockIndex(pos);
    if (visitedBlocks.add(index)) {
      // 半径64ブロックの範囲制限（水平方向のみ。垂直方向は制限なし、あるいは管の長さによる）
      long dx = pos.getX() - getBlockPos().getX();
      long dz = pos.getZ() - getBlockPos().getZ();
      if (dx * dx + dz * dz > 64 * 64) {
        return;
      }

      Block block = getLevel().getBlockState(pos).getBlock();
      Fluid fluidAt = BlockUtils.getFluid(block);

      if (fluidAt == pumpingFluid) {
        fluidsToExpand.add(index);

        if (canDrainBlock(block, pos, pumpingFluid)) {
          getLayerQueue(pos.getY()).add(index);
        }
      }
    }
  }

  private boolean isPumpableFluid(BlockPos pos) {
    Fluid fluid = BlockUtils.getFluid(BlockUtils.getBlock(getLevel(), pos));

    if (fluid == Fluids.EMPTY) {
      return false;
    } else {
      if (tank instanceof SingleUseTank sut) {
        return sut.getAcceptedFluid() == fluid;
      }
      return true;
    }
  }

  private boolean canDrainBlock(Block block, BlockPos pos, Fluid fluid) {
    FluidStack fluidStack = BlockUtils.drainBlock(block, getLevel(), pos, false);

    if (fluidStack.isEmpty() || fluidStack.getAmount() <= 0) {
      return false;
    } else {
      return fluidStack.getFluid() == fluid;
    }
  }

  private boolean isFluidAllowed(Fluid fluid) {
    return true;
  }


  @Override
  public void load(CompoundTag data) {
    super.load(data);
    tank.readFromNBT(data.getCompound("tank"));
    powered = data.getBoolean("powered");
    aimY = data.getInt("aimY");
    tubeY = data.getDouble("tubeY");
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    CompoundTag tankNbt = new CompoundTag();
    tank.writeToNBT(tankNbt);
    data.put("tank", tankNbt);
    data.putBoolean("powered", powered);
    data.putInt("aimY", aimY);
    data.putDouble("tubeY", tubeY);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return getBattery().receiveEnergy(maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return getBattery().extractEnergy(maxExtract, simulate);
  }

  @Override
  public int getEnergyStored() {
    return getBattery().getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return getBattery().getMaxEnergyStored();
  }

  @Override
  public boolean canExtract() {
    return getBattery().canExtract();
  }

  @Override
  public boolean canReceive() {
    return getBattery().canReceive();
  }

  @Override
  public boolean hasWork() {
    return true; // Simplified
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return this.tank.getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return this.tank.getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0; // Pump doesn't accept fluid
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return tank.drain(resource, action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return tank.drain(maxDrain, action);
  }

  @Override
  public boolean canConnectRedstoneEngine(Direction side) {
    return true;
  }

  @Override
  public int getLEDLevel(int led) {
    if (led == 0) { // Red LED
      return ledState & 15;
    } else { // Green LED
      return (ledState >> 4) > 0 ? 15 : 0;
    }
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag nbt = super.getUpdateTag();
    saveAdditional(nbt);
    return nbt;
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    load(pkt.getTag());
  }

  @Override
  public net.minecraft.world.phys.AABB getRenderBoundingBox() {
    return new net.minecraft.world.phys.AABB(worldPosition).expandTowards(0, -getTubeHeight() - 1, 0);
  }

  public double getTubeHeight() {
    if (Double.isNaN(tubeY)) {
      return 0.0;
    }
    return Math.max(0.0, getBlockPos().getY() - tubeY);
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    super.writeData(stream);
    stream.writeDouble(tubeY);
    stream.writeShort(aimY);
    stream.writeBoolean(powered);
    ledState = ((tick - tickPumped) < 48 ? 16 : 0) | (getBattery().getEnergyStored() * 15 / getBattery().getMaxEnergyStored());
    stream.writeByte(ledState);
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    super.readData(stream);
    tubeY = stream.readDouble();
    aimY = stream.readShort();
    powered = stream.readBoolean();
    ledState = stream.readByte();
  }

}
