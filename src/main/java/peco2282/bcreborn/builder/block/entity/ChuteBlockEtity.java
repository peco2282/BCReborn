package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.BCBaseContainerBlockEntity;

import java.util.function.BooleanSupplier;

public class ChuteBlockEtity extends BCBaseContainerBlockEntity {
  public static final int SLOT = 4;
  private static final String COOLTIME = "Cooltime";
  private final NonNullList<ItemStack> items = NonNullList.createWithCapacity(SLOT);
  private static final int COOLTIME_TICK = 10; // ticks
  private final CT cooltime = new CT(COOLTIME_TICK);
  private Direction facing;
  private static class CT {
    int ct;
    public CT(int ct) {
      this.ct = ct;
    }
    private void reset() {
      this.ct = COOLTIME_TICK;
    }
    private void dec() {
      --ct;
    }
    private void set(int ct) {
      this.ct = ct;
    }

    private int get() {
      return ct;
    }
  }

  public ChuteBlockEtity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.CHUTE.get(), p_155229_, p_155230_);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, ChuteBlockEtity entity) {
    if (entity.isEmpty()) return;
    if (entity.isCooltime()) {
      entity.cooltime.dec();
      return;
    }
    entity.cooltime.reset();
  }

  @Override
  protected void loadAdditional(CompoundTag p_335335_, HolderLookup.Provider p_329555_) {
    super.loadAdditional(p_335335_, p_329555_);
    this.cooltime.set(p_335335_.getInt(COOLTIME));
    ContainerHelper.loadAllItems(p_335335_, this.items, p_329555_);
  }

  @Override
  protected void saveAdditional(CompoundTag p_187461_, HolderLookup.Provider p_335192_) {
    super.saveAdditional(p_187461_, p_335192_);
    p_187461_.putInt(COOLTIME, cooltime.get());
    ContainerHelper.saveAllItems(p_187461_, this.items, true, p_335192_);
  }

  public boolean isCooltime() {
    return this.cooltime.get() > 0 && this.cooltime.get() <= COOLTIME_TICK;
  }

  @Override
  protected Component getDefaultName() {
    return Component.literal("IronHopper");
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return items;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> p_330472_) {
    items.clear();
    items.addAll(p_330472_);
  }

  @Override
  protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return null;
  }

  @Override
  public int getContainerSize() {
    return items.size();
  }

  public void inside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (entity instanceof ItemEntity item && !item.getItem().isEmpty()) {
      moveItems(level, pos, state, this, () -> addItem(this, item));
    }
  }

  private void setCooldown(int ct) {
    this.cooltime.set(ct);
  }

  private boolean inventoryFull() {
    for (ItemStack itemstack : this.items) {
      if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
        return false;
      }
    }
    return true;
  }

  public static boolean addItem(Container inventory, ItemEntity entity) {
    boolean flag = false;
    ItemStack stack = entity.getItem().copy();
    return false;
  }

  public void moveItems(Level level, BlockPos pos, BlockState state, BlockEntity entity, BooleanSupplier supplier) {}
}
