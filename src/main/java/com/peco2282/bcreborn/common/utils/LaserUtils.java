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
package com.peco2282.bcreborn.common.utils;

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;

import java.util.ArrayList;
import java.util.List;

public final class LaserUtils {
	private LaserUtils() {

	}

	public static LaserData createLaser(Position p1, Position p2, LaserKind kind) {
		if (p1.equals(p2)) {
			return null;
		}

		return new LaserData(p1, p2, kind);
	}

	public static List<LaserData> createLaserBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, LaserKind kind) {
		List<LaserData> lasers = new ArrayList<>(12);
		Position[] p = new Position[8];

		p[0] = new Position(xMin + 0.5, yMin + 0.5, zMin + 0.5);
		p[1] = new Position(xMax + 0.5, yMin + 0.5, zMin + 0.5);
		p[2] = new Position(xMin + 0.5, yMax + 0.5, zMin + 0.5);
		p[3] = new Position(xMax + 0.5, yMax + 0.5, zMin + 0.5);
		p[4] = new Position(xMin + 0.5, yMin + 0.5, zMax + 0.5);
		p[5] = new Position(xMax + 0.5, yMin + 0.5, zMax + 0.5);
		p[6] = new Position(xMin + 0.5, yMax + 0.5, zMax + 0.5);
		p[7] = new Position(xMax + 0.5, yMax + 0.5, zMax + 0.5);

		lasers.add(createLaser(p[0], p[1], kind));
		lasers.add(createLaser(p[0], p[2], kind));
		lasers.add(createLaser(p[2], p[3], kind));
		lasers.add(createLaser(p[1], p[3], kind));
		lasers.add(createLaser(p[4], p[5], kind));
		lasers.add(createLaser(p[4], p[6], kind));
		lasers.add(createLaser(p[5], p[7], kind));
		lasers.add(createLaser(p[6], p[7], kind));
		lasers.add(createLaser(p[0], p[4], kind));
		lasers.add(createLaser(p[1], p[5], kind));
		lasers.add(createLaser(p[2], p[6], kind));
		lasers.add(createLaser(p[3], p[7], kind));

		return lasers;
	}

	public static List<LaserData> createLaserDataBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, LaserKind kind) {
		return createLaserBox(xMin, yMin, zMin, xMax, yMax, zMax, kind);
	}
}
