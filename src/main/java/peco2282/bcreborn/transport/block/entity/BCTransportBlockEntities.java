package peco2282.bcreborn.transport.block.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.registry.BCRegistry;
import peco2282.bcreborn.transport.block.BCTransportBlocks;
import peco2282.bcreborn.transport.block.BaseBlockPipe;
import peco2282.bcreborn.utils.RegistryUtil;

import java.util.function.Supplier;

public class BCTransportBlockEntities {
  public static final RegistryObject<BlockEntityType<ItemPipeBlockEntity>> ITEM_PIPE = register("wooden_item_pipe", () -> BlockEntityType.Builder.of((p, s) -> new ItemPipeBlockEntity(p, s, BaseBlockPipe.PipeMaterial.WOOD), BCTransportBlocks.WOOD_ITEM_PIPE.get()).build(null));
  private static <T extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> entity) {
    return BCRegistry.registerBlockEntityType(name, entity);
  }

  public static void init() {
  }
}
