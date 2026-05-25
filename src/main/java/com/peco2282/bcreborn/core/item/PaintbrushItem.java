package com.peco2282.bcreborn.core.item;


import com.peco2282.bcreborn.api.blocks.IColorRemovable;
import com.peco2282.bcreborn.api.core.EnumColor;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class PaintbrushItem extends BuildCraftItem {
    private static final String TAG_COLOR = "color";

    public PaintbrushItem() {
        super(new Properties().stacksTo(1).durability(63));
    }

    public static int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_COLOR) ? tag.getByte(TAG_COLOR) : -1;
    }

    public static void setColor(ItemStack stack, int color) {
        if (color < 0) {
            stack.removeTagKey(TAG_COLOR);
        } else {
            stack.getOrCreateTag().putByte(TAG_COLOR, (byte) color);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        Component base = super.getName(stack);
        int color = getColor(stack);

        if (color >= 0) {
            return Component.literal(base.getString() + " (" + EnumColor.fromId(color) + ")");
        }

        return base;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int color = getColor(stack);
        if (color >= 0) {
            tooltip.add(Component.translatable("color.minecraft." + DyeColor.byId(15 - color).getName()));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();

        int color = getColor(stack);
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (color >= 0) {
            DyeColor dyeColor = DyeColor.byId(15 - color);

            if (block.recolorBlock(state, level, pos, side, dyeColor)) {
                if (!level.isClientSide) {
                    stack.hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
                }

                if (context.getPlayer() != null) {
                    context.getPlayer().swing(context.getHand());
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else if (block instanceof IColorRemovable removable) {
            if (removable.removeColorFromBlock(level, pos.getX(), pos.getY(), pos.getZ(), side)) {
                if (context.getPlayer() != null) {
                    context.getPlayer().swing(context.getHand());
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.level.LevelReader world, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        return true;
    }
}