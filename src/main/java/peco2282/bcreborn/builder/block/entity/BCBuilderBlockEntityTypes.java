package peco2282.bcreborn.builder.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.builder.block.BCBuilderBlocks;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCBuilderBlockEntityTypes {
  public static final RegistryObject<BlockEntityType<FillerBlockEntity>> FILLER = register("filler", () -> BlockEntityType.Builder.of(FillerBlockEntity::new, BCBuilderBlocks.FILLER.get()).build(null));
  public static final RegistryObject<BlockEntityType<QuarryBlockEntity>> QUARRY = register("quarry", () -> BlockEntityType.Builder.of(QuarryBlockEntity::new, BCBuilderBlocks.QUARRY.get()).build(null));
  public static final RegistryObject<BlockEntityType<ChuteBlockEtity>> CHUTE = register("chute", () -> BlockEntityType.Builder.of(ChuteBlockEtity::new, BCBuilderBlocks.QUARRY.get()).build(null));

  private static <T extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<T>> register(final String name, final Supplier<BlockEntityType<T>> type) {
    return BCRegistry.registerBlockEntityType(name, type);
  }
}
