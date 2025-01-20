package peco2282.bcreborn.core.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Consumer;
import java.util.function.Supplier;

@InitRegister
public class BCCoreFluids {
  public static final RegistryObject<Oil.Source> OIL_SOURCE = register("oil_source", Oil.Source::new);
  public static final RegistryObject<Oil.Flowing> OIL_FLOWING = register("oil_flowing", Oil.Flowing::new);
  public static final RegistryObject<Fuel.Source> FUEL_SOURCE = register("fuel_source", Fuel.Source::new);
  public static final RegistryObject<Fuel.Flowing> FUEL_FLOWING = register("fuel_flowing", Fuel.Flowing::new);

  public static final RegistryObject<FluidType> OIL = BCRegistry.registerFluidType("oil", () -> new FluidType(
      FluidType.Properties.create()
          .canPushEntity(true)
          .canDrown(false)
          .canHydrate(true)
          .canSwim(false)
          .temperature(5)
          .density(30)
          .viscosity(10)
  ) {
    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(new IClientFluidTypeExtensions() {
        @Override
        public ResourceLocation getFlowingTexture() {
          return BCReborn.location("fluid/oil_flowing");
        }

        @Override
        public ResourceLocation getStillTexture() {
          return BCReborn.location("fluid/oil_source");
        }
      });
    }
  });

  public static final RegistryObject<FluidType> FUEL = BCRegistry.registerFluidType("fuel", () -> new FluidType(
      FluidType.Properties.create()
          .canPushEntity(true)
          .canDrown(false)
          .canHydrate(true)
          .density(30)
          .viscosity(10)
          .canSwim(false)
  ) {
    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(new IClientFluidTypeExtensions() {
        @Override
        public ResourceLocation getFlowingTexture() {
          return BCReborn.location("fluid/fuel_flowing");
        }

        @Override
        public ResourceLocation getStillTexture() {
          return BCReborn.location("fluid/fuel_source");
        }
      });
    }
  });


  private static <F extends Fluid> RegistryObject<F> register(String name, Supplier<F> supplier) {
    return BCRegistry.registerFluid(name, supplier);
  }
}
