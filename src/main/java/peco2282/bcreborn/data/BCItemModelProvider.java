package peco2282.bcreborn.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.enums.EnumEngineType;
import peco2282.bcreborn.api.enums.EnumPowerStage;
import peco2282.bcreborn.builder.block.BCBuilderBlocks;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.core.item.ItemGear;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class BCItemModelProvider extends BlockStateProvider {
  final ExistingFileHelper helper;

  public BCItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, BCReborn.MODID, existingFileHelper);
    this.helper = existingFileHelper;
  }

  @Override
  protected void registerStatesAndModels() {
    textureItem();

    blockState();
    blockModel();
    textureBlockItem();
  }

  private void blockModel() {
    engine();
    models().getBuilder("oil")
        .texture("particle", modLoc("fluid/oil_source"));

    models().getBuilder("fuel")
        .texture("particle", modLoc("fluid/fuel_source"));
  }

  private void textureBlockItem() {
    cube("wood_engine", it -> "engine/wood/blue/engine_1");
    cube("stone_engine", it -> "engine/stone/blue/engine_1");
    cube("iron_engine", it -> "engine/iron/blue/engine_1");
    cube("creative_engine", it -> "engine/creative/blue/engine_1");

    cube("filler", UnaryOperator.identity());

    cube("marker_volume", it -> it + "_off");
  }

  private void blockState() {
    Property<?>[] ignores = new Property<?>[]{
        BCProperties.ACTIVE,
        BCProperties.ENGINE_TYPE,
    };
    getVariantBuilder(BCCoreBlocks.WOOD_ENGINE.get())
        .forAllStatesExcept(s -> engineModels(s, EnumEngineType.WOOD), ignores)
    ;
    getVariantBuilder(BCCoreBlocks.STONE_ENGINE.get())
        .forAllStatesExcept(s -> engineModels(s, EnumEngineType.STONE), ignores)
    ;
    getVariantBuilder(BCCoreBlocks.IRON_ENGINE.get())
        .forAllStatesExcept(s -> engineModels(s, EnumEngineType.IRON), ignores)
    ;
    getVariantBuilder(BCCoreBlocks.CREATIVE_ENGINE.get())
        .forAllStatesExcept(s -> engineModels(s, EnumEngineType.CREATIVE) , ignores)
    ;

    getVariantBuilder(BCBuilderBlocks.FILLER.get())
        .forAllStatesExcept(state -> {
          Direction facing = state.getValue(BCProperties.BLOCK_FACING);
          int x = switch (facing) {
            case DOWN, UP, NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
          };
          return ConfiguredModel
              .builder().modelFile(existing(modLoc("block/filler")))
              .rotationX(x)
              .rotationY(0)
              .build();
        }, BCProperties.FILLER_TYPE);

    getVariantBuilder(BCCoreBlocks.MARKER_VOLUME.get())
        .forAllStatesExcept(state -> {
          boolean active = state.getValue(BCProperties.ACTIVE);
          return ConfiguredModel
             .builder()
             .modelFile(existing(modLoc("block/marker_volume_" + (active ? "on" : "off"))))
             .build();
        }, BCProperties.BLOCK_FACING);
  }

  private ConfiguredModel[] engineModels(BlockState state, EnumEngineType type) {
      Direction direction = state.getValue(BCProperties.BLOCK_FACING);
      int line = state.getValue(BCProperties.ENGINE_MODEL);

      EnumPowerStage stage = state.getValue(BCProperties.ENERGY_STAGE);
      int rotX, rotY;
      switch (direction) {
        case DOWN -> {
          rotX = 180;
          rotY = 0;
        }
        case SOUTH -> {
          rotX = 90;
          rotY = 180;
        }
        case WEST -> {
          rotX = 90;
          rotY = 270;
        }
        case EAST -> {
          rotX = 90;
          rotY = 90;
        }
        case NORTH -> {
          rotX = 90;
          rotY = 0;
        }
        default -> {
          rotX = 0;
          rotY = 0;
        }
      }

      return ConfiguredModel
          .builder()
          .modelFile(unchecked(modLoc("block/engine/" + type.getSerializedName() + "/" + stage.getSerializedName() + "/engine_" + line)))
          .rotationX(rotX)
          .rotationY(rotY).build();
  }

  private void engine() {
    String[] names = Arrays.stream(EnumEngineType.values()).map(EnumEngineType::getSerializedName).toArray(String[]::new);
    String[] types = Arrays.stream(EnumPowerStage.values()).map(EnumPowerStage::getSerializedName).toArray(String[]::new);
    for (String name : names) {
      for (String type: types){
        for (int i = 1; i < 10; i++) {
          models().getBuilder(String.format(Locale.ENGLISH, "block/engine/%s/%s/engine_%s", name, type, i))
              .parent(existing(modLoc("block/engine/base/engine_" + i)))
              .texture("0", modLoc("block/engine/" + name + "/side"))
              .texture("1", modLoc("block/engine/" + name + "/back"))
              .renderType(mcLoc("cutout"));
        }
      }
    }
  }

  private void textureItem() {
    registerGears();
  }


  private void registerGears() {
    for (RegistryObject<ItemGear> item : List.of(BCCoreItems.GEAR_WOOD, BCCoreItems.GEAR_STONE, BCCoreItems.GEAR_IRON, BCCoreItems.GEAR_GOLD, BCCoreItems.GEAR_DIAMOND)) {
      generatedTexture(item.get(), modLoc("item/" + item.get().getId()));
//      itemModels().basicItem(item.get()).parent(generated()).texture("layer0", BCReborn.location("item/" + item.get().getId()));
    }
  }

  private void generatedTexture(Item item, ResourceLocation texture) {
    itemModels().basicItem(item).parent(generated()).texture("layer0", texture);
  }

  ModelFile generated() {
    return existing(mcLoc("minecraft:item/generated"));
  }

  ModelFile unchecked(ResourceLocation location) {
    return new ModelFile.UncheckedModelFile(location);
  }
  ModelFile existing(ResourceLocation location) {
    return new ModelFile.ExistingModelFile(location, helper);
  }

  void cube(String name, UnaryOperator<String> operator) {
    itemModels()
        .getBuilder(name)
        .parent(unchecked(modLoc("block/" + operator.apply(name))))
        .renderType(mcLoc("cutout"));

  }
}
