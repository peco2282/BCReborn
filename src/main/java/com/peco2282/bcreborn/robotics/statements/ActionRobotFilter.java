package com.peco2282.bcreborn.robotics.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;


import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.*;
import com.peco2282.bcreborn.common.inventory.filters.*;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;


public class ActionRobotFilter extends BCStatement implements IActionInternal {

	public ActionRobotFilter() {
		super("buildcraft:robot.work_filter");
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.robot.filter");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", "triggers/action_robot_filter"));
	}

	@Override
	public int minParameters() {
		return 1;
	}

	@Override
	public int maxParameters() {
		return 3;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}

	public static Collection<ItemStack> getGateFilterStacks(DockingStation station) {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();

		for (StatementSlot slot : station.getActiveActions()) {
			if (slot.statement instanceof ActionRobotFilter) {
				for (IStatementParameter p : slot.parameters) {
					if (p instanceof StatementParameterItemStack param) {
                        ItemStack stack = param.getItemStack();

						if (!stack.isEmpty()) {
							result.add(stack);
						}
					}
				}
			}
		}

		return result;
	}

	public static IStackFilter getGateFilter(DockingStation station) {
		Collection<ItemStack> stacks = getGateFilterStacks(station);

		if (stacks.size() == 0) {
			return new PassThroughStackFilter();
		} else {
			return new ArrayStackOrListFilter(stacks.toArray(new ItemStack[stacks.size()]));
		}
	}

	public static IFluidFilter getGateFluidFilter(DockingStation station) {
		Collection<ItemStack> stacks = getGateFilterStacks(station);

		if (stacks.size() == 0) {
			return new PassThroughFluidFilter();
		} else {
			return new ArrayFluidFilter(stacks.toArray(new ItemStack[stacks.size()]));
		}
	}

	public static boolean canInteractWithItem(DockingStation station, IStackFilter filter, Class<?> actionClass) {
		boolean actionFound = false;

		for (StatementSlot s : station.getActiveActions()) {
			if (actionClass.isAssignableFrom(s.statement.getClass())) {
				StatementParameterStackFilter param = new StatementParameterStackFilter(s.parameters);

				if (!param.hasFilter() || param.matches(filter)) {
					actionFound = true;
					break;
				}
			}
		}

		return actionFound;
	}

	public static boolean canInteractWithFluid(DockingStation station, IFluidFilter filter, Class<?> actionClass) {
		boolean actionFound = false;

		for (StatementSlot s : station.getActiveActions()) {
			if (actionClass.isAssignableFrom(s.statement.getClass())) {
				StatementParameterStackFilter param = new StatementParameterStackFilter(s.parameters);

				if (!param.hasFilter()) {
					actionFound = true;
					break;
				} else {
					for (ItemStack stack : param.getStacks()) {
						if (!stack.isEmpty()) {
							FluidStack fluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);

							if (!fluid.isEmpty() && filter.matches(fluid.getFluid())) {
								actionFound = true;
								break;
							}
						}
					}
				}
			}
		}

		return actionFound;

	}

	@Override
	public void actionActivate(IStatementContainer source,
                               IStatementParameter[] parameters) {

	}
}
