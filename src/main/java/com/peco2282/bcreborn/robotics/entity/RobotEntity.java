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
package com.peco2282.bcreborn.robotics.entity;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.events.RobotEvent;
import com.peco2282.bcreborn.api.robots.*;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.tiles.IDebuggable;
import com.peco2282.bcreborn.common.CodingUtils;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.item.WrenchItem;
import com.peco2282.bcreborn.robotics.RoboticsEntityTypes;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.AIRobotMain;
import com.peco2282.bcreborn.robotics.ai.AIRobotShutdown;
import com.peco2282.bcreborn.robotics.ai.AIRobotSleep;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import com.peco2282.bcreborn.robotics.registry.RobotRegistry;
import com.peco2282.bcreborn.robotics.statements.ActionRobotWorkInArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;


public class RobotEntity extends RobotEntityBase implements
  IEntityAdditionalSpawnData, Container, IFluidHandler, IDebuggable {

  public static final ResourceLocation ROBOT_BASE = BCRebornRobotics.location("textures/entities/robot_base.png");
  public static final int MAX_WEARABLES = 8;
  public static final int TRANSFER_INV_SLOTS = 4;
  private static final EntityDataAccessor<Float> DATA_LASER_X =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Float> DATA_LASER_Y =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Float> DATA_LASER_Z =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Byte> DATA_LASER_VISIBLE =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.BYTE);
  private static final EntityDataAccessor<String> DATA_BOARD_ID =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.STRING);
  private static final EntityDataAccessor<Float> DATA_ITEM_ANGLE_1 =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Float> DATA_ITEM_ANGLE_2 =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);
  private static final EntityDataAccessor<Integer> DATA_ENERGY_SPEND_PER_CYCLE =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Byte> DATA_ACTIVE =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.BYTE);
  private static final EntityDataAccessor<Integer> DATA_ENERGY =
    SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
  private static final Set<Integer> blacklistedItemsForUpdate = Sets.newHashSet();
  private final List<ItemStack> wearables = new ArrayList<>();
  private final ItemStack[] inv = CodingUtils.apply(new ItemStack[TRANSFER_INV_SLOTS], its -> Arrays.fill(its, ItemStack.EMPTY));
  private final int maxFluid = FluidType.BUCKET_VOLUME * 4;
  private final WeakHashMap<Entity, Long> unreachableEntities = new WeakHashMap<>();
  private final EnergyStorage battery = new EnergyStorage(MAX_ENERGY, MAX_ENERGY, 100);
  public LaserData laser = new LaserData();
  public DockingStation<?> linkedDockingStation;
  public BlockIndex linkedDockingStationIndex;
  public Direction linkedDockingStationSide;
  public BlockIndex currentDockingStationIndex;
  public Direction currentDockingStationSide;
  public boolean isDocked = false;
  public RedstoneBoardRobot<?> board;
  public AIRobotMain mainAI;
  public ItemStack itemInUse;
  public float itemAngle1 = 0;
  public float itemAngle2 = 0;
  public boolean itemActive = false;
  public float itemActiveStage = 0;
  public long lastUpdateTime = 0;
  private DockingStation<?> currentDockingStation;
  private boolean needsUpdate = false;
  private FluidStack tank;
  private ResourceLocation texture;
  private ListTag stackRequestNBT;
  private boolean firstUpdateDone = false;

  private boolean isActiveClient = false;

  private long robotId = RobotEntityBase.NULL_ROBOT_ID;

  private int energySpendPerCycle = 0;
  private int ticksCharging = 0;
  private float energyFX = 0;
  private int steamDx = 0;
  private int steamDy = -1;
  private int steamDz = 0;

  public RobotEntity(Level world, RedstoneBoardRobotNBT boardNBT) {
    this(RoboticsEntityTypes.ROBOT.get(), world);

    board = boardNBT.create(this);

    if (!world.isClientSide) {
      entityData.set(DATA_BOARD_ID, board.getNBTHandler().getID().toString());
      mainAI = new AIRobotMain(this);
      mainAI.start();
    }
  }


  public RobotEntity(EntityType<RobotEntity> type, Level world) {
    super(type, world);
    setDeltaMovement(0, 0, 0);

    noCulling = true;
    laser.isVisible = false;


    setNullBoundingBox();

    noPhysics = true;
    this.setPersistenceRequired(); // persistenceRequired = true
    this.setBoundingBox(
      new AABB(getX() - 0.25F, getY() - 0.25F, getZ() - 0.25F, getX() + 0.25F, getY() + 0.25F, getZ() + 0.25F)
    );
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();

    entityData.define(DATA_LASER_X, 0F); // 12
    entityData.define(DATA_LASER_Y, 0F); // 13
    entityData.define(DATA_LASER_Z, 0F); // 14
    entityData.define(DATA_LASER_VISIBLE, (byte) 0); // 15
    entityData.define(DATA_BOARD_ID, ""); // 16
    entityData.define(DATA_ITEM_ANGLE_1, 0F); // 17
    entityData.define(DATA_ITEM_ANGLE_2, 0F); // 18
    entityData.define(DATA_ENERGY_SPEND_PER_CYCLE, 0); // 19
    entityData.define(DATA_ACTIVE, (byte) 0); // 20
    entityData.define(DATA_ENERGY, 0); // 21
  }

  protected void updateDataClient() {
    laser.tail.x = entityData.get(DATA_LASER_X);
    laser.tail.y = entityData.get(DATA_LASER_Y);
    laser.tail.z = entityData.get(DATA_LASER_Z);
    laser.isVisible = entityData.get(DATA_LASER_VISIBLE) == 1;

    RedstoneBoardNBT<?> boardNBT = RegistryUtil.getRedstoneBoard(entityData
      .get(DATA_BOARD_ID));

    texture = ((RedstoneBoardRobotNBT) boardNBT).getRobotTexture();

    itemAngle1 = entityData.get(DATA_ITEM_ANGLE_1);
    itemAngle2 = entityData.get(DATA_ITEM_ANGLE_2);
    energySpendPerCycle = entityData.get(DATA_ENERGY_SPEND_PER_CYCLE);
    isActiveClient = entityData.get(DATA_ACTIVE) == 1;
    battery.setEnergy(entityData.get(DATA_ENERGY));
  }

  protected void updateDataServer() {
    entityData.set(DATA_LASER_X, (float) laser.tail.x);
    entityData.set(DATA_LASER_Y, (float) laser.tail.y);
    entityData.set(DATA_LASER_Z, (float) laser.tail.z);
    entityData.set(DATA_LASER_VISIBLE, (byte) (laser.isVisible ? 1 : 0));
    entityData.set(DATA_ITEM_ANGLE_1, itemAngle1);
    entityData.set(DATA_ITEM_ANGLE_2, itemAngle2);
  }

  public boolean isActive() {
    if (level().isClientSide) {
      return isActiveClient;
    } else {
      return mainAI.getActiveAI() instanceof AIRobotSleep || mainAI.getActiveAI() instanceof AIRobotShutdown;
    }
  }

  protected void init() {
    if (level().isClientSide) {
      BCNetworkManager.sendRequestInitialization(getId(), itemInUse, itemActive);
    }
  }

  public void setLaserDestination(float x, float y, float z) {
    if (x != laser.tail.x || y != laser.tail.y || z != laser.tail.z) {
      laser.tail.x = x;
      laser.tail.y = y;
      laser.tail.z = z;

      needsUpdate = true;
    }
  }

  public void showLaser() {
    if (!laser.isVisible) {
      laser.isVisible = true;
      needsUpdate = true;
    }
  }

  public void hideLaser() {
    if (laser.isVisible) {
      laser.isVisible = false;
      needsUpdate = true;
    }
  }

  protected void firstUpdate() {
    if (stackRequestNBT != null) {

    }

    if (!level().isClientSide) {
      getRegistry().registerRobot(this);
    }
  }

  @Override
  public void tick() {
    this.level().getProfiler().push("bcEntityRobot");
    if (!firstUpdateDone) {
      firstUpdate();
      firstUpdateDone = true;
    }

    if (ticksCharging > 0) {
      ticksCharging--;
    }

    if (!level().isClientSide) {
      // The client-side sleep indicator should also display if the robot is charging.
      // To not break gates and other things checking for sleep, this is done here.
      entityData.set(DATA_ACTIVE, (byte) (isActive() || ticksCharging > 0 ? 1 : 0));
      entityData.set(DATA_ENERGY, getEnergy());

      if (needsUpdate) {
        updateDataServer();
        needsUpdate = false;
      }
    }

    if (level().isClientSide) {
      updateDataClient();
      updateRotationYaw(60.0f);
      updateEnergyFX();
    }

    if (currentDockingStation != null) {
      setDeltaMovement(0, 0, 0);
      setPos(
        currentDockingStation.x() + 0.5F + currentDockingStation.side().getStepX() * 0.5F,
        currentDockingStation.y() + 0.5F + currentDockingStation.side().getStepY() * 0.5F,
        currentDockingStation.z() + 0.5F + currentDockingStation.side().getStepZ() * 0.5F
      );
    }

    if (!level().isClientSide) {
      if (linkedDockingStation == null) {
        if (linkedDockingStationIndex != null) {
          linkedDockingStation = getRegistry().getStation(linkedDockingStationIndex.toBlockPos(),
            linkedDockingStationSide);
        }

        if (linkedDockingStation == null) {
          shutdown("no docking station");
        } else {
          if (linkedDockingStation.robotTaking() != this) {
            if (linkedDockingStation.robotIdTaking() == robotId) {
              BCLog.logger.warn("A robot entity was not properly unloaded");
              linkedDockingStation.invalidateRobotTakingEntity();
            }
            if (linkedDockingStation.robotTaking() != this) {
              shutdown("wrong docking station");
            }
          }
        }
      }

      if (currentDockingStationIndex != null && currentDockingStation == null) {
        currentDockingStation = getRegistry().getStation(
          currentDockingStationIndex.toBlockPos(),
          currentDockingStationSide);
      }

      if (getY() < -128) {
        remove(RemovalReason.DISCARDED);
        BCLog.logger.info("Destroying robot " + this + " - Fallen into Void");
        getRegistry().killRobot(this);
      }

      if (linkedDockingStation == null || linkedDockingStation.isInitialized()) {
        this.level().getProfiler().push("bcRobotAI");
        mainAI.cycle();
        this.level().getProfiler().pop();

        if (energySpendPerCycle != mainAI.getActiveAI().getEnergyCost()) {
          energySpendPerCycle = mainAI.getActiveAI().getEnergyCost();
          entityData.set(DATA_ENERGY_SPEND_PER_CYCLE, energySpendPerCycle);
        }
      }
    }


    // tick all carried itemstacks
    for (int i = 0; i < inv.length; i++) {
      updateItem(inv[i], i, false);
    }

    // tick the item the robot is currently holding
    updateItem(itemInUse, 0, true);

    // do not tick wearables or equipment from EntityLiving


    super.tick();
    this.level().getProfiler().pop();
  }

  @OnlyIn(Dist.CLIENT)
  private void updateEnergyFX() {
    energyFX += energySpendPerCycle;
    ParticleStatus status = Minecraft.getInstance().options.particles().get();
    int particleLevel = switch (status) {
      case ALL -> 0;
      case DECREASED -> 1;
      case MINIMAL -> 2;
    };
    if (energyFX >= (100 << (2 * particleLevel))) {
      energyFX = 0;
      spawnEnergyFX();
    }
  }

  @OnlyIn(Dist.CLIENT)
  private void spawnEnergyFX() {
    Minecraft.getInstance().particleEngine.add(new RobotEnergyParticle(
      (ClientLevel) level(),
      getX() + steamDx * 0.25, getY() + steamDy * 0.25, getZ() + steamDz * 0.25,
      steamDx * 0.05, steamDy * 0.05, steamDz * 0.05,
      energySpendPerCycle * 0.075F < 1 ? 1 : energySpendPerCycle * 0.075F));
  }

  public void setNullBoundingBox() {
    setBoundingBox(
      new AABB(getX(), getY(), getZ(), getX(), getY(), getZ())
    );
  }

  private void shutdown(String reason) {
    if (!(mainAI.getDelegateAI() instanceof AIRobotShutdown)) {
      BCLog.logger.info("Shutting down robot " + this + " - " + reason);
      mainAI.startDelegateAI(new AIRobotShutdown(this));
    }
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf data) {
    data.writeByte(wearables.size());
    for (ItemStack s : wearables) {
      data.writeItem(s);
    }
    data.writeUtf(board.getNBTHandler().getID().toString());
  }

  @Override
  public void readSpawnData(FriendlyByteBuf data) {
    int amount = data.readUnsignedByte();
    while (amount > 0) {
      wearables.add(data.readItem());
      amount--;
    }
    String boardId = data.readUtf();
    RedstoneBoardNBT<?> boardNBT = RegistryUtil.getRedstoneBoard(boardId);
    if (boardNBT instanceof RedstoneBoardRobotNBT robotBoardNBT) {
      this.board = robotBoardNBT.create(this);
    }
    init();
  }

  @Override
  public void setItemSlot(EquipmentSlot slot, ItemStack itemstack) {
    if (slot == EquipmentSlot.MAINHAND) {
      itemInUse = itemstack;
    }
  }

