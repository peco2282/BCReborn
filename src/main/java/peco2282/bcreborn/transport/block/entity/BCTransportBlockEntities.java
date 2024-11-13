package peco2282.bcreborn.transport.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.registry.BCRegistry;
import peco2282.bcreborn.transport.block.BCTransportBlocks;
import peco2282.bcreborn.transport.block.BasePipeBlock;
import peco2282.bcreborn.transport.block.entity.pipe.*;
import peco2282.bcreborn.utils.OptionalWith;

import java.util.function.Supplier;

@InitRegister
public class BCTransportBlockEntities {
  public static final RegistryObject<BlockEntityType<WoddenItemPipeBlockEntity>> WOODEN_ITEM_PIPE = register("wooden_item_pipe", () -> BlockEntityType.Builder.of(WoddenItemPipeBlockEntity::new, BCTransportBlocks.WOOD_ITEM_PIPE.get()).build(null));
  public static final RegistryObject<BlockEntityType<CobbleStoneItemPipeBlockEntity>> STONE_ITEM_PIPE = register("stone_item_pipe", () -> BlockEntityType.Builder.of(CobbleStoneItemPipeBlockEntity::new, BCTransportBlocks.COBBLESTONE_ITEM_PIPE.get()).build(null));
  public static final RegistryObject<BlockEntityType<StoneItemPipeBlockEntity>> COBBLESTONE_ITEM_PIPE = register("cobblestone_item_pipe", () -> BlockEntityType.Builder.of(StoneItemPipeBlockEntity::new, BCTransportBlocks.STONE_ITEM_PIPE.get()).build(null));
  public static final RegistryObject<BlockEntityType<IronItemPipeBlockEntity>> IRON_ITEM_PIPE = register("iron_item_pipe", () -> BlockEntityType.Builder.of(IronItemPipeBlockEntity::new, BCTransportBlocks.IRON_ITEM_PIPE.get()).build(null));
  public static final RegistryObject<BlockEntityType<GoldItemPipeBlockEntity>> GOLD_ITEM_PIPE = register("gold_item_pipe", () -> BlockEntityType.Builder.of(GoldItemPipeBlockEntity::new, BCTransportBlocks.GOLD_ITEM_PIPE.get()).build(null));
  public static final RegistryObject<BlockEntityType<DiamondItemPipeBlockEntity>> DIAMOND_ITEM_PIPE = register("diamond_item_pipe", () -> BlockEntityType.Builder.of(DiamondItemPipeBlockEntity::new, BCTransportBlocks.DIAMOND_ITEM_PIPE.get()).build(null));

  private static <T extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> entity) {
    return BCRegistry.registerBlockEntityType(name, entity);
  }
}
