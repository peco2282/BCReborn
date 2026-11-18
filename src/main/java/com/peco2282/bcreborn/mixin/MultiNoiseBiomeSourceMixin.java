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
package com.peco2282.bcreborn.mixin;

import com.peco2282.bcreborn.energy.worldgen.OilBiomes;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Repaints parts of the vanilla desert and ocean as oil fields (original GenLayerAddOilDesert / GenLayerAddOilOcean),
 * see {@link OilBiomes}. The oil biomes are added to the possible biomes of any source that has the desert or the
 * ocean, so their features are ordered with the rest; a source built before they were known never swaps.
 */
@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {
  @Inject(method = "collectPossibleBiomes", at = @At("RETURN"), cancellable = true)
  private void bcreborn$addOilBiomes(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) {
    OilBiomes.Field field = OilBiomes.field();
    if (field == null) return;
    var biomes = cir.getReturnValue().toList();
    Stream<Holder<Biome>> result = biomes.stream();
    if (biomes.contains(field.desert())) result = Stream.concat(result, Stream.of(field.oilDesert()));
    if (biomes.contains(field.ocean())) result = Stream.concat(result, Stream.of(field.oilOcean()));
    cir.setReturnValue(result);
  }

  @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
    at = @At("RETURN"), cancellable = true)
  private void bcreborn$replaceWithOilField(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
    OilBiomes.Field field = OilBiomes.field();
    if (field == null) return;
    Holder<Biome> biome = cir.getReturnValue();
    Holder<Biome> replaced = field.replace(biome, QuartPos.toBlock(x), QuartPos.toBlock(z));
    if (replaced != biome && ((BiomeSource) (Object) this).possibleBiomes().contains(replaced)) {
      cir.setReturnValue(replaced);
    }
  }
}
