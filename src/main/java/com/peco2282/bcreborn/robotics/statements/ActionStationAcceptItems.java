package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementManager;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionStationAcceptItems extends ActionStationInputItems {

	public ActionStationAcceptItems() {
		super("buildcraft:station.accept_items");
		StatementManager.statements.put("buildcraft:station.drop_in_pipe", this);
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.action.station.accept_items");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", "triggers/action_station_accept_items"));
	}

	@Override
	public int maxParameters() {
		return 3;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}
}
