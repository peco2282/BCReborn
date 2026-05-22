package com.peco2282.bcreborn.common.utils;

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;
import com.peco2282.bcreborn.common.block.EntityBlock;
import com.peco2282.bcreborn.core.EntityTypesCore;
import net.minecraft.world.level.Level;

public final class LaserUtils {
	private LaserUtils() {

	}

	public static EntityBlock createLaser(Level world, Position p1, Position p2, LaserKind kind) {
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

		EntityBlock block = new EntityBlock(EntityTypesCore.ENTITY_BLOCK.get(), world, i, j, k, iSize, jSize, kSize);
		// block.setBrightness(210); // TODO: レンダリング側で対応

		world.addFreshEntity(block);

		return block;
	}

	public static EntityBlock[] createLaserBox(Level world, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, LaserKind kind) {
		EntityBlock[] lasers = new EntityBlock[12];
		Position[] p = new Position[8];

		p[0] = new Position(xMin, yMin, zMin);
		p[1] = new Position(xMax, yMin, zMin);
		p[2] = new Position(xMin, yMax, zMin);
		p[3] = new Position(xMax, yMax, zMin);
		p[4] = new Position(xMin, yMin, zMax);
		p[5] = new Position(xMax, yMin, zMax);
		p[6] = new Position(xMin, yMax, zMax);
		p[7] = new Position(xMax, yMax, zMax);

		lasers[0] = createLaser(world, p[0], p[1], kind);
		lasers[1] = createLaser(world, p[0], p[2], kind);
		lasers[2] = createLaser(world, p[2], p[3], kind);
		lasers[3] = createLaser(world, p[1], p[3], kind);
		lasers[4] = createLaser(world, p[4], p[5], kind);
		lasers[5] = createLaser(world, p[4], p[6], kind);
		lasers[6] = createLaser(world, p[5], p[7], kind);
		lasers[7] = createLaser(world, p[6], p[7], kind);
		lasers[8] = createLaser(world, p[0], p[4], kind);
		lasers[9] = createLaser(world, p[1], p[5], kind);
		lasers[10] = createLaser(world, p[2], p[6], kind);
		lasers[11] = createLaser(world, p[3], p[7], kind);

		return lasers;
	}

	public static LaserData[] createLaserDataBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		LaserData[] lasers = new LaserData[12];
		Position[] p = new Position[8];

		p[0] = new Position(xMin, yMin, zMin);
		p[1] = new Position(xMax, yMin, zMin);
		p[2] = new Position(xMin, yMax, zMin);
		p[3] = new Position(xMax, yMax, zMin);
		p[4] = new Position(xMin, yMin, zMax);
		p[5] = new Position(xMax, yMin, zMax);
		p[6] = new Position(xMin, yMax, zMax);
		p[7] = new Position(xMax, yMax, zMax);

		lasers[0] = new LaserData(p[0], p[1]);
		lasers[1] = new LaserData(p[0], p[2]);
		lasers[2] = new LaserData(p[2], p[3]);
		lasers[3] = new LaserData(p[1], p[3]);
		lasers[4] = new LaserData(p[4], p[5]);
		lasers[5] = new LaserData(p[4], p[6]);
		lasers[6] = new LaserData(p[5], p[7]);
		lasers[7] = new LaserData(p[6], p[7]);
		lasers[8] = new LaserData(p[0], p[4]);
		lasers[9] = new LaserData(p[1], p[5]);
		lasers[10] = new LaserData(p[2], p[6]);
		lasers[11] = new LaserData(p[3], p[7]);

		return lasers;
	}
}
