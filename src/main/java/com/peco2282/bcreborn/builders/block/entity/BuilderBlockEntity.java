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

import com.peco2282.bcreborn.api.core.IPathProvider;
import java.util.Iterator;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;
import com.peco2282.bcreborn.api.tiles.IControllable;
import net.minecraft.world.level.GameType;
import com.peco2282.bcreborn.common.inventory.InvUtils;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.internal.IBoxProvider;
import com.peco2282.bcreborn.common.blueprint.BptBuilderBlueprint;
import com.peco2282.bcreborn.common.blueprint.BptBuilderBase;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.builders.blueprints.RecursiveBlueprintBuilder;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.block.BuilderBlock;
import com.peco2282.bcreborn.builders.menu.BuilderMenu;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.BlockEntityBuffer;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.Template;
import com.peco2282.bcreborn.common.blueprint.BptBuilderTemplate;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Port of the original {@code TileBuilder}: builds the blueprint or template in slot 0 with the building materials
 * in slots 1..27.
 * <p>
 * The blueprint is split into the main structure and its sub-blueprints ({@link RecursiveBlueprintBuilder}); each
 * part gets its own {@link BptBuilderBase} and box. When everything is built the blueprint moves into the first free
 * material slot (or drops). Off / On come from gates ({@link IControllable}).
 * <p>
 * With path markers next to the builder ({@link IPathProvider}), the markers are taken up and the blueprint is
 * built again and again along their path, each copy right behind the previous one (original {@code PathIterator}).
 */
