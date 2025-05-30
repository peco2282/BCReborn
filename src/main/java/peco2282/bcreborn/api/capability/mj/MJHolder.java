/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.capability.mj;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("deprecation")
public class MJHolder implements INBTSerializable<CompoundTag> {
  private final AtomicLong capacity = new AtomicLong(0);
  private final AtomicLong microJoules = new AtomicLong(0);
  public static final String CAP = "capacity";
  public static final String MJ = "current";
  public static final Codec<MJHolder> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.LONG.fieldOf(CAP).forGetter(MJHolder::capacity),
                      Codec.LONG.fieldOf(MJ).forGetter(MJHolder::current))
                  .apply(instance, MJHolder::new));

  public MJHolder(long capacity) {
    this(capacity, 0);
  }

  public MJHolder(long capacity, long current) {
    assert capacity > 0;
    assert current >= 0;
    this.microJoules.set(current);
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
    var tag = new CompoundTag();
    tag.putLong(CAP, capacity.get());
    tag.putLong(MJ, microJoules.get());
    return tag;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
    capacity.set(nbt.getLong(CAP));
    microJoules.set(nbt.getLong(MJ));
  }

  public long add(long mj) {
    assert mj >= 0;
    if (isFull()) return 0;
    if (current() + mj > capacity.get()) {
      var overflow = current() + mj - capacity.get();
      microJoules.set(capacity.get());
      return overflow;
    } else {
      microJoules.addAndGet(mj);
      return mj;
    }
  }

  public long capacity() {
    return capacity.get();
  }

  public void extract(long mj) {
    assert mj >= 0;
    microJoules.addAndGet(-mj);
  }

  public long current() {
    return microJoules.get();
  }

  public boolean isFull() {
    return microJoules.get() >= capacity.get();
  }

  public boolean isEmpty() {
    return microJoules.get() == 0;
  }
}
