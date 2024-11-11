package peco2282.bcreborn.lib.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import peco2282.bcreborn.BCReborn;

public class LiquidDamageType {
  public static final ResourceKey<DamageType> OIL_EX = ResourceKey.create(Registries.DAMAGE_TYPE, BCReborn.location("oil"));
  public static final ResourceKey<DamageType> FUEL_EX = ResourceKey.create(Registries.DAMAGE_TYPE, BCReborn.location("fuel"));

  public static void bootstrap(BootstrapContext<DamageType> context) {
    context.register(OIL_EX, new DamageType("oil", .2F));
    context.register(FUEL_EX, new DamageType("fuel", .5F));
  }
}
