package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.block.entity.WoodEngineBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@InitRegister(modId = BCRebornCore.MODID, priority = 1)
public class BlockEntityTypesCore {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  public static final RegistryObject<BlockEntityType<WoodEngineBlockEntity>> WOODEN_ENGINE = register("wood_engine", of(WoodEngineBlockEntity::new, BlocksCore.WOODEN_ENGINE));

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }

  @SafeVarargs
  private static <T extends BlockEntity> Supplier<BlockEntityType<T>> of(BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block>... validBlocks) {
    return () -> new BlockEntityType<>(supplier, Arrays.stream(validBlocks).map(Supplier::get).collect(Collectors.toSet()), null);
  }
}
