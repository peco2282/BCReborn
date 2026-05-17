package com.peco2282.bcreborn.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("UnusedReturnValue")
public final class BCRegistry {
  private static final Map<String, BCRegistry> CREATED = new ConcurrentHashMap<>();
  private final String modid;

  private final DeferredRegister<Block> BLOCKS;
  private final DeferredRegister<Item> ITEMS;
  private final DeferredRegister<Fluid> FLUIDS;
  private final DeferredRegister<EntityType<?>> ENTITY_TYPES;
  private final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
  private final DeferredRegister<MenuType<?>> MENU_TYPES;
  private final DeferredRegister<CreativeModeTab> CREATIVE_TABS;

  private BCRegistry(@NotNull String modid) {
    this.modid = modid;

    BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
    ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
    FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, modid);
    ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modid);
    BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modid);
    MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, modid);
    CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);
  }

  public <B extends Block> RegistryObject<B> registerBlock(@NotNull String name, @NotNull Supplier<B> block) {
    return BLOCKS.register(name, block);
  }

  public interface BlockItemSupplier<B extends BlockItem> {
    B get(Block block, Item.Properties properties);
  }

  public <B extends Block> RegistryObject<B> registerBlockItem(@NotNull String name, @NotNull Supplier<B> block) {
    return registerBlockItem(name, BlockItem::new, block);
  }

  public <B extends Block, BI extends BlockItem> RegistryObject<B> registerBlockItem(
      @NotNull String name,
      @NotNull BlockItemSupplier<BI> item,
      @NotNull Supplier<B> block
  ) {
    var reg = BLOCKS.register(name, block);
    registerItem(name, () -> item.get(reg.get(), new Item.Properties()));
    return reg;
  }

  public <I extends Item> RegistryObject<I> registerItem(@NotNull String name, @NotNull Supplier<I> item) {
    return ITEMS.register(name, item);
  }

  public <F extends Fluid> RegistryObject<F> registerFluid(@NotNull String name, @NotNull Supplier<F> fluid) {
    return FLUIDS.register(name, fluid);
  }

  public <E extends Entity> RegistryObject<EntityType<E>> registerEntityType(@NotNull String name, @NotNull Supplier<EntityType<E>> entityType) {
    return ENTITY_TYPES.register(name, entityType);
  }

  public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntityType(@NotNull String name, @NotNull Supplier<BlockEntityType<T>> blockEntityType) {
    return BLOCK_ENTITY_TYPES.register(name, blockEntityType);
  }

  public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(@NotNull String name, @NotNull Supplier<MenuType<T>> menuType) {
    return MENU_TYPES.register(name, menuType);
  }

  public RegistryObject<CreativeModeTab> registerCreativeTab(@NotNull String name, @NotNull Supplier<CreativeModeTab> tab) {
    return CREATIVE_TABS.register(name, tab);
  }

  public void register(IEventBus bus) {
    BLOCKS.register(bus);
    ITEMS.register(bus);
    FLUIDS.register(bus);
    ENTITY_TYPES.register(bus);
    BLOCK_ENTITY_TYPES.register(bus);
    MENU_TYPES.register(bus);
    CREATIVE_TABS.register(bus);
  }

  @Contract(pure = true)
  public String getModId() {
    return modid;
  }

  public static BCRegistry getRegistry(String modid) {
    return CREATED.computeIfAbsent(modid, BCRegistry::new);
  }
}
