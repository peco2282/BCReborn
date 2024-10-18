package peco2282.bcreborn.builder.saved;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.stream.Stream;

public class RangeCache {
  public static final String DATA = "Data";
  public static final String START_X = "StartX";
  public static final String START_Y = "StartY";
  public static final String START_Z = "StartZ";
  public static final String END_X = "EndX";
  public static final String END_Y = "EndY";
  public static final String END_Z = "EndZ";
  public static final String DATE = "Date";
  public static final String BLOCK_NAME = "BlockName";
  public static final String BLOCK_STATE = "BlockState";
  public static final Set<String> KEYS = Set.of(DATA, START_X, START_Y, START_Z, END_X, END_Y, END_Z, DATE);
  private static final Codec<RangeCache> CODEC = RecordCodecBuilder
      .create(instance -> instance.group(
          BlockPos.CODEC.fieldOf("start").forGetter(RangeCache::getStart),
          BlockPos.CODEC.fieldOf("end").forGetter(RangeCache::getEnd),
          Codec.LONG.fieldOf("mills").forGetter(RangeCache::getMillis)
      ).apply(instance, RangeCache::new));
  private final Level level;
  private final BlockPos start;
  private final BlockPos end;
  private final int startX;
  private final int startY;
  private final int startZ;
  private final int endX;
  private final int endY;
  private final int endZ;
  private final long millis;

  private CompoundTag saved;

  private Map<BlockPos, BlockState> blockMap;

  public RangeCache(BlockPos start, BlockPos end, long millis) {
    this.level = Minecraft.getInstance().level;
    this.start = start;
    this.end = end;
    this.startX = start.getX();
    this.startY = start.getY();
    this.startZ = start.getZ();
    this.endX = end.getX();
    this.endY = end.getY();
    this.endZ = end.getZ();

    this.millis = millis;
  }

  public static RangeCache load(CompoundTag tag, HolderLookup.Provider provider) {
    long mills = tag.getLong(DATE);
    BlockPos start = new BlockPos(tag.getInt(START_X), tag.getInt(START_Y), tag.getInt(START_Z));
    BlockPos end = new BlockPos(tag.getInt(END_X), tag.getInt(END_Y), tag.getInt(END_Z));
    var cache = new RangeCache(start, end, mills);
    var tpl = buildSaved(tag.getCompound(DATA));
    cache.saved = tpl.getA();
    cache.blockMap = tpl.getB();
    return cache;
  }

  private static Tuple<CompoundTag, Map<BlockPos, BlockState>> buildSaved(CompoundTag savedTag) {
    CompoundTag tag = new CompoundTag();
    Map<BlockPos, BlockState> blockMap = new HashMap<>();
    for (String key : savedTag.getAllKeys()) {
      var pos = Arrays.stream(key.split("-")).map(Integer::parseInt).toArray(Integer[]::new);
      assert pos.length == 3;
      CompoundTag subTag = savedTag.getCompound(key);
      String blockName = subTag.getString(BLOCK_NAME);
      CompoundTag stateTag = subTag.getCompound(BLOCK_STATE);
      BlockState state = BlockState.CODEC.decode(NbtOps.INSTANCE, stateTag).getOrThrow().getFirst();
      Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockName));
      assert state.getBlock().toString().equals(block.toString());
      blockMap.compute(new BlockPos(pos[0], pos[1], pos[2]), (k, v) -> {
        if (v == null) return state;
        throw new RuntimeException(k + "'s state is present. (" + v + ")");
      });
    }
    return new Tuple<>(tag, blockMap);
  }

  private static CompoundTag blockState(BlockState state) {
    final CompoundTag tag = new CompoundTag();
    String name = name(state.getBlock());
    if (name.contains("[unregistered]")) throw new RuntimeException("Unregistered block");
    tag.putString(BLOCK_NAME, name);
    final Tag stateTag = BlockState.CODEC.encode(state, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();
    tag.put(BLOCK_STATE, stateTag);

    return tag;
  }

  private static String name(Block block) {
    return BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName();
  }

  private static UnsupportedOperationException uoe(String key) {
    return new UnsupportedOperationException(key + " is not supported");
  }

  public long getMillis() {
    return this.millis;
  }

  public Date createdAt() {
    return new Date(this.millis);
  }

  public BlockPos getStart() {
    return start;
  }

  public BlockPos getEnd() {
    return end;
  }

  public CompoundTag save(CompoundTag data) {
    CompoundTag tag = new CompoundTag();

    final Stream<BlockState> states = level.getBlockStates(new AABB(startX, startY, startZ, endX, endY, endZ));
    states.forEach(System.out::println);

    for (int x = startX; x <= endX; x++) {
      for (int y = startY; y <= endY; y++) {
        for (int z = startZ; z <= endZ; z++) {
          BlockState state = level.getBlockState(new BlockPos(x, y, z));
          blockMap.compute(new BlockPos(x, y, z), (k, v) -> {
            if (v == null) return state;
            throw new RuntimeException(k + "'s state is present. (" + v + ")");
          });
          tag.put("%s-%s-%s".formatted(x, y, z), blockState(state));
        }
      }
    }
    data.put(DATA, tag);
    data.putInt(START_X, startX);
    data.putInt(START_Y, startY);
    data.putInt(START_Z, startZ);
    data.putInt(END_X, endX);
    data.putInt(END_Y, endY);
    data.putInt(END_Z, endZ);
    data.putLong(DATE, millis);
    return saved = data;
  }

  public RangeCache put(String key, Tag tag) {
    if (KEYS.contains(key)) throw uoe(key);
    saved.put(key, tag);
    return this;
  }

  public RangeCache put(String key, String value) {
    if (KEYS.contains(key)) throw uoe(key);
    saved.putString(key, value);
    return this;
  }

  public RangeCache put(String key, int value) {
    if (KEYS.contains(key)) throw uoe(key);
    saved.putInt(key, value);
    return this;
  }

  public RangeCache put(String key, long value) {
    if (KEYS.contains(key)) throw uoe(key);
    saved.putLong(key, value);
    return this;
  }

  public RangeCache put(String key, short value) {
    if (KEYS.contains(key)) throw uoe(key);
    saved.putShort(key, value);
    return this;
  }

  public RangeCache put(String key, UUID value) {
    if (KEYS.contains(key)) throw uoe(key);
    saved.putUUID(key, value);
    return this;
  }
}
