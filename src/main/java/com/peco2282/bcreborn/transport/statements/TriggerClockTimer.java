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
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.Function;

public class TriggerClockTimer extends BCStatement implements ITriggerInternal {

	public enum Time implements StringRepresentable {

		SHORT(5), MEDIUM(10), LONG(15);
		public static final Time[] VALUES = values();
		public final int delay;

		Time(int delay) {
			this.delay = delay;
		}

    @Override
    public String getSerializedName() {
      return name().toLowerCase(Locale.ENGLISH);
    }
  }

	public final Time time;

	public TriggerClockTimer(Time time) {
		super("buildcraft:timer." + time.name().toLowerCase(Locale.ENGLISH));

		this.time = time;
	}

	@Override
	public String getDescription() {
		return String.format(StringUtils.localize("gate.trigger.timer"), time.delay);
	}

	@Override
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornTransport.location("triggers/trigger_timer_" + time.name().toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public boolean isTriggerActive(IStatementContainer source,
								   IStatementParameter[] parameters) {
		return false;
	}
}
