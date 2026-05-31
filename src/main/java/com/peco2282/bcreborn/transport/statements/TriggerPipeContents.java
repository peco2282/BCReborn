/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.transport.statements;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.transport.Gate;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;
import java.util.function.Function;

public class TriggerPipeContents extends BCStatement implements ITriggerInternal {

	public enum PipeContents {
		empty,
		containsItems,
		containsFluids,
		containsEnergy,
		requestsEnergy,
		tooMuchEnergy;
		public ITriggerInternal trigger;
	}

	private PipeContents kind;

	public TriggerPipeContents(PipeContents kind) {
		super("buildcraft:pipe.contents." + kind.name().toLowerCase(Locale.ENGLISH), "buildcraft.pipe.contents." + kind.name());
		this.kind = kind;
		kind.trigger = this;
	}

	@Override
	public int maxParameters() {
    return switch (kind) {
      case containsItems, containsFluids -> 1;
      default -> 0;
    };
	}

	@Override
	public String getDescription() {
		return StringUtils.localize("gate.trigger.pipe." + kind.name());
	}

	@Override
	public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
		if (!(container instanceof Gate gate)) {
			return false;
		}

		IPipe apiPipe = gate.getPipe();
		if (apiPipe == null) {
			return false;
		}
		
		PipeBlockEntity pipe;
		if (apiPipe instanceof PipeBlockEntity) {
			pipe = (PipeBlockEntity) apiPipe;
		} else if (apiPipe.getTile() instanceof PipeBlockEntity) {
			pipe = (PipeBlockEntity) apiPipe.getTile();
		} else {
			return false;
		}

		IStatementParameter parameter = parameters.length > 0 ? parameters[0] : null;

		if (kind == PipeContents.empty) {
			return pipe.getTravelingItems().isEmpty();
		} else if (kind == PipeContents.containsItems) {
			if (parameter != null && parameter.getItemStack() != null && !parameter.getItemStack().isEmpty()) {
				for (TravelingItem item : pipe.getTravelingItems()) {
					if (ItemStack.isSameItemSameTags(parameter.getItemStack(), item.getStack())) {
						return true;
					}
				}
			} else {
				return !pipe.getTravelingItems().isEmpty();
			}
		}

		return false;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterItemStack(ItemStack.EMPTY);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipecontents_" + kind.name().toLowerCase(Locale.ENGLISH)));
	}
}
