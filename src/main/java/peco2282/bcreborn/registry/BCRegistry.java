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


/**
 * This class serves as a central registry for all blocks, items, fluids, menus,
 * creative mode tabs, block entities, and other game elements in the BCReborn mod.
 * It provides utility methods for registering these elements with Forge and Minecraft.
 *
 * @author peco2282
 */
public class BCRegistry {
  /** Registry for blocks in the mod. */
  private static final DeferredRegister<Block> BLOCK = createRegistry(ForgeRegistries.BLOCKS);
  /** Registry for items in the mod. */
  private static final DeferredRegister<Item> ITEM = createRegistry(ForgeRegistries.ITEMS);
  /** Registry for fluids in the mod. */
  private static final DeferredRegister<Fluid> FLUID = createRegistry(ForgeRegistries.FLUIDS);
  /** Registry for menu types in the mod. */
  private static final DeferredRegister<MenuType<?>> MENU_TYPE = createRegistry(ForgeRegistries.MENU_TYPES);
  /** Registry for creative mode tabs in the mod. */
  private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = createRegistry(Registries.CREATIVE_MODE_TAB);
  /** Registry for block entity types in the mod. */
  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = createRegistry(ForgeRegistries.BLOCK_ENTITY_TYPES);
  /** Registry for custom menu textures in the mod. */
  private static final DeferredRegister<MenuTextureRegistry> MENU_TEXTURE = createRegistry(MenuTextureRegistry.MENU_TEXTURE);
  /** Registry for fluid types in the mod. */
  private static final DeferredRegister<FluidType> FLUID_TYPE = createRegistry(ForgeRegistries.FLUID_TYPES.getKey());
  /** Registry for placement modifier types in the mod. */
  private static final DeferredRegister<PlacementModifierType<?>> PMT = createRegistry(Registries.PLACEMENT_MODIFIER_TYPE);

  /**
   * Creates a registry using a Forge registry.
   *
   * @param registry The Forge registry to wrap.
   * @param <R> The type of the registry entries.
   * @param <F> The Forge registry type.
   * @return A DeferredRegister object for the given registry.
   */
  private static <R, F extends IForgeRegistry<R>> DeferredRegister<R> createRegistry(F registry) {
    return DeferredRegister.create(registry, BCReborn.MODID);
  }
  /**
   * Creates a registry using a Minecraft registry key.
   *
   * @param registry The resource key for the registry.
   * @param <K> The type of the registry entries.
   * @param <R> The Minecraft registry type.
   * @return A DeferredRegister object for the given registry.
   */
  private static <K, R extends Registry<K>> DeferredRegister<K> createRegistry(ResourceKey<R> registry) {
    return DeferredRegister.create(registry, BCReborn.MODID);
  }

  /**
   * Registers a block and its corresponding block item using the default item function.
   *
   * @param name The registry name of the block.
   * @param block The supplier for the block.
   * @param <B> The type of the block.
   * @return A RegistryObject containing the block.
   */
  /**
   * Registers a block and its corresponding block item using the default item function.
   *
   * @param name   The registry name of the block.
   * @param block  The supplier for the block.
   * @param <B>    The type of the block.
   * @return A RegistryObject containing the block.
   */
  public static <B extends Block & BCBlock> RegistryObject<B> registerBlockItem(String name, Supplier<B> block) {
      return registerBlockItem(name, block, BlockItemNeptune::new);
  }

  public static <B extends Block & BCBlock, BI extends BlockItem & BCItem> RegistryObject<B> registerBlockItem(String name, Supplier<B> block, BlockItemFunction<B, BI> function) {
    var ret = registerBlock(name, block);
    registerItem(name, () -> function.create(ret.get(), ret.get().itemProperties(), ret.get().getId()));
    return ret;
  }

  /**
   * Registers a block with the provided name and supplier.
   *
   * @param name   The registry name of the block.
   * @param block  The supplier for the block.
   * @param <B>    The type of the block.
   * @return A RegistryObject containing the block.
   */
  public static <B extends Block & BCBlock> RegistryObject<B> registerBlock(String name, Supplier<B> block) {
      return BLOCK.register(name, block);
  }

