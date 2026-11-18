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
package com.peco2282.bcreborn.transport.item;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Original {@code ItemGateCopier}: right-click a gate to copy its rules, sneak + right-click another gate to paste
 * them. Copying anything that is not a gate clears the copier. Pasting onto a smaller gate or one with other logic
 * still applies what fits, with a warning.
 */
public class GateCopierItem extends Item {
  public static final String TAG_LOGIC = "logic";
  public static final String TAG_MATERIAL = "material";

  public GateCopierItem(Properties properties) {
    super(properties.stacksTo(1));
  }

  /** Original {@code getIconIndex()}: the "full" icon once something was copied. */
  public static boolean hasCopy(ItemStack stack) {
    return stack.getTag() != null && stack.getTag().contains(TAG_LOGIC);
  }

  /** A pipe is handled before the pipe's own click (which would open the gate GUI). */
  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PipeBlockEntity)) return InteractionResult.PASS;
    return use(context);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    return use(context);
  }

  private static InteractionResult use(UseOnContext context) {
    Level level = context.getLevel();
    if (level.isClientSide) return InteractionResult.SUCCESS;
    Player player = context.getPlayer();
    ItemStack stack = context.getItemInHand();
    BlockPos pos = context.getClickedPos();
    PipeBlockEntity pipe = level.getBlockEntity(pos) instanceof PipeBlockEntity p ? p : null;
    // Original: anything that is not a pipe always copies (and so clears the copier).
    boolean copying = pipe == null || player == null || !player.isShiftKeyDown();
    Gate gate = pipe == null ? null : gateAt(pipe, pos, context.getClickedFace(), context.getHitResult());

    if (copying) {
      if (gate == null) {
        stack.setTag(null);
        message(player, "clear");
        return InteractionResult.CONSUME;
      }
      stack.setTag(copy(gate));
      message(player, "gate_copied");
      return InteractionResult.CONSUME;
    }

    CompoundTag data = stack.getTag();
    if (data == null || !data.contains(TAG_LOGIC)) {
      message(player, "no_information");
      return InteractionResult.CONSUME;
    }
    if (gate == null) {
      message(player, "no_gate");
      return InteractionResult.CONSUME;
    }
    for (String warning : paste(gate, data)) {
      message(player, warning);
    }
    pipe.setChanged();
    level.sendBlockUpdated(pos, pipe.getBlockState(), pipe.getBlockState(), 3);
    message(player, "gate_pasted");
    return InteractionResult.CONSUME;
  }

  /** The gate on the aimed side: the hit arm or pluggable, otherwise the clicked face. */
  @Nullable
  static Gate gateAt(PipeBlockEntity pipe, BlockPos pos, Direction face, @Nullable BlockHitResult hit) {
    Direction side = hit == null ? null : IronPipeLogic.resolveHitArm(pos, hit);
    PipePluggable<?> pluggable = pipe.getPipePluggable(side != null ? side : face);
    return pluggable instanceof GatePluggable gatePluggable ? gatePluggable.realGate : null;
  }

  /** What the copier stores: the gate's statements, its material and its logic. */
  public static CompoundTag copy(Gate gate) {
    CompoundTag data = new CompoundTag();
    gate.writeStatementsToNBT(data);
    data.putByte(TAG_MATERIAL, (byte) gate.material.ordinal());
    data.putByte(TAG_LOGIC, (byte) gate.logic.ordinal());
    return data;
  }

  /**
   * Original paste: warn about what does not fit, then load the statements anyway.
   *
   * @return the warning message keys
   */
  public static java.util.List<String> paste(Gate gate, CompoundTag data) {
    java.util.List<String> warnings = new java.util.ArrayList<>();
    GateDefinition.GateMaterial source = GateDefinition.GateMaterial.fromOrdinal(data.getByte(TAG_MATERIAL));
    GateDefinition.GateMaterial target = gate.material;
    if (target.numSlots < source.numSlots) warnings.add("warning.slots");
    if (target.numActionParameters < source.numActionParameters) warnings.add("warning.action_parameters");
    if (target.numTriggerParameters < source.numTriggerParameters) warnings.add("warning.trigger_parameters");
    if (data.getByte(TAG_LOGIC) != gate.logic.ordinal()) warnings.add("warning.logic");
    gate.readStatementsFromNBT(data);
    if (!gate.verifyGateStatements()) warnings.add("warning.load");
    return warnings;
  }

  private static void message(@Nullable Player player, String key) {
    if (player != null) player.displayClientMessage(Component.translatable("chat.bcreborntransport.gate_copier." + key), false);
  }
}
