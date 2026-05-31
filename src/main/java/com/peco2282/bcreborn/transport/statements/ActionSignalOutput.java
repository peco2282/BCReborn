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

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.transport.Gate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Function;

public class ActionSignalOutput extends BCStatement implements IActionInternal {

	public final PipeWire color;

	public ActionSignalOutput(PipeWire color) {
		super("pipe.wire.output." + color.name().toLowerCase(Locale.ENGLISH));

		this.color = color;
	}

	@Override
	public String getDescription() {
		return String.format(StringUtils.localize("gate.action.pipe.wire"), StringUtils.localize("color." + color.name().toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("buildcrafttransport:triggers/action_signal_" + color.name().toLowerCase(Locale.ENGLISH)));
	}

	@Override
	public int maxParameters() {
		return 3;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new ActionParameterSignal();
	}

	@Override
	public void actionActivate(IStatementContainer container, IStatementParameter[] parameters) {
		Gate gate = (Gate) container;

		gate.broadcastSignal(color);

		for (IStatementParameter param : parameters) {
			if (param != null && param instanceof ActionParameterSignal signal) {
				if (signal.color != null) {
					gate.broadcastSignal(signal.color);
				}
			}
		}
	}
}
