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
package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

/**
 * Original {@code TileAssemblyTable.rpcSelectRecipe()} / {@code "select"} command: plans or cancels a recipe.
 * Only accepted from a player who has this table's menu open nearby.
 */
@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record SelectAssemblyRecipePacket(BlockPos pos, ResourceLocation recipeId, boolean select) implements CustomPacket {
  public static SelectAssemblyRecipePacket decode(FriendlyByteBuf buffer) {
    return new SelectAssemblyRecipePacket(buffer.readBlockPos(), buffer.readResourceLocation(), buffer.readBoolean());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeResourceLocation(recipeId);
    buffer.writeBoolean(select);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> getMenuBlockEntity(ctx, pos, SiliconBlockEntityTypes.ASSEMBLY_TABLE.get())
      .ifPresent(table -> {
        if (select) {
          table.planOutput(recipeId);
        } else {
          table.cancelPlanOutput(recipeId);
        }
      }));
    ctx.setPacketHandled(true);
  }
}
