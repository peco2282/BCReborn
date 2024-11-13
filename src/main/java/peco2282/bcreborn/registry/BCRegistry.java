package peco2282.bcreborn.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.block.BCBlockEntity;
import peco2282.bcreborn.api.item.BCItem;
import peco2282.bcreborn.bean.ContextProcessor;
import peco2282.bcreborn.lib.block.menu.BCMenu;
import peco2282.bcreborn.lib.item.BlockItemNeptune;

import java.util.function.Supplier;

public class BCRegistry {
  private static final DeferredRegister<Block> BLOCK = createRegistry(ForgeRegistries.BLOCKS);
  private static final DeferredRegister<Item> ITEM = createRegistry(ForgeRegistries.ITEMS);
  private static final DeferredRegister<Fluid> FLUID = createRegistry(ForgeRegistries.FLUIDS);
  private static final DeferredRegister<MenuType<?>> MENU_TYPE = createRegistry(ForgeRegistries.MENU_TYPES);
  private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = createRegistry(Registries.CREATIVE_MODE_TAB);
  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = createRegistry(ForgeRegistries.BLOCK_ENTITY_TYPES);
  private static final DeferredRegister<MenuTextureRegistry> MENU_TEXTURE = createRegistry(MenuTextureRegistry.MENU_TEXTURE);
  private static final DeferredRegister<FluidType> FLUID_TYPE = createRegistry(ForgeRegistries.FLUID_TYPES.getKey());
  private static final DeferredRegister<PlacementModifierType<?>> PMT = createRegistry(Registries.PLACEMENT_MODIFIER_TYPE);

  private static <R, F extends IForgeRegistry<R>> DeferredRegister<R> createRegistry(F registry) {
    return DeferredRegister.create(registry, BCReborn.MODID);
  }
  private static <K, R extends Registry<K>> DeferredRegister<K> createRegistry(ResourceKey<R> registry) {
    return DeferredRegister.create(registry, BCReborn.MODID);
  }

  public static <B extends Block & BCBlock> RegistryObject<B> registerBlockItem(String name, Supplier<B> block) {
    return registerBlockItem(name, block, BlockItemNeptune::new);
  }

  public static <B extends Block & BCBlock, BI extends BlockItem & BCItem> RegistryObject<B> registerBlockItem(String name, Supplier<B> block, BlockItemFunction<B, BI> function) {
    var ret = registerBlock(name, block);
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

  public static RegistryObject<FluidType> registerFluidType(String name, Supplier<FluidType> fluid) {
    return FLUID_TYPE.register(name, fluid);
  }

  public static <P extends PlacementModifier> RegistryObject<PlacementModifierType<P>> registerPlacementModifierType(String name, MapCodec<P> pmt) {
    return PMT.register(name, () -> () -> pmt);
  }

  public static void init(IEventBus bus) {
    ContextProcessor.getInstance().initRegister();

    BLOCK.register(bus);
    ITEM.register(bus);
    FLUID.register(bus);
    MENU_TYPE.register(bus);
    CREATIVE_MODE_TABS.register(bus);
    BLOCK_ENTITY_TYPE.register(bus);
    MENU_TEXTURE.register(bus);
    FLUID_TYPE.register(bus);
    PMT.register(bus);
  }

  @FunctionalInterface
  public interface BlockItemFunction<B extends Block & BCBlock, BI extends BlockItem> {
    BI create(B block, Item.Properties properties, String id);
  }
}
