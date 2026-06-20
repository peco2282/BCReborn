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
package com.peco2282.bcreborn.common.utils;

import com.google.common.collect.ForwardingCollection;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

public class RevolvingList<T> extends ForwardingCollection<T> {

  private final Deque<T> list = new LinkedList<>();

  public RevolvingList() {
  }

  public RevolvingList(Collection<? extends T> collection) {
    list.addAll(collection);
  }

  @Override
  protected Collection<T> delegate() {
    return list;
  }

  public void rotateLeft() {
    if (list.isEmpty()) {
    } else {
      list.addFirst(list.removeLast());
    }
  }

  public void rotateRight() {
    if (list.isEmpty()) {
    } else {
      list.addLast(list.removeFirst());
    }
  }

  public T getCurrent() {
    if (list.isEmpty()) {
      return null;
    } else {
      return list.getFirst();
    }
  }

  public void setCurrent(T e) {
    if (!contains(e)) {
    } else {
      while (!getCurrent().equals(e)) {
        rotateRight();
      }
    }
  }
}
