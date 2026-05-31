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
package com.peco2282.bcreborn.transport.gates;

import com.peco2282.bcreborn.api.gates.GateExpansionController;
import com.peco2282.bcreborn.api.gates.GateExpansions;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.transport.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition.GateLogic;
import com.peco2282.bcreborn.transport.gates.GateDefinition.GateMaterial;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GateFactory {

	/**
	 * Deactivate constructor
	 */
	private GateFactory() {
	}

	public static Gate makeGate(IPipe pipe, GateMaterial material, GateLogic logic, Direction direction) {
		return new Gate(pipe, material, logic, direction);
	}

	public static Gate makeGate(IPipe pipe, ItemStack stack, Direction direction) {
		if (stack.isEmpty() /* || !(stack.getItem() instanceof ItemGate) */) {
			return null;
		}

		// TODO: ItemGate
		Gate gate = makeGate(pipe, GateMaterial.REDSTONE, GateLogic.AND, direction);

		/*
		for (IGateExpansion expansion : ItemGate.getInstalledExpansions(stack)) {
			gate.addGateExpansion(expansion);
		}
		*/

		return gate;
	}

	public static Gate makeGate(IPipe pipe, CompoundTag nbt) {
		GateMaterial material = GateMaterial.REDSTONE;
		GateLogic logic = GateLogic.AND;
		Direction direction = Direction.UP;

		if (nbt.contains("material")) {
			try {
				material = GateMaterial.valueOf(nbt.getString("material"));
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
		if (nbt.contains("logic")) {
			try {
				logic = GateLogic.valueOf(nbt.getString("logic"));
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
		if (nbt.contains("direction")) {
			direction = Direction.values()[nbt.getInt("direction")];
		}

		Gate gate = makeGate(pipe, material, logic, direction);
		gate.readFromNBT(nbt);

		ListTag exList = nbt.getList("expansions", 10);
		for (int i = 0; i < exList.size(); i++) {
			CompoundTag conNBT = exList.getCompound(i);
			IGateExpansion ex = GateExpansions.getExpansion(conNBT.getString("type"));
			if (ex != null) {
				GateExpansionController con = ex.makeController(pipe.getTile() instanceof BlockEntity be ? be : null);
				con.readFromNBT(conNBT.getCompound("data"));
				gate.expansions.put(ex, con);
			}
		}

		return gate;
	}
}
