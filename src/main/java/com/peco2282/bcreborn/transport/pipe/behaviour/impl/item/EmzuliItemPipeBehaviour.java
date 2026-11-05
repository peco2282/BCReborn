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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.menu.EmzuliPipeMenu;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.extraction.StandardItemExtractionModule;
import com.peco2282.bcreborn.transport.pipe.runtime.EmzuliState;
import com.peco2282.bcreborn.transport.statements.ActionExtractionPreset;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * Port of the original {@code PipeItemsEmzuli}: a Wood pipe with four extraction presets.
 * <p>
 * Each of the four filter slots belongs to a preset (red, blue, green, yellow). A slot extracts only while a gate
 * on the pipe runs the matching "extraction preset" action, and only items matching its filter. Items extracted
 * through a slot are painted with that slot's colour (set in the GUI), so a Daizuli pipe can sort them. The pipe
 * cycles through the usable slots every tick. Without an active preset nothing is extracted.
 * <ul>
 *   <li>Right click: open the GUI (filters and paint buttons)</li>
 *   <li>Wrench: switch the extraction side, like the Wood pipe</li>
 * </ul>
 */
public class EmzuliItemPipeBehaviour implements ItemPipeBehaviour {

  public static final EmzuliItemPipeBehaviour INSTANCE = new EmzuliItemPipeBehaviour();

  private EmzuliItemPipeBehaviour() {
  }

  /** Same connection rules as the Wood pipe (the original extends {@code PipeItemsWood}). */
  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    return WoodenItemPipeBehaviour.INSTANCE.canConnectTo(pipe, dir, neighbor);
  }

  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    WoodenItemPipeBehaviour.INSTANCE.onPlace(pipe, level, oldState, isMoving);
  }

  @Override
  public void updateShape(PipeBlockEntity entity, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos neighborPos) {
    WoodenItemPipeBehaviour.INSTANCE.updateShape(entity, direction, neighbor, level, neighborPos);
  }

  /** The wrench switches the extraction side (original {@code PipeItemsWood.blockActivated()}). */
  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
    return WoodenItemPipeBehaviour.INSTANCE.onUse(pipe, level, pos, player, hand, hit);
  }

  /** Original {@code blockActivated()}: opens the GUI unless the player holds a pipe. */
  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack held = player.getItemInHand(hand);
    if (held.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof PipeBlock) {
      return InteractionResult.PASS;
    }
    if (player instanceof ServerPlayer serverPlayer && pipe.getEmzuliState() != null) {
      NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
        (id, inv, user) -> new EmzuliPipeMenu(id, inv, pipe),
        Component.translatable("menu.bcreborntransport.emzuli_pipe_menu")
      ), buf -> buf.writeBlockPos(pos));
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** Original {@code checkExtract()} + {@code onPostTick()}: extract through the current slot, then move on. */
  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    EmzuliState emzuli = pipe.getEmzuliState();
    if (emzuli == null) return;
    if (emzuli.canExtractCurrent()) {
      ItemStack filter = emzuli.getCurrentFilter();
      StandardItemExtractionModule.INSTANCE.extract(pipe, stack -> ItemStack.isSameItem(stack, filter), emzuli.currentPaint());
    }
    emzuli.incrementFilter();
  }

  /** Original {@code getActions()}: the four extraction presets. */
  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (var color : EmzuliState.PRESET_COLORS) {
      actions.add(TransportStatements.ACTION_EXTRACTION_PRESET.get(color).get());
    }
  }

  /** Original {@code actionsActivated()}: the active presets are replaced by this gate's active preset actions. */
  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    EmzuliState emzuli = pipe.getEmzuliState();
    if (emzuli == null) return;
    emzuli.clearPresets();
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionExtractionPreset preset) {
        emzuli.activatePreset(preset.color);
      }
    }
  }
}
