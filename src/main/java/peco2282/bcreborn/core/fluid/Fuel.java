package peco2282.bcreborn.core.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import peco2282.bcreborn.core.block.BCCoreBlocks;

public abstract sealed class Fuel extends FlowingFluid permits Fuel.Source, Fuel.Flowing {
  @Override
  protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76046_) {
    super.createFluidStateDefinition(p_76046_);
    p_76046_.add(LEVEL);
  }

  @Override
  public Fluid getFlowing() {
    return BCCoreFluids.FUEL_FLOWING.get();
  }

  @Override
  public Fluid getSource() {
    return BCCoreFluids.FUEL_SOURCE.get();
  }

  /**
   * @param p_256009_
   * @deprecated Forge: Use {@link #canConvertToSource(FluidState, Level, BlockPos)} instead.
   */
  @Override
  @SuppressWarnings("dep-ann")
  protected boolean canConvertToSource(Level p_256009_) {
    return false;
  }

  @Override
  protected void beforeDestroyingBlock(LevelAccessor p_76002_, BlockPos p_76003_, BlockState p_76004_) {

  }

  @Override
  protected int getSlopeFindDistance(LevelReader p_76074_) {
    return 4;
  }

  @Override
  protected int getDropOff(LevelReader p_76087_) {
    return 0;
  }

  @Override
  public Item getBucket() {
    return Items.MILK_BUCKET;
  }

  @Override
  protected boolean canBeReplacedWith(FluidState p_76127_, BlockGetter p_76128_, BlockPos p_76129_, Fluid p_76130_, Direction p_76131_) {
    return false;
  }

  @Override
  public int getTickDelay(LevelReader p_76120_) {
    return 20;
  }

  @Override
  protected float getExplosionResistance() {
    return 30;
  }

  @Override
  protected BlockState createLegacyBlock(FluidState p_76136_) {
    return isSource(p_76136_) ?
        BCCoreBlocks.FUEL_SOURCE.get().defaultBlockState() :
        BCCoreBlocks.FUEL_FLOWING.get().defaultBlockState()
            .setValue(LEVEL, getLegacyLevel(p_76136_));
  }

  @Override
  public boolean isSource(FluidState p_76140_) {
    return this instanceof Source;
  }

  @Override
  public FluidType getFluidType() {
    return BCCoreFluids.FUEL.get();
  }

  public static final class Source extends Fuel {
    @Override
    public int getAmount(FluidState p_164509_) {
      return 8;
    }
  }

  public static final class Flowing extends Fuel {
    @Override
    public int getAmount(FluidState p_164509_) {
      return p_164509_.getValue(LEVEL);
    }
  }
}
