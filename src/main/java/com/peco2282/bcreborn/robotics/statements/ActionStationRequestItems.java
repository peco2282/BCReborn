package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.StatementParameterItemStackExact;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionStationRequestItems extends ActionStationInputItems {

	public ActionStationRequestItems() {
		super("buildcraft:station.request_items");
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.station.request_items");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", "triggers/action_station_request_items"));
	}

	@Override
	public int maxParameters() {
		return 3;
	}

	@Override
	public int minParameters() {
		return 1;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStackExact(EntityRobot.TRANSFER_INV_SLOTS);
	}
}
