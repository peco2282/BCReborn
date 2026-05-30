package com.peco2282.bcreborn.energy.block.entity;

import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.data.tag.CommonItemTags;
import com.peco2282.bcreborn.energy.BlockEntityTypesEnergy;
import com.peco2282.bcreborn.energy.menu.IronEngineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class IronEngineBlockEntity extends ContainerEngineBlockEntity<IronEngineBlockEntity> implements IFluidHandler {

  public IronEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BlockEntityTypesEnergy.IRON_ENGINE.get(), p_155229_, p_155230_, 1);
    // 大容量・高出力（暫定・旧BC基準寄り）
    configureEnergy(80000, 120);
  }


  @Override
  protected ResourceBuilder getEngineResource() {
    return ResourceBuilder.energy("iron_engine");
  }

  @Override
  public boolean isFuelable(ItemStack stack) {
    // 暫定：固体燃料対応（タグ or バニラ燃焼可能）
    return stack.is(CommonItemTags.ENGINE_ENERGY) || net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null) > 0;
  }

  @Override
  public boolean isBurning() {
    return burnTime > 0;
  }

  @Override
  public void updateProgress() {
    // 燃焼終了していて、燃料があれば投入して開始
    if (burnTime <= 0 && !isOverheated()) {
      ItemStack stack = getItem(0);
      if (!stack.isEmpty() && isFuelable(stack)) {
        int time = net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null);
        if (time > 0) {
          totalBurnTime = burnTime = time;
          // 1個消費
          stack.shrink(1);
          if (stack.isEmpty()) setItem(0, ItemStack.EMPTY);
          setActive(true);
        }
      }
    }
  }

  @Override
  public void overheat() {

  }

  @Override
  public void explode() {

  }

  @Override
  public void burning() {
    if (burnTime > 0) {
      // 出力は段階倍率を反映
      if (this.energyStorage != null) {
        float mult = getOutputMultiplier();
        int base = 60; // 暫定基準出力
        int gen = Math.max(0, Math.round(base * mult));
        this.energyStorage.generateEnergy(gen, false);
      }
      burnTime--;
      if (energyStorage.getEnergyStored() > 0 && canPushEnergy()) {
        setPumping(true);
      } else {
        setPumping(false);
      }
      if (burnTime <= 0) {
        setActive(false);
        setPumping(false);
      }
    } else {
      setPumping(false);
    }
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    if (data.contains("burnTime")) this.burnTime = data.getInt("burnTime");
    if (data.contains("totalBurnTime")) this.totalBurnTime = data.getInt("totalBurnTime");
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    data.putInt("burnTime", burnTime);
    data.putInt("totalBurnTime", totalBurnTime);
  }

  @Override
  public IronEngineMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new IronEngineMenu(p_58627_, p_58628_, this);
  }

  // --- 暫定・内部状態 ---
  private int burnTime = 0;
  private int totalBurnTime = 0;

  public int getBurnTime() { return burnTime; }
  public int getTotalBurnTime() { return totalBurnTime; }

  /**
   * Returns the number of fluid storage units ("tanks") available
   *
   * @return The number of tanks available
   */
  @Override
  public int getTanks() {
    return 0;
  }

  /**
   * Returns the FluidStack in a given tank.
   *
   * <p>
   * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
   * altering internal contents. Any implementers who are able to detect modification via this method
   * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
   * </p>
   *
   * <p>
   * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
   * </p>
   *
   * @param tank Tank to query.
   * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
   */
  @Override
  public @NotNull FluidStack getFluidInTank(int tank) {
    return FluidStack.EMPTY;
  }

  /**
   * Retrieves the maximum fluid amount for a given tank.
   *
   * @param tank Tank to query.
   * @return The maximum fluid amount held by the tank.
   */
  @Override
  public int getTankCapacity(int tank) {
    return 0;
  }

  /**
   * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
   * basically always return TRUE for this.
   *
   * @param tank  Tank to query for validity
   * @param stack Stack to test with for validity
   * @return TRUE if the tank can hold the FluidStack, not considering current state.
   * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
   */
  @Override
  public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
    return false;
  }

  /**
   * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
   *
   * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
   * @param action   If SIMULATE, fill will only be simulated.
   * @return Amount of resource that was (or would have been, if simulated) filled.
   */
  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0;
  }

  /**
   * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
   *
   * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
   * @param action   If SIMULATE, drain will only be simulated.
   * @return FluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Override
  public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
    return FluidStack.EMPTY;
  }

  /**
   * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
   * <p>
   * This method is not Fluid-sensitive.
   *
   * @param maxDrain Maximum amount of fluid to drain.
   * @param action   If SIMULATE, drain will only be simulated.
   * @return FluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Override
  public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
    return FluidStack.EMPTY;
  }
}
