package peco2282.bcreborn.core.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCCoreFluids {
  public static final RegistryObject<Oil.Source> OIL_SOURCE = register("oil", Oil.Source::new);
  public static final RegistryObject<Oil.Flowing> OIL_FLOWING = register("oil_flowing", Oil.Flowing::new);
  public static final RegistryObject<Fuel.Source> FUEL_SOURCE = register("fuel", Fuel.Source::new);
  public static final RegistryObject<Fuel.Flowing> FUEL_FLOWING = register("fuel_flowing", Fuel.Flowing::new);

  private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> supplier) {
    return BCRegistry.registerFluid(name, supplier);
  }
  public static void init() {}
}
