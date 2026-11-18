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

import com.peco2282.bcreborn.transport.item.GateCopierItem;
import com.peco2282.bcreborn.transport.item.PowerAdapterItem;
import net.minecraft.world.item.Item;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import com.peco2282.bcreborn.transport.item.LensItem;
import com.peco2282.bcreborn.transport.item.PlugItem;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;
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

  public static final RegistryObject<PlugItem> PIPE_PLUG = REGISTRY.registerItem("pipe_plug", PlugItem::new);
  /** Original pipeWaterproof: turns an item pipe into a fluid pipe. */
  public static final RegistryObject<Item> PIPE_SEALANT = REGISTRY.registerItem("pipe_waterproof", () -> new Item(new Item.Properties()));
  /** Original {@code ItemPowerAdapter}. */
  public static final RegistryObject<PowerAdapterItem> POWER_ADAPTER = REGISTRY.registerItem("pipe_power_adapter", PowerAdapterItem::new);
  /** Original {@code ItemGateCopier}. */
  public static final RegistryObject<GateCopierItem> GATE_COPIER = REGISTRY.registerItem("gate_copier", () -> new GateCopierItem(new Item.Properties()));
  /** Original {@code ItemLens}: one lens and one filter per dye colour, plus the clear variants. */
  private static final Map<DyeColor, RegistryObject<LensItem>> LENSES = new EnumMap<>(DyeColor.class);
  private static final Map<DyeColor, RegistryObject<LensItem>> FILTERS = new EnumMap<>(DyeColor.class);
  public static final RegistryObject<LensItem> PIPE_LENS_CLEAR = REGISTRY.registerItem("pipe_lens_clear", () -> new LensItem(null, false));
  public static final RegistryObject<LensItem> PIPE_FILTER_CLEAR = REGISTRY.registerItem("pipe_filter_clear", () -> new LensItem(null, true));

  static {
    for (DyeColor color : DyeColor.values()) {
      LENSES.put(color, REGISTRY.registerItem("pipe_lens_" + color.getName(), () -> new LensItem(color, false)));
      FILTERS.put(color, REGISTRY.registerItem("pipe_filter_" + color.getName(), () -> new LensItem(color, true)));
    }
  }

  /**
   * @param color  the lens colour, or null for the clear variant
   * @param filter true for a filter, false for a lens
   */
  public static LensItem lens(@Nullable DyeColor color, boolean filter) {
    if (color == null) return (filter ? PIPE_FILTER_CLEAR : PIPE_LENS_CLEAR).get();
    return (filter ? FILTERS : LENSES).get(color).get();
  }

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
    output.accept(PIPE_PLUG.get());
    output.accept(PIPE_SEALANT.get());
    output.accept(GATE_COPIER.get());
    output.accept(POWER_ADAPTER.get());
    for (DyeColor color : DyeColor.values()) output.accept(lens(color, false));
    output.accept(PIPE_LENS_CLEAR.get());
    for (DyeColor color : DyeColor.values()) output.accept(lens(color, true));
    output.accept(PIPE_FILTER_CLEAR.get());
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
