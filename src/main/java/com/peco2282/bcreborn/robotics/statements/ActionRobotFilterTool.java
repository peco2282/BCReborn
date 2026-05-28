package com.peco2282.bcreborn.robotics.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.*;
import com.peco2282.bcreborn.common.inventory.filters.ArrayStackOrListFilter;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.common.inventory.filters.PassThroughStackFilter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ActionRobotFilterTool extends BCStatement implements IActionInternal {

	public ActionRobotFilterTool() {
		super("robot.work_filter_tool");
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.robot.filter_tool");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornRobotics.location("triggers/action_robot_filter_tool"));
	}

	@Override
	public int minParameters() {
		return 1;
	}

	@Override
	public int maxParameters() {
		return 1;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}

	public static Collection<ItemStack> getGateFilterStacks(DockingStation station) {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();

		for (StatementSlot slot : station.getActiveActions()) {
			if (slot.statement instanceof ActionRobotFilterTool) {
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

	@Override
	public void actionActivate(IStatementContainer source,
                               IStatementParameter[] parameters) {
	}
}
