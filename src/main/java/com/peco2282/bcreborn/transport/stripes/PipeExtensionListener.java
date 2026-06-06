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
package com.peco2282.bcreborn.transport.stripes;

import com.peco2282.bcreborn.api.transport.IStripesActivator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class PipeExtensionListener {
	private static class PipeExtensionRequest {
		public ItemStack stack;
		public BlockPos pos;
		public Direction o;
		public IStripesActivator h;

		@Override
		public boolean equals(Object o1) {
			if (this == o1) return true;
			if (o1 == null || getClass() != o1.getClass()) return false;
			PipeExtensionRequest that = (PipeExtensionRequest) o1;
			return Objects.equals(pos, that.pos) && o == that.o;
		}

		@Override
		public int hashCode() {
			return Objects.hash(pos, o);
		}
	}

	private final Map<Level, HashSet<PipeExtensionRequest>> requests = new HashMap<>();

	public void requestPipeExtension(ItemStack stack, Level world, BlockPos aPos, Direction direction, IStripesActivator activator) {
		if (world.isClientSide) {
			return;
		}

		requests.computeIfAbsent(world, k -> new HashSet<>()).add(new PipeExtensionRequest() {{
			this.stack = stack.copy();
			this.pos = aPos;
			this.o = direction;
			this.h = activator;
		}});
	}

	@SubscribeEvent
	public void tick(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END && requests.containsKey(event.level)) {
			HashSet<PipeExtensionRequest> rSet = requests.get(event.level);
			Level w = event.level;
			for (PipeExtensionRequest r : rSet) {
				// TODO: Implement modern pipe extension logic
				// For now, just send the item back to avoid errors
				r.h.sendItem(r.stack, r.o.getOpposite());
			}
			rSet.clear();
		}
	}
}
