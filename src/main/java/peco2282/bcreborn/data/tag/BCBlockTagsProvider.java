package peco2282.bcreborn.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.builder.block.BCBuilderBlocks;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.transport.block.BCTransportBlocks;

import java.util.concurrent.CompletableFuture;

public class BCBlockTagsProvider extends BlockTagsProvider {
  public BCBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, BCReborn.MODID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider p_256380_) {
    tag(BCBlockTag.ENGINE)
        .add(BCCoreBlocks.WOOD_ENGINE.get())
        .add(BCCoreBlocks.STONE_ENGINE.get())
        .add(BCCoreBlocks.IRON_ENGINE.get())
        .add(BCCoreBlocks.CREATIVE_ENGINE.get());

    tag(BCBlockTag.ITEM_PIPE)
        .add(BCTransportBlocks.WOOD_ITEM_PIPE.get())
        .add(BCTransportBlocks.STONE_ITEM_PIPE.get())
        .add(BCTransportBlocks.COBBLESTONE_ITEM_PIPE.get())
        .add(BCTransportBlocks.IRON_ITEM_PIPE.get())
        .add(BCTransportBlocks.GOLD_ITEM_PIPE.get())
        .add(BCTransportBlocks.DIAMOND_ITEM_PIPE.get());

    tag(BCBlockTag.FLUID_PIPE)
        .add(BCTransportBlocks.WOOD_FLUID_PIPE.get())
        .add(BCTransportBlocks.STONE_FLUID_PIPE.get())
        .add(BCTransportBlocks.COBBLESTONE_FLUID_PIPE.get())
        .add(BCTransportBlocks.IRON_FLUID_PIPE.get())
        .add(BCTransportBlocks.GOLD_FLUID_PIPE.get())
        .add(BCTransportBlocks.DIAMOND_FLUID_PIPE.get());

    tag(BCBlockTag.ENERGY_PIPE)
        .add(BCTransportBlocks.WOOD_ENERGY_PIPE.get())
        .add(BCTransportBlocks.STONE_ENERGY_PIPE.get())
        .add(BCTransportBlocks.COBBLESTONE_ENERGY_PIPE.get())
        .add(BCTransportBlocks.IRON_ENERGY_PIPE.get())
        .add(BCTransportBlocks.GOLD_ENERGY_PIPE.get())
        .add(BCTransportBlocks.DIAMOND_ENERGY_PIPE.get());

    tag(BCBlockTag.PIPE)
        .addTag(BCBlockTag.ITEM_PIPE)
        .addTag(BCBlockTag.FLUID_PIPE)
        .addTag(BCBlockTag.ENERGY_PIPE);

    tag(BCBlockTag.CORE)
        .addTag(BCBlockTag.ENGINE)
        .add(BCCoreBlocks.SPRING.get())
        .add(BCCoreBlocks.DECORATED.get())
        .add(BCCoreBlocks.MARKER_VOLUME.get());

    tag(BCBlockTag.BUILDER)
        .add(BCBuilderBlocks.FILLER.get())
        .add(BCBuilderBlocks.QUARRY.get());

    tag(BCBlockTag.TRANSPORT)
        .addTag(BCBlockTag.PIPE);
  }
}
