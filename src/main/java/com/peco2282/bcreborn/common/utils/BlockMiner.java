package com.peco2282.bcreborn.common.utils;



import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;

import java.util.List;

public class BlockMiner {
	protected final Level world;
	protected final BlockEntity owner;
	protected final int x, y, z, minerId;

	private boolean hasMined, hasFailed;
	private int energyRequired, energyAccepted;

	public BlockMiner(Level world, BlockEntity owner, int x, int y, int z) {
		this.world = world;
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.z = z;
		this.minerId = world.random.nextInt();
	}

	public boolean hasMined() {
		return hasMined;
	}

	public boolean hasFailed() {
		return hasFailed;
	}

	public void mineStack(ItemStack stack) {
		// First, try to add to a nearby chest
		var c = stack.getCount() - Utils.addToRandomInventoryAround(owner.getLevel(), owner.getBlockPos(), stack);
		stack.setCount(c);

		// Second, try to add to adjacent pipes
		if (stack.getCount() > 0) {
			var c1 = stack.getCount() - Utils.addToRandomInjectableAround(owner.getLevel(), owner.getBlockPos(), null, stack);
			stack.setCount(c1);
		}

		// Lastly, throw the object away
		if (stack.getCount() > 0) {
			float f = world.random.nextFloat() * 0.8F + 0.1F;
			float f1 = world.random.nextFloat() * 0.8F + 0.1F;
			float f2 = world.random.nextFloat() * 0.8F + 0.1F;

			ItemEntity entityitem = new ItemEntity(owner.getLevel(), owner.getBlockPos().getX() + f, owner.getBlockPos().getY() + f1 + 0.5F, owner.getBlockPos().getZ() + f2, stack);

			entityitem.lifespan = 6000;
			entityitem.setPickUpDelay(10);

			float f3 = 0.05F;
			entityitem.setDeltaMovement((float) world.random.nextGaussian() * f3, (float) world.random.nextGaussian() * f3 + 1.0F, (float) world.random.nextGaussian() * f3);
			owner.getLevel().addFreshEntity(entityitem);
		}
	}

	public void invalidate() {
		world.destroyBlockProgress(minerId, new BlockPos(x, y, z), -1);
	}

	public int acceptEnergy(int offeredAmount) {
		BlockPos pos = new BlockPos(x, y, z);
		if (BlockUtils.isUnbreakableBlock(world, pos)) {
			hasFailed = true;
			return 0;
		}

		energyRequired = BlockUtils.computeBlockBreakEnergy(world, pos);

		int usedAmount = MathUtils.clamp(offeredAmount, 0, Math.max(0, energyRequired - energyAccepted));
		energyAccepted += usedAmount;

		if (energyAccepted >= energyRequired) {
			world.destroyBlockProgress(minerId, new BlockPos(x, y, z), -1);

			hasMined = true;

			Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
//			int meta = world.getBlockMetadata(x, y, z);
			BlockState meta = world.getBlockState(new BlockPos(x, y, z));

			BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos),
					BCFakePlayer.createBuildCraftPlayer((ServerLevel) owner.getLevel(), owner.getBlockPos()));
			MinecraftForge.EVENT_BUS.post(breakEvent);

			if (!breakEvent.isCanceled()) {
				List<ItemStack> stacks = BlockUtils.getItemStackFromBlock((ServerLevel) world, pos);

				if (stacks != null) {
					for (ItemStack s : stacks) {
						if (s != null) {
							mineStack(s);
						}
					}
				}

				world.levelEvent(2001, pos, Block.getId(world.getBlockState(pos)));

				world.removeBlock(pos, false);
			} else {
				hasFailed = true;
			}
		} else {
			world.destroyBlockProgress(minerId, pos, MathUtils.clamp((int) Math.floor(energyAccepted * 10 / energyRequired), 0, 9));
		}
		return usedAmount;
	}
}
