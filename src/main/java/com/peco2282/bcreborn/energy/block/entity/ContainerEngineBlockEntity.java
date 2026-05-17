package com.peco2282.bcreborn.energy.block.entity;

import com.peco2282.bcreborn.common.ContainerBlockEntity;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ContainerEngineBlockEntity<E extends ContainerEngineBlockEntity<E>> extends EngineBlockEntity<E>
    implements WorldlyContainer, Container, ContainerBlockEntity, MenuProvider {
  protected final SimpleInventory inv;
  protected final int[] defaultSlotArray;
  public ContainerEngineBlockEntity(
      BlockEntityType<E> p_155228_,
      BlockPos p_155229_,
      BlockState p_155230_,
      int invSize
  ) {
    super(p_155228_, p_155229_, p_155230_);
    inv = new SimpleInventory(invSize, "Engine", 64);
    defaultSlotArray = new int[invSize];
    for (int i = 0; i < invSize; i++) {
      defaultSlotArray[i] = i;
    }
  }

  @Override
  public Component getName() {
    return Component.literal(inv.getName());
  }

  // Container
  @Override
  public int getContainerSize() {
    return inv.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int p_18941_) {
    return inv.getItem(p_18941_);
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
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public boolean stillValid(Player p_18946_) {
    return level.getBlockEntity(worldPosition).getClass().equals(getClass());
  }

  // WorldlyContainer
  @Override
  public int[] getSlotsForFace(Direction p_19238_) {
    return p_19238_ == orientation ? defaultSlotArray : new int[0];
  }

  @Override
  public boolean canPlaceItemThroughFace(int p_19235_, ItemStack p_19236_, @Nullable Direction p_19237_) {
    return p_19237_ != orientation;
  }

  @Override
  public boolean canTakeItemThroughFace(int p_19233_, ItemStack p_19234_, Direction p_19235_) {
    return p_19235_ != orientation;
  }

  @Override
  public Component getDisplayName() {
    return Component.empty();
  }
}