public class BuilderBlockEntity extends AbstractBuilderBlockEntity implements MenuProvider, ILEDProvider, IBoxProvider,
  IControllable, IHasWork {
  /** Original {@code POWER_ACTIVATION}: the builder only works with more energy than this (or in creative). */
  private static final int POWER_ACTIVATION = 25;

  private final SimpleInventory inv = new SimpleInventory(28, "Builder", 64);
  private final Box box = new Box();
  private List<RequirementItemStack> requiredToBuild = new ArrayList<>();
  /** Original {@code initNBT}: the saved build state, applied on {@link #initialize()}. */
  @Nullable
  private CompoundTag initNBT = null;
  private boolean done = true;
  private boolean isBuilding = false;
  private boolean shouldUpdateRequirements;
  @Nullable
  private BptBuilderBase currentBuilder;
  @Nullable
  private RecursiveBlueprintBuilder recursiveBuilder;
  /** Original {@code path}: the positions of the path markers that stood next to the builder. */
  @Nullable
  private List<BlockPos> path;
  @Nullable
  private PathIterator currentPathIterator;

  public BuilderBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BuildersBlockEntityTypes.BUILDER.get(), p_155229_, p_155230_);
    box.kind = Box.Kind.STRIPES;
    setBattery(new EnergyStorage(10000, 1000, 1000));
  }

  /**
   * Original {@code initialize()}: restores the saved build state, then starts the blueprint.
   */
  @Override
  public void initialize() {
    super.initialize();
    cache = BlockEntityBuffer.makeBuffer(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), false);
    if (level.isClientSide) {
      return;
    }
    if (initNBT != null) {
      iterateBpt(true);
      if (initNBT.contains("iterator")) {
        // Original: walk the path again up to the copy that was being built when the world was saved.
        BlockPos expected = NbtUtils.readBlockPos(initNBT.getCompound("iterator"));
        while (!done && currentBuilder != null && currentPathIterator != null) {
          if (currentPathIterator.position().equals(expected)) break;
          iterateBpt(true);
        }
      }
      if (currentBuilder != null && initNBT.contains("builderState")) {
        currentBuilder.loadBuildStateToNBT(initNBT.getCompound("builderState"), this);
      }
      initNBT = null;
    }
    box.kind = Box.Kind.STRIPES;
    // Original: a path provider next to the builder hands over its path and is removed.
    takePathFromMarkers();
    iterateBpt(false);
  }

  private void takePathFromMarkers() {
    for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-1, -1, -1), worldPosition.offset(1, 1, 1))) {
      if (level.getBlockEntity(pos) instanceof IPathProvider provider) {
        path = new ArrayList<>(provider.getPath());
        provider.removeFromWorld();
        currentPathIterator = null;
        setChanged();
        return;
      }
    }
  }

  /** Original {@code instanciateBluePrintBuilder()}: a builder for the blueprint in slot 0 at the given place. */
  @Nullable
  private BptBuilderBase instantiateBlueprintBuilder(int x, int y, int z, Direction direction) {
    BlueprintBase bpt = BlueprintItem.loadBlueprint(inv.getItem(0));
    if (bpt == null) return null;
    bpt = bpt.adjustToWorld(level, x, y, z, direction);
    if (bpt instanceof Blueprint blueprint) return new BptBuilderBlueprint(blueprint, level, x, y, z);
    if (bpt instanceof Template) return new BptBuilderTemplate(bpt, level, x, y, z);
    return null;
  }

  /** The direction the builder builds towards (original: the block metadata's opposite). */
  private Direction buildDirection() {
    return getBlockState().getValue(BuilderBlock.HORIZONTAL_FACING).getOpposite();
  }

  public boolean isBuildingBlueprint() {
    return inv.getItem(0).getItem() instanceof BlueprintItem;
  }

  /**
   * Original {@code iterateBpt()}: starts the blueprint in slot 0, moves on to the next part when one is built, and
   * hands the blueprint back when everything is done.
   */
  public void iterateBpt(boolean forceIterate) {
    if (!isBuildingBlueprint()) {
      if (box.isInitialized() || currentBuilder != null) {
        currentBuilder = null;
        recursiveBuilder = null;
        currentPathIterator = null;
        box.reset();
        scheduleRequirementUpdate();
        setChanged();
      }
      return;
    }

    if ((currentBuilder == null || currentBuilder.isDone(this) || forceIterate) && path != null && path.size() > 1) {
      iteratePath();
    } else if (currentBuilder == null || currentBuilder.isDone(this) || forceIterate) {
      if (currentBuilder != null && currentBuilder.isDone(this) && recursiveBuilder != null) {
        currentBuilder.postProcessing(level);
        currentBuilder = recursiveBuilder.nextBuilder();
        scheduleRequirementUpdate();
      } else {
        BlueprintBase bpt = BlueprintItem.loadBlueprint(inv.getItem(0));
        if (bpt != null) {
          BlockPos pos = getBlockPos();
          recursiveBuilder = new RecursiveBlueprintBuilder(bpt, level, pos.getX(), pos.getY(), pos.getZ(), buildDirection());
          currentBuilder = recursiveBuilder.nextBuilder();
          scheduleRequirementUpdate();
        } else {
          currentBuilder = null;
        }
      }

      if (currentBuilder == null) {
        done = true;
      } else {
        box.initialize(currentBuilder);
        done = false;
      }
      setChanged();
    }

    if (done && !inv.getItem(0).isEmpty()) {
      ItemStack blueprint = inv.getItem(0);
      boolean dropBlueprint = true;
      for (int i = 1; i < inv.getContainerSize(); ++i) {
        if (inv.getItem(i).isEmpty()) {
          inv.setItem(i, blueprint);
          dropBlueprint = false;
          break;
        }
      }
      if (dropBlueprint) {
        InvUtils.dropItems(level, blueprint, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
      }
      inv.setItem(0, ItemStack.EMPTY);
      recursiveBuilder = null;
      currentPathIterator = null;
      box.reset();
      setChanged();
    }
  }

  /** Original {@code iterateBpt()}, path branch: the next copy on this stretch of the path, or the next stretch. */
  private void iteratePath() {
    if (currentPathIterator == null) {
      Iterator<BlockPos> it = path.iterator();
      BlockPos start = it.next();
      currentPathIterator = new PathIterator(start, it, buildDirection());
    }
    if (currentBuilder != null && currentBuilder.isDone(this)) {
      currentBuilder.postProcessing(level);
    }
    currentBuilder = currentPathIterator.next();
    if (currentBuilder != null) {
      box.reset();
      box.initialize(currentBuilder);
    } else {
      currentPathIterator = currentPathIterator.iterate();
    }
    done = currentPathIterator == null;
    scheduleRequirementUpdate();
    setChanged();
  }

  /**
   * Original {@code TileBuilder.PathIterator}: walks from one path marker to the next in steps of a tenth of a
   * block and offers a builder wherever the blueprint no longer overlaps the previous copy.
   */
  private class PathIterator {
    private final Iterator<BlockPos> remaining;
    private final BlockPos to;
    private final double cx;
    private final double cy;
    private final double cz;
    private final Direction direction;
    private float ix;
    private float iy;
    private float iz;
    private double lastDistance;
    @Nullable
    private AABB oldBoundingBox;

    PathIterator(BlockPos from, Iterator<BlockPos> it, Direction initialDirection) {
      to = it.next();
      remaining = it;
      double dx = to.getX() - from.getX();
      double dy = to.getY() - from.getY();
      double dz = to.getZ() - from.getZ();
      double size = Math.sqrt(dx * dx + dy * dy + dz * dz);
      cx = dx / size / 10;
      cy = dy / size / 10;
      cz = dz / size / 10;
      ix = from.getX();
      iy = from.getY();
      iz = from.getZ();
      lastDistance = distanceToEnd();
      if (dx == 0 && dz == 0) {
        direction = initialDirection;
      } else if (Math.abs(dx) > Math.abs(dz)) {
        direction = dx > 0 ? Direction.EAST : Direction.WEST;
      } else {
        direction = dz > 0 ? Direction.SOUTH : Direction.NORTH;
      }
    }

    private double distanceToEnd() {
      return (ix - to.getX()) * (ix - to.getX()) + (iy - to.getY()) * (iy - to.getY()) + (iz - to.getZ()) * (iz - to.getZ());
    }

    /** Where the walk stands; saved so a reloaded builder can find its copy again. */
    BlockPos position() {
      return new BlockPos((int) ix, (int) iy, (int) iz);
    }

    /** @return the builder for the next copy on this stretch, or null when the stretch is used up */
    @Nullable
    BptBuilderBase next() {
      while (true) {
        BptBuilderBase bpt = instantiateBlueprintBuilder(Math.round(ix), Math.round(iy), Math.round(iz), direction);
        if (bpt == null) return null;
        AABB boundingBox = bpt.getBoundingBox();
        if (oldBoundingBox == null || !collision(oldBoundingBox, boundingBox)) {
          oldBoundingBox = boundingBox;
          return bpt;
        }
        ix += cx;
        iy += cy;
        iz += cz;
        double distance = distanceToEnd();
        if (distance > lastDistance) return null;
        lastDistance = distance;
      }
    }

    /** @return the iterator for the next stretch, or null at the end of the path */
    @Nullable
    PathIterator iterate() {
      if (!remaining.hasNext()) return null;
      PathIterator next = new PathIterator(to, remaining, direction);
      next.oldBoundingBox = oldBoundingBox;
      return next;
    }

    private boolean collision(AABB left, AABB right) {
      if (left.maxX < right.minX || left.minX > right.maxX) return false;
      if (left.maxY < right.minY || left.minY > right.maxY) return false;
      return !(left.maxZ < right.minZ) && !(left.minZ > right.maxZ);
    }
  }

  /**
   * Original {@code updateEntity()}.
   */
  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      tickClientBuildingItems();
      return;
    }
    // Original: super.updateEntity() (TileAbstractBuilder) moves the building items and places their blocks.
    updateBuildingItems();

    if (shouldUpdateRequirements && level.getGameTime() % 10 == 0) {
      updateRequirements();
      shouldUpdateRequirements = false;
    }

    if ((currentBuilder == null || currentBuilder.isDone(this)) && box.isInitialized()) {
      box.reset();
      setChanged();
      return;
    }

    iterateBpt(false);

    if (getControlMode() != IControllable.Mode.Off
      && (isCreative() || getBattery().getEnergyStored() > POWER_ACTIVATION)) {
      build();
    }

    if (!isBuilding && isBuildingBlueprint()) {
      scheduleRequirementUpdate();
    }
    isBuilding = isBuildingBlueprint();
  }

  private boolean isCreative() {
    return level.getServer() != null && level.getServer().getWorldData().getGameType() == GameType.CREATIVE;
  }

  /** Original {@code build()}: places (or launches) the next block of the current part. */
  public void build() {
    if (currentBuilder != null && currentBuilder.buildNextSlot(level, this, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) {
      scheduleRequirementUpdate();
    }
  }

  public void scheduleRequirementUpdate() {
    shouldUpdateRequirements = true;
  }

  @Nullable
  private List<RequirementItemStack> computeRequirements() {
    if (currentBuilder instanceof BptBuilderBlueprint blueprint) {
      blueprint.initialize();
      return blueprint.getNeededItems();
    }
    return null;
  }

  /** Original {@code updateRequirements()}: sends the missing items to every player with the GUI open. */
  private void updateRequirements() {
    List<RequirementItemStack> requirements = computeRequirements();
    List<RequirementItemStack> list = requirements == null ? List.of() : requirements;
    for (Player p : guiWatchers) {
      if (p instanceof ServerPlayer serverPlayer) {
        BCNetworkManager.sendSyncBuilderRequirements(serverPlayer, getBlockPos(), list);
      }
    }
  }

  @Nullable
  public BptBuilderBase getBlueprint() {
    return currentBuilder;
  }

  public boolean isDone() {
    return done;
  }

  @Override
  public boolean hasWork() {
    return !done;
  }

  @Override
  public Box getBox() {
    return box;
  }

  /** Original {@code isBuildingMaterialSlot()}: slot 0 holds the blueprint. */
  @Override
  public boolean isBuildingMaterial(int i) {
    return i != 0;
  }

  @Override
  public Mode getControlMode() {
    return mode == null ? Mode.On : mode;
  }

  @Override
  public void setControlMode(Mode mode) {
    this.mode = mode;
    setChanged();
  }

  /** Original {@code acceptsControlMode()}: Off and On only. */
  @Override
  public boolean acceptsControlMode(Mode mode) {
    return mode == Mode.Off || mode == Mode.On;
  }

  public Container getInventory() {
    return inv;
  }

  @Override
  public List<ItemStack> getInventoryList() {
    List<ItemStack> list = new LinkedList<>();
    for (int i = 1; i < inv.getContainerSize(); i++) {
      list.add(inv.getItem(i));
    }
    return list;
  }

  public SimpleInventory getBuilderInventory() {
    return inv;
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readTag(nbt);
    if (nbt.contains("box")) {
      box.initialize(nbt.getCompound("box"));
    }
    done = nbt.getBoolean("done");
    if (nbt.contains("path", Tag.TAG_LIST)) {
      path = new ArrayList<>();
      ListTag list = nbt.getList("path", Tag.TAG_COMPOUND);
      for (int i = 0; i < list.size(); i++) path.add(NbtUtils.readBlockPos(list.getCompound(i)));
    }
    // Original: the rest of the load happens in initialize().
    initNBT = nbt.getCompound("bptBuilder").copy();
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeTag(nbt);
    if (box.isInitialized()) {
      CompoundTag boxTag = new CompoundTag();
      box.writeTag(boxTag);
      nbt.put("box", boxTag);
    }
    nbt.putBoolean("done", done);
    if (path != null) {
      ListTag list = new ListTag();
      for (BlockPos pos : path) list.add(NbtUtils.writeBlockPos(pos));
      nbt.put("path", list);
    }
    CompoundTag bptNBT = new CompoundTag();
    if (currentPathIterator != null) {
      bptNBT.put("iterator", NbtUtils.writeBlockPos(currentPathIterator.position()));
    } else if (initNBT != null && initNBT.contains("iterator")) {
      bptNBT.put("iterator", initNBT.getCompound("iterator"));
    }
    if (currentBuilder != null) {
      CompoundTag builderState = new CompoundTag();
      currentBuilder.saveBuildStateToNBT(builderState, this);
      bptNBT.put("builderState", builderState);
    } else if (initNBT != null && initNBT.contains("builderState")) {
      bptNBT.put("builderState", initNBT.getCompound("builderState"));
    }
    nbt.put("bptBuilder", bptNBT);
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    super.writeData(data);
    data.writeBoolean(done);
    data.writeBoolean(isBuilding);
    box.writeData(data);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    super.readData(data);
    done = data.readBoolean();
    isBuilding = data.readBoolean();
    box.readData(data);
    if (box.isInitialized()) {
      box.createLaserData();
    }
  }

  /** Original {@code updateRequirementsOnGuiOpen()}. */
  public void updateRequirementsOnGuiOpen(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      List<RequirementItemStack> requirements = computeRequirements();
      BCNetworkManager.sendSyncBuilderRequirements(serverPlayer, getBlockPos(), requirements == null ? List.of() : requirements);
    }
  }

  public void addGuiWatcher(Player player) {
    guiWatchers.add(player);
  }

  public void removeGuiWatcher(Player player) {
    guiWatchers.remove(player);
  }

  public List<RequirementItemStack> getRequiredItems() {
    return requiredToBuild;
  }

  public void setItemRequirements(List<RequirementItemStack> requirements) {
    this.requiredToBuild = new ArrayList<>(requirements);
  }

  @Override
  public int getLEDLevel(int led) {
    return isBuilding ? 15 : 0;
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornbuilders.builder");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
    return new BuilderMenu(windowId, inventory, this);
  }

  @Override
  public AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new BuilderMenu(p_58627_, p_58628_, this);
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

  public void eraseTank(int tankId) {
    List<Tank> tanks = getFluidTanks();
    if (tankId >= 0 && tankId < tanks.size()) {
      tanks.get(tankId).setFluid(net.minecraftforge.fluids.FluidStack.EMPTY);
      setChanged();
    }
  }

  /** Original {@code getNeededItems()}: the client shows the synced list, the server asks the current builder. */
  public List<RequirementItemStack> getNeededItems() {
    if (level != null && !level.isClientSide) {
      List<RequirementItemStack> requirements = computeRequirements();
      return requirements == null ? List.of() : requirements;
    }
    return List.copyOf(requiredToBuild);
  }
}
