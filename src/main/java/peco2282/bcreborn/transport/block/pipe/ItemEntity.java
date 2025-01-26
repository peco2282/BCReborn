package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class ItemEntity implements Entity {
  private ItemStack stack;
  public static final ItemEntity EMPTY = wrap(ItemStack.EMPTY);

  public static final Function<ItemStack, ItemEntity> FACTORY = ItemEntity::wrap;

  private static ItemEntity wrap(ItemStack stack) {
    return new ItemEntity(stack);
  }

  public ItemEntity(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public CompoundTag serialize(HolderLookup.Provider registryAccess) {
    return (CompoundTag) stack.save(registryAccess, new CompoundTag());
  }

  @Override
  public void deserialize(HolderLookup.Provider registryAccess, CompoundTag tag) {
    stack = ItemStack.parseOptional(registryAccess, tag);
  }
}
