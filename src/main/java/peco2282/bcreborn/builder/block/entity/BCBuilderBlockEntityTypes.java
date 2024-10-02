package peco2282.bcreborn.builder.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCBuilderBlockEntityTypes {
  public static final RegistryObject<BlockEntityType<FillerBlockEntity>> FILLER = register("filler", () -> BlockEntityType.Builder.of(FillerBlockEntity::new).build(null));

  private static <T extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<T>> register(final String name, final Supplier<BlockEntityType<T>> type) {
    return BCRegistry.registerBlockEntityType(name, type);
  }

  public static void init() {

  }
}
