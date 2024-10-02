package peco2282.bcreborn.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.enums.EnumPowerStage;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.core.item.ItemGear;

import java.util.List;
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
    models().getBuilder("oil")
        .texture("particle", modLoc("fluid/oil_source"));

    models().getBuilder("fuel")
        .texture("particle", modLoc("fluid/fuel_source"));
  }

  private void textureBlockItem() {
    cube("wood_engine", it -> "engine/wood/blue/" + it + "_1");
    cube("stone_engine", it -> "engine/stone/blue/" + it + "_1");
    cube("iron_engine", it -> "engine/iron/blue/" + it + "_1");
    cube("creative_engine", it -> "engine/creative/blue/" + it + "_1");
  }

  private void blockState() {
    Property<?>[] ignores = new Property<?>[]{
        BCProperties.ACTIVE,
        BCProperties.ENGINE_TYPE,
    };
    getVariantBuilder(BCCoreBlocks.WOOD_ENGINE.get())
        .forAllStatesExcept(state -> {
          Direction direction = state.getValue(BCProperties.BLOCK_FACING);
          int line = state.getValue(BCProperties.ENGINE_MODEL) % 9 + 1;
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
              .modelFile(unchecked(modLoc("block/engine/wood/" + stage.getModelName() +"/wood_engine_" + line)))
              .rotationX(rotX)
              .rotationY(rotY).build();
        }, ignores)
    ;

    getVariantBuilder(BCCoreBlocks.STONE_ENGINE.get())
        .forAllStatesExcept(state -> {
          Direction direction = state.getValue(BCProperties.BLOCK_FACING);
          int line = state.getValue(BCProperties.ENGINE_MODEL) % 9 + 1;

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
              .modelFile(unchecked(modLoc("block/engine/stone/" + stage.getModelName() + "/stone_engine_" + line)))
              .rotationX(rotX)
              .rotationY(rotY).build();
        }, ignores)
    ;
    getVariantBuilder(BCCoreBlocks.IRON_ENGINE.get())
        .forAllStatesExcept(state -> {
          Direction direction = state.getValue(BCProperties.BLOCK_FACING);
          int line = state.getValue(BCProperties.ENGINE_MODEL) % 9 + 1;

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
              .modelFile(unchecked(modLoc("block/engine/iron/" + stage.getModelName() + "/iron_engine_" + line)))
              .rotationX(rotX)
              .rotationY(rotY).build();
        }, ignores)
    ;
    getVariantBuilder(BCCoreBlocks.CREATIVE_ENGINE.get())
        .forAllStatesExcept(state -> {
          Direction direction = state.getValue(BCProperties.BLOCK_FACING);
          int line = state.getValue(BCProperties.ENGINE_MODEL) % 9 + 1;

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
              .modelFile(unchecked(modLoc("block/engine/creative/" + stage.getModelName() + "/creative_engine_" + line)))
              .rotationX(rotX)
              .rotationY(rotY).build();
        }, ignores)
    ;
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
    return unchecked(mcLoc("minecraft:item/generated"));
  }

  ModelFile unchecked(ResourceLocation location) {
    return new ModelFile.UncheckedModelFile(location);
  }

  void cube(String name, UnaryOperator<String> operator) {
    itemModels()
        .getBuilder(name)
        .parent(unchecked(modLoc("block/" + operator.apply(name))));

  }
}
