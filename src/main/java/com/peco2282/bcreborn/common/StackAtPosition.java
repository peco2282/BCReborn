package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class StackAtPosition implements ISerializable {
  public ItemStack stack;
  public Position pos;
  public boolean display;

  // Rendering only!
  public boolean generatedListId;
  public int glListId;

  @Override
  public void readData(FriendlyByteBuf data) {
    stack = data.readItem();
    ;
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    data.writeItem(stack);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StackAtPosition)) {
      return false;
    }
    StackAtPosition other = (StackAtPosition) o;
    return other.stack.equals(stack) && other.pos.equals(pos) && other.display == display;
  }

  @Override
  public int hashCode() {
    return stack.hashCode() * 17 + pos.hashCode();
  }
}
