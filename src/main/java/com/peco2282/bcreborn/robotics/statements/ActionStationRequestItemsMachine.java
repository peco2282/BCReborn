package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionStationRequestItemsMachine extends BCStatement implements IActionInternal {

	public ActionStationRequestItemsMachine() {
		super("buildcraft:station.provide_machine_request");
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.station.provide_machine_request");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", "triggers/action_station_machine_request"));
	}

	@Override
	public void actionActivate(IStatementContainer source,
							   IStatementParameter[] parameters) {

	}
}
