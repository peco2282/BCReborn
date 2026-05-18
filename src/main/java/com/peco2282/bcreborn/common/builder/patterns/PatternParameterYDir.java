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

public class PatternParameterYDir implements IStatementParameter {
    public static final Codec<PatternParameterYDir> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("up").forGetter(p -> p.up)
    ).apply(instance, PatternParameterYDir::new));
    public boolean up = false;

    public PatternParameterYDir() {
    }

    public PatternParameterYDir(boolean up) {
        this.up = up;
    }

    @Override
    public String getUniqueTag() {
        return "buildcraft:fillerParameterYDir";
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
        return "direction." + (up ? "up" : "down");
    }

    @Override
    public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
        up = !up;
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        up = compound.getBoolean("up");
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        compound.putBoolean("up", up);
    }

    @Override
    public IStatementParameter rotateLeft() {
        return this;
    }
}
