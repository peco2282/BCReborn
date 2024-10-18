package peco2282.bcreborn.builder.saved;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;
import peco2282.bcreborn.api.event.SaveDataEvent;

public class BluePrintSavedData extends SavedData {
  private final RangeCache cache;

  public BluePrintSavedData(BlockPos start, BlockPos end) {
    this(new RangeCache(start, end, System.currentTimeMillis()));
  }

  public BluePrintSavedData(RangeCache cache) {
    this.cache = cache;
  }

  public BluePrintSavedData() {
    this(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));
  }

  public static Factory<BluePrintSavedData> factory() {
    return new Factory<>(BluePrintSavedData::new, BluePrintSavedData::load, DataFixTypes.STRUCTURE);
  }

  private static BluePrintSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
    return new BluePrintSavedData(RangeCache.load(tag, provider));
  }

  @Override
  public CompoundTag save(CompoundTag p_77763_, HolderLookup.Provider p_334349_) {
    CompoundTag tag = cache.save(p_77763_);

    SaveDataEvent event = new SaveDataEvent(cache);
    MinecraftForge.EVENT_BUS.post(event);
    return tag;
  }

  public RangeCache getCache() {
    return cache;
  }
}
