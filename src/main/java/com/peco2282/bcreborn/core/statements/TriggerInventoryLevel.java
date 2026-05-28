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
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Locale;
import java.util.function.Function;

public class TriggerInventoryLevel extends BCStatement implements ITriggerExternal {

	public enum TriggerType {

		BELOW25(0.25F), BELOW50(0.5F), BELOW75(0.75F);
		public final float level;

		TriggerType(float level) {
			this.level = level;
		}
	}

	public TriggerType type;

	public TriggerInventoryLevel(TriggerType type) {
		super("buildcraft:inventorylevel." + type.name().toLowerCase(Locale.ENGLISH),
				"buildcraft.inventorylevel." + type.name().toLowerCase(Locale.ENGLISH),
				"buildcraft.filteredBuffer." + type.name().toLowerCase(Locale.ENGLISH));
		this.type = type;
	}

	@Override
	public int maxParameters() {
		return 1;
	}

	@Override
	public int minParameters() {
		return 1;
	}

	@Override
	public String getDescription() {
		return String.format(StringUtils.localize("gate.trigger.inventorylevel.below"), (int) (type.level * 100));
	}

	@Override
	public boolean isTriggerActive(BlockEntity tile, Direction side, IStatementContainer container, IStatementParameter[] parameters) {
		// A parameter is required
		if (parameters == null || parameters.length < 1 || parameters[0] == null) {
			return false;
		}

		if (tile == null) return false;

		return tile.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).map(handler -> {
			ItemStack searchStack = parameters[0].getItemStack();

			if (searchStack.isEmpty()) {
				return false;
			}

			int stackSpace = 0;
			int foundItems = 0;
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stackInSlot = handler.getStackInSlot(i);
				if (stackInSlot.isEmpty() || ItemStack.isSameItemSameTags(stackInSlot, searchStack)) {
					stackSpace++;
					foundItems += stackInSlot.getCount();
				}
			}

			if (stackSpace > 0) {
				float percentage = foundItems / ((float) stackSpace * (float) searchStack.getMaxStackSize());
				return percentage < type.level;
			}
			return false;
		}).orElse(false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_inventory_" + type.name().toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}
}
