package com.peco2282.bcreborn.silicon.item;

import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.silicon.entity.PackageEntity;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PackageItem extends BuildCraftItem {

	public static final class DispenseBehaviour extends DefaultDispenseItemBehavior {
		@Override
		public ItemStack execute(BlockSource source, ItemStack stack) {
			Level world = source.getLevel();
			var direction = source.getBlockState().getValue(DispenserBlock.FACING);

			PackageEntity entityPackage = new PackageEntity(world,
					source.x() + direction.getStepX(),
					source.y() + direction.getStepY(),
					source.z() + direction.getStepZ(), stack.copy());
			entityPackage.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), 1.1F, 6.0F);
			world.addFreshEntity(entityPackage);
			stack.shrink(1);
			return stack;
		}
	}

	public PackageItem() {
		super(new Properties().stacksTo(1));
	}

	public static void update(ItemStack stack) {
		// NOP
	}

	public static ItemStack getStack(ItemStack stack, int slot) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag != null && tag.contains("item" + slot)) {
			return ItemStack.of(tag.getCompound("item" + slot));
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!world.isClientSide) {
			world.addFreshEntity(new PackageEntity(world, player, stack.copy()));
		}

		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag != null && !tag.isEmpty()) {
			tooltip.add(Component.literal("{{BC_PACKAGE_SPECIAL:0}}"));
			tooltip.add(Component.literal("{{BC_PACKAGE_SPECIAL:1}}"));
			tooltip.add(Component.literal("{{BC_PACKAGE_SPECIAL:2}}"));
		}
	}
}