//  @Override
//  public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
//    return false;
//  }
//
//  @Override
//  protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
//  }

  @Override
  public void travel(Vec3 travelVector) {
    Vec3 motion = getDeltaMovement();
    this.setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
  }

  @Override
  public boolean onClimbable() {
    return false;
  }

  public ResourceLocation getTexture() {
    return texture;
  }

  @Override
  public void addAdditionalSaveData(CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);

    if (linkedDockingStationIndex != null) {
      CompoundTag linkedStationNBT = new CompoundTag();
      CompoundTag linkedStationIndexNBT = new CompoundTag();
      linkedDockingStationIndex.writeTo(linkedStationIndexNBT);
      linkedStationNBT.put("index", linkedStationIndexNBT);
      linkedStationNBT.putInt("side", linkedDockingStationSide.get3DDataValue());
      nbt.put("linkedStation", linkedStationNBT);
    }

    if (currentDockingStationIndex != null) {
      CompoundTag currentStationNBT = new CompoundTag();
      CompoundTag currentStationIndexNBT = new CompoundTag();
      currentDockingStationIndex.writeTo(currentStationIndexNBT);
      currentStationNBT.put("index", currentStationIndexNBT);
      currentStationNBT.putInt("side", currentDockingStationSide.get3DDataValue());
      nbt.put("currentStation", currentStationNBT);
    }

    CompoundTag nbtLaser = new CompoundTag();
    laser.writeToNBT(nbtLaser);
    nbt.put("laser", nbtLaser);

    CompoundTag batteryNBT = new CompoundTag();
    battery.write(batteryNBT);
    nbt.put("battery", batteryNBT);

    if (itemInUse != null) {
      CompoundTag itemNBT = new CompoundTag();
      itemInUse.save(itemNBT);
      nbt.put("itemInUse", itemNBT);
      nbt.putBoolean("itemActive", itemActive);
    }

    for (int i = 0; i < inv.length; ++i) {
      CompoundTag stackNbt = new CompoundTag();

      if (inv[i] != null) {
        nbt.put("inv[" + i + "]", inv[i].save(stackNbt));
      }
    }

    if (!wearables.isEmpty()) {
      ListTag wearableList = new ListTag();

      for (ItemStack wearable : wearables) {
        CompoundTag item = new CompoundTag();
        wearable.save(item);
        wearableList.add(item);
      }

      nbt.put("wearables", wearableList);
    }

    CompoundTag ai = new CompoundTag();
    mainAI.writeToNBT(ai);
    nbt.put("mainAI", ai);

    if (mainAI.getDelegateAI() != board) {
      CompoundTag boardNBT = new CompoundTag();
      board.writeSelfToNBT(boardNBT);
      nbt.put("board", boardNBT);
    }

    nbt.putLong("robotId", robotId);

    if (tank != null) {
      CompoundTag tankNBT = new CompoundTag();

      tank.writeToNBT(tankNBT);

      nbt.put("tank", tankNBT);
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);

    if (nbt.contains("linkedStation")) {
      CompoundTag linkedStationNBT = nbt.getCompound("linkedStation");
      linkedDockingStationIndex = new BlockIndex(linkedStationNBT.getCompound("index"));
      linkedDockingStationSide = Direction.from3DDataValue(linkedStationNBT.getInt("side"));
    }

    if (nbt.contains("currentStation")) {
      CompoundTag currentStationNBT = nbt.getCompound("currentStation");
      currentDockingStationIndex = new BlockIndex(currentStationNBT.getCompound("index"));
      currentDockingStationSide = Direction.from3DDataValue(currentStationNBT.getInt("side"));

    }

    laser.readFromNBT(nbt.getCompound("laser"));

    battery.read(nbt.getCompound("battery"));

    wearables.clear();
    if (nbt.contains("wearables")) {
      ListTag list = nbt.getList("wearables", 10);
      for (int i = 0; i < list.size(); i++) {
        ItemStack stack = ItemStack.of(list.getCompound(i));
        if (!stack.isEmpty()) {
          wearables.add(stack);
        }
      }
    }

    if (nbt.contains("itemInUse")) {
      itemInUse = ItemStack.of(nbt.getCompound("itemInUse"));
      itemActive = nbt.getBoolean("itemActive");
    }

    for (int i = 0; i < inv.length; ++i) {
      inv[i] = ItemStack.of(nbt.getCompound("inv[" + i + "]"));
    }

    CompoundTag ai = nbt.getCompound("mainAI");
    mainAI = (AIRobotMain) AIRobot.loadAI(ai, this);

    if (nbt.contains("board")) {
      board = (RedstoneBoardRobot<?>) AIRobot.loadAI(nbt.getCompound("board"), this);
    } else {
      board = (RedstoneBoardRobot<?>) mainAI.getDelegateAI();
    }

    if (board == null) {
      board = RoboticsRedstoneRobots.EMPTY.get().create(this);
    }

    if (!level().isClientSide) {
      entityData.set(DATA_BOARD_ID, board.getNBTHandler().getID().toString());
    }

    stackRequestNBT = nbt.getList("stackRequests", ListTag.TAG_COMPOUND);

    if (nbt.contains("robotId")) {
      robotId = nbt.getLong("robotId");
    }

    if (nbt.contains("tank")) {
      tank = FluidStack.loadFluidStackFromNBT(nbt.getCompound("tank"));
    } else {
      tank = null;
    }

    // Restore robot persistence on pre-6.1.9 robotics
    this.setPersistenceRequired();
  }

  @Override
  public void dock(DockingStation<?> station) {
    currentDockingStation = station;

    setSteamDirection(
      currentDockingStation.side.getStepX(),
      currentDockingStation.side.getStepY(),
      currentDockingStation.side.getStepZ());

    currentDockingStationIndex = currentDockingStation.index();
    currentDockingStationSide = currentDockingStation.side();
  }

  @Override
  public void undock() {
    if (currentDockingStation != null) {
      currentDockingStation.release(this);
      currentDockingStation = null;

      setSteamDirection(0, -1, 0);

      currentDockingStationIndex = null;
      currentDockingStationSide = null;
    }
  }

  @Override
  public DockingStation<?> getDockingStation() {
    return currentDockingStation;
  }

  @Override
  public void setMainStation(DockingStation<?> station) {
    if (linkedDockingStation != null && linkedDockingStation != station) {
      linkedDockingStation.unsafeRelease(this);
    }

    linkedDockingStation = station;
    if (station != null) {
      linkedDockingStationIndex = linkedDockingStation.index();
      linkedDockingStationSide = linkedDockingStation.side();
    } else {
      linkedDockingStationIndex = null;
      linkedDockingStationSide = Direction.UP;
    }
  }

  @Override
  public int getContainerSize() {
    return inv.length;
  }

  @Override
  public boolean isEmpty() {
    return inv.length == 0;
  }

  @Override
  public ItemStack getItem(int var1) {
    return inv[var1];
  }

  @Override
  public ItemStack removeItem(int var1, int var2) {
    ItemStack result = inv[var1].split(var2);

    if (inv[var1].getCount() == 0) {
      inv[var1] = null;
    }

    updateClientSlot(var1);

    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int var1) {
    ItemStack stack = inv[var1];
    inv[var1] = null;
    return stack;
  }

  @Override
  public void setItem(int var1, ItemStack var2) {
    inv[var1] = var2;

    updateClientSlot(var1);
  }

  @Override
  public Component getDisplayName() {
    return Component.empty();
  }

  @Override
  public int getMaxStackSize() {
    return 64;
  }

  @Override
  public void setChanged() {
  }

  public void updateClientSlot(final int slot) {
    BCNetworkManager.sendEntityClientSetInventory(this, this.getId(), (short) slot, inv[slot]);
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return inv[slot].isEmpty()
      || (inv[slot].is(stack.getItem()) && inv[slot].isStackable() && inv[slot].getCount()
      + stack.getCount() <= inv[slot].getItem().getMaxStackSize(inv[slot]));
  }

  @Override
  public boolean isMoving() {
    return getDeltaMovement().x != 0 || getDeltaMovement().y != 0 || getDeltaMovement().z != 0;
  }

  @Override
  public void setItemInUse(ItemStack stack) {
    itemInUse = stack;
    BCNetworkManager.sendEntityClientSetItemInUse(this, this.getId(), stack);
  }

  public void setSteamDirection(final int x, final int y, final int z) {
    if (!level().isClientSide) {
      BCNetworkManager.sendSetSteamDirection(this, this.getId(), x, y, z);
    } else {
      Vec3 v = new Vec3(x, y, z);
      v = v.normalize();

      steamDx = (int) v.x;
      steamDy = (int) v.y;
      steamDz = (int) v.z;
    }
  }

