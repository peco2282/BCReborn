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
package com.peco2282.bcreborn.common.gui.tooltips;

import com.google.common.collect.ForwardingList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

public class ToolTip extends ForwardingList<ToolTipLine> {

	private final List<ToolTipLine> delegate = new ArrayList<ToolTipLine>();
	private final long delay;
	private long mouseOverStart;

	public ToolTip(ToolTipLine... lines) {
		this.delay = 0;
		Collections.addAll(delegate, lines);
	}

	public ToolTip(int delay, ToolTipLine... lines) {
		this.delay = delay;
		Collections.addAll(delegate, lines);
	}

	@Override
	protected final List<ToolTipLine> delegate() {
		return delegate;
	}

	public final Component getTooltipComponent() {
		return delegate.stream()
				.map(ToolTipLine::getComponent)
				.collect(Collector.of(
						Component::empty,
            MutableComponent::append,
            MutableComponent::append,
						Component::copy
				));
	}

	public void onTick(boolean mouseOver) {
		if (delay == 0) {
			return;
		}
		if (mouseOver) {
			if (mouseOverStart == 0) {
				mouseOverStart = System.currentTimeMillis();
			}
		} else {
			mouseOverStart = 0;
		}
	}

	public boolean isReady() {
		if (delay == 0) {
			return true;
		}
		if (mouseOverStart == 0) {
			return false;
		}
		return System.currentTimeMillis() - mouseOverStart >= delay;
	}

	public void refresh() {
	}
}
