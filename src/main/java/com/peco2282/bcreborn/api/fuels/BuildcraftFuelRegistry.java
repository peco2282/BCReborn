/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.fuels;


public final class BuildcraftFuelRegistry {
	private static IFuelManager fuel;

	private static ICoolantManager coolant;

	public static IFuelManager getFuelManager() {
		return fuel;
	}

	public static ICoolantManager getCoolantManager() {
		return coolant;
	}

	public static void setFuelManager(IFuelManager manager) {
		if (fuel != null) {
			throw new IllegalStateException("Fuel manager already set");
		}
		fuel = manager;
	}

	public static void setCoolantManager(ICoolantManager manager) {
		if (coolant != null) {
			throw new IllegalStateException("Coolant manager already set");
		}
		coolant = manager;
	}

	private BuildcraftFuelRegistry() {
	}
}
