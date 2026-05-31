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
package com.peco2282.bcreborn.factory;



import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PumpDimensionList {

	private List<Entry> entries;

	public PumpDimensionList(String string) {

		entries = new LinkedList<>();

		for (String entryString : string.trim().split(",")) {

			Entry e = new Entry();

			if (entryString.startsWith("+/")) {
				e.isWhitelist = true;
			} else if (entryString.startsWith("-/")) {
				e.isWhitelist = false;
			} else {
				throw new RuntimeException("Malformed pumping.controlList entry: " + entryString + " (must start with +/ or -/)");
			}

			String secondString = entryString.substring(2);
			int i = secondString.indexOf('/');

			if (i < 0) {
				throw new RuntimeException("Malformed pumping.controlList entry: " + secondString
						+ " (missing second /)");
			}

			String dimIDString = secondString.substring(0, i);

			if ("*".equals(dimIDString)) {
				e.matchAnyDim = true;
			} else {
				e.dimID = Integer.parseInt(dimIDString);
			}

			e.fluidName = secondString.substring(i + 1).toLowerCase(Locale.ENGLISH);

			if (e.fluidName.equals("*")) {
				e.matchAnyFluid = true;
			}

			entries.add(0, e);
		}

		entries = new ArrayList<>(entries);
	}

	private static class Entry {
		boolean isWhitelist;
		String fluidName;
		int dimID;
		boolean matchAnyFluid;
		boolean matchAnyDim;

		boolean matches(Fluid fluid, int dim) {
			if (!matchAnyFluid) {
				if (!fluid.getFluidType().getDescriptionId().equals(fluidName)) {
					return false;
				}
			}

			if (!matchAnyDim && dimID != dim) {
				return false;
			}

			return true;
		}
	}

	public boolean isFluidAllowed(Fluid fluid, int dim) {
		for (Entry e : entries) {
			if (e.matches(fluid, dim)) {
				return e.isWhitelist;
			}
		}
		return false;
	}


}
