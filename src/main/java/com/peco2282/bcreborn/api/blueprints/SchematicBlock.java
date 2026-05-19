/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.blueprints;

import com.peco2282.bcreborn.api.core.BlockIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class SchematicBlock extends SchematicBlockBase {
	public static final BlockIndex[] RELATIVE_INDEXES = new BlockIndex[] {
			new BlockIndex(0, -1, 0),
			new BlockIndex(0, 1, 0),
			new BlockIndex(0, 0, -1),
			new BlockIndex(0, 0, 1),
			new BlockIndex(-1, 0, 0),
			new BlockIndex(1, 0, 0),
	};
	protected Direction facing = Direction.UP;

	public Block block = null;
	public BuildingPermission defaultPermission = BuildingPermission.ALL;

	/**
	 * This field contains requirements for a given block when stored in the
	 * blueprint. Modders can either rely on this list or compute their own int
	 * Schematic.
	 */
	public ItemStack[] storedRequirements = new ItemStack[0];

	private boolean doNotUse = false;

	@Override
	public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
		if (block != null) {
			if (storedRequirements.length != 0) {
				Collections.addAll(requirements, storedRequirements);
			} else {
				requirements.add(new ItemStack(block));
			}
		}
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		BlockState state = context.world().getBlockState(new BlockPos(x, y, z));
		return block == state.getBlock();
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		super.placeInWorld(context, x, y, z, stacks);

		this.setBlockInWorld(context, x, y, z);
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		super.storeRequirements(context, x, y, z);

		if (block != null) {
			BlockPos pos = new BlockPos(x, y, z);
			List<ItemStack> req = Block.getDrops(
					context.world().getBlockState(pos),
					(net.minecraft.server.level.ServerLevel) context.world(),
					pos,
					context.world().getBlockEntity(pos));

			if (req != null) {
				storedRequirements = req.toArray(new ItemStack[0]);
			}
		}
	}

	@Override
	public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
		super.writeSchematicToNBT(nbt, registry);

		writeBlockToNBT(nbt, registry);
		writeRequirementsToNBT(nbt, registry);
	}

	@Override
	public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
		super.readSchematicFromNBT(nbt, registry);

		readBlockFromNBT(nbt, registry);
		if (!doNotUse()) {
			readRequirementsFromNBT(nbt, registry);
		}
	}

	/**
	 * Get a list of relative block coordinates which have to be built before
	 * this block can be placed.
	 */
	public Set<BlockIndex> getPrerequisiteBlocks(IBuilderContext context) {
		Set<BlockIndex> indexes = new HashSet<BlockIndex>();
		if (block instanceof FallingBlock) {
			indexes.add(RELATIVE_INDEXES[1]); // DOWN index
		}
		return indexes;
	}

	@Override
	public BuildingStage getBuildStage() {
		if (block instanceof LiquidBlock) {
			return BuildingStage.EXPANDING;
		} else {
			return BuildingStage.STANDALONE;
		}
	}

	@Override
	public BuildingPermission getBuildingPermission() {
		return defaultPermission;
	}

	// Utility functions
	protected void setBlockInWorld(IBuilderContext context, int x, int y, int z) {
		if (block != null) {
			context.world().setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
		}
	}

	public boolean doNotUse() {
		return doNotUse;
	}

	protected void readBlockFromNBT(CompoundTag nbt, MappingRegistry registry) {
		try {
			block = registry.getBlockForId(nbt.getInt("blockId"));
		} catch (MappingNotFoundException e) {
			doNotUse = true;
		}
	}

	protected void readRequirementsFromNBT(CompoundTag nbt, MappingRegistry registry) {
		if (nbt.contains("rq")) {
			ListTag rq = nbt.getList("rq", Tag.TAG_COMPOUND);

			ArrayList<ItemStack> rqs = new ArrayList<ItemStack>();
			for (int i = 0; i < rq.size(); ++i) {
				try {
					CompoundTag sub = rq.getCompound(i);
					if (sub.getInt("id") >= 0) {
						registry.stackToWorld(sub);
						ItemStack stack = ItemStack.of(sub);
						if (!stack.isEmpty()) {
							rqs.add(stack);
						}
					} else {
						defaultPermission = BuildingPermission.CREATIVE_ONLY;
					}
				} catch (MappingNotFoundException e) {
					defaultPermission = BuildingPermission.CREATIVE_ONLY;
				} catch (Throwable t) {
					t.printStackTrace();
					defaultPermission = BuildingPermission.CREATIVE_ONLY;
				}
			}

			storedRequirements = rqs.toArray(new ItemStack[0]);
		} else {
			storedRequirements = new ItemStack[0];
		}
	}

	protected void writeBlockToNBT(CompoundTag nbt, MappingRegistry registry) {
		nbt.putInt("blockId", registry.getIdForBlock(block));
	}

	protected void writeRequirementsToNBT(CompoundTag nbt, MappingRegistry registry) {
		if (storedRequirements.length > 0) {
			ListTag rq = new ListTag();

			for (ItemStack stack : storedRequirements) {
				CompoundTag sub = new CompoundTag();
				stack.save(sub);
				registry.stackToRegistry(sub);
				rq.add(sub);
			}

			nbt.put("rq", rq);
		}
	}
}
