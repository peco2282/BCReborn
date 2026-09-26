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
package com.peco2282.bcreborn.common.packet;

import com.peco2282.bcreborn.builders.menu.*;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Optional;

/** Server-thread validation for mutations requested by machine screens. */
public final class ServerPacketAccess {
  private ServerPacketAccess() {}

  public static <BE extends BlockEntity> Optional<BE> getMenuBlockEntity(
      ServerPlayer player, BlockPos pos, BlockEntityType<BE> type) {
    if (player == null || player.isSpectator() || !player.isAlive()
      || player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64
      || !player.serverLevel().hasChunkAt(pos)) return Optional.empty();

    AbstractContainerMenu menu = player.containerMenu;
    BlockEntity target = menuTarget(menu);
    if (target == null || target.isRemoved() || !target.getBlockPos().equals(pos)
      || target.getLevel() != player.level() || !menu.stillValid(player)) return Optional.empty();
    return player.serverLevel().getBlockEntity(pos, type).filter(be -> be == target);
  }

  private static BlockEntity menuTarget(AbstractContainerMenu menu) {
    if (menu instanceof BuilderMenu builder) return builder.getBuilder();
    if (menu instanceof FillerMenu filler) return filler.getFiller();
    if (menu instanceof ArchitectMenu architect) return architect.getArchitect();
    if (menu instanceof BlueprintLibraryMenu library) return library.getLibrary();
    if (menu instanceof ZonePlanMenu zone) return zone.getTile();
    return null;
  }
}
