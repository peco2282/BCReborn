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

import com.peco2282.bcreborn.api.gates.GateExpansions;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableDynamicRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.Gate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Set;

public class GatePluggable extends PipePluggable {

	@OnlyIn(Dist.CLIENT)
	private static final class GatePluggableRenderer implements IPipePluggableRenderer, IPipePluggableDynamicRenderer {
		public static final GatePluggableRenderer INSTANCE = new GatePluggableRenderer();

		private GatePluggableRenderer() {

		}

		@Override
		public void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, int renderPass, int x, int y, int z) {
			// PipeRendererTESR.renderGate(x, y, z, (GatePluggable) pipePluggable, side);
		}

		@Override
		public void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, double x, double y, double z) {
			// PipeRendererTESR.renderGate(x, y, z, (GatePluggable) pipePluggable, side);
		}
	}

	public GateDefinition.GateMaterial material;
	public GateDefinition.GateLogic logic;
	public IGateExpansion[] expansions;
	public boolean isLit, isPulsing;

	public Gate realGate, instantiatedGate;
	private float pulseStage;

	public GatePluggable() {
	}

	public GatePluggable(Gate gate) {
		instantiatedGate = gate;
		initFromGate(gate);
	}

	private void initFromGate(Gate gate) {
		this.material = gate.material;
		this.logic = gate.logic;

		Set<IGateExpansion> gateExpansions = gate.expansions.keySet();
		this.expansions = gateExpansions.toArray(new IGateExpansion[gateExpansions.size()]);
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putByte(ItemGate.NBT_TAG_MAT, (byte) material.ordinal());
		nbt.putByte(ItemGate.NBT_TAG_LOGIC, (byte) logic.ordinal());

		ListTag expansionsList = new ListTag();
		for (IGateExpansion expansion : expansions) {
			expansionsList.add(StringTag.valueOf(expansion.getUniqueIdentifier()));
		}
		nbt.put(ItemGate.NBT_TAG_EX, expansionsList);
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		material = GateDefinition.GateMaterial.fromOrdinal(nbt.getByte(ItemGate.NBT_TAG_MAT));
		logic = GateDefinition.GateLogic.fromOrdinal(nbt.getByte(ItemGate.NBT_TAG_LOGIC));

		ListTag expansionsList = nbt.getList(ItemGate.NBT_TAG_EX, Tag.TAG_STRING);
		final int expansionsSize = expansionsList.size();
		expansions = new IGateExpansion[expansionsSize];
		for (int i = 0; i < expansionsSize; i++) {
			expansions[i] = GateExpansions.getExpansion(expansionsList.getString(i));
		}
	}

	@Override
	public void writeData(FriendlyByteBuf buf) {
		buf.writeByte(material.ordinal());
		buf.writeByte(logic.ordinal());
		buf.writeBoolean(realGate != null ? realGate.isGateActive() : false);
		buf.writeBoolean(realGate != null ? realGate.isGatePulsing() : false);

		final int expansionsSize = expansions.length;
		buf.writeShort(expansionsSize);

		for (IGateExpansion expansion : expansions) {
			buf.writeShort(GateExpansions.getExpansionID(expansion));
		}
	}

	@Override
	public void readData(FriendlyByteBuf buf) {
		material = GateDefinition.GateMaterial.fromOrdinal(buf.readByte());
		logic = GateDefinition.GateLogic.fromOrdinal(buf.readByte());
		isLit = buf.readBoolean();
		isPulsing = buf.readBoolean();

		final int expansionsSize = buf.readUnsignedShort();
		expansions = new IGateExpansion[expansionsSize];

		for (int i = 0; i < expansionsSize; i++) {
			expansions[i] = GateExpansions.getExpansionByID(buf.readUnsignedShort());
		}
	}

	@Override
	public boolean requiresRenderUpdate(PipePluggable o) {
		// rendered by TESR
		return false;
	}

	@Override
	public ItemStack[] getDropItems(IPipeTile pipe) {
		ItemStack gate = ItemGate.makeGateItem(material, logic);
		if (gate.isEmpty()) return new ItemStack[0];
		for (IGateExpansion expansion : expansions) {
			ItemGate.addGateExpansion(gate, expansion);
		}
		return new ItemStack[]{gate};
	}

	@Override
	public void update(IPipeTile pipe, Direction direction) {
		if (isPulsing || pulseStage > 0.11F) {
			// if it is moving, or is still in a moved state, then complete
			// the current movement
			pulseStage = (pulseStage + 0.01F) % 1F;
		} else {
			pulseStage = 0;
		}
	}

	@Override
	public void onAttachedPipe(IPipeTile pipe, Direction direction) {
		// TODO: Implement logic without BuildCraft 1.7.10 classes
	}

	@Override
	public void onDetachedPipe(IPipeTile pipe, Direction direction) {
		// TODO: Implement logic without BuildCraft 1.7.10 classes
	}

	@Override
	public boolean isBlocking(IPipeTile pipe, Direction direction) {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof GatePluggable)) {
			return false;
		}
		GatePluggable o = (GatePluggable) obj;
		if (o.material == null || material == null || o.material.ordinal() != material.ordinal()) {
			return false;
		}
		if (o.logic == null || logic == null || o.logic.ordinal() != logic.ordinal()) {
			return false;
		}
		if (o.expansions.length != expansions.length) {
			return false;
		}
		for (int i = 0; i < expansions.length; i++) {
			if (o.expansions[i] != expansions[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public AABB getBoundingBox(Direction side) {
		float min = 0.25F + 0.05F;
		float max = 0.75F - 0.05F;
		return new AABB(min, 0, min, max, 0.1, max); // Placeholder
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IPipePluggableRenderer getRenderer() {
		return GatePluggableRenderer.INSTANCE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IPipePluggableDynamicRenderer getDynamicRenderer() {
		return GatePluggableRenderer.INSTANCE;
	}

	public float getPulseStage() {
		return pulseStage;
	}

	public GateDefinition.GateMaterial getMaterial() {
		return material;
	}

	public GateDefinition.GateLogic getLogic() {
		return logic;
	}

	public IGateExpansion[] getExpansions() {
		return expansions;
	}
}
