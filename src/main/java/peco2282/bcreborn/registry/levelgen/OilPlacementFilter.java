package peco2282.bcreborn.registry.levelgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.ForgeRegistries;
import peco2282.bcreborn.registry.BCMisc;

import java.util.List;
import java.util.Objects;

public class OilPlacementFilter extends PlacementFilter {
  private static final OilPlacementFilter INSTANCE = new OilPlacementFilter();
  public static final MapCodec<OilPlacementFilter> CODEC = MapCodec.unit(OilPlacementFilter::filter);
  private static final List<Biome> TARGET = List.of();

  public OilPlacementFilter() {
  }

  public static OilPlacementFilter filter() {
    return INSTANCE;
  }

  private static boolean times(RandomSource source, int count) {
    boolean result = true;
    for (int i = 0; i < count; ++i) {
      result = result && source.nextBoolean();
    }
    return result;
  }

  private static boolean equals(Biome a, Biome b) {
    if (a == b || a.equals(b)) {
      return true;
    }

    ResourceLocation aKey = ForgeRegistries.BIOMES.getKey(a);
    ResourceLocation bKey = ForgeRegistries.BIOMES.getKey(b);
    return Objects.equals(aKey, bKey);
  }

  @Override
  protected boolean shouldPlace(PlacementContext p_226382_, RandomSource p_226383_, BlockPos p_226384_) {
    Holder<Biome> biome = p_226382_.getLevel().getBiome(p_226384_);
    final HolderLookup<Biome> lookup = p_226382_.getLevel().holderLookup(Registries.BIOME);
    System.out.println("BIOME*** " + biome);
    if (
        equals(biome.get(), lookup.getOrThrow(Biomes.DESERT).get()) ||
            equals(biome.get(), lookup.getOrThrow(Biomes.BEACH).get())
    ) {
      return times(p_226383_, 2);
    }
    return times(p_226383_, 3);
  }

  @Override
  public PlacementModifierType<?> type() {
    return BCMisc.OIL_PMT.get();
  }
}
