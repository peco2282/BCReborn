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
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.transport.Gate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Function;

public class TriggerPipeSignal extends BCStatement implements ITriggerInternal {

	boolean active;
	PipeWire color;

	public TriggerPipeSignal(boolean active, PipeWire color) {
		super("buildcraft:pipe.wire.input." + color.name().toLowerCase(Locale.ENGLISH) + (active ? ".active" : ".inactive"),
				"buildcraft.pipe.wire.input." + color.name().toLowerCase(Locale.ENGLISH) + (active ? ".active" : ".inactive"));

		this.active = active;
		this.color = color;
	}

	@Override
	public int maxParameters() {
		return 3;
	}

	@Override
	public String getDescription() {
		return String.format(StringUtils.localize("gate.trigger.pipe.wire." + (active ? "active" : "inactive")), StringUtils.localize("color." + color.name().toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
		if (!(container instanceof Gate gate)) {
			return false;
		}

		IPipe pipe = gate.getPipe();
		if (pipe == null) {
			return false;
		}

		if (active) {
			if (!pipe.isWireActive(color)) {
				return false;
			}
		} else {
			if (pipe.isWireActive(color)) {
				return false;
			}
		}

		for (IStatementParameter param : parameters) {
			if (param instanceof TriggerParameterSignal signal) {
				if (signal.color != null) {
					if (signal.active) {
						if (!pipe.isWireActive(signal.color)) {
							return false;
						}
					} else {
						if (pipe.isWireActive(signal.color)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_" + color.name().toLowerCase(Locale.ENGLISH) + "_" + (active ? "active" : "inactive")));
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new TriggerParameterSignal();
	}
}
