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
package com.peco2282.bcreborn.common.gui.buttons;

import net.minecraft.nbt.CompoundTag;

/**
 * T should be an Enum of button states
 */
public final class MultiButtonController<T extends IMultiButtonState> {

  private final T[] validStates;
  private int currentState;

  @SafeVarargs
  private MultiButtonController(int startState, T... validStates) {
    this.currentState = startState;
    this.validStates = validStates;
  }

  @SafeVarargs
  public static <T extends IMultiButtonState> MultiButtonController<T> getController(int startState, T... validStates) {
    return new MultiButtonController<>(startState, validStates);
  }

  public MultiButtonController<?> copy() {
    return new MultiButtonController<>(currentState, validStates.clone());
  }

  public T[] getValidStates() {
    return validStates;
  }

  public void incrementState() {
    int newState = currentState + 1;
    if (newState >= validStates.length) {
      newState = 0;
    }
    currentState = newState;
  }

  public int getCurrentState() {
    return currentState;
  }

  public void setCurrentState(int state) {
    currentState = state;
  }

  public void setCurrentState(T state) {
    for (int i = 0; i < validStates.length; i++) {
      if (validStates[i] == state) {
        currentState = i;
        return;
      }
    }
  }

  public T getButtonState() {
    return validStates[currentState];
  }

  public void writeToNBT(CompoundTag nbt, String tag) {
    nbt.putString(tag, getButtonState().name());
  }

  public void readFromNBT(CompoundTag nbt, String tag) {
    if (nbt.contains(tag, CompoundTag.TAG_STRING)) {
      String name = nbt.getString(tag);
      for (int i = 0; i < validStates.length; i++) {
        if (validStates[i].name().equals(name)) {
          currentState = i;
          break;
        }
      }
    } else if (nbt.contains(tag, CompoundTag.TAG_BYTE)) {
      currentState = nbt.getByte(tag);
    }
  }
}
