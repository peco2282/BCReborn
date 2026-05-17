package com.peco2282.bcreborn.builders;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.CodingUtils;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornBuilders.MODID, priority = 0)
public class BlockEntityTypesBuilders {
  public static final RegistryObject<BlockEntityType<BlueprintLibraryBlockEntity>> BLUEPRINT_LIBRARY = register(
      "blueprint_library", of(BlueprintLibraryBlockEntity::new, BuildersBlock.BLUEPRINT_LIBRARY));
  private static final BCRegistry REGISTRY = BCRebornBuilders.getRegistry();

  public static final RegistryObject<BlockEntityType<ArchitectBlockEntity>> ARCHITECT =
      register("architect", of(ArchitectBlockEntity::new, BuildersBlock.ARCHITECT));
  public static final RegistryObject<BlockEntityType<ConstructionMarkerBlockEntity>> CONSTRUCTION_MARKER =
      register("construction_marker", of(ConstructionMarkerBlockEntity::new, BuildersBlock.CONSTRUCTION_MARKER));
  public static final RegistryObject<BlockEntityType<BuilderBlockEntity>> BUILDER =
      register("builder", of(BuilderBlockEntity::new, BuildersBlock.BUILDER));

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }

  @SafeVarargs
  private static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block>... validBlocks) {
    return () -> new BlockEntityType<>(supplier, CodingUtils.map2Set(Arrays.asList(validBlocks), Supplier::get), null);
  }
}
