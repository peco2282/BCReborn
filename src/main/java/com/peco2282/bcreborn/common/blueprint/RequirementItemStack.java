package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.core.ISerializable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class RequirementItemStack implements ISerializable {
  public ItemStack stack;
  public int size;

  public RequirementItemStack(ItemStack stack, int size) {
    this.stack = new ItemStack(stack.getItem(), 1);
    this.size = size;
  }

  @Override
  public int hashCode() {
    return this.stack.hashCode() * 13 + this.size;
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    data.writeItem(this.stack);
    data.writeInt(this.size);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    this.stack = data.readItem();
    this.size = data.readInt();
  }

  public static RequirementItemStack decode(FriendlyByteBuf stream) {
    return new RequirementItemStack(stream.readItem(), stream.readInt());
  }
}
