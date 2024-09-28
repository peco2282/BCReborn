package peco2282.bcreborn.core.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.core.block.BCCoreBlocks;

import java.util.function.Supplier;

@SuppressWarnings("DataFlowIssue")
public class BlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BCReborn.MODID);
  private static <T extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<T>> register(final String name, final Supplier<BlockEntityType<T>> type) {
    return REGISTRY.register(name, type);
  }

  public static void init(IEventBus bus) {
    REGISTRY.register(bus);
  }

  public static final RegistryObject<BlockEntityType<EngineBlockEntity>> ENGINE = register("engine", () -> BlockEntityType.Builder.of(EngineBlockEntity::new, BCCoreBlocks.WOOD_ENGINE.get(), BCCoreBlocks.STONE_ENGINE.get(), BCCoreBlocks.IRON_ENGINE.get(), BCCoreBlocks.CREATIVE_ENGINE.get()).build(null));
  public static final RegistryObject<BlockEntityType<IronEngineBlockEntity>> IRON_ENGINE = register("iron_engine", () -> BlockEntityType.Builder.of(IronEngineBlockEntity::new, BCCoreBlocks.IRON_ENGINE.get()).build(null));


}
