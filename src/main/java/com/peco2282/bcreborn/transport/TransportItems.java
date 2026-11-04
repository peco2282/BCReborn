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
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.transport.item.PipeWireItem;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GateExpansionPulsar;
import com.peco2282.bcreborn.transport.gates.ItemGate;

@InitRegister(modId = BCRebornTransport.MODID)
public class TransportItems {
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  public static final RegistryObject<FacadeItem> FACADE = REGISTRY.registerItem("facade", FacadeItem::new);
  public static final RegistryObject<ItemGate> GATE =
    REGISTRY.registerItem("gate", ItemGate::new);
  public static final RegistryObject<PipeWireItem> PIPE_WIRE_RED = REGISTRY.registerItem("pipe_wire_red", () -> new PipeWireItem(PipeWire.RED));
  public static final RegistryObject<PipeWireItem> PIPE_WIRE_BLUE = REGISTRY.registerItem("pipe_wire_blue", () -> new PipeWireItem(PipeWire.BLUE));
  public static final RegistryObject<PipeWireItem> PIPE_WIRE_GREEN = REGISTRY.registerItem("pipe_wire_green", () -> new PipeWireItem(PipeWire.GREEN));
  public static final RegistryObject<PipeWireItem> PIPE_WIRE_YELLOW = REGISTRY.registerItem("pipe_wire_yellow", () -> new PipeWireItem(PipeWire.YELLOW));

  /** Links each {@link PipeWire} colour to its item (original: {@code PipeWire.item = pipeWire}). */
  public static void bindPipeWires() {
    PipeWire.RED.setItem(PIPE_WIRE_RED.get());
    PipeWire.BLUE.setItem(PIPE_WIRE_BLUE.get());
    PipeWire.GREEN.setItem(PIPE_WIRE_GREEN.get());
    PipeWire.YELLOW.setItem(PIPE_WIRE_YELLOW.get());
  }

  public static void registerCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(PIPE_WIRE_RED.get());
    output.accept(PIPE_WIRE_BLUE.get());
    output.accept(PIPE_WIRE_GREEN.get());
    output.accept(PIPE_WIRE_YELLOW.get());
    for (var material : GateDefinition.GateMaterial.values()) {
      for (var logic : GateDefinition.GateLogic.values()) {
        var gate = ItemGate.makeGateItem(material, logic);
        output.accept(gate.copy());
        ItemGate.addGateExpansion(gate,
          GateExpansionPulsar.INSTANCE);
        output.accept(gate);
      }
    }
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
