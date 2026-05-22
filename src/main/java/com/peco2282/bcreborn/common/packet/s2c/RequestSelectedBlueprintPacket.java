package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestSelectedBlueprintPacket(
    BlockPos pos,
    int blueprintId
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeInt(blueprintId);
  }

  public static RequestSelectedBlueprintPacket decode(FriendlyByteBuf buf) {
    return new RequestSelectedBlueprintPacket(buf.readBlockPos(), buf.readInt());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> context) {
    NetworkEvent.Context ctx = context.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BLUEPRINT_LIBRARY.get())
          .ifPresent(library -> {
            // TODO: implement when BlueprintDatabase is available
            BCNetworkManager.sendUploadNothing(pos);
          });
    });
    ctx.setPacketHandled(true);
  }
}
