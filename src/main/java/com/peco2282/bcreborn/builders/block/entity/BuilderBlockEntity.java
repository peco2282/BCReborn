package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class BuilderBlockEntity extends BuildCraftBlockEntity {
    private static int POWER_ACTIVATION = 500;

    private SimpleInventory inv = new SimpleInventory(28, "Builder", 64);
    private List<RequirementItemStack> requiredToBuild;
    private CompoundTag initNBT = null;
    private boolean done = true;
    private boolean isBuilding = false;

    public BuilderBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityTypesBuilders.BUILDER.get(), p_155229_, p_155230_);
    }

    @Override
    protected void tick(Level level, BlockPos pos, BlockState state) {
    }

    public void setItemRequirements(List<RequirementItemStack> requirements) {
        this.requiredToBuild = new ArrayList<>(requirements);
    }
}
