package com.peco2282.bcreborn.common.packet;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.packet.c2s.*;
import com.peco2282.bcreborn.common.packet.s2c.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class PacketController {
  private static final String PROTOCOL_VERSION = "1.0";

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
      BCReborn.getLocation("main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals
  );

  private static int id = 0;

  public static void init() {
    // Client -> Server

    /// Blueprint selection
    registerC2S(
        SelectBlueprintPacket.class,
        SelectBlueprintPacket::encode,
        SelectBlueprintPacket::decode,
        SelectBlueprintPacket::handle
    );
    registerC2S(
        UploadBlueprintBeginPacket.class,
        UploadBlueprintBeginPacket::encode,
        UploadBlueprintBeginPacket::decode,
        UploadBlueprintBeginPacket::handle
    );
    registerC2S(
        UploadBlueprintChunkPacket.class,
        UploadBlueprintChunkPacket::encode,
        UploadBlueprintChunkPacket::decode,
        UploadBlueprintChunkPacket::handle
    );
    registerC2S(
        UploadBlueprintEndPacket.class,
        UploadBlueprintEndPacket::encode,
        UploadBlueprintEndPacket::decode,
        UploadBlueprintEndPacket::handle
    );
    registerC2S(
        UploadNothingToServerPacket.class,
        UploadNothingToServerPacket::encode,
        UploadNothingToServerPacket::decode,
        UploadNothingToServerPacket::handle
    );

    /// Architect selection
    registerC2S(
        SetArchitectNamePacket.class,
        SetArchitectNamePacket::encode,
        SetArchitectNamePacket::decode,
        SetArchitectNamePacket::handle
    );
    registerC2S(
        SetReadArchitectConfigurationPacket.class,
        SetReadArchitectConfigurationPacket::encode,
        SetReadArchitectConfigurationPacket::decode,
        SetReadArchitectConfigurationPacket::handle
    );
    registerC2S(
        SetFillerPatternPacket.class,
        SetFillerPatternPacket::encode,
        SetFillerPatternPacket::decode,
        SetFillerPatternPacket::handle
    );
    registerC2S(
        DeleteBlueprintPacket.class,
        DeleteBlueprintPacket::encode,
        DeleteBlueprintPacket::decode,
        DeleteBlueprintPacket::handle
    );
    registerC2S(
        EraseBuilderTankPacket.class,
        EraseBuilderTankPacket::encode,
        EraseBuilderTankPacket::decode,
        EraseBuilderTankPacket::handle
    );
    registerS2C(
        SyncBuilderRequirementsPacket.class,
        SyncBuilderRequirementsPacket::encode,
        SyncBuilderRequirementsPacket::decode,
        SyncBuilderRequirementsPacket::handle
    );

    // Server -> Client
    registerS2C(
        DownloadBlueprintPacket.class,
        DownloadBlueprintPacket::encode,
        DownloadBlueprintPacket::decode,
        DownloadBlueprintPacket::handle
    );
    registerS2C(
        RequestSelectedBlueprintPacket.class,
        RequestSelectedBlueprintPacket::encode,
        RequestSelectedBlueprintPacket::decode,
        RequestSelectedBlueprintPacket::handle
    );

    /// Architect
    registerS2C(
        ArchitectNameSyncPacket.class,
        ArchitectNameSyncPacket::encode,
        ArchitectNameSyncPacket::decode,
        ArchitectNameSyncPacket::handle
    );

    /// Builder
    registerS2C(
        SetItemRequirementsPacket.class,
        SetItemRequirementsPacket::encode,
        SetItemRequirementsPacket::decode,
        SetItemRequirementsPacket::handle
    );
    registerS2C(
        ClearItemRequirementsPacket.class,
        ClearItemRequirementsPacket::encode,
        ClearItemRequirementsPacket::decode,
        ClearItemRequirementsPacket::handle
    );
    registerS2C(
        PacketGuiWidget.class,
        PacketGuiWidget::encode,
        PacketGuiWidget::decode,
        PacketGuiWidget::handle
    );
  }

  private static <P> void registerS2C(
      Class<P> clazz,
      BiConsumer<P, FriendlyByteBuf> encoder,
      Function<FriendlyByteBuf, P> decoder,
      BiConsumer<P, Supplier<NetworkEvent.Context>> handler
      ) {
    CHANNEL.registerMessage(id++, clazz, encoder, decoder, handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  }

  private static <P> void registerC2S(
      Class<P> clazz,
      BiConsumer<P, FriendlyByteBuf> encoder,
      Function<FriendlyByteBuf, P> decoder,
      BiConsumer<P, Supplier<NetworkEvent.Context>> handler
      ) {
    CHANNEL.registerMessage(id++, clazz, encoder, decoder, handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
  }
}
