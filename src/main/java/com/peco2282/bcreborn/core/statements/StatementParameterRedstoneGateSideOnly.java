package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class StatementParameterRedstoneGateSideOnly implements
		IStatementParameter {

	@OnlyIn(Dist.CLIENT)
	private static TextureAtlasSprite icon;

	public boolean isOn = false;

	public StatementParameterRedstoneGateSideOnly() {

	}

	@Override
	public ItemStack getItemStack() {
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getIcon() {
		if (!isOn) {
			return null;
		} else {
			return icon;
		}
	}

	@Override
	public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
		isOn = !isOn;
	}

	@Override
	public void writeToNBT(CompoundTag compound) {
		compound.putBoolean("isOn", isOn);
	}

	@Override
	public void readFromNBT(CompoundTag compound) {
		if (compound.contains("isOn")) {
			isOn = compound.getBoolean("isOn");
		}
	}

	@Override
	public String getDescription() {
		return isOn ? StringUtils.localize("gate.parameter.redstone.gateSideOnly") : "";
	}

	@Override
	public String getUniqueTag() {
		return "buildcraft:redstoneGateSideOnly";
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/redstone_gate_side_only"));
	}

	@Override
	public IStatementParameter rotateLeft() {
		return this;
	}
}