//  @Override
//  public void setHealth(float par1) {
//    // deactivate health management
//  }

  @Override
  public boolean hurt(DamageSource source, float f) {
    if (source.is(DamageTypes.GENERIC_KILL)) return super.hurt(source, f); // By /kill command
    // Ignore hits from mobs or when docked.
    Entity src = source.getEntity();
    if (src != null && !(src instanceof Fallable) && !(src instanceof Mob) && currentDockingStation == null) {
      if (ForgeHooks.onLivingAttack(this, source, f)) {
        return false;
      }

      if (!level().isClientSide) {
        hurtTime = hurtDuration = 10;

        int mul = 2600;
        for (ItemStack s : wearables) {
          if (s.getItem() instanceof ArmorItem) {
            mul = mul * 2 / (2 + ((ArmorItem) s.getItem()).getDefense()) / 2;
          } else {
            mul *= 0.7;
          }
        }

        int energy = Math.round(f * mul);
        if (battery.getEnergyStored() - energy > 0) {
          battery.setEnergy(battery.getEnergyStored() - energy);
          return true;
        } else {
          onRobotHit(true);
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public float getAimYaw() {
    return itemAngle1;
  }

  @Override
  public float getAimPitch() {
    return itemAngle2;
  }

  @Override
  public void aimItemAt(float yaw, float pitch) {
    itemAngle1 = yaw;
    itemAngle2 = pitch;

    updateDataServer();
  }

  @Override
  public void aimItemAt(int x, int y, int z) {
    int deltaX = x - (int) Math.floor(getX());
    int deltaY = y - (int) Math.floor(getY());
    int deltaZ = z - (int) Math.floor(getZ());

    if (deltaX != 0 || deltaZ != 0) {
      itemAngle1 = (float) (Math.atan2(deltaZ, deltaX) * 180f / Math.PI) + 180f;
    }
    double d3 = Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    itemAngle2 = (float) (-(Math.atan2(deltaY, d3) * 180.0D / Math.PI));

    setSteamDirection(deltaX, deltaY, deltaZ);

    updateDataServer();
  }

  private void updateRotationYaw(float maxStep) {
    float step = Mth.wrapDegrees(itemAngle1 - getYRot());

    if (step > maxStep) {
      step = maxStep;
    }

    if (step < -maxStep) {
      step = -maxStep;
    }

    setYRot(getYRot() + step);
  }

  @Override
  public void setItemActive(final boolean isActive) {
    if (isActive != itemActive) {
      itemActive = isActive;
      BCNetworkManager.sendSetItemActive(this, this.getId(), isActive);
    }
  }

  @Override
  public RedstoneBoardRobot<?> getBoard() {
    return board;
  }

  @Override
  public DockingStation<?> getLinkedStation() {
    return linkedDockingStation;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public boolean shouldRenderAtSqrDistance(double par1) {
    return true;
  }

  @Override
  public int getEnergy() {
    return battery.getEnergyStored();
  }

  @Override
  public EnergyStorage getBattery() {
    return battery;
  }

  @Override
  public boolean removeWhenFarAway(double distance) {
    return false;
  }

  public AIRobot<?> getOverridingAI() {
    return mainAI.getOverridingAI();
  }

  @Override
  public ItemStack getMainHandItem() {
    return itemInUse == null ? ItemStack.EMPTY : itemInUse;
  }

  @Override
  public ItemStack getItemBySlot(EquipmentSlot slot) {
    if (slot == EquipmentSlot.MAINHAND) {
      return itemInUse == null ? ItemStack.EMPTY : itemInUse;
    }

    return ItemStack.EMPTY;
  }

  public void overrideAI(AIRobot<?> ai) {
    mainAI.setOverridingAI(ai);
  }

  public void attackTargetEntityWithCurrentItem(Entity par1Entity) {
    if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(
      BCFakePlayer.getBuildCraftPlayer((ServerLevel) level(), (int) getX(), (int) getY(), (int) getZ()).get(),
      par1Entity))) {
      return;
    }

    if (par1Entity.isAttackable()) {
      if (!par1Entity.skipAttackInteraction(this)) {
        Multimap<Attribute, AttributeModifier> attributes = itemInUse != null ? itemInUse.getAttributeModifiers(EquipmentSlot.MAINHAND) : null;
        float attackDamage = 2.0F;
        int knockback = 0;

        if (attributes != null) {
          for (AttributeModifier modifier : attributes.get(Attributes.ATTACK_DAMAGE)) {
            switch (modifier.getOperation()) {
              case ADDITION:
                attackDamage += modifier.getAmount();
                break;
              case MULTIPLY_BASE:
                attackDamage *= modifier.getAmount();
                break;
              case MULTIPLY_TOTAL:
                attackDamage *= 1.0F + modifier.getAmount();
                break;
            }
          }
        }

        if (par1Entity instanceof LivingEntity le && !itemInUse.isEdible()) {
          attackDamage += EnchantmentHelper.getDamageBonus(itemInUse, le.getMobType());
          knockback += EnchantmentHelper.getKnockbackBonus(this);
        }

        if (attackDamage > 0.0F) {
          int fireAspect = EnchantmentHelper.getFireAspect(this);

          if (par1Entity instanceof LivingEntity && fireAspect > 0 && !par1Entity.isOnFire()) {
            par1Entity.setSecondsOnFire(fireAspect * 4);
          }

          if (par1Entity.hurt(damageSources().mobAttack(this), attackDamage)) {
            if (par1Entity instanceof LivingEntity) {
              this.setLastHurtByMob((LivingEntity) par1Entity);
            }

            if (knockback > 0) {
              par1Entity.addDeltaMovement(new Vec3(-Mth.sin(this.getYRot() * (float) Math.PI / 180.0F) * (float) knockback * 0.5F, 0.1D, Mth.cos(this.getYRot() * (float) Math.PI / 180.0F) * (float) knockback * 0.5F));
              Vec3 motion = this.getDeltaMovement();
              this.setDeltaMovement(motion.x * 0.6D, motion.y, motion.z * 0.6D);
              this.setSprinting(false);
            }

            if (par1Entity instanceof LivingEntity le) {
              EnchantmentHelper.doPostHurtEffects(le, this);
            }

            EnchantmentHelper.doPostDamageEffects(this, par1Entity);

            ItemStack itemstack = itemInUse;

            if (itemstack != null && par1Entity instanceof LivingEntity le) {
              itemstack.hurtEnemy(le, lastHurtByPlayer);
            }

            if (itemInUse.getCount() == 0) {
              setItemInUse(null);
            }
          }
        }
      }
    }
  }

  @Override
  public IZone getZoneToWork() {
    return getZone(ActionRobotWorkInArea.AreaType.WORK);
  }

  @Override
  public IZone getZoneToLoadUnload() {
    IZone zone = getZone(ActionRobotWorkInArea.AreaType.LOAD_UNLOAD);
    if (zone == null) {
      zone = getZoneToWork();
    }
    return zone;
  }

  private IZone getZone(ActionRobotWorkInArea.AreaType areaType) {
    if (linkedDockingStation != null) {
      for (StatementSlot s : linkedDockingStation.getActiveActions()) {
        if (s.statement instanceof ActionRobotWorkInArea
          && ((ActionRobotWorkInArea) s.statement).getAreaType() == areaType) {
          IZone zone = ActionRobotWorkInArea.getArea(s);

          if (zone != null) {
            return zone;
          }
        }
      }
    }

    return null;
  }

  @Override
  public boolean containsItems() {
    for (ItemStack element : inv) {
      if (element != null) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean hasFreeSlot() {
    for (ItemStack element : inv) {
      if (element == null) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void unreachableEntityDetected(Entity entity) {
    unreachableEntities.put(entity, level().getGameTime() + 1200);
  }

  @Override
  public boolean isKnownUnreachable(Entity entity) {
    if (unreachableEntities.containsKey(entity)) {
      if (unreachableEntities.get(entity) >= level().getGameTime()) {
        return true;
      } else {
        unreachableEntities.remove(entity);
        return false;
      }
    } else {
      return false;
    }
  }

  protected void onRobotHit(boolean attacked) {
    if (!level().isClientSide) {
      if (attacked) {
        convertToItems();
      } else {
        if (!wearables.isEmpty()) {
          spawnAtLocation(wearables.remove(wearables.size() - 1), 0);
          syncWearablesToClient();
        } else if (!itemInUse.isEmpty()) {
          spawnAtLocation(itemInUse, 0);
          itemInUse = ItemStack.EMPTY;
        } else {
          convertToItems();
        }
      }
    }
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (stack.isEmpty() || stack.isEmpty()) {
      return InteractionResult.FAIL;
    }

    RobotEvent.Interact robotInteractEvent = new RobotEvent.Interact(this, player, stack);
    MinecraftForge.EVENT_BUS.post(robotInteractEvent);
    if (robotInteractEvent.isCanceled()) {
      return InteractionResult.FAIL;
    }

    if (player.isShiftKeyDown() && stack.getItem() == CoreItems.WRENCH.get()) {
      RobotEvent.Dismantle robotDismantleEvent = new RobotEvent.Dismantle(this, player);
      MinecraftForge.EVENT_BUS.post(robotDismantleEvent);
      if (robotDismantleEvent.isCanceled()) {
        return InteractionResult.FAIL;
      }

      onRobotHit(false);

      if (level().isClientSide) {
        ((WrenchItem) stack.getItem()).wrenchUsed(player, 0, 0, 0);
      }
      return InteractionResult.sidedSuccess(level().isClientSide);
    } else if (wearables.size() < MAX_WEARABLES && stack.getItem().canEquip(stack, EquipmentSlot.HEAD, this)) {
      if (!level().isClientSide) {
        wearables.add(stack.split(1));
        syncWearablesToClient();
      } else {
        player.swing(hand);
      }
      return InteractionResult.sidedSuccess(level().isClientSide);
    } else if (wearables.size() < MAX_WEARABLES && stack.getItem() instanceof IRobotOverlayItem overlay && overlay.isValidRobotOverlay(stack)) {
      if (!level().isClientSide) {
        wearables.add(stack.split(1));
        syncWearablesToClient();
      } else {
        player.swing(hand);
      }
      return InteractionResult.sidedSuccess(level().isClientSide);
    } else if (wearables.size() < MAX_WEARABLES && (
      stack.is(Items.SKELETON_SKULL) ||
        stack.is(Items.ZOMBIE_HEAD) ||
        stack.is(Items.CREEPER_HEAD) ||
        stack.is(Items.PLAYER_HEAD) ||
        stack.is(Items.DRAGON_HEAD) ||
        stack.is(Items.WITHER_SKELETON_SKULL) ||
        stack.is(Items.DRAGON_HEAD) ||
        stack.is(Items.ARMOR_STAND))
    ) {
      if (!level().isClientSide) {
        ItemStack skullStack = stack.split(1);
        initSkullItem(skullStack);
        wearables.add(skullStack);
        syncWearablesToClient();
      } else {
        player.swing(hand);
      }
      return InteractionResult.sidedSuccess(level().isClientSide);
    } else {
      return super.interact(player, hand);
    }
  }

  private void initSkullItem(ItemStack skullStack) {
    if (skullStack.hasTag()) {
      CompoundTag nbttagcompound = skullStack.getTag();
      GameProfile gameProfile = null;

      if (nbttagcompound.contains("SkullOwner", CompoundTag.TAG_COMPOUND)) {
        gameProfile = NbtUtils.readGameProfile(nbttagcompound.getCompound("SkullOwner"));
      } else if (nbttagcompound.contains("SkullOwner", CompoundTag.TAG_STRING)
        && !Strings.isNullOrEmpty(nbttagcompound.getString("SkullOwner"))) {
        gameProfile = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
      }
      if (gameProfile != null && !Strings.isNullOrEmpty(gameProfile.getName())) {
        if (!gameProfile.isComplete()
          || !gameProfile.getProperties().containsKey("textures")) {
          gameProfile = level().getServer().getProfileCache()
            .get(gameProfile.getName()).orElse(gameProfile);

          Property property = Iterables.getFirst(gameProfile
            .getProperties().get("textures"), null);

          if (property == null) {
            gameProfile = level().getServer().getSessionService()
              .fillProfileProperties(gameProfile, true);
          }
        }
      }
      if (gameProfile != null && gameProfile.isComplete()
        && gameProfile.getProperties().containsKey("textures")) {
        CompoundTag profileNBT = new CompoundTag();
        NbtUtils.writeGameProfile(profileNBT, gameProfile);
        nbttagcompound.put("SkullOwner", profileNBT);
      } else {
        nbttagcompound.remove("SkullOwner");
      }
    }
  }

  private void syncWearablesToClient() {
    BCNetworkManager.sendSyncWearables(this, getId(), wearables);
  }

  private List<ItemStack> getDrops() {
    List<ItemStack> drops = new ArrayList<>();
    drops.add(RobotItem.createRobotStack(board.getNBTHandler(), battery.getEnergyStored()));
    if (itemInUse != null) {
      drops.add(itemInUse);
    }
    for (ItemStack element : inv) {
      if (element != null) {
        drops.add(element);
      }
    }
    drops.addAll(wearables);
    return drops;
  }

  private void convertToItems() {
    if (!level().isClientSide && !isRemoved()) {
      if (mainAI != null) {
        mainAI.abort();
      }
      List<ItemStack> drops = getDrops();
      for (ItemStack stack : drops) {
        spawnAtLocation(stack, 0);
      }
      remove(RemovalReason.DISCARDED);
    }

    getRegistry().killRobot(this);
  }

  @Override
  public void onChunkUnload() {
    getRegistry().unloadRobot(this);
  }

  public void setUniqueRobotId(long iRobotId) {
    robotId = iRobotId;
  }

  @Override
  public long getRobotId() {
    return robotId;
  }

  @Override
  public RobotRegistry getRegistry() {
    return (RobotRegistry) RobotManager.registry().getRegistry(level());
  }

  @Override
  public void releaseResources() {
    getRegistry().releaseResources(this);
  }

  /**
   * Tries to receive items in parameters, return items that are left after
   * the operation.
   */
  @Override
  public ItemStack receiveItem(BlockEntity tile, ItemStack stack) {
    if (currentDockingStation != null
      && currentDockingStation.index().nextTo(new BlockIndex(tile))
      && mainAI != null) {

      return mainAI.getActiveAI().receiveItem(stack);
    } else {
      return stack;
    }
  }

  @Override
  public int getTanks() {
    return 0;
  }

  @Override
  public @NotNull FluidStack getFluidInTank(int tank) {
    return null;
  }

  @Override
  public int getTankCapacity(int tank) {
    return 0;
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
    return false;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    boolean doFill = action.execute();
    int result;

    if (tank != null && !tank.isFluidEqual(resource)) {
      return 0;
    }

    if (tank == null) {
      tank = new FluidStack(resource.getFluid(), 0);
    }

    if (tank.getAmount() + resource.getAmount() <= maxFluid) {
      result = resource.getAmount();

      if (doFill) {
        tank.setAmount(tank.getAmount() + resource.getAmount());
      }
    } else {
      result = maxFluid - tank.getAmount();

      if (doFill) {
        tank.setAmount(maxFluid);
      }
    }

    if (tank != null && tank.getAmount() == 0) {
      tank = null;
    }

    return result;
  }

  @Override
  public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
    if (tank != null && tank.isFluidEqual(resource)) {
      return drain(resource.getAmount(), action);
    } else {
      return FluidStack.EMPTY;
    }
  }

  @Override
  public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
    boolean doDrain = action.execute();
    FluidStack result;

    if (tank == null) {
      result = FluidStack.EMPTY;
    } else if (tank.getAmount() <= maxDrain) {
      result = tank.copy();

      if (doDrain) {
        tank = null;
      }
    } else {
      result = tank.copy();
      result.setAmount(maxDrain);

      if (doDrain) {
        tank.setAmount(tank.getAmount() - maxDrain);
      }
    }

    if (tank != null && tank.getAmount() == 0) {
      tank = null;
    }

    return result;
  }

//	@Override
//	public boolean canFill(ForgeDirection from, Fluid fluid) {
//		return tank == null
//				|| tank.getAmount() == 0
//				|| (tank.getAmount() < maxFluid
//				&& tank.getFluid().getID() == fluid.getID());
//	}
//
//	@Override
//	public boolean canDrain(ForgeDirection from, Fluid fluid) {
//		return tank != null
//				&& tank.getAmount() != 0
//				&& tank.getFluid().getID() == fluid.getID();
//	}


//	@Override
//	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
//		return new FluidTankInfo[]{new FluidTankInfo(tank, maxFluid)};
//	}

  @Override
  public IFluidHandler getFluidHandler() {
    return new FluidTank(maxFluid, Predicate.isEqual(tank));
  }

  @Override
  public void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player) {
    info.add("Robot " + board.getNBTHandler().getID() + " (" + getBattery().getEnergyStored() + "/" + getBattery().getMaxEnergyStored() + " RF)");
    info.add(String.format("Position: %.2f, %.2f, %.2f", getX(), getY(), getZ()));
    info.add("AI tree:");
    AIRobot<?> aiRobot = mainAI;
    while (aiRobot != null) {
      info.add("- " + aiRobot.getType().id() + " (" + aiRobot.getEnergyCost() + " RF/t)");
      if (aiRobot instanceof IDebuggable) {
        ((IDebuggable) aiRobot).getDebugInfo(info, side, debugger, player);
      }
      aiRobot = aiRobot.getDelegateAI();
    }
  }

  public int receiveEnergy(int maxReceive, boolean simulate) {
    int energyReceived = getBattery().receiveEnergy(maxReceive, simulate);

    // 5 RF/t is set as the "sleep threshold" for detecting charging.
    if (!simulate && energyReceived > 5 && ticksCharging <= 25) {
      ticksCharging += 5;
    }

    return energyReceived;
  }

  public List<ItemStack> getWearables() {
    return wearables;
  }

  private void updateItem(ItemStack stack, int i, boolean held) {
    if (stack != null && !stack.isEmpty()) {
      int id = Item.getId(stack.getItem());
      // did this item not throw an exception before?
      if (!blacklistedItemsForUpdate.contains(id)) {
        try {
          stack.inventoryTick(level(), this, i, held);
//					stack.getItem().onUpdate(stack, level(), this, i, held);
        } catch (Exception e) {
          // the item threw an exception, print it and do not let it update once more
          e.printStackTrace();
          blacklistedItemsForUpdate.add(id);
        }
      }
    }
  }

  public void doInitialize(ServerPlayer p) {
    for (int i = 0; i < inv.length; ++i) {
      BCNetworkManager.sendClientSetInventory(p, this.getId(), (short) i, inv[i]);
    }

    if (currentDockingStation != null) {
      setSteamDirection(
        currentDockingStation.side.getStepX(),
        currentDockingStation.side.getStepY(),
        currentDockingStation.side.getStepZ());
    } else {
      setSteamDirection(0, -1, 0);
    }
  }

  public void clientSetInventory(int slot, ItemStack stack) {
    inv[slot] = stack;
  }

  public void doItemActivate(boolean activate) {
    itemActive = activate;
    itemActiveStage = 0;
    lastUpdateTime = new Date().getTime();

    if (!itemActive) {
      setSteamDirection(0, -1, 0);
    }
  }

  public void doSyncWearables(List<ItemStack> data) {
    wearables.clear();
    wearables.addAll(data);
  }

  @Override
  public void clearContent() {

  }
}
