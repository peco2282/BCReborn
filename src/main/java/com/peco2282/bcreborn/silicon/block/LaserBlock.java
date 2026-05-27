/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.block;

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.block.entity.LaserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LaserBlock extends BuildCraftBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    protected static final VoxelShape DOWN_BASE = Block.box(0, 12, 0, 16, 16, 16);
    protected static final VoxelShape DOWN_HEAD = Block.box(5, 3, 5, 11, 12, 11);
    protected static final VoxelShape DOWN = Shapes.or(DOWN_BASE, DOWN_HEAD);

    protected static final VoxelShape UP_BASE = Block.box(0, 0, 0, 16, 4, 16);
    protected static final VoxelShape UP_HEAD = Block.box(5, 4, 5, 11, 13, 11);
    protected static final VoxelShape UP = Shapes.or(UP_BASE, UP_HEAD);

    protected static final VoxelShape NORTH_BASE = Block.box(0, 0, 12, 16, 16, 16);
    protected static final VoxelShape NORTH_HEAD = Block.box(5, 5, 3, 11, 11, 12);
    protected static final VoxelShape NORTH = Shapes.or(NORTH_BASE, NORTH_HEAD);

    protected static final VoxelShape SOUTH_BASE = Block.box(0, 0, 0, 16, 16, 4);
    protected static final VoxelShape SOUTH_HEAD = Block.box(5, 5, 4, 11, 11, 13);
    protected static final VoxelShape SOUTH = Shapes.or(SOUTH_BASE, SOUTH_HEAD);

    protected static final VoxelShape WEST_BASE = Block.box(12, 0, 0, 16, 16, 16);
    protected static final VoxelShape WEST_HEAD = Block.box(3, 5, 5, 12, 11, 11);
    protected static final VoxelShape WEST = Shapes.or(WEST_BASE, WEST_HEAD);

    protected static final VoxelShape EAST_BASE = Block.box(0, 0, 0, 4, 16, 16);
    protected static final VoxelShape EAST_HEAD = Block.box(4, 5, 5, 13, 11, 11);
    protected static final VoxelShape EAST = Shapes.or(EAST_BASE, EAST_HEAD);

    public LaserBlock() {
        super(Properties.of().strength(10F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LaserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SiliconBlockEntityTypes.LASER.get(), LaserBlockEntity.ticker());
    }

    @Override
    public boolean isRotatable() {
        return true;
    }
}
