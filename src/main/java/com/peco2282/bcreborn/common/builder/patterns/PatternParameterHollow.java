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
package com.peco2282.bcreborn.common.builder.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class PatternParameterHollow implements IStatementParameter {
    public static final Codec<PatternParameterHollow> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("filled").forGetter(p -> p.filled)
    ).apply(instance, PatternParameterHollow::new));
    private TextureAtlasSprite icon;
    public boolean filled = false;

    public PatternParameterHollow() {
    }

    public PatternParameterHollow(boolean hollow) {
        this.filled = !hollow;
    }

    @Override
    public String getUniqueTag() {
        return "buildcraft:fillerParameterHollow";
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return this.icon;
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        this.icon = textureGetter.apply(BCRebornCore.location("item/filler_parameter/hollow.png"));
    }

    @Override
    public String getDescription() {
        return "fillerpattern.parameter." + (filled ? "filled" : "hollow");
    }

    @Override
    public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
        filled = !filled;
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        filled = compound.getBoolean("filled");
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        compound.putBoolean("filled", filled);
    }

    @Override
    public IStatementParameter rotateLeft() {
        return this;
    }
}