  /**
   * Registers an item with the provided name and supplier.
   *
   * @param name   The registry name of the item.
   * @param item   The supplier for the item.
   * @param <I>    The type of the item.
   * @return A RegistryObject containing the item.
   */
  public static <I extends Item & BCItem> RegistryObject<I> registerItem(String name, Supplier<I> item) {
      return ITEM.register(name, item);
  }

  /**
   * Registers a fluid with the provided name and supplier.
   *
   * @param name   The registry name of the fluid.
   * @param fluid  The supplier for the fluid.
   * @param <F>    The type of the fluid.
   * @return A RegistryObject containing the fluid.
   */
  public static <F extends Fluid> RegistryObject<F> registerFluid(String name, Supplier<F> fluid) {
      return FLUID.register(name, fluid);
  }

  /**
   * Registers a menu type with the provided name and supplier.
   *
   * @param name   The registry name of the menu type.
   * @param menu   The supplier for the menu type.
   * @param <M>    The type of the menu.
   * @return A RegistryObject containing the menu type.
   */
  public static <M extends BCMenu> RegistryObject<MenuType<M>> registerMenuType(String name, Supplier<MenuType<M>> menu) {
      return MENU_TYPE.register(name, menu);
  }

  /**
   * Registers a creative mode tab with the provided name and supplier.
   *
   * @param name   The registry name of the creative mode tab.
   * @param tab    The supplier for the creative mode tab.
   * @return A RegistryObject containing the creative mode tab.
   */
  public static RegistryObject<CreativeModeTab> registerTab(String name, Supplier<CreativeModeTab> tab) {
      return CREATIVE_MODE_TABS.register(name, tab);
  }

  /**
   * Registers a block entity type with the provided name and supplier.
   *
   * @param name    The registry name of the block entity type.
   * @param entity  The supplier for the block entity type.
   * @param <E>     The type of the block entity.
   * @return A RegistryObject containing the block entity type.
   */
  public static <E extends BlockEntity & BCBlockEntity> RegistryObject<BlockEntityType<E>> registerBlockEntityType(String name, Supplier<BlockEntityType<E>> entity) {
      return BLOCK_ENTITY_TYPE.register(name, entity);
  }

  /**
   * Registers a fluid type with the provided name and supplier.
   *
   * @param name   The registry name of the fluid type.
   * @param fluid  The supplier for the fluid type.
   * @return A RegistryObject containing the fluid type.
   */
  public static RegistryObject<FluidType> registerFluidType(String name, Supplier<FluidType> fluid) {
      return FLUID_TYPE.register(name, fluid);
  }

  /**
   * Registers a placement modifier type with the provided name and codec.
   *
   * @param name   The registry name of the placement modifier type.
   * @param pmt    The codec for the placement modifier type.
   * @param <P>    The type of the placement modifier.
   * @return A RegistryObject containing the placement modifier type.
   */
  public static <P extends PlacementModifier> RegistryObject<PlacementModifierType<P>> registerPlacementModifierType(String name, MapCodec<P> pmt) {
      return PMT.register(name, () -> () -> pmt);
  }

  /**
   * Initializes all the registries and links them to the given event bus.
   *
   * @param bus  The event bus to which the registries should be registered.
   */
  public static void init(IEventBus bus) {
      ContextProcessor.initRegister();

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

  /**
   * Functional interface representing a function that creates a block item.
   *
   * @param <B> The type of the block.
   * @param <BI> The type of the block item.
   */
  @FunctionalInterface
  public interface BlockItemFunction<B extends Block & BCBlock, BI extends BlockItem> {
    /**
     * Creates a block item for the given block.
     *
     * @param block The block for which to create the block item.
     * @param properties The item properties of the block item.
     * @param id The registry ID of the block item.
     * @return A new block item instance for the given block.
     */
    BI create(B block, Item.Properties properties, String id);
  }
}
