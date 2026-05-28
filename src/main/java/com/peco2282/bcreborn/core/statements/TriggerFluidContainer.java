/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
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

import java.util.Locale;
import java.util.function.Function;

public class TriggerFluidContainer extends BCStatement implements ITriggerExternal {

	public enum State {

		Empty, Contains, Space, Full
	}

	public State state;

	public TriggerFluidContainer(State state) {
		super("buildcraft:fluid." + state.name().toLowerCase(Locale.ENGLISH), "buildcraft.fluid." + state.name().toLowerCase(Locale.ENGLISH));
		this.state = state;
	}

	@Override
	public int maxParameters() {
		return state == State.Contains || state == State.Space ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.trigger.fluid." + state.name().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public boolean isTriggerActive(BlockEntity tile, Direction side, IStatementContainer statementContainer, IStatementParameter[] parameters) {
		if (tile == null) return false;

		return tile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).map(handler -> {
			FluidStack searchedFluid = FluidStack.EMPTY;

			if (parameters != null && parameters.length >= 1 && parameters[0] != null && !parameters[0].getItemStack().isEmpty()) {
				searchedFluid = FluidUtil.getFluidContained(parameters[0].getItemStack()).orElse(FluidStack.EMPTY);
			}

			boolean foundItems = false;
			boolean foundSpace = false;

			for (int i = 0; i < handler.getTanks(); i++) {
				FluidStack stack = handler.getFluidInTank(i);
				if (!stack.isEmpty()) {
					if (searchedFluid.isEmpty() || searchedFluid.isFluidEqual(stack)) {
						foundItems = true;
					}
				}

				if (handler.fill(searchedFluid.isEmpty() ? new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1) : searchedFluid, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE) > 0) {
					foundSpace = true;
				} else if (stack.isEmpty() || stack.getAmount() < handler.getTankCapacity(i)) {
					foundSpace = true;
				}
			}

			switch (state) {
				case Empty:
					return !foundItems;
				case Contains:
					return foundItems;
				case Space:
					return foundSpace;
				default:
					return !foundSpace;
			}
		}).orElse(false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_liquidcontainer_" + state.name().toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}
}
