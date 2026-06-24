/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.registry.BCRegistryKeys;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
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

  private final DeferredRegister<Schematic> SCHEMATIC;
  private final DeferredRegister<IStatement> STATEMENT;
  private final DeferredRegister<IStatementParameter> STATEMENT_PARAMETER;
  private final DeferredRegister<RedstoneBoardNBT<?>> REDSTONE_BOARD;

  private BCRegistry(String modid) {
    this.modid = modid;

    BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
    ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
    FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, modid);
    ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modid);
    BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modid);
    MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, modid);
    CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);

    SCHEMATIC = DeferredRegister.create(BCRegistryKeys.SCHEMATIC, modid);
    STATEMENT = DeferredRegister.create(BCRegistryKeys.STATEMENT, modid);
    STATEMENT_PARAMETER = DeferredRegister.create(BCRegistryKeys.STATEMENT_PARAMETER, modid);
    REDSTONE_BOARD = DeferredRegister.create(BCRegistryKeys.REDSTONE_BOARD, modid);
  }

  public static BCRegistry getRegistry(String modid) {
    return CREATED.computeIfAbsent(modid, BCRegistry::new);
  }

  public <B extends Block> RegistryObject<B> registerBlock(String name, Supplier<B> block) {
    return BLOCKS.register(name, block);
  }

  public <B extends Block> RegistryObject<B> registerBlockItem(String name, Supplier<B> block) {
    return registerBlockItem(name, BlockItem::new, block);
  }

  public <B extends Block, BI extends BlockItem> RegistryObject<B> registerBlockItem(
    String name,
    BlockItemSupplier<BI> item,
    Supplier<B> block
  ) {
    var reg = BLOCKS.register(name, block);
    registerItem(name, () -> item.get(reg.get(), new Item.Properties()));
    return reg;
  }

  public <I extends Item> RegistryObject<I> registerItem(String name, Supplier<I> item) {
    return ITEMS.register(name, item);
  }

  public <F extends Fluid> RegistryObject<F> registerFluid(String name, Supplier<F> fluid) {
    return FLUIDS.register(name, fluid);
  }

  public <E extends Entity> RegistryObject<EntityType<E>> registerEntityType(String name, Supplier<EntityType<E>> entityType) {
    return ENTITY_TYPES.register(name, entityType);
  }

  public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntityType(String name, Supplier<BlockEntityType<T>> blockEntityType) {
    return BLOCK_ENTITY_TYPES.register(name, blockEntityType);
  }

  public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, Supplier<MenuType<T>> menuType) {
    return MENU_TYPES.register(name, menuType);
  }

  public RegistryObject<CreativeModeTab> registerCreativeTab(String name, Supplier<CreativeModeTab> tab) {
    return CREATIVE_TABS.register(name, tab);
  }

  public <S extends Schematic> RegistryObject<S> registerSchematic(String name, Supplier<S> schematic) {
    return SCHEMATIC.register(name, schematic);
  }

  public <S extends IStatement> RegistryObject<S> registerStatement(String name, Supplier<S> statement) {
    return STATEMENT.register(name, statement);
  }

  public <P extends IStatementParameter> RegistryObject<P> registerStatementParameter(String name, Supplier<P> parameter) {
    return STATEMENT_PARAMETER.register(name, parameter);
  }

  public <T, R extends RedstoneBoardNBT<T>> RegistryObject<R> registerRedstoneBoard(String name, Supplier<R> board) {
    return REDSTONE_BOARD.register(name, board);
  }

  public void register(IEventBus bus) {
    BLOCKS.register(bus);
    ITEMS.register(bus);
    FLUIDS.register(bus);
    ENTITY_TYPES.register(bus);
    BLOCK_ENTITY_TYPES.register(bus);
    MENU_TYPES.register(bus);
    CREATIVE_TABS.register(bus);

    SCHEMATIC.register(bus);
    STATEMENT.register(bus);
    STATEMENT_PARAMETER.register(bus);
    REDSTONE_BOARD.register(bus);
  }

  @Contract(pure = true)
  public String getModId() {
    return modid;
  }

  public interface BlockItemSupplier<B extends BlockItem> {
    B get(Block block, Item.Properties properties);
  }
}
