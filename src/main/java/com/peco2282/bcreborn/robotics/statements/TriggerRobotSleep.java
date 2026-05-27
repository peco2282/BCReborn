package com.peco2282.bcreborn.robotics.statements;

import java.util.List;
import java.util.function.Function;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.robotics.RobotUtils;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TriggerRobotSleep extends BCStatement implements ITriggerInternal {

	public TriggerRobotSleep() {
		super("buildcraft:robot.sleep");
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.trigger.robot.sleep");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", "triggers/trigger_robot_sleep"));
	}

	@Override
	public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
		List<DockingStation> stations = RobotUtils.getStations(container.getTile());

		for (DockingStation station : stations) {
			if (station.robotTaking() != null) {
				EntityRobot robot = (EntityRobot) station.robotTaking();

				if (robot.isActive()) {
					return true;
				}
			}
		}

		return false;
	}
}
