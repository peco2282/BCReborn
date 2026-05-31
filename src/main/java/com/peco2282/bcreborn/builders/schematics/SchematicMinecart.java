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
package com.peco2282.bcreborn.builders.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicEntity;
import com.peco2282.bcreborn.api.blueprints.Translation;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.AABB;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SchematicMinecart extends SchematicEntity {

	private Item baseItem;

	public SchematicMinecart(Item baseItem) {
		this.baseItem = baseItem;
	}

	@Override
	public void translateToBlueprint(Translation transform) {
		super.translateToBlueprint(transform);

		ListTag nbttaglist = entityNBT.getList("Pos", 6);
		Position pos = new Position(nbttaglist.getDouble(0),
				nbttaglist.getDouble(1), nbttaglist.getDouble(2));
		pos.x -= 0.5;
		pos.z -= 0.5;
		entityNBT.put("Pos", this.newDoubleNBTList(pos.x, pos.y, pos.z));
	}


	@Override
	public void translateToWorld(Translation transform) {
		super.translateToWorld(transform);

		ListTag nbttaglist = entityNBT.getList("Pos", 6);
		Position pos = new Position(nbttaglist.getDouble(0),
				nbttaglist.getDouble(1), nbttaglist.getDouble(2));
		pos.x += 0.5;
		pos.z += 0.5;
		entityNBT.put("Pos", this.newDoubleNBTList(pos.x, pos.y, pos.z));
	}

	@Override
	public void readFromWorld(IBuilderContext context, Entity entity) {
		super.readFromWorld(context, entity);

		storedRequirements = new ItemStack[1];
		storedRequirements[0] = new ItemStack(baseItem);
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context) {
		ListTag nbttaglist = entityNBT.getList("Pos", 6);
		Position newPosition = new Position(nbttaglist.getDouble(0),
				nbttaglist.getDouble(1), nbttaglist.getDouble(2));

		AABB searchBox = new AABB(newPosition.x - 0.1, newPosition.y - 0.1, newPosition.z - 0.1, newPosition.x + 0.1, newPosition.y + 0.1, newPosition.z + 0.1);
		List<Minecart> entities = context.world().getEntitiesOfClass(Minecart.class, searchBox);

		for (Minecart e : entities) {
			Position existingPositon = new Position(e.getX(), e.getY(), e.getZ());
			if (existingPositon.isClose(newPosition, 0.1F)) {
				return true;
			}
		}

		return false;
	}

}
