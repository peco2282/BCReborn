package peco2282.bcreborn.core.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;

import java.util.function.Supplier;

public class BCCoreFluids {
  private static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, BCReborn.MODID);

  public static final RegistryObject<Oil.Source> OIL_SOURCE = register("oil_source", Oil.Source::new);
  public static final RegistryObject<Oil.Flowing> OIL_FLOWING = register("oil_flowing", Oil.Flowing::new);
  public static final RegistryObject<GoldOil.Source> GOLD_SOURCE = register("gold_source", GoldOil.Source::new);
  public static final RegistryObject<GoldOil.Flowing> GOLD_FLOWING = register("gold_flowing", GoldOil.Flowing::new);

  private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> supplier) {
    return REGISTRY.register(name, supplier);
  }

  public static final void init(IEventBus bus) {
    REGISTRY.register(bus);
  }
}
