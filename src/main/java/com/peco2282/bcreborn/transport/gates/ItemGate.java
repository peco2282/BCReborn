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
package com.peco2282.bcreborn.transport.gates;

import com.peco2282.bcreborn.api.gates.GateExpansions;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableItem;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.api.serialization.NbtReader;
import com.peco2282.bcreborn.api.serialization.NbtWriter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.transport.gates.GateDefinition.GateLogic;
import com.peco2282.bcreborn.transport.gates.GateDefinition.GateMaterial;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemGate extends Item implements IPipePluggableItem {

  protected static final String NBT_TAG_MAT = "mat";
  protected static final String NBT_TAG_LOGIC = "logic";
  protected static final String NBT_TAG_EX = "ex";
  private static ArrayList<ItemStack> allGates;

  public ItemGate() {
    super(new Item.Properties());
  }

  private static CompoundTag getNBT(ItemStack stack) {
    if (stack.isEmpty() || !(stack.getItem() instanceof ItemGate)) {
      return new CompoundTag();
    } else {
      return stack.getOrCreateTag();
    }
  }

  public static void setMaterial(ItemStack stack, GateMaterial material) {
    CompoundTag nbt = stack.getOrCreateTag();
    nbt.putByte(NBT_TAG_MAT, (byte) material.ordinal());
  }

  public static GateMaterial getMaterial(ItemStack stack) {
    CompoundTag nbt = getNBT(stack);

    if (nbt.isEmpty()) {
      return GateMaterial.REDSTONE;
    } else {
      return GateMaterial.fromOrdinal(nbt.getByte(NBT_TAG_MAT));
    }
  }

  public static GateLogic getLogic(ItemStack stack) {
    CompoundTag nbt = getNBT(stack);

    if (nbt.isEmpty()) {
      return GateLogic.AND;
    } else {
      return GateLogic.fromOrdinal(nbt.getByte(NBT_TAG_LOGIC));
    }
  }

  public static void setLogic(ItemStack stack, GateLogic logic) {
    CompoundTag nbt = stack.getOrCreateTag();
    nbt.putByte(NBT_TAG_LOGIC, (byte) logic.ordinal());
  }

  public static void addGateExpansion(ItemStack stack, IGateExpansion expansion) {
    NbtWriter.of(stack.getOrCreateTag())
      .putList(NBT_TAG_EX, expansionList -> {
        expansionList.add(StringTag.valueOf(expansion.getUniqueIdentifier().toString()));
      })
      .done();
  }

  public static boolean hasGateExpansion(ItemStack stack, IGateExpansion expansion) {
    return NbtReader.of(getNBT(stack))
      .getStrings(NBT_TAG_EX)
      .contains(expansion.getUniqueIdentifier().toString());
  }

  public static Set<IGateExpansion> getInstalledExpansions(ItemStack stack) {
    Set<IGateExpansion> expansions = new HashSet<>();
    NbtReader.of(getNBT(stack))
      .applyStrings(NBT_TAG_EX, list -> {
        for (String exTag : list) {
          expansions.add(GateExpansions.getExpansion(exTag));
        }
      });
    return expansions;
  }

  public static ItemStack makeGateItem(GateMaterial material, GateLogic logic) {
    // ItemStack stack = new ItemStack(BuildCraftTransport.pipeGate); // TODO
    ItemStack stack = ItemStack.EMPTY;
    if (stack.isEmpty()) return stack;
    CompoundTag nbt = stack.getOrCreateTag();
    nbt.putByte(NBT_TAG_MAT, (byte) material.ordinal());
    nbt.putByte(NBT_TAG_LOGIC, (byte) logic.ordinal());

    return stack;
  }

  public static ItemStack makeGateItem(Gate gate) {
    // ItemStack stack = new ItemStack(BuildCraftTransport.pipeGate); // TODO
    ItemStack stack = ItemStack.EMPTY;
    if (stack.isEmpty()) return stack;
    CompoundTag nbt = stack.getOrCreateTag();
    nbt.putByte(NBT_TAG_MAT, (byte) gate.material.ordinal());
    nbt.putByte(NBT_TAG_LOGIC, (byte) gate.logic.ordinal());

    for (IGateExpansion expansion : gate.expansions.keySet()) {
      addGateExpansion(stack, expansion);
    }

    return stack;
  }

  public static ArrayList<ItemStack> getAllGates() {
    if (allGates == null) {
      allGates = new ArrayList<>();
      for (GateDefinition.GateMaterial m : GateDefinition.GateMaterial.VALUES) {
        for (GateDefinition.GateLogic l : GateDefinition.GateLogic.VALUES) {
          if (m == GateMaterial.REDSTONE && l == GateLogic.OR) {
            continue;
          }

          allGates.add(ItemGate.makeGateItem(m, l));
        }
      }
    }
    return allGates;
  }

  @Override
  public Component getName(ItemStack stack) {
    return Component.literal(GateDefinition.getLocalizedName(getMaterial(stack), getLogic(stack)));
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
    list.add(Component.literal(StringUtils.localize("tip.gate.wires")));
    list.add(Component.literal(StringUtils.localize("tip.gate.wires." + getMaterial(stack).getTag())));
    Set<IGateExpansion> expansions = getInstalledExpansions(stack);

    if (!expansions.isEmpty()) {
      list.add(Component.literal(StringUtils.localize("tip.gate.expansions")));

      for (IGateExpansion expansion : expansions) {
        list.add(Component.literal(expansion.getDisplayName()));
      }
    }
  }

  @Override
  public PipePluggable<?> createPipePluggable(IPipe pipe, Direction side, ItemStack stack) {
    return GateFactory.makeGate(pipe, stack, side)
      .map(GatePluggable::new)
      .orElseGet(GatePluggable::new);
  }
}
