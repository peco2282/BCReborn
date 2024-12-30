package peco2282.bcreborn.transport.block.pipe;

import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.pipe.*;

public record PipeMaterialHandler(PipeMaterial material) {
  @NotNull
  public ItemPipeBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return switch (material()) {
      case WOOD -> new WoddenItemPipeBlockEntity(pos, state);
      case STONE -> new StoneItemPipeBlockEntity(pos, state);
      case COBBLESTONE -> new CobbleStoneItemPipeBlockEntity(pos, state);
      case IRON -> new IronItemPipeBlockEntity(pos, state);
      case GOLD -> new GoldItemPipeBlockEntity(pos, state);
      case DIAMOND -> new DiamondItemPipeBlockEntity(pos, state);
    };
  }

  @SuppressWarnings("unchecked")
  public <E extends BlockEntity, F extends BlockEntity> BlockEntityTicker<F> serverTicker(
      BlockEntityType<E> type,
      Function3<BlockEntityType<F>, BlockEntityType<E>, BlockEntityTicker<? super E>, BlockEntityTicker<F>> creator
  ) {
    BlockEntityType<?> bet;
    BlockEntityTicker<?> ticker;
    switch (material()) {
      case WOOD -> {
        bet = BCTransportBlockEntities.WOODEN_ITEM_PIPE.get();
        ticker = (BlockEntityTicker<WoddenItemPipeBlockEntity>) WoddenItemPipeBlockEntity::tick;
      }
      case STONE -> {
        bet = BCTransportBlockEntities.STONE_ITEM_PIPE.get();
        ticker = (BlockEntityTicker<StoneItemPipeBlockEntity>) StoneItemPipeBlockEntity::tick;
      }
      case COBBLESTONE -> {
        bet = BCTransportBlockEntities.COBBLESTONE_ITEM_PIPE.get();
        ticker = (BlockEntityTicker<CobbleStoneItemPipeBlockEntity>) CobbleStoneItemPipeBlockEntity::tick;
      }
      case IRON -> {
        bet = BCTransportBlockEntities.IRON_ITEM_PIPE.get();
        ticker = (BlockEntityTicker<IronItemPipeBlockEntity>) IronItemPipeBlockEntity::tick;
      }
      case GOLD -> {
        bet = BCTransportBlockEntities.GOLD_ITEM_PIPE.get();
        ticker = (BlockEntityTicker<GoldItemPipeBlockEntity>) GoldItemPipeBlockEntity::tick;
      }
      case DIAMOND -> {
        bet = BCTransportBlockEntities.DIAMOND_ITEM_PIPE.get();
        ticker = (BlockEntityTicker<DiamondItemPipeBlockEntity>) DiamondItemPipeBlockEntity::tick;
      }
      case null -> throw new AssertionError();
    }
    ;
    return creator.apply(
        (BlockEntityType<F>) bet,
        type,
        (BlockEntityTicker<E>) ticker
    );
  }
}
