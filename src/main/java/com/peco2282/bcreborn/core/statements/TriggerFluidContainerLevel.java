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

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Locale;
import java.util.function.Function;

public class TriggerFluidContainerLevel extends BCStatement implements ITriggerExternal {

  public TriggerType type;

  public TriggerFluidContainerLevel(TriggerType type) {
    super("buildcraft:fluid." + type.name().toLowerCase(Locale.ENGLISH), "buildcraft.fluid." + type.name().toLowerCase(Locale.ENGLISH));
    this.type = type;
  }

  @Override
  public int maxParameters() {
    return 1;
  }

  @Override
  public String getDescription() {
    return String.format(StringUtils.localize("gate.trigger.fluidlevel.below"), (int) (type.level * 100));
  }

  @Override
  public boolean isTriggerActive(BlockEntity tile, Direction side, IStatementContainer statementContainer, IStatementParameter[] parameters) {
    if (tile == null) return false;

    return tile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).map(handler -> {
      FluidStack searchedFluid = FluidStack.EMPTY;

      if (parameters != null && parameters.length >= 1 && parameters[0] != null && !parameters[0].getItemStack().isEmpty()) {
        searchedFluid = FluidUtil.getFluidContained(parameters[0].getItemStack()).orElse(FluidStack.EMPTY);
      }

      for (int i = 0; i < handler.getTanks(); i++) {
        FluidStack stack = handler.getFluidInTank(i);
        if (stack.isEmpty()) {
          if (searchedFluid.isEmpty()) {
            return true;
          }
          if (handler.fill(searchedFluid, IFluidHandler.FluidAction.SIMULATE) > 0) {
            return true;
          }
          continue;
        }

        if (searchedFluid.isEmpty() || searchedFluid.isFluidEqual(stack)) {
          float percentage = (float) stack.getAmount() / (float) handler.getTankCapacity(i);
          return percentage < type.level;
        }
      }
      return false;
    }).orElse(false);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_liquidcontainer_" + type.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return new StatementParameterItemStack(ItemStack.EMPTY);
  }

  public enum TriggerType {

    BELOW25(0.25F), BELOW50(0.5F), BELOW75(0.75F);

    public final float level;

    TriggerType(float level) {
      this.level = level;
    }
  }
}
