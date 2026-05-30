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

		double iSize = p2.x - p1.x;
		double jSize = p2.y - p1.y;
		double kSize = p2.z - p1.z;

		double i = p1.x;
		double j = p1.y;
		double k = p1.z;

		if (iSize != 0) {
			i += 0.5;
			j += 0.45;
			k += 0.45;

			jSize = 0.10;
			kSize = 0.10;
		} else if (jSize != 0) {
			i += 0.45;
			j += 0.5;
			k += 0.45;

			iSize = 0.10;
			kSize = 0.10;
		} else if (kSize != 0) {
			i += 0.45;
			j += 0.45;
			k += 0.5;

			iSize = 0.10;
			jSize = 0.10;
		}

//		EntityBlock block = new EntityBlock(EntityTypesCore.ENTITY_BLOCK.get(), world, i, j, k, iSize, jSize, kSize);
		// block.setBrightness(210); // TODO: レンダリング側で対応

//		world.addFreshEntity(block);

		return new LaserData(new Position(i, j, k), new Position(i + iSize, j + jSize, k + kSize), kind);
	}

	public static List<LaserData> createLaserBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, LaserKind kind) {
		List<LaserData> lasers = new ArrayList<>(12);
		Position[] p = new Position[8];

		p[0] = new Position(xMin, yMin, zMin);
		p[1] = new Position(xMax, yMin, zMin);
		p[2] = new Position(xMin, yMax, zMin);
		p[3] = new Position(xMax, yMax, zMin);
		p[4] = new Position(xMin, yMin, zMax);
		p[5] = new Position(xMax, yMin, zMax);
		p[6] = new Position(xMin, yMax, zMax);
		p[7] = new Position(xMax, yMax, zMax);

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

		lasers.add(new LaserData(p[0], p[1], kind));
		lasers.add(new LaserData(p[0], p[2], kind));
		lasers.add(new LaserData(p[2], p[3], kind));
		lasers.add(new LaserData(p[1], p[3], kind));
		lasers.add(new LaserData(p[4], p[5], kind));
		lasers.add(new LaserData(p[4], p[6], kind));
		lasers.add(new LaserData(p[5], p[7], kind));
		lasers.add(new LaserData(p[6], p[7], kind));
		lasers.add(new LaserData(p[0], p[4], kind));
		lasers.add(new LaserData(p[1], p[5], kind));
		lasers.add(new LaserData(p[2], p[6], kind));
		lasers.add(new LaserData(p[3], p[7], kind));

		return lasers;
	}
}
