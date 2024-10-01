package peco2282.bcreborn.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.api.item.BCItem;
import peco2282.bcreborn.builder.block.BCBuilderBlocks;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.block.entity.BCCoreBlockEntityTypes;
import peco2282.bcreborn.core.block.menu.BCCoreMenuTypes;
import peco2282.bcreborn.core.fluid.BCCoreFluids;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.lib.block.menu.BCMenu;
import peco2282.bcreborn.lib.item.BlockItemNeptune;

import java.util.function.Supplier;

public class BCRegistry {
  private static final DeferredRegister<Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, BCReborn.MODID);
  private static final DeferredRegister<Item> ITEM = DeferredRegister.create(ForgeRegistries.ITEMS, BCReborn.MODID);
  private static final DeferredRegister<Fluid> FLUID = DeferredRegister.create(ForgeRegistries.FLUIDS, BCReborn.MODID);
  private static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BCReborn.MODID);
  private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BCReborn.MODID);
  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BCReborn.MODID);

  public static <B extends Block & BCBlock> RegistryObject<B> registerBlockItem(String name, Supplier<B> block) {
    return registerBlockItem(name, block, BlockItemNeptune::new);
  }

  public static <B extends Block & BCBlock, BI extends BlockItem & BCItem> RegistryObject<B> registerBlockItem(String name, Supplier<B> block, BlockItemFunction<B, BI> function) {
    var ret = BLOCK.register(name, block);
    registerItem(name, () -> function.create(ret.get(), ret.get().itemProperties(), ret.get().getId()));
    return ret;
  }

  public static <B extends Block & BCBlock> RegistryObject<B> registerBlock(String name, Supplier<B> block) {
    return BLOCK.register(name, block);
  }

  public static <I extends Item & BCItem> RegistryObject<I> registerItem(String name, Supplier<I> item) {
    return ITEM.register(name, item);
  }

  public static <F extends Fluid> RegistryObject<F> registerFluid(String name, Supplier<F> fluid) {
    return FLUID.register(name, fluid);
  }

  public static <M extends BCMenu> RegistryObject<MenuType<M>> registerMenuType(String name, Supplier<MenuType<M>> menu) {
    return MENU_TYPE.register(name, menu);
  }

  public static RegistryObject<CreativeModeTab> registerTab(String name, Supplier<CreativeModeTab> tab) {
    return CREATIVE_MODE_TABS.register(name, tab);
  }

  public static <E extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<E>> registerBlockEntityType(String name, Supplier<BlockEntityType<E>> entity) {
    return BLOCK_ENTITY_TYPE.register(name, entity);
  }

  private static void init() {
    // Lib
    BCCoreBlocks.init();
    BCCoreFluids.init();

    // Core
    BCCoreItems.init();
    BCCoreBlockEntityTypes.init();
    BCCoreMenuTypes.init();

    // Builder
    BCBuilderBlocks.init();
  }

  public static void init(IEventBus bus) {
    init();

    BLOCK.register(bus);
    ITEM.register(bus);
    FLUID.register(bus);
    MENU_TYPE.register(bus);
    CREATIVE_MODE_TABS.register(bus);
    BLOCK_ENTITY_TYPE.register(bus);
  }

  @FunctionalInterface
  public interface BlockItemFunction<B extends Block & BCBlock, BI extends BlockItem> {
    BI create(B block, Item.Properties properties, String id);
  }
}
