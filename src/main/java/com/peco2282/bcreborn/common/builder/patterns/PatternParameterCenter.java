package com.peco2282.bcreborn.common.builder.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class PatternParameterCenter implements IStatementParameter {
    public static final Codec<PatternParameterCenter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("direction").forGetter(p -> p.direction)
    ).apply(instance, PatternParameterCenter::new));
    private static final int[] shiftLeft = {6, 3, 0, 7, 4, 1, 8, 5, 2};
    private int direction;

    public PatternParameterCenter() {
    }

    public PatternParameterCenter(int direction) {
        this.direction = direction;
    }

    @Override
    public String getUniqueTag() {
        return "buildcraft:fillerParameterCenter";
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return null;
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    }

    @Override
    public String getDescription() {
        return "direction.center." + direction;
    }

    @Override
    public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
        direction = (direction + 1) % 9;
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        direction = compound.getByte("dir");
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        compound.putByte("dir", (byte) direction);
    }

    @Override
    public IStatementParameter rotateLeft() {
        return new PatternParameterCenter(shiftLeft[direction % 9]);
    }

    public int getDirection() {
        return direction;
    }
}
