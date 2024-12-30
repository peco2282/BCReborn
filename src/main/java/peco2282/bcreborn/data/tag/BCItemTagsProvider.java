package peco2282.bcreborn.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.core.item.BCCoreItems;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides item tags for the BC Reborn mod.
 *
 * <p>It extends {@link ItemTagsProvider} to define and register custom item tags,
 * such as gear-related tags.</p>
 *
 * @author peco2282
 */
public class BCItemTagsProvider extends ItemTagsProvider {

  /**
   * Constructs a new instance of {@code BCItemTagsProvider}.
   *
   * @param p_275343_ The output target to save the tags.
   * @param p_275729_ The lookup provider for obtaining item holders.
   * @param p_275322_ The lookup provider for block tags, used for item-to-block tag mapping.
   */
  public BCItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_) {
    super(p_275343_, p_275729_, p_275322_);
  }

  /**
   * Registers item tags for the mod.
   *
   * <p>This method defines the {@code GEAR} tag and adds various gear items to it,
   * such as wooden, stone, iron, gold, and diamond gears.</p>
   *
   * @param p_256380_ The provider used for looking up item holders.
   */
  @Override
  protected void addTags(HolderLookup.Provider p_256380_) {
    tag(BCItemTag.GEAR)
        .add(BCCoreItems.GEAR_WOOD.get())
        .add(BCCoreItems.GEAR_STONE.get())
        .add(BCCoreItems.GEAR_IRON.get())
        .add(BCCoreItems.GEAR_GOLD.get())
        .add(BCCoreItems.GEAR_DIAMOND.get());
  }
}
