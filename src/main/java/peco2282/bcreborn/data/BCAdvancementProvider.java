package peco2282.bcreborn.data;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.item.BCCoreItems;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BCAdvancementProvider extends ForgeAdvancementProvider {
  /**
   * Constructs an advancement provider using the generators to write the
   * advancements to a file.
   *
   * @param output             the target directory of the data generator
   * @param registries         a future of a lookup for registries and their objects
   * @param existingFileHelper a helper used to find whether a file exists
   */
  public BCAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
    super(output, registries, existingFileHelper, List.of(new Core()));
  }

  private static class Core implements AdvancementGenerator {
    /**
     * A method used to generate advancements for a mod. Advancements should be
     * built via {@link net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator#generate(HolderLookup.Provider, Consumer, ExistingFileHelper)}.
     *
     * @param registries         a lookup for registries and their objects
     * @param saver              a consumer used to write advancements to a file
     * @param existingFileHelper a helper used to find whether a file exists
     */
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
      Advancements.ROOT = Advancement.Builder
          .advancement()
          .display(
              new DisplayInfo(
                  new ItemStack(BCCoreItems.GEAR_WOOD.get()),
                  Component.translatable("advancements.buildcraftcore.root.title"),
                  Component.translatable("advancements.buildcraftcore.root.description"),
                  Optional.empty(), AdvancementType.TASK,
                  false, false, false
              )
          )
          .requirements(AdvancementRequirements.Strategy.OR)
          .addCriterion("has_stick",
              new Criterion<>(
                  new InventoryChangeTrigger(), new InventoryChangeTrigger.TriggerInstance(
                  Optional.empty(),
                  InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                  List.of(
                      ItemPredicate.Builder.item().of(BCCoreItems.GEAR_WOOD.get()).build()
                  )
              )
              )
          ).save(saver, BCReborn.location("root"));

//      AdvancementHolder guide = new Advancement.Builder()
//          .display(
//              new DisplayInfo(
//                  new ItemStack(BCLibItems.GUIDE.get()),
//                  Component.translatable("advancements.buildcraftcore.guide.title"),
//                  Component.translatable("advancements.buildcraftcore.guide.description"),
//                  Optional.empty(),
//                  AdvancementType.TASK,
//                  true, true, false
//              )
//          )
//          .addCriterion("code_trigger", new Criterion<>(
//              new ImpossibleTrigger(),
//              new ImpossibleTrigger.TriggerInstance()
//          ))
//          .parent(root)
//          .save(saver, BCReborn.location("guide"));
    }
  }

  private static class Builder implements AdvancementGenerator {
    /**
     * A method used to generate advancements for a mod. Advancements should be
     * built via {@link net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator#generate(HolderLookup.Provider, Consumer, ExistingFileHelper)}.
     *
     * @param registries         a lookup for registries and their objects
     * @param saver              a consumer used to write advancements to a file
     * @param existingFileHelper a helper used to find whether a file exists
     */
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {

    }
  }
}
