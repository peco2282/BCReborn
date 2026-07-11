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
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.ITriggerProvider;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultTriggerProvider implements ITriggerProvider {

  public static ITriggerExternal triggerEmptyInventory = new TriggerInventory(TriggerInventory.State.Empty);
  public static ITriggerExternal triggerContainsInventory = new TriggerInventory(TriggerInventory.State.Contains);
  public static ITriggerExternal triggerSpaceInventory = new TriggerInventory(TriggerInventory.State.Space);
  public static ITriggerExternal triggerFullInventory = new TriggerInventory(TriggerInventory.State.Full);
  public static ITriggerExternal triggerInventoryBelow25 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW25);
  public static ITriggerExternal triggerInventoryBelow50 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW50);
  public static ITriggerExternal triggerInventoryBelow75 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW75);

  public static ITriggerExternal triggerEmptyFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Empty);
  public static ITriggerExternal triggerContainsFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Contains);
  public static ITriggerExternal triggerSpaceFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Space);
  public static ITriggerExternal triggerFullFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Full);
  public static ITriggerExternal triggerFluidContainerBelow25 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW25);
  public static ITriggerExternal triggerFluidContainerBelow50 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW50);
  public static ITriggerExternal triggerFluidContainerBelow75 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW75);

  public static ITriggerExternal triggerMachineActive = new TriggerMachine(true);
  public static ITriggerExternal triggerMachineInactive = new TriggerMachine(false);

  public static ITriggerInternal triggerRedstoneActive = new TriggerRedstoneInput(true);
  public static ITriggerInternal triggerRedstoneInactive = new TriggerRedstoneInput(false);

  @Override
  public Collection<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity tile) {
    LinkedList<ITriggerExternal> res = new LinkedList<>();

    boolean blockInventoryTriggers = false;
    boolean blockFluidHandlerTriggers = false;

    if (tile instanceof IBlockDefaultTriggers) {
      blockInventoryTriggers = ((IBlockDefaultTriggers) tile).blockInventoryTriggers(side);
      blockFluidHandlerTriggers = ((IBlockDefaultTriggers) tile).blockFluidHandlerTriggers(side);
    }

    if (!blockInventoryTriggers && tile.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).isPresent()) {
      res.add(triggerEmptyInventory);
      res.add(triggerContainsInventory);
      res.add(triggerSpaceInventory);
      res.add(triggerFullInventory);
      res.add(triggerInventoryBelow25);
      res.add(triggerInventoryBelow50);
      res.add(triggerInventoryBelow75);
    }

    if (!blockFluidHandlerTriggers && tile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).isPresent()) {
      res.add(triggerEmptyFluid);
      res.add(triggerContainsFluid);
      res.add(triggerSpaceFluid);
      res.add(triggerFullFluid);
      res.add(triggerFluidContainerBelow25);
      res.add(triggerFluidContainerBelow50);
      res.add(triggerFluidContainerBelow75);
    }

    if (tile instanceof IHasWork) {
      res.add(triggerMachineActive);
      res.add(triggerMachineInactive);
    }

    return res;
  }

  @Override
  public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
    LinkedList<ITriggerInternal> res = new LinkedList<>();

    if (container instanceof IRedstoneStatementContainer) {
      res.add(triggerRedstoneActive);
      res.add(triggerRedstoneInactive);
    }

    // TriggerEnergy is still problematic, skipping for now as it depends on complex pipe logic
		/*
		if (TriggerEnergy.isTriggeringPipe(container.getTile()) || TriggerEnergy.getTriggeringNeighbor(container.getTile()) != null) {
			res.add((ITriggerInternal) BuildCraftCore.triggerEnergyHigh);
			res.add((ITriggerInternal) BuildCraftCore.triggerEnergyLow);
		}
		*/

    return res;
  }
}
