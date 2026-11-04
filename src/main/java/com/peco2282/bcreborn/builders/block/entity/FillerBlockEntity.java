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
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.api.tiles.IHasWork;
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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

/**
 * Port of the original {@code buildcraft.builders.TileFiller}.
 * <p>
 * Takes a landmark box, then builds the selected filler pattern into it with {@link BptBuilderTemplate}
 * (which charges its own break / build energy). Works while at least {@link #POWER_ACTIVATION} RF is stored and
 * accepts the Off / On / Loop control modes from gates.
 */
public class FillerBlockEntity extends AbstractBuilderBlockEntity implements MenuProvider, IBlockEntityContainer, ILEDProvider, IAreaProvider, IBoxProvider, IBoxesProvider, IDropControlInventory, IControllable, IHasWork {
  /** Original: the filler only works while at least this much energy is stored (it is not consumed). */
  public static final int POWER_ACTIVATION = 500;
  private final SimpleInventory inv = new SimpleInventory(28, "Filler", 64);
  public int currentPattern = 0;
  public Box box = new Box();
  public boolean excavate = true;
  private boolean isWorking = false;
  private boolean done = false;
  @Nullable
  private BptBuilderTemplate currentTemplate;
  /** Parameters of the current pattern; never null (patterns read {@code parameters.length}). */
  private IStatementParameter[] patternParameters = new IStatementParameter[0];

