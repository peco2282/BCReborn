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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * Original {@code PipeLogicWood}: the side a wooden-family pipe extracts from. It must face an inventory (item
 * pipes) or a fluid handler (fluid pipes) that is not a pipe. The pipe keeps its side while it stays valid and only
 * moves on when it no longer is; the wrench moves it to the next valid side.
 */
public final class WoodPipeLogic {
  private WoodPipeLogic() {
  }

  /** Original {@code isValidFacing()} / {@code isValidConnectingTile()}. */
  public static boolean isValidSource(PipeBlockEntity pipe, Direction side) {
    Level level = pipe.getLevel();
    if (level == null || pipe.hasBlockingPluggable(side)) return false;
    BlockEntity neighbour = level.getBlockEntity(pipe.getBlockPos().relative(side));
    if (neighbour == null || neighbour instanceof PipeBlockEntity) return false;
    return pipe.getTransportType() == PipeType.FLUID
      ? neighbour.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).isPresent()
      : neighbour.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite()).isPresent();
  }

  /**
   * Original {@code switchSource()}: the next valid side after the current one.
   *
   * @return the new side, or null if no side is valid (the pipe keeps its side then)
   */
  @Nullable
  public static Direction switchSource(PipeBlockEntity pipe) {
    int current = pipe.getExtractionSide().ordinal();
    for (int i = current + 1; i <= current + 6; i++) {
      Direction side = Direction.values()[i % 6];
      if (isValidSource(pipe, side)) {
        apply(pipe, side);
        return side;
      }
    }
    return null;
  }

  /** Wood, Emerald and Emzuli (the original PipeItemsWood / PipeFluidsWood family) show their side as an arm. */
  public static boolean isWoodFamily(PipeMaterial material) {
    return material == PipeMaterial.WOOD || material == PipeMaterial.EMERALD || material == PipeMaterial.EMZULI;
  }

  /** Original {@code switchSourceIfNeeded()} on placement. */
  public static void switchSourceIfNeeded(PipeBlockEntity pipe) {
    if (pipe.getLevel() == null || pipe.getLevel().isClientSide) return;
    if (!isValidSource(pipe, pipe.getExtractionSide())) switchSource(pipe);
  }

  /**
   * Original {@code switchSourceIfNeeded()} when a neighbour changes. Called from a shape update, so it only stores
   * the side; {@code PipeBlock.updateShape} puts it into the block state it returns.
   */
  public static void chooseSourceIfNeeded(PipeBlockEntity pipe) {
    if (pipe.getLevel() == null || pipe.getLevel().isClientSide || isValidSource(pipe, pipe.getExtractionSide())) return;
    int current = pipe.getExtractionSide().ordinal();
    for (int i = current + 1; i <= current + 6; i++) {
      Direction side = Direction.values()[i % 6];
      if (isValidSource(pipe, side)) {
        pipe.setExtractionSide(side);
        return;
      }
    }
  }

  /** The wrench (original {@code blockActivated()} with an {@code IToolWrench}). */
  public static void onWrench(PipeBlockEntity pipe, @Nullable Player player) {
    Direction side = switchSource(pipe);
    if (player != null && side != null) {
      player.displayClientMessage(Component.translatable("message.bcreborntransport.extraction_side", side.getName()), true);
    }
  }

  /** Stores the side and redraws the arms (the side shows as the extraction arm, and decides the item connections). */
  private static void apply(PipeBlockEntity pipe, Direction side) {
    pipe.setExtractionSide(side);
    Level level = pipe.getLevel();
    BlockPos pos = pipe.getBlockPos();
    BlockState state = level.getBlockState(pos);
    if (!(state.getBlock() instanceof PipeBlock block)) return;
    BlockState updated = state;
    for (Direction dir : Direction.values()) {
      updated = updated.setValue(PipeBlock.PROPERTY_MAP.get(dir), block.canConnectTo(level, pos, dir));
    }
    updated = updated.setValue(PipeBlock.EXTRACTION_SIDE, side.get3DDataValue());
    if (updated != state) level.setBlock(pos, updated, 3);
  }
}
