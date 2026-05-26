package com.peco2282.bcreborn.common.packet;

import com.peco2282.bcreborn.common.blueprint.BlueprintReadConfiguration;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.packet.c2s.*;
import com.peco2282.bcreborn.common.packet.s2c.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public class BCNetworkManager {
  private static final SimpleChannel channel = PacketController.CHANNEL;
  // Client -> Server
  /// Blueprint
  public static void sendSelectBlueprint(BlockPos pos, int blueprintId) {
    sendToServer(new RequestSelectedBlueprintPacket(pos, blueprintId));
  }
  public static void sendUploadBlueprintBegin(BlockPos pos, LibraryId libraryId, int count) {
    sendToServer(new UploadBlueprintBeginPacket(pos, libraryId, count));
  }

  public static void sendUploadBlueprintChunk(BlockPos pos, int chunk, byte[] data) {
    sendToServer(new UploadBlueprintChunkPacket(pos, chunk, data));
  }

  public static void sendUploadBlueprintChunk(BlockPos pos, int chunk, byte[] data, int start) {
    sendToServer(new UploadBlueprintChunkPacket(pos, chunk, data, start));
  }

  public static void sendUploadBlueprintEnd(BlockPos pos) {
    sendToServer(new UploadBlueprintEndPacket(pos));
  }

  public static void sendUploadNothing(BlockPos pos) {
    sendToServer(new UploadNothingToServerPacket(pos));
  }

  public static void sendUploadBuildersInAction(BlockPos pos) {
    sendToServer(new UploadBuildersInActionPacket(pos));
  }

  public static void sendDeleteBlueprint(BlockPos pos) {
    sendToServer(new DeleteBlueprintPacket(pos));
  }

  public static void sendEraseBuilderTank(BlockPos pos, int tankId) {
    sendToServer(new EraseBuilderTankPacket(pos, tankId));
  }

  public static void sendSyncBuilderRequirements(ServerPlayer player, BlockPos pos, List<RequirementItemStack> requirements) {
    sendToPlayer(player, new SyncBuilderRequirementsPacket(pos, requirements));
  }

  /// Architect
  public static void sendSetArchitectName(BlockPos pos, String name) {
    sendToServer(new SetArchitectNamePacket(pos, name));
  }

  public static void sendSetReadArchitectConfiguration(BlockPos pos, BlueprintReadConfiguration config) {
    sendToServer(new SetReadArchitectConfigurationPacket(pos, config));
  }

  public static void sendSetFillerPattern(BlockPos pos, int delta) {
    sendToServer(new SetFillerPatternPacket(pos, delta));
  }

  /// List
  public static void sendListSetStack(int lineIndex, int slotIndex, ItemStack stack) {
    sendToServer(new ListSetStackPacket(lineIndex, slotIndex, stack));
  }

  public static void sendListSwitchButton(int lineIndex, int button) {
    sendToServer(new ListSwitchButtonPacket(lineIndex, button));
  }

  public static void sendListSetLabel(String label) {
    sendToServer(new ListSetLabelPacket(label));
  }

  // Server -> Client
  /// Blueprint
  public static void sendRequestBlueprint(ServerPlayer player, BlockPos pos, int blueprintId) {
    sendToPlayer(player, new RequestSelectedBlueprintPacket(pos, blueprintId));
  }

  public static void sendDownloadBlueprint(ServerPlayer player, BlockPos pos, LibraryId libraryId, byte[] data) {
    sendToPlayer(player, new DownloadBlueprintPacket(pos, libraryId, data));
  }

  public static void sendDownloadBlueprint(ServerPlayer player, BlockPos pos, LibraryId libraryId, byte[] data, short start) {
    sendToPlayer(player, new DownloadBlueprintPacket(pos, libraryId, data));
  }

  /// Architect
  public static void sendArchitectNameSync(BlockPos pos, BlockEntity blockEntity, String name, int distance) {
    sendToNear(blockEntity.getBlockPos().getCenter(), blockEntity.getLevel().dimension(), new ArchitectNameSyncPacket(pos, name), distance);
  }

  /// Builder
  public static void sendClearItemRequirements(BlockPos pos, BlockEntity blockEntity) {
    sendToWorld(blockEntity.getLevel().dimension(), new ClearItemRequirementsPacket(pos));
  }

  public static void sendLaunchItem(ServerPlayer player,BlockPos pos, BuildingItem item) {
    sendToPlayer(player, new LaunchItemPacket(pos, item));
  }

  public static void sendNearLaunchItem(Vec3 target, ResourceKey<Level> dimension, BlockPos pos, BuildingItem item) {
    sendToNear(target, dimension, new LaunchItemPacket(pos, item), 64);
  }
  // Helpers
  public static void sendToServer(CustomPacket packet) {
    channel.sendToServer(packet);
  }

  public static void sendToPlayer(ServerPlayer player, CustomPacket packet) {
    channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
  }

  public static void sendToNear(Vec3 vec3, ResourceKey<Level> dimension, CustomPacket packet, int distance) {
    channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(vec3.x, vec3.y, vec3.z, distance, dimension)), packet);
  }

  public static void sendToWorld(ResourceKey<Level> dimension, CustomPacket packet) {
    channel.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
  }
}
