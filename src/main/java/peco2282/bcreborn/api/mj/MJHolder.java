package peco2282.bcreborn.api.mj;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.atomic.AtomicLong;

public class MJHolder implements INBTSerializable<CompoundTag> {
  private final long capacity;
  private final AtomicLong microJoules = new AtomicLong(0);
  public static final String CAP = "capacity";
  public static final String MJ = "current";

  public MJHolder(long capacity) {
    this.capacity = capacity;
  }

  @Override
  public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
    var tag = new CompoundTag();
    tag.putLong(CAP, capacity);
    tag.putLong(MJ, microJoules.get());
    return tag;
  }

  @Override
  public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
    microJoules.set(nbt.getLong(CAP));
  }

  public long add(long mj) {
    if (isFull()) return 0;
    if (current() + mj > capacity) {
      var overflow = current() + mj - capacity;
      microJoules.set(capacity);
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
    return microJoules.get() >= capacity;
  }

  public boolean isEmpty() {
    return microJoules.get() == 0;
  }
}
