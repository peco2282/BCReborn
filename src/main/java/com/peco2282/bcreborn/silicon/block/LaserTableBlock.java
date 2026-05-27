/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.block;

import com.peco2282.bcreborn.api.power.ILaserTargetBlock;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.block.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LaserTableBlock extends BuildCraftBlock implements ILaserTargetBlock {
    protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public LaserTableBlock() {
        super(Properties.of().strength(10F).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof LaserTableBaseBlockEntity table) {
            // TODO: GUI opening logic
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (this == SiliconBlocks.ASSEMBLY_TABLE.get()) return new AssemblyTableBlockEntity(pos, state);
        if (this == SiliconBlocks.ADVANCED_CRAFTING_TABLE.get()) return new AdvancedCraftingTableBlockEntity(pos, state);
        if (this == SiliconBlocks.INTEGRATION_TABLE.get()) return new IntegrationTableBlockEntity(pos, state);
        if (this == SiliconBlocks.CHARGING_TABLE.get()) return new ChargingTableBlockEntity(pos, state);
        if (this == SiliconBlocks.PROGRAMMING_TABLE.get()) return new ProgrammingTableBlockEntity(pos, state);
        if (this == SiliconBlocks.STAMPING_TABLE.get()) return new StampingTableBlockEntity(pos, state);
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        BlockEntityType<? extends LaserTableBaseBlockEntity> targetType = null;
        if (this == SiliconBlocks.ASSEMBLY_TABLE.get()) targetType = SiliconBlockEntityTypes.ASSEMBLY_TABLE.get();
        else if (this == SiliconBlocks.ADVANCED_CRAFTING_TABLE.get()) targetType = SiliconBlockEntityTypes.ADVANCED_CRAFTING_TABLE.get();
        else if (this == SiliconBlocks.INTEGRATION_TABLE.get()) targetType = SiliconBlockEntityTypes.INTEGRATION_TABLE.get();
        else if (this == SiliconBlocks.CHARGING_TABLE.get()) targetType = SiliconBlockEntityTypes.CHARGING_TABLE.get();
        else if (this == SiliconBlocks.PROGRAMMING_TABLE.get()) targetType = SiliconBlockEntityTypes.PROGRAMMING_TABLE.get();
        else if (this == SiliconBlocks.STAMPING_TABLE.get()) targetType = SiliconBlockEntityTypes.STAMPING_TABLE.get();

        if (targetType != null) {
            return createTickerHelper(type, targetType, LaserTableBaseBlockEntity.ticker());
        }
        return null;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    }

    @Override
    public boolean isRotatable() {
        return false;
    }
}
