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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornTransport.MODID)
public class TransportItems {
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  public static final RegistryObject<FacadeItem> FACADE = REGISTRY.registerItem("facade", FacadeItem::new);

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    // 全ての有効なブロックのFacadeを追加
    if (TransportConfig.isFacadeShowAllInCreative()) {
      //noinspection deprecation
      BuiltInRegistries.BLOCK.stream()
        .map(Block::defaultBlockState)
        .filter(FacadeItem::isBlockValidForCreativeTab)
        .forEach(state -> output.accept(FACADE.get().getFacadeForBlock(state)));
    }
  }
}
