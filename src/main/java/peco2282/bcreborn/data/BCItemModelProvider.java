package peco2282.bcreborn.data;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import peco2282.bcreborn.core.item.GearItem;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;


/**
 * Provides model and state registrations for blocks and items used in the BCReborn mod.
 * This class is responsible for generating the necessary block models,
 * item models, and defining textures for use within the Minecraft forge system.
 *
 * @author peco2282
 */
public class BCItemModelProvider extends BlockStateProvider {
  final ExistingFileHelper helper;
  final HolderLookup.Provider provider;

  /**
   * Constructs a new instance of BCItemModelProvider.
   *
   * @param output             The PackOutput used for generating data resource packs.
   * @param provider           A future provider for HolderLookup, allowing instance lookups in the world.
   * @param existingFileHelper Helper for existing files, used to validate resource presence.
   */
  public BCItemModelProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
    super(output, BCReborn.MODID, existingFileHelper);
    this.helper = existingFileHelper;
    this.provider = provider.getNow(null);
  }

  /**
   * Registers block states and item models by invoking associated methods for each type.
   * Ensures all textures, models, and configurations for in-game representation are created.
   */
  @Override
  protected void registerStatesAndModels() {
    registerItemTextures();

    registerBlockStates();
    registerBlockModels();
    textureBlockItem();
  }

  /**
   * Defines and registers custom block models used in the BCReborn mod.
   * Includes textures for fluid sources and other custom blocks.
   */
  private void registerBlockModels() {
    registerEngineModels();
    models().getBuilder("oil_source")
        .texture("particle", modLoc("fluid/oil_source"));

    models().getBuilder("fuel_source")
        .texture("particle", modLoc("fluid/fuel_source"));
  }

  /**
   * Registers textures and models for block items and their in-game representation.
   * Includes cube-based models and render types for specific items.
   */
  private void textureBlockItem() {
    createCubeModel("wood_engine", it -> "engine/wood/blue/engine_1");
    createCubeModel("stone_engine", it -> "engine/stone/blue/engine_1");
    createCubeModel("iron_engine", it -> "engine/iron/blue/engine_1");
    createCubeModel("creative_engine", it -> "engine/creative/blue/engine_1");

    createCubeModel("filler", UnaryOperator.identity());
    itemModels()
        .getBuilder("marker_volume")
        .parent(existing(modLoc("block/marker_volume_on")))
        .renderType(mcLoc("cutout"));
  }

  /**
   * Registers states and configurations for blocks, such as rotation, facing direction,
   * and conditional states. Handles custom models for various block states.
   */
  private void registerBlockStates() {
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
        .forAllStatesExcept(s -> engineModels(s, EnumEngineType.CREATIVE), ignores)
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

    getVariantBuilder(BCCoreBlocks.OIL_SOURCE.get())
        .forAllStatesExcept(s -> ConfiguredModel.builder().modelFile(unchecked(modLoc("block/oil_source"))).build(), BlockStateProperties.LEVEL);
  }

  /**
   * Generates and returns the configured models for an engine based on its type, direction,
   * and current state.
   *
   * @param state The current state of the block.
   * @param type  The type of the engine, defined by enum EnumEngineType.
   * @return An array of ConfiguredModel objects with the appropriate configurations.
   */
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

  /**
   * Registers engine models for all engine types and power stages defined in the BCReborn mod.
   * Combines parent models and textures to create a complete representation for each engine configuration.
   */
  private void registerEngineModels() {
    String[] names = Arrays.stream(EnumEngineType.values()).map(EnumEngineType::getSerializedName).toArray(String[]::new);
    String[] types = Arrays.stream(EnumPowerStage.values()).map(EnumPowerStage::getSerializedName).toArray(String[]::new);
    for (String name : names) {
      for (String type : types) {
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

  /**
   * Registers textures for specific items, such as gears, used in the BCReborn mod.
   * Ensures proper textures are applied to each item instance.
   */
  private void registerItemTextures() {
    registerGears();
  }


  /**
   * Handles the registration of item models for gear items.
   * Applies appropriate generated textures for all gear types.
   */
  private void registerGears() {
    for (RegistryObject<GearItem> item : List.of(BCCoreItems.GEAR_WOOD, BCCoreItems.GEAR_STONE, BCCoreItems.GEAR_IRON, BCCoreItems.GEAR_GOLD, BCCoreItems.GEAR_DIAMOND)) {
      generatedTexture(item.get(), modLoc("item/" + item.get().getId()));
//      itemModels().basicItem(item.get()).parent(generated()).texture("layer0", BCReborn.location("item/" + item.get().getId()));
    }
  }

  /**
   * Assigns a generated texture model to the specified item.
   *
   * @param item    The item to which the texture will be applied.
   * @param texture The texture resource location.
   */
  private void generatedTexture(Item item, ResourceLocation texture) {
    itemModels().basicItem(item).parent(generated()).texture("layer0", texture);
  }

  /**
   * Retrieves the base "generated" model used commonly for basic item models.
   *
   * @return A ModelFile representing the "generated" base model.
   */
  ModelFile generated() {
    return existing(mcLoc("minecraft:item/generated"));
  }

  /**
   * Creates a model file with no validation checks for the provided resource location.
   *
   * @param location The resource location of the model.
   * @return A ModelFile pointing to the specified resource.
   */
  ModelFile unchecked(ResourceLocation location) {
    return new ModelFile.UncheckedModelFile(location);
  }

  /**
   * Retrieves a pre-existing model file from the specified resource location.
   *
   * @param location The resource location of the existing model file.
   * @return A ModelFile pointing to the specified resource.
   */
  ModelFile existing(ResourceLocation location) {
    return new ModelFile.ExistingModelFile(location, helper);
  }

  /**
   * Creates and registers a cube model for the specified block/item name.
   *
   * @param name     The name of the block/item.
   * @param operator A UnaryOperator to manipulate the model path if necessary.
   */
  void createCubeModel(String name, UnaryOperator<String> operator) {
    itemModels()
        .getBuilder(name)
        .parent(unchecked(modLoc("block/" + operator.apply(name))))
        .renderType(mcLoc("cutout"));

  }
}
