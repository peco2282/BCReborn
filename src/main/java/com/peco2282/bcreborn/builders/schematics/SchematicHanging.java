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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SchematicHanging extends SchematicEntity {

	private Item baseItem;

	public SchematicHanging(Item baseItem) {
		this.baseItem = baseItem;
	}

	@Override
	public void translateToBlueprint(Translation transform) {
		super.translateToBlueprint(transform);

		Position pos = new Position(entityNBT.getInt("TileX"), entityNBT.getInt("TileY"), entityNBT.getInt("TileZ"));
		pos = transform.translate(pos);
		entityNBT.putInt("TileX", (int) pos.x);
		entityNBT.putInt("TileY", (int) pos.y);
		entityNBT.putInt("TileZ", (int) pos.z);
	}

	@Override
	public void translateToWorld(Translation transform) {
		super.translateToWorld(transform);

		Position pos = new Position(entityNBT.getInt("TileX"), entityNBT.getInt("TileY"), entityNBT.getInt("TileZ"));
		pos = transform.translate(pos);
		entityNBT.putInt("TileX", (int) pos.x);
		entityNBT.putInt("TileY", (int) pos.y);
		entityNBT.putInt("TileZ", (int) pos.z);
	}

	@Override
	public void rotateLeft(IBuilderContext context) {
		super.rotateLeft(context);

		Position pos = new Position(entityNBT.getInt("TileX"), entityNBT.getInt("TileY"), entityNBT.getInt("TileZ"));
		pos = context.rotatePositionLeft(pos);
		entityNBT.putInt("TileX", (int) pos.x);
		entityNBT.putInt("TileY", (int) pos.y);
		entityNBT.putInt("TileZ", (int) pos.z);

		int direction = entityNBT.getByte("Direction");
		direction = direction < 3 ? direction + 1 : 0;
		entityNBT.putByte("Direction", (byte) direction);
	}

	@Override
	public void readFromWorld(IBuilderContext context, Entity entity) {
		super.readFromWorld(context, entity);

		if (baseItem == Items.ITEM_FRAME) {
			CompoundTag tag = entityNBT.getCompound("Item");
			ItemStack stack = ItemStack.of(tag);

			if (stack != null) {
				storedRequirements = new ItemStack[2];
				storedRequirements[0] = new ItemStack(baseItem);
				storedRequirements[1] = stack;
			} else {
				storedRequirements = new ItemStack[1];
				storedRequirements[0] = new ItemStack(baseItem);
			}
		} else {
			storedRequirements = new ItemStack[1];
			storedRequirements[0] = new ItemStack(baseItem);
		}
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context) {
		Position newPosition = new Position(entityNBT.getInt("TileX"), entityNBT.getInt("TileY"), entityNBT.getInt("TileZ"));

		int dir = entityNBT.getInt("Direction");

		AABB searchBox = new AABB(newPosition.x - 1, newPosition.y - 1, newPosition.z - 1, newPosition.x + 1, newPosition.y + 1, newPosition.z + 1);
		List<HangingEntity> entities = context.world().getEntitiesOfClass(HangingEntity.class, searchBox);

		for (HangingEntity h : entities) {
			Position existingPositon = new Position(h.getX(), h.getY(), h.getZ());

			if (existingPositon.isClose(newPosition, 0.1F) && dir == h.getDirection().get2DDataValue()) {
				return true;
			}
		}

		return false;
	}
}
