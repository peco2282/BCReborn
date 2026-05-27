/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class StatementParameterDirection implements IStatementParameter {

	@OnlyIn(Dist.CLIENT)
	private static TextureAtlasSprite[] icons;

	public Direction direction = null;

	public StatementParameterDirection() {

	}

	@Override
	public ItemStack getItemStack() {
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getIcon() {
		if (direction == null) {
			return null;
		} else {
			return icons[direction.ordinal()];
		}
	}

	@Override
	public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
		// Direction parameter logic usually depends on pipe connection, but we can cycle directions
		int dirIdx = direction == null ? -1 : direction.ordinal();
		dirIdx = (dirIdx + (mouse.getButton() > 0 ? -1 : 1)) % 7;
		if (dirIdx < -1) dirIdx = 5;
		if (dirIdx == -1 || dirIdx == 6) {
			direction = null;
		} else {
			direction = Direction.values()[dirIdx];
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		if (direction != null) {
			nbt.putByte("direction", (byte) direction.ordinal());
		}
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		if (nbt.contains("direction")) {
			direction = Direction.values()[nbt.getByte("direction")];
		} else {
			direction = null;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof StatementParameterDirection) {
			StatementParameterDirection param = (StatementParameterDirection) object;
			return param.direction == this.direction;
		}
		return false;
	}

	@Override
	public String getDescription() {
		if (direction == null) {
			return "";
		} else {
			return StringUtils.localize("direction." + direction.name().toLowerCase());
		}
	}

	@Override
	public String getUniqueTag() {
		return "buildcraft:pipeActionDirection";
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icons = new TextureAtlasSprite[6];
		icons[0] = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_dir_down"));
		icons[1] = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_dir_up"));
		icons[2] = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_dir_north"));
		icons[3] = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_dir_south"));
		icons[4] = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_dir_west"));
		icons[5] = textureGetter.apply(new ResourceLocation("buildcraftcore", "triggers/trigger_dir_east"));
	}

	@Override
	public IStatementParameter rotateLeft() {
		StatementParameterDirection d = new StatementParameterDirection();
		if (direction != null) {
			d.direction = direction.getCounterClockWise(Direction.Axis.Y);
		}
		return d;
	}
}