  public FillerBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.FILLER.get(), pos, state);
    setBattery(new EnergyStorage(10000, 1000, 1000));
    // Original default pattern: PatternFill
    currentPattern = indexOf(BCFillerPatterns.FILL.getId());
    initPatternParameters();
  }

  @Override
  public void initialize() {
    super.initialize();
    cache = BlockEntityBuffer.makeBuffer(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), false);
    if (!box.isInitialized()) {
      // Original TileFiller.initialize(): take a landmark box that this filler touches.
      IAreaProvider provider = level.isClientSide ? null : Utils.getNearbyAreaProvider(level, worldPosition);
      if (provider != null) {
        box.initialize(provider.xMin(), provider.yMin(), provider.zMin(), provider.xMax(), provider.yMax(), provider.zMax());
        if (box.isInitialized()) {
          box.createLaserData();
          provider.removeFromWorld();
        }
      }
    } else {
      box.createLaserData();
    }
  }

  /**
   * Original {@code TileFiller.updateEntity()}.
   */
  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) return;
    // Original: super.updateEntity() (TileAbstractBuilder) moves the building items and places their blocks.
    updateBuildingItems();

    if (getControlMode() == IControllable.Mode.Off || !box.isInitialized()
      || getBattery().getEnergyStored() < POWER_ACTIVATION) {
      setWorking(false);
      return;
    }

    if (done) {
      if (getControlMode() == IControllable.Mode.Loop) {
        done = false;
      } else {
        setWorking(false);
        return;
      }
    }

    if (currentTemplate == null) {
      initTemplate();
    }

    if (currentTemplate != null) {
      currentTemplate.buildNextSlot(level, this, pos.getX(), pos.getY(), pos.getZ());
      setWorking(hasBuildingItemsInFlight());
      if (currentTemplate.isDone(this)) {
        done = true;
        currentTemplate = null;
        setWorking(false);
        setChanged();
      }
    } else {
      setWorking(false);
    }
  }

  private void setWorking(boolean working) {
    if (isWorking != working) {
      isWorking = working;
      setChanged();
    }
  }

  /**
   * Original {@code initTemplate()}: builds the template of the current pattern with its parameters and the
   * excavate flag.
   */
  private void initTemplate() {
    IFillerPattern pattern = getPatternOrNull();
    if (pattern instanceof FillerPattern fp && box.isInitialized() && box.sizeX() > 0 && box.sizeY() > 0 && box.sizeZ() > 0) {
      currentTemplate = fp.getTemplateBuilder(box, level, patternParameters);
      currentTemplate.blueprint.excavate = excavate;
    }
  }

  /**
   * Original {@code initPatternParameters()}: the first {@code minParameters()} slots get the pattern's defaults.
   */
  private void initPatternParameters() {
    IFillerPattern pattern = getPatternOrNull();
    if (pattern == null) {
      patternParameters = new IStatementParameter[0];
      return;
    }
    patternParameters = new IStatementParameter[pattern.maxParameters()];
    for (int i = 0; i < pattern.minParameters(); i++) {
      patternParameters[i] = pattern.createParameter(i);
    }
  }

  /**
   * Original {@code setPattern()}: a different pattern restarts the filler with default parameters.
   */
  public void setPattern(int index) {
    int size = BCFillerPatterns.collection().size();
    if (size == 0 || index < 0 || index >= size || index == currentPattern) {
      return;
    }
    currentPattern = index;
    currentTemplate = null;
    done = false;
    initPatternParameters();
    setChanged();
  }

  public IStatementParameter[] getPatternParameters() {
    return patternParameters.clone();
  }

  /**
   * Original {@code setParameters} command: replaces one parameter and restarts the pattern.
   */
  public void setPatternParameter(int index, @Nullable IStatementParameter parameter) {
    if (index < 0 || index >= patternParameters.length) return;
    patternParameters[index] = parameter;
    currentTemplate = null;
    done = false;
    setChanged();
  }

  public boolean isExcavate() {
    return excavate;
  }

  /**
   * Original {@code setFlags} command.
   */
  public void setExcavate(boolean excavate) {
    if (this.excavate == excavate) return;
    this.excavate = excavate;
    currentTemplate = null;
    done = false;
    setChanged();
  }

  public boolean isDone() {
    return done;
  }

  @Nullable
  private IFillerPattern getPatternOrNull() {
    var patterns = BCFillerPatterns.collection();
    if (currentPattern < 0 || currentPattern >= patterns.size()) return null;
    return patterns.get(currentPattern).getValue();
  }

  private static int indexOf(@Nullable ResourceLocation id) {
    if (id == null) return 0;
    var patterns = BCFillerPatterns.collection();
    for (int i = 0; i < patterns.size(); i++) {
      if (patterns.get(i).getKey().location().equals(id)) return i;
    }
    return 0;
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

  @Override
  public boolean acceptsControlMode(Mode mode) {
    return mode == Mode.On || mode == Mode.Off || mode == Mode.Loop;
  }

  @Override
  public boolean hasWork() {
    return !done && getControlMode() != Mode.Off;
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readTag(nbt);
    // Pattern is stored by id (original: getUniqueTag()); "delta" is the older registry-index format.
    if (nbt.contains("pattern", Tag.TAG_STRING)) {
      currentPattern = indexOf(ResourceLocation.tryParse(nbt.getString("pattern")));
    } else if (nbt.contains("delta")) {
      currentPattern = nbt.getInt("delta");
    }
    initPatternParameters();
    if (nbt.contains("pp", Tag.TAG_LIST)) {
      readParameters(nbt.getList("pp", Tag.TAG_COMPOUND));
    }
    if (nbt.contains("box")) {
      box.initialize(nbt.getCompound("box"));
    }
    done = nbt.getBoolean("done");
    excavate = !nbt.contains("excavate") || nbt.getBoolean("excavate");
    currentTemplate = null;
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeTag(nbt);
    IFillerPattern pattern = getPatternOrNull();
    if (pattern != null) {
      nbt.putString("pattern", BCFillerPatterns.collection().get(currentPattern).getKey().location().toString());
    }
    nbt.put("pp", writeParameters());
    nbt.putBoolean("excavate", excavate);
    if (box.isInitialized()) {
      CompoundTag boxNbt = new CompoundTag();
      box.writeTag(boxNbt);
      nbt.put("box", boxNbt);
    }
    nbt.putBoolean("done", done);
  }

  /**
   * Parameters are saved per index; on load each one is recreated with the pattern's own
   * {@code createParameter(i)} and then read, so no parameter registry is needed.
   */
  private ListTag writeParameters() {
    ListTag list = new ListTag();
    for (int i = 0; i < patternParameters.length; i++) {
      if (patternParameters[i] == null) continue;
      CompoundTag tag = new CompoundTag();
      tag.putInt("index", i);
      patternParameters[i].writeToNBT(tag);
      list.add(tag);
    }
    return list;
  }

  private void readParameters(ListTag list) {
    IFillerPattern pattern = getPatternOrNull();
    if (pattern == null) return;
    for (int i = 0; i < list.size(); i++) {
      CompoundTag tag = list.getCompound(i);
      int index = tag.getInt("index");
      if (index < 0 || index >= patternParameters.length) continue;
      IStatementParameter parameter = pattern.createParameter(index);
      if (parameter != null) {
        parameter.readFromNBT(tag);
        patternParameters[index] = parameter;
      }
    }
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
    data.writeBoolean(done);
    data.writeBoolean(excavate);
    data.writeNbt(wrapParameters());
    box.writeData(data);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    super.readData(data);
    int pattern = data.readInt();
    if (pattern != currentPattern) {
      currentPattern = pattern;
      initPatternParameters();
    }
    isWorking = data.readBoolean();
    done = data.readBoolean();
    excavate = data.readBoolean();
    CompoundTag parameters = data.readNbt();
    if (parameters != null) {
      readParameters(parameters.getList("pp", Tag.TAG_COMPOUND));
    }
    box.readData(data);
    if (level != null && level.isClientSide) {
      if (box.isInitialized()) {
        box.createLaserData();
      }
    }
  }

  private CompoundTag wrapParameters() {
    CompoundTag tag = new CompoundTag();
    tag.put("pp", writeParameters());
    return tag;
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
    // Original: LED 0 = done, LED 1 = building
    return (led == 0 ? done : isWorking) ? 15 : 0;
  }

  @Override
  public AABB getRenderBoundingBox() {
    return new Box(this).extendToEncompass(box).getBoundingBox();
  }

  public IFillerPattern getPattern() {
    return BCFillerPatterns.getCurrentlySelected(currentPattern);
  }

  public void nextPattern() {
    int size = BCFillerPatterns.collection().size();
    if (size > 0) setPattern((currentPattern + 1) % size);
  }

  public void previousPattern() {
    int size = BCFillerPatterns.collection().size();
    if (size > 0) setPattern((currentPattern - 1 + size) % size);
  }
}
