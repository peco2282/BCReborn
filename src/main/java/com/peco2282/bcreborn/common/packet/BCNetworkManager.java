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

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.BlueprintReadConfiguration;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.packet.c2s.*;
import com.peco2282.bcreborn.common.packet.s2c.*;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.Consumer;

public class BCNetworkManager {
  private static final SimpleChannel channel = BCRebornCore.CHANNEL;
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

  // Robotics
  public static void sendRequestRequesterList(BlockPos pos) {
    sendToServer(new RequestRequesterListPacket(pos));
  }

  public static void sendSyncRequesterList(ServerPlayer player, BlockPos pos, ItemStack[] requests) {
    sendToPlayer(player, new SyncRequesterListPacket(pos, requests));
  }

  // ZonePlan
  public static void sendRequestZonePlanLoadArea(BlockPos pos, int index) {
    sendToServer(new RequestZonePlanLoadAreaPacket(pos, index));
  }

  public static void sendRequestZonePlanSaveArea(BlockPos pos, int index, ZonePlan plan) {
    sendToServer(new RequestZonePlanSaveAreaPacket(pos, index, plan));
  }

  public static void sendSyncZonePlanAreaLoaded(ServerPlayer player, BlockPos pos, ZonePlan plan) {
    sendToPlayer(player, new SyncZonePlanAreaLoadedPacket(pos, plan));
  }

  public static void sendRequestZonePlanComputeMap(BlockPos pos, int cx, int cz, int width, int height, float blocksPerPixel) {
    sendToServer(new RequestZonePlanComputeMapPacket(pos, cx, cz, width, height, blocksPerPixel));
  }

  public static void sendSyncZonePlanImage(ServerPlayer player, BlockPos pos, int totalSize, int offset, byte[] data) {
    sendToPlayer(player, new SyncZonePlanImagePacket(pos, totalSize, offset, data));
  }

  public static void sendRequestZonePlanSetName(BlockPos pos, String name) {
    // sendToServer(new RequestZonePlanSetNamePacket(pos, name)); // To be created
  }

  public static void computeMap(ZonePlanBlockEntity be, int cx, int cz, int width, int height, float blocksPerPixel, ServerPlayer player) {
    final byte[] textureData = new byte[width * height];
    // TODO: implement actual map computation or delegate to BE
    // For now, let's assume we send it in chunks
    int MAX_PACKET_LENGTH = 30000;
    for (int i = 0; i < textureData.length; i += MAX_PACKET_LENGTH) {
      int len = Math.min(textureData.length - i, MAX_PACKET_LENGTH);
      byte[] chunk = new byte[len];
      System.arraycopy(textureData, i, chunk, 0, len);
      sendSyncZonePlanImage(player, be.getBlockPos(), textureData.length, i, chunk);
    }
  }

  public static void sendRequestInitialization(int entityId, ItemStack itemInUse, boolean itemActive) {
    sendToServer(new RequestInitializationPacket(entityId, itemInUse, itemActive));
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

  public static void sendLaunchItem(ServerPlayer player, BlockPos pos, BuildingItem item) {
    sendToPlayer(player, new LaunchItemPacket(pos, item));
  }

  public static void sendNearLaunchItem(Vec3 target, ResourceKey<Level> dimension, BlockPos pos, BuildingItem item) {
    sendToNear(target, dimension, new LaunchItemPacket(pos, item), 64);
  }

  public static void sendInitialize(ServerPlayer player, int entityId, ItemStack itemInUse, boolean itemActive) {
    sendToPlayer(player, new InitializePacket(entityId, itemInUse, itemActive));
  }

  public static void sendClientSetInventory(ServerPlayer player, int entityId, short slot, ItemStack stack) {
    sendToPlayer(player, new ClientSetInventoryPacket(entityId, slot, stack));
  }

  public static void sendEntityClientSetInventory(Entity entity, int entityId, short slot, ItemStack stack) {
    sendToEntity(entity, new ClientSetInventoryPacket(entityId, slot, stack));
  }

  public static void sendClientSetItemInUse(ServerPlayer player, int entityId, ItemStack itemInUse) {
    sendToPlayer(player, new ClientSetItemInUsePacket(entityId, itemInUse));
  }

  public static void sendEntityClientSetItemInUse(Entity entity, int entityId, ItemStack itemInUse) {
    sendToEntity(entity, new ClientSetItemInUsePacket(entityId, itemInUse));
  }

  public static void sendSetItemActive(Entity entity, int entityId, boolean active) {
    sendToEntity(entity, new SetItemActivePacket(entityId, active));
  }

  public static void sendSetSteamDirection(Entity entity, int entityId, int x, int y, int z) {
    sendToEntity(entity, new SetSteamDirectionPacket(entityId, x, y, z));
  }

  public static void sendSyncWearables(Entity entity, int entityId, List<ItemStack> wearables) {
    sendToEntity(entity, new SyncWearablesPacket(entityId, wearables));
  }

  public static void sendBlockEntityUpdate(BuildCraftBlockEntity entity, BlockPos pos, Consumer<FriendlyByteBuf> consumer) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    consumer.accept(buf);
    if (buf.readableBytes() > 0) {
      byte[] data = new byte[buf.readableBytes()];
      buf.getBytes(0, data);
      sendToTargetChunk(entity, new BlockEntityUpdaterPacket(pos, data));
    }
  }

  // Helpers
  public static void sendToServer(CustomPacket packet) {
    channel.sendToServer(packet);
  }

  public static void sendToPlayer(ServerPlayer player, CustomPacket packet) {
    channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
  }

  public static void sendToEntity(Entity entity, CustomPacket packet) {
    channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
  }

  public static void sendToNear(Vec3 vec3, ResourceKey<Level> dimension, CustomPacket packet, int distance) {
    channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(vec3.x, vec3.y, vec3.z, distance, dimension)), packet);
  }

  public static void sendToWorld(ResourceKey<Level> dimension, CustomPacket packet) {
    channel.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
  }

  public static void sendToTargetChunk(BuildCraftBlockEntity entity, CustomPacket packet) {
    channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> entity.getLevel().getChunkAt(entity.getBlockPos())), packet);
  }
}
