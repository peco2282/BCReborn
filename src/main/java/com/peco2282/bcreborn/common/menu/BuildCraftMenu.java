package com.peco2282.bcreborn.common.menu;

import com.peco2282.bcreborn.common.gui.slots.IPhantomSlot;
import com.peco2282.bcreborn.common.gui.slots.SlotBase;
import com.peco2282.bcreborn.common.gui.widgets.Widget;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.PacketGuiWidget;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BuildCraftMenu<M extends BuildCraftMenu<M>> extends AbstractContainerMenu {
  private final List<Widget> widgets = new ArrayList<>();
  private int inventorySize;

  public BuildCraftMenu(@Nullable MenuType<M> p_38851_, int p_38852_, Inventory p_38853_) {
    super(p_38851_, p_38852_);
    this.inventorySize = p_38853_.getContainerSize();
  }

  public List<Widget> getWidgets() {
    return widgets;
  }

  public Slot addSlot(Slot slot) {
    return super.addSlot(slot);
  }

  public void addWidget(Widget widget) {
    widget.addToContainer(this);
    widgets.add(widget);
  }

 	public void sendWidgetDataToClient(Widget widget, ContainerListener player, byte[] data) {
    if (player instanceof ServerPlayer serverPlayer) {
        PacketGuiWidget pkt = new PacketGuiWidget(containerId, widgets.indexOf(widget), data);
        BCNetworkManager.sendToPlayer(serverPlayer, pkt);
    }
  }

  public void handleWidgetClientData(int widgetId, FriendlyByteBuf data) {
    InputStream input = new ByteBufInputStream(data);
    DataInputStream stream = new DataInputStream(input);

    try {
      widgets.get(widgetId).handleClientPacketData(stream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addSlotListener(ContainerListener p_38894_) {
    super.addSlotListener(p_38894_);
    for (Widget widget : widgets) {
      widget.initWidget(p_38894_);
    }
  }

//  @Override
//  public void addCraftingToCrafters(ICrafting player) {
//    super.addCraftingToCrafters(player);
//    for (Widget widget : widgets) {
//      widget.initWidget(player);
//    }
//  }


  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    for (Widget widget : widgets) {
      for (ContainerListener player : containerListeners) {
        widget.updateWidget(player);
      }
    }
  }

//  @Override
//  public void detectAndSendChanges() {
//    super.detectAndSendChanges();
//    for (Widget widget : widgets) {
//      for (ICrafting player : (List<ICrafting>) crafters) {
//        widget.updateWidget(player);
//      }
//    }
//  }


  @Override
  public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
    if (p_150400_ < 0 || p_150400_ >= this.slots.size()) {
        super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
        return;
    }
    Slot slot = this.slots.get(p_150400_);
    if (slot instanceof IPhantomSlot) {
      slotClickPhantom(slot, p_150400_, p_150401_, p_150402_, p_150403_);
      return;
    }

    super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
  }

//  @Override
//  public ItemStack slotClick(int slotNum, int mouseButton, int modifier, EntityPlayer player) {
//    Slot slot = slotNum < 0 ? null : (Slot) this.inventorySlots.get(slotNum);
//    if (slot instanceof IPhantomSlot) {
//      return slotClickPhantom(slot, mouseButton, modifier, player);
//    }
//    return super.slotClick(slotNum, mouseButton, modifier, player);
//  }

  protected ItemStack slotClickPhantom(Slot slot, int index, int mouseButton, ClickType clickType, Player player) {
    ItemStack stack = ItemStack.EMPTY;

    if (clickType == ClickType.CLONE) {
      if (((IPhantomSlot) slot).canAdjust()) {
        slot.set(ItemStack.EMPTY);
      }
    } else if (clickType == ClickType.PICKUP) {
      Inventory playerInv = player.getInventory();
      slot.setChanged();
      ItemStack stackSlot = slot.getItem();
      ItemStack stackHeld = getCarried();

      if (!stackSlot.isEmpty()) {
        stack = stackSlot.copy();
      }

      if (stackSlot.isEmpty()) {
        if (!stackHeld.isEmpty() && slot.mayPlace(stackHeld)) {
          fillPhantomSlot(slot, stackHeld, mouseButton, 0);
        }
      } else if (stackHeld.isEmpty()) {
        adjustPhantomSlot(slot, mouseButton, 0);
        slot.onTake(player, getCarried());
      } else if (slot.mayPlace(stackHeld)) {
        if (ItemStack.isSameItemSameTags(stackSlot, stackHeld)) {
          adjustPhantomSlot(slot, mouseButton, 0);
        } else {
          fillPhantomSlot(slot, stackHeld, mouseButton, 0);
        }
      }
    }
    return stack;
  }

  protected void adjustPhantomSlot(Slot slot, int mouseButton, int modifier) {
    if (!((IPhantomSlot) slot).canAdjust()) {
      return;
    }
    ItemStack stackSlot = slot.getItem();
    int stackSize;
    if (modifier == 1) {
      stackSize = mouseButton == 0 ? (stackSlot.getCount() + 1) / 2 : stackSlot.getCount() * 2;
    } else {
      stackSize = mouseButton == 0 ? stackSlot.getCount() - 1 : stackSlot.getCount() + 1;
    }

    if (stackSize > slot.getMaxStackSize()) {
      stackSize = slot.getMaxStackSize();
    }

    stackSlot.setCount(stackSize);

    if (stackSlot.getCount() <= 0) {
      slot.set(ItemStack.EMPTY);
    }
  }

  protected void fillPhantomSlot(Slot slot, ItemStack stackHeld, int mouseButton, int modifier) {
    if (!((IPhantomSlot) slot).canAdjust()) {
      return;
    }
    int stackSize = mouseButton == 0 ? stackHeld.getCount() : 1;
    if (stackSize > slot.getMaxStackSize()) {
      stackSize = slot.getMaxStackSize();
    }
    ItemStack phantomStack = stackHeld.copy();
    phantomStack.setCount(stackSize);

    slot.set(phantomStack);
  }

  protected boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
    boolean changed = false;
    if (stackToShift.isStackable()) {
      for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
        Slot slot = slots.get(slotIndex);
        ItemStack stackInSlot = slot.getItem();
        if (stackInSlot != null && StackHelper.canStacksMerge(stackInSlot, stackToShift)) {
          int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
          int max = Math.min(stackToShift.getMaxStackSize(), slot.getMaxStackSize());
          if (resultingStackSize <= max) {
            stackToShift.setCount(0);
            stackInSlot.setCount(resultingStackSize);
            slot.setChanged();
            changed = true;
          } else if (stackInSlot.getCount() < max) {
            stackToShift.setCount(stackToShift.getCount() - (max - stackInSlot.getCount()));
            stackInSlot.setCount(max);
            slot.setChanged();
            changed = true;
          }
        }
      }
    }
    if (stackToShift.getCount() > 0) {
      for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
        Slot slot = slots.get(slotIndex);
        ItemStack stackInSlot = slot.getItem();
        if (stackInSlot == ItemStack.EMPTY) {
          int max = Math.min(stackToShift.getMaxStackSize(), slot.getMaxStackSize());
          stackInSlot = stackToShift.copy();
          stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
          stackToShift.setCount(stackToShift.getCount() - stackInSlot.getCount());
          slot.set(stackInSlot);
          slot.setChanged();
          changed = true;
        }
      }
    }
    return changed;
  }

  private boolean tryShiftItem(ItemStack stackToShift, int numSlots) {
    for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
      Slot slot = slots.get(machineIndex);
      if (slot instanceof SlotBase && !((SlotBase) slot).canShift()) {
        continue;
      }
      if (slot instanceof IPhantomSlot) {
        continue;
      }
      if (!slot.mayPlace(stackToShift)) {
        continue;
      }
      if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1)) {
        return true;
      }
    }
    return false;
  }

  public ItemStack transferStackInSlot(Player player, int slotIndex) {
    return quickMoveStack(player, slotIndex);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    ItemStack originalStack = ItemStack.EMPTY;
    Slot slot = slots.get(slotIndex);
    int numSlots = slots.size();
    if (slot != null && slot.hasItem()) {
      ItemStack stackInSlot = slot.getItem();
      originalStack = stackInSlot.copy();
      if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots)) {
        // NOOP
      } else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
        if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots)) {
          return ItemStack.EMPTY;
        }
      } else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
        if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9)) {
          return ItemStack.EMPTY;
        }
      } else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots)) {
        return ItemStack.EMPTY;
      }
      if (stackInSlot.getCount() <= 0) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
      if (stackInSlot.getCount() == originalStack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(player, stackInSlot);
    }
    return originalStack;
  }

  public int getInventorySize() {
    return inventorySize;
  }
}
