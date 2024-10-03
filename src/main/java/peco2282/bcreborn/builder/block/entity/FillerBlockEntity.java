package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.api.mj.MJGenerator;
import peco2282.bcreborn.api.mj.MJHolder;
import peco2282.bcreborn.lib.block.entity.TileNeptune;
import peco2282.bcreborn.utils.BlockUtil;

public class FillerBlockEntity extends TileNeptune {

  private static final long capacity = 16000 * 1_000_000L;
  private final MJHolder HOLDER = new MJHolder(capacity);
  private EnumFillerType type = EnumFillerType.NONE;

  public FillerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.FILLER.get(), p_155229_, p_155230_);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, FillerBlockEntity entity) {
    entity.update(level, pos, state);
  }

  public EnumFillerType getFillerType() {
    return type;
  }

  private void update(Level level, BlockPos pos, BlockState state) {
    type = state.getValue(BCProperties.FILLER_TYPE);
    BlockState gen;
    {
      BlockState up = level.getBlockState(pos.above());
      BlockState down = level.getBlockState(pos.below());
      BlockState west = level.getBlockState(pos.west());
      BlockState east = level.getBlockState(pos.east());
      BlockState south = level.getBlockState(pos.south());
      BlockState north = level.getBlockState(pos.north());
      var ans = BlockUtil.anyMatch(b -> b instanceof MJGenerator, up, down, west, east, south, north);
      if (!ans.getA()) return;
      gen = ans.getB();
    }
    HOLDER.add(((MJGenerator) gen).perTick(level, gen));
  }

  @Override
  protected void saveAdditional(CompoundTag p_187471_, HolderLookup.Provider p_327783_) {
    super.saveAdditional(p_187471_, p_327783_);
    p_187471_.put("Holder", HOLDER.serializeNBT(p_327783_));
  }

  @Override
  protected void loadAdditional(CompoundTag p_331149_, HolderLookup.Provider p_333170_) {
    super.loadAdditional(p_331149_, p_333170_);
    HOLDER.deserializeNBT(p_333170_, p_331149_.getCompound("Holder"));
  }
}
