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

import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public final class GateDefinition {

	private GateDefinition() {
	}

	public static String getLocalizedName(GateMaterial material, GateLogic logic) {
		if (material == GateMaterial.REDSTONE) {
			return StringUtils.localize("gate.name.basic");
		} else {
			return String.format(StringUtils.localize("gate.name"), StringUtils.localize("gate.material." + material.getTag()),
					StringUtils.localize("gate.logic." + logic.getTag()));
		}
	}

	public enum GateMaterial {

		REDSTONE("gate_interface_1.png", 146, 1, 0, 0, 1),
		IRON("gate_interface_2.png", 164, 2, 0, 0, 2),
		GOLD("gate_interface_3.png", 200, 4, 1, 0, 3),
		DIAMOND("gate_interface_4.png", 200, 8, 1, 0, 4),
		EMERALD("gate_interface_5.png", 200, 4, 3, 3, 4),
		QUARTZ("gate_interface_6.png", 164, 2, 1, 1, 3);

		public static final GateMaterial[] VALUES = values();
		public final ResourceLocation guiFile;
		public final int guiHeight;
		public final int numSlots;
		public final int numTriggerParameters;
		public final int numActionParameters;
		public final int maxWireColor;

		GateMaterial(String guiFile, int guiHeight, int numSlots, int triggerParameterSlots,
					 int actionParameterSlots, int maxWireColor) {
			this.guiFile = new ResourceLocation("bcreborntransport", "textures/gui/" + guiFile);
			this.guiHeight = guiHeight;
			this.numSlots = numSlots;
			this.numTriggerParameters = triggerParameterSlots;
			this.numActionParameters = actionParameterSlots;
			this.maxWireColor = maxWireColor;
		}

		public String getTag() {
			return name().toLowerCase(Locale.ENGLISH);
		}

		public static GateMaterial fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal >= VALUES.length) {
				return REDSTONE;
			}
			return VALUES[ordinal];
		}
	}

	public enum GateLogic {

		AND, OR;
		public static final GateLogic[] VALUES = values();

		public String getTag() {
			return name().toLowerCase(Locale.ENGLISH);
		}

		public static GateLogic fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal >= VALUES.length) {
				return AND;
			}
			return VALUES[ordinal];
		}
	}
}
