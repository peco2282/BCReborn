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
package com.peco2282.bcreborn.energy.block.entity;

import com.peco2282.bcreborn.api.fuels.BuildcraftFuelRegistry;
import com.peco2282.bcreborn.api.fuels.ICoolant;
import com.peco2282.bcreborn.api.fuels.IFuel;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.energy.EnergyBlockEntityTypes;
import com.peco2282.bcreborn.energy.fluids.Tank;
import com.peco2282.bcreborn.energy.fluids.TankManager;
import com.peco2282.bcreborn.energy.menu.IronEngineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IronEngineBlockEntity extends EngineBlockEntityContainer<IronEngineBlockEntity> implements IFluidHandler {

  public static final float HEAT_PER_FE = 0.00023F;
  public static final float COOLDOWN_RATE = 0.05F;
  public static final int MAX_COOLANT_PER_TICK = 40;

  public static final int TANK_FUEL = 0;
  public static final int TANK_COOLANT = 1;

  private final TankManager<Tank> tankManager = new TankManager<>(
    new Tank("fuel", 10000),
    new Tank("coolant", 10000)
  );

  private int burnTime = 0;
  private int totalBurnTime = 0;
  private int penaltyCoolingTime = 0;
  private IFuel currentFuel;
  private ResourceLocation currentFuelId;

  public IronEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(EnergyBlockEntityTypes.IRON_ENGINE.get(), p_155229_, p_155230_, 1);
    configureEnergy(100000, 100);
  }

  @Override
  protected ResourceBuilder getEngineResource() {
    return ResourceBuilder.energy("iron_engine");
  }

  @Override
  public boolean isFuelable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBurning() {
    return isRedstonePowered && burnTime > 0 && currentFuel != null && penaltyCoolingTime <= 0 && !isOverheated();
  }

  @Override
  protected void engineUpdate() {
    super.engineUpdate();
    // Fuel registries may not yet be initialized when NBT is loaded.
    if (burnTime > 0 && currentFuel == null && BuildcraftFuelRegistry.getFuelManager() != null) {
      if (currentFuelId != null) {
        BuiltInRegistries.FLUID.getOptional(currentFuelId).ifPresent(fluid ->
          currentFuel = BuildcraftFuelRegistry.getFuelManager().getFuel(fluid));
      }
      if (currentFuel == null) {
        // Missing or removed fuel stops safely rather than leaving a deadlocked burn.
        burnTime = 0;
        currentFuelId = null;
      }
      setChanged();
    }
  }

  @Override
  public void updateProgress() {
    if (isRedstonePowered && burnTime <= 0 && !isOverheated() && penaltyCoolingTime <= 0) {
      FluidStack fuelStack = tankManager.get(TANK_FUEL).getFluid();
      if (!fuelStack.isEmpty()) {
        IFuel fuel = BuildcraftFuelRegistry.getFuelManager().getFuel(fuelStack.getFluid());
        if (fuel != null && fuel.getBurnTimePerMilliBucket() > 0 && fuelStack.getAmount() >= 1) {
          currentFuel = fuel;
          currentFuelId = BuiltInRegistries.FLUID.getKey(fuelStack.getFluid());
          burnTime = fuel.getBurnTimePerMilliBucket();
          totalBurnTime = burnTime;
          tankManager.get(TANK_FUEL).drain(1, FluidAction.EXECUTE);
          setActive(true);
          setChanged();
        }
      }
    }
  }

  @Override
  public void overheat() {
    super.overheat();
    burnTime = 0;
    penaltyCoolingTime = 10;
    tankManager.get(TANK_COOLANT).drain(Integer.MAX_VALUE, FluidAction.EXECUTE);
    setChanged();
  }

  @Override
  public void explode() {
    level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 4, true, net.minecraft.world.level.Level.ExplosionInteraction.BLOCK);
    level.setBlock(getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
  }

  @Override
  public void burning() {
    if (!isBurning()) return;
    if (burnTime > 0 && currentFuel != null) {
      if (this.energyStorage != null) {
        float mult = getOutputMultiplier();
        // The fuel API supplies FE/t; do not apply an implicit MJ conversion here.
        int gen = Math.round(currentFuel.getPowerPerCycle() * mult);
        this.energyStorage.generateEnergy(gen, false);
      }
      burnTime--;
      setChanged();

      heat += currentFuel.getPowerPerCycle() * HEAT_PER_FE;

      setPumping(true);
      if (burnTime <= 0) {
        setActive(false);
      }
    } else {
      setPumping(false);
    }
  }

  @Override
  protected void updateHeatAndStage(boolean burning) {
    // TileEngineIron owns a real temperature rather than deriving it from its
    // energy buffer. Match the 1.7.10 natural/coolant cooling thresholds.
    if (heat > MIN_HEAT && (penaltyCoolingTime > 0 || !isRedstonePowered)) {
      heat = Math.max(MIN_HEAT, heat - COOLDOWN_RATE);
      coolEngine(MIN_HEAT);
    } else if (heat > IDEAL_HEAT) {
      coolEngine(IDEAL_HEAT);
    }
    if (heat <= MIN_HEAT) {
      heat = MIN_HEAT;
      if (penaltyCoolingTime > 0) penaltyCoolingTime--;
    }

    EnergyStage next = computeStageFromHeat(heat);
    if (next != energyStage) {
      energyStage = next;
      if (next == EnergyStage.OVERHEAT) overheat();
      setChanged();
    }
  }

  private void coolEngine(float targetHeat) {
    FluidStack coolantStack = tankManager.get(TANK_COOLANT).getFluid();
    if (coolantStack.isEmpty()) return;
    ICoolant coolant = BuildcraftFuelRegistry.getCoolantManager().getCoolant(coolantStack.getFluid());
    if (coolant == null) return;
    int amount = Math.min(MAX_COOLANT_PER_TICK, coolantStack.getAmount());
    float cooling = amount * coolant.getDegreesCoolingPerMB(heat);
    tankManager.get(TANK_COOLANT).drain(amount, FluidAction.EXECUTE);
    heat = Math.max(targetHeat, heat - cooling);
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    tankManager.readTag(data);
    burnTime = data.getInt("burnTime");
    totalBurnTime = data.getInt("totalBurnTime");
    penaltyCoolingTime = data.getInt("penaltyCoolingTime");
    currentFuel = null;
    currentFuelId = data.contains("currentFuel") ? ResourceLocation.tryParse(data.getString("currentFuel")) : null;
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    tankManager.writeTag(data);
    data.putInt("burnTime", burnTime);
    data.putInt("totalBurnTime", totalBurnTime);
    data.putInt("penaltyCoolingTime", penaltyCoolingTime);
    if (burnTime > 0 && currentFuelId != null) data.putString("currentFuel", currentFuelId.toString());
  }

  @Override
  public IronEngineMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new IronEngineMenu(p_58627_, p_58628_, this);
  }

  public int getBurnTime() {
    return burnTime;
  }

  public int getTotalBurnTime() {
    return totalBurnTime;
  }

  /**
   * Returns the number of fluid storage units ("tanks") available
   *
   * @return The number of tanks available
   */
  @Override
  public int getTanks() {
    return tankManager.getTanks();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return tankManager.get(tank).getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return tankManager.get(tank).getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    if (tank == TANK_FUEL) {
      return BuildcraftFuelRegistry.getFuelManager().getFuel(stack.getFluid()) != null;
    } else if (tank == TANK_COOLANT) {
      return BuildcraftFuelRegistry.getCoolantManager().getCoolant(stack.getFluid()) != null;
    }
    return false;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (isFluidValid(TANK_FUEL, resource)) {
      return tankManager.get(TANK_FUEL).fill(resource, action);
    } else if (isFluidValid(TANK_COOLANT, resource)) {
      return tankManager.get(TANK_COOLANT).fill(resource, action);
    }
    return 0;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return tankManager.drain(resource, action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return tankManager.drain(maxDrain, action);
  }

  @Override
  public <C> @NotNull LazyOptional<C> getCapability(Capability<C> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.FLUID_HANDLER) {
      return LazyOptional.of(() -> this).cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornenergy.iron_engine");
  }
}
