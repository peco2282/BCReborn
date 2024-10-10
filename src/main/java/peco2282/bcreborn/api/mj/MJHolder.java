package peco2282.bcreborn.api.mj;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.atomic.AtomicLong;

public class MJHolder implements INBTSerializable<CompoundTag> {
  private final AtomicLong capacity = new AtomicLong(0);
  private final AtomicLong microJoules = new AtomicLong(0);
  public static final String CAP = "capacity";
  public static final String MJ = "current";

  public MJHolder(long capacity) {
    this.capacity.set(capacity);
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

  public void extract(long mj) {
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
