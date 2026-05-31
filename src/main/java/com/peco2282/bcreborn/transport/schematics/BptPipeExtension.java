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
package com.peco2282.bcreborn.transport.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import net.minecraft.world.item.Item;

import java.util.HashMap;

public class BptPipeExtension {

	private static final HashMap<Item, BptPipeExtension> bptPipeExtensionRegistry = new HashMap<Item, BptPipeExtension>();

	public BptPipeExtension(Item i) {
		bptPipeExtensionRegistry.put(i, this);
	}

	public void postProcessing(SchematicTile slot, IBuilderContext context) {

	}

	public void rotateLeft(SchematicTile slot, IBuilderContext context) {

	}

	public static boolean contains(Item i) {
		return bptPipeExtensionRegistry.containsKey(i);
	}

	public static BptPipeExtension get(Item i) {
		return bptPipeExtensionRegistry.get(i);
	}

}
