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
package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record SetItemRequirementsPacket(
    BlockPos pos,
    List<RequirementItemStack> requirements
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeCollection(requirements, (buf, req) -> req.writeData(buf));
  }

  public static SetItemRequirementsPacket decode(FriendlyByteBuf buffer) {
    return new SetItemRequirementsPacket(
        buffer.readBlockPos(),
        buffer.readCollection(ArrayList::new, RequirementItemStack::decode)
    );
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BUILDER.get())
          .ifPresent(be -> {
            be.setItemRequirements(requirements);
          });
    });
    ctx.setPacketHandled(true);
  }
}
