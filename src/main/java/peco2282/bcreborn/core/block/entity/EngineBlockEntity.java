package peco2282.bcreborn.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.block.IEngine;
import peco2282.bcreborn.api.enums.EnumEngineType;
import peco2282.bcreborn.api.enums.EnumPowerStage;
import peco2282.bcreborn.api.mj.MJCapailityHelper;
import peco2282.bcreborn.api.mj.impl.MJEngineConnector;
import peco2282.bcreborn.core.block.menu.EngineIronMenu;
import peco2282.bcreborn.core.block.menu.EngineStoneMenu;
import peco2282.bcreborn.lib.block.entity.NeptuneContainerBlockEntity;

@SuppressWarnings("UnnecessaryBoxing")
public class EngineBlockEntity extends NeptuneContainerBlockEntity implements MenuProvider, IEngine {
  private boolean isActive;
  private EnumPowerStage stage;
  private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

  private final MJCapailityHelper helper = new MJCapailityHelper(new MJEngineConnector(), null, null);

  private long timer = 0;

  public EngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    this(BCCoreBlockEntityTypes.ENGINE.get(), p_155229_, p_155230_);
  }

  protected EngineBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    this.isActive = state.getValue(BCProperties.ACTIVE);
    this.stage = state.getValue(BCProperties.ENERGY_STAGE);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, EngineBlockEntity entity) {
    entity.update(pos, entity);
  }

  private void update(BlockPos pos,  EngineBlockEntity entity) {
    if (level == null) return;
    BlockState state = level.getBlockState(pos);
    this.stage = state.getValue(BCProperties.ENERGY_STAGE);
    this.isActive = state.getValue(BCProperties.ACTIVE);
//    System.out.println("update " + state);
    if (this.isActive) {
      // TODO Change algo
      timer = entity.timer;
      int curr = state.getValue(BCProperties.ENGINE_MODEL);
      if (timer > stage.threshold()) state = state.setValue(BCProperties.ENERGY_STAGE, stage = stage.next());
      state = state.setValue(BCProperties.ENGINE_MODEL, Integer.valueOf((curr % 9) + 1));
      level.setBlock(pos, state, 2);
    }
  }

  @Override
  protected void loadAdditional(CompoundTag p_331149_, HolderLookup.Provider p_333170_) {
    super.loadAdditional(p_331149_, p_333170_);
    this.isActive = p_331149_.getBoolean("Active");
    this.stage = EnumPowerStage.valueOf(p_331149_.getString("Stage"));
  }

  @Override
  protected void saveAdditional(CompoundTag p_187471_, HolderLookup.Provider p_327783_) {
    super.saveAdditional(p_187471_, p_327783_);
    p_187471_.putBoolean("Active", this.isActive);
    p_187471_.putString("Stage", this.stage.name());
  }

  @Override
  protected Component getDefaultName() {
    return Component.literal(stage.getModelName() + " Engine");
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> p_330472_) {
    this.items = p_330472_;
  }

  @Override
  protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new EngineIronMenu(p_58627_, p_58628_, null);
  }

  @Override
  public boolean isActive(Level level, BlockPos pos, BlockState state) {
    return isActive;
  }

  @Override
  public int getContainerSize() {
    return 0;
  }

  @Override
  public long perTick(Level level, BlockState state) {
    EnumEngineType type = state.getValue(BCProperties.ENGINE_TYPE);
    return (long) stage.power() * type.output;
  }

  @Override
  public boolean canGenerate() {
    return isActive;
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
    return helper.getCapability(this, cap, side);
  }

  public static class Provider implements MenuProvider {
    EnumEngineType type;

    public Provider(EnumEngineType type) {
      this.type = type;
    }

    @Override
    public Component getDisplayName() {
      return switch (type) {
        case WOOD -> Component.literal("wood");
        case STONE -> Component.literal("stone");
        case IRON -> Component.literal("iron");
        case CREATIVE -> Component.literal("creative");
      };
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
      return switch (type) {
        case WOOD, CREATIVE -> null;
        case STONE -> new EngineStoneMenu(p_39954_, p_39955_, null);
        case IRON -> new EngineIronMenu(p_39954_, p_39955_, null);
      };
    }
  }
}
