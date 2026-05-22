/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.builders.blueprints;


import com.peco2282.bcreborn.api.blueprints.BlueprintDeployer;
import com.peco2282.bcreborn.api.blueprints.Translation;
import com.peco2282.bcreborn.common.blueprint.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class RealBlueprintDeployer extends BlueprintDeployer {

	@Override
	public void deployBlueprint(Level world, int x, int y, int z,
                              Direction dir, File file) {

    try {
      deployBlueprint(world, x, y, z, dir, (Blueprint) BlueprintBase.loadBluePrint(NbtIo.read(file)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

	@Override
	public void deployBlueprintFromFileStream(Level world, int x, int y, int z,
											  Direction dir, byte[] data) {

    try {
      deployBlueprint(world, x, y, z, dir, (Blueprint) BlueprintBase.loadBluePrint(NbtIo.readCompressed(new ByteArrayInputStream(data))));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
	}

	private void deployBlueprint(Level world, int x, int y, int z, Direction dir, Blueprint bpt) {
		bpt.id = new LibraryId();
		bpt.id.extension = "bpt";

		BptContext context = bpt.getContext(world, bpt.getBoxForPos(x, y, z));

		if (bpt.rotate) {
			if (dir == Direction.EAST) {
				// Do nothing
			} else if (dir == Direction.SOUTH) {
				bpt.rotateLeft(context);
			} else if (dir == Direction.WEST) {
				bpt.rotateLeft(context);
				bpt.rotateLeft(context);
			} else if (dir == Direction.NORTH) {
				bpt.rotateLeft(context);
				bpt.rotateLeft(context);
				bpt.rotateLeft(context);
			}
		}

		Translation transform = new Translation();

		transform.x = x - bpt.anchorX;
		transform.y = y - bpt.anchorY;
		transform.z = z - bpt.anchorZ;

		bpt.translateToWorld(transform);

		new BptBuilderBlueprint(bpt, world, x, y, z).deploy();
	}
}

