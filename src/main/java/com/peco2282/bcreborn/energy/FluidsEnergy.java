package com.peco2282.bcreborn.energy;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.energy.fluids.FuelFluid;
import com.peco2282.bcreborn.energy.fluids.OilFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class FluidsEnergy {
  private static final BCRegistry REGISTRY = BCRebornEnergy.getRegistry();

  private static final DeferredRegister<FluidType> FLUID_TYPES =
      DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BCRebornEnergy.MODID);

  // --- Oil (Black Oil) ---

  public static final RegistryObject<FluidType> OIL_TYPE = FLUID_TYPES.register("oil",
      () -> new FluidType(FluidType.Properties.create()
          .descriptionId("fluid.bcrebornenergy.oil")
          .density(900)
          .viscosity(1500)
          .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
          .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
      ) {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
          consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = BCRebornEnergy.location("block/fluids/oil_still");
            private static final ResourceLocation FLOW = BCRebornEnergy.location("block/fluids/oil_flow");

            @Override
            public ResourceLocation getStillTexture() {
              return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
              return FLOW;
            }

            @Override
            public int getTintColor() {
              return 0xFF1A1A1A;
            }
          });
        }
      });

  public static final RegistryObject<OilFluid.Source> OIL_SOURCE = REGISTRY.registerFluid("oil",
      () -> new OilFluid.Source(oilProperties()));

  public static final RegistryObject<OilFluid.Flowing> OIL_FLOWING = REGISTRY.registerFluid("flowing_oil",
      () -> new OilFluid.Flowing(oilProperties()));

  public static final RegistryObject<LiquidBlock> OIL_BLOCK = REGISTRY.registerBlock("oil",
      () -> new LiquidBlock(OIL_SOURCE, BlockBehaviour.Properties.of()
          .mapColor(MapColor.COLOR_BLACK)
          .noCollission()
          .strength(100.0F)
          .noLootTable()
      ));

  public static final RegistryObject<BucketItem> OIL_BUCKET = REGISTRY.registerItem("oil_bucket",
      () -> new BucketItem(OIL_SOURCE, new Item.Properties()
          .craftRemainder(Items.BUCKET)
          .stacksTo(1)
      ));

  // --- Fuel (Gold Oil) ---

  public static final RegistryObject<FluidType> FUEL_TYPE = FLUID_TYPES.register("fuel",
      () -> new FluidType(FluidType.Properties.create()
          .descriptionId("fluid.bcrebornenergy.fuel")
          .density(800)
          .viscosity(1000)
          .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
          .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
      ) {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
          consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = BCRebornEnergy.location("block/fluids/fuel_still");
            private static final ResourceLocation FLOW = BCRebornEnergy.location("block/fluids/fuel_flow");

            @Override
            public ResourceLocation getStillTexture() {
              return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
              return FLOW;
            }

            @Override
            public int getTintColor() {
              return 0xFFD4A017;
            }
          });
        }
      });

  public static final RegistryObject<FuelFluid.Source> FUEL_SOURCE = REGISTRY.registerFluid("fuel",
      () -> new FuelFluid.Source(fuelProperties()));

  public static final RegistryObject<FuelFluid.Flowing> FUEL_FLOWING = REGISTRY.registerFluid("flowing_fuel",
      () -> new FuelFluid.Flowing(fuelProperties()));

  public static final RegistryObject<LiquidBlock> FUEL_BLOCK = REGISTRY.registerBlock("fuel",
      () -> new LiquidBlock(FUEL_SOURCE, BlockBehaviour.Properties.of()
          .mapColor(MapColor.GOLD)
          .noCollission()
          .strength(100.0F)
          .noLootTable()
      ));

  public static final RegistryObject<BucketItem> FUEL_BUCKET = REGISTRY.registerItem("fuel_bucket",
      () -> new BucketItem(FUEL_SOURCE, new Item.Properties()
          .craftRemainder(Items.BUCKET)
          .stacksTo(1)
      ));

  private static ForgeFlowingFluid.Properties oilProperties() {
    return new ForgeFlowingFluid.Properties(OIL_TYPE, OIL_SOURCE, OIL_FLOWING)
        .slopeFindDistance(3)
        .levelDecreasePerBlock(2)
        .block(OIL_BLOCK)
        .bucket(OIL_BUCKET);
  }

  private static ForgeFlowingFluid.Properties fuelProperties() {
    return new ForgeFlowingFluid.Properties(FUEL_TYPE, FUEL_SOURCE, FUEL_FLOWING)
        .slopeFindDistance(3)
        .levelDecreasePerBlock(2)
        .block(FUEL_BLOCK)
        .bucket(FUEL_BUCKET);
  }

  public static void registerFluidTypes(IEventBus bus) {
    FLUID_TYPES.register(bus);
  }
}
