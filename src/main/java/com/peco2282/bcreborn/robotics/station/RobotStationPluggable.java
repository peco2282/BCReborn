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
package com.peco2282.bcreborn.robotics.station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IDockingStationProvider;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.tiles.IDebuggable;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableItem;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class RobotStationPluggable extends PipePluggable<RobotStationPluggable> implements IPipePluggableItem, IDebuggable,
    IDockingStationProvider {

  @OnlyIn(Dist.CLIENT)
  public static class RobotStationPluggableRenderer implements IPipePluggableRenderer {
    private static final ResourceLocation TEXTURE_NONE =
        BCRebornTransport.location("textures/block/pipes/pipe_robot_station.png");
    private static final ResourceLocation TEXTURE_RESERVED =
        BCRebornTransport.location("textures/block/pipes/pipe_robot_station_reserved.png");
    private static final ResourceLocation TEXTURE_LINKED =
        BCRebornTransport.location("textures/block/pipes/pipe_robot_station_linked.png");

    private static final float Z_FIGHT = 1.0f / 4096.0f;

    @Override
    public void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      if (renderPass != 0) return;

      RobotStationState state = ((RobotStationPluggable) pipePluggable).getRenderState();
      ResourceLocation texture = switch (state) {
        case Reserved -> TEXTURE_RESERVED;
        case Linked -> TEXTURE_LINKED;
        default -> TEXTURE_NONE;
      };

      VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

      poseStack.pushPose();
      applyDirectionTransform(poseStack, side);

      // Box 1: stem (narrow, from face outward)
      renderBox(poseStack, consumer, 0.4325f, 0.0f, 0.4325f, 0.5675f, 0.1875f + Z_FIGHT, 0.5675f, packedLight, packedOverlay);
      // Box 2: base plate (wider, slightly thicker)
      renderBox(poseStack, consumer, 0.25f, 0.1875f, 0.25f, 0.75f, 0.25f + Z_FIGHT, 0.75f, packedLight, packedOverlay);

      poseStack.popPose();
    }

    /**
     * Rotates the pose stack so that the pluggable faces outward from the given side.
     * The base geometry is defined for the DOWN face (Y- direction).
     */
    private void applyDirectionTransform(PoseStack poseStack, Direction side) {
      // Translate to block center, rotate, then translate back
      poseStack.translate(0.5, 0.5, 0.5);
      switch (side) {
        case DOWN:
          // geometry already defined for DOWN (Y-)
          break;
        case UP:
          poseStack.mulPose(Axis.XP.rotationDegrees(180));
          break;
        case NORTH:
          poseStack.mulPose(Axis.XP.rotationDegrees(90));
          break;
        case SOUTH:
          poseStack.mulPose(Axis.XP.rotationDegrees(-90));
          break;
        case WEST:
          poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
          break;
        case EAST:
          poseStack.mulPose(Axis.ZP.rotationDegrees(90));
          break;
      }
      poseStack.translate(-0.5, -0.5, -0.5);
    }

    private void renderBox(PoseStack poseStack, VertexConsumer consumer,
                           float minX, float minY, float minZ,
                           float maxX, float maxY, float maxZ,
                           int packedLight, int packedOverlay) {
      Matrix4f m = poseStack.last().pose();
      Matrix3f n = poseStack.last().normal();

      // Down (-Y)
      vertex(m, n, consumer, minX, minY, minZ, 0, 0,  0, -1,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, minY, minZ, 1, 0,  0, -1,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, minY, maxZ, 1, 1,  0, -1,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, minY, maxZ, 0, 1,  0, -1,  0, packedLight, packedOverlay);
      // Up (+Y)
      vertex(m, n, consumer, minX, maxY, maxZ, 0, 0,  0,  1,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, maxY, maxZ, 1, 0,  0,  1,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, maxY, minZ, 1, 1,  0,  1,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, maxY, minZ, 0, 1,  0,  1,  0, packedLight, packedOverlay);
      // North (-Z)
      vertex(m, n, consumer, minX, minY, minZ, 0, 0,  0,  0, -1, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, maxY, minZ, 0, 1,  0,  0, -1, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, maxY, minZ, 1, 1,  0,  0, -1, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, minY, minZ, 1, 0,  0,  0, -1, packedLight, packedOverlay);
      // South (+Z)
      vertex(m, n, consumer, maxX, minY, maxZ, 0, 0,  0,  0,  1, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, maxY, maxZ, 0, 1,  0,  0,  1, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, maxY, maxZ, 1, 1,  0,  0,  1, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, minY, maxZ, 1, 0,  0,  0,  1, packedLight, packedOverlay);
      // West (-X)
      vertex(m, n, consumer, minX, minY, maxZ, 0, 0, -1,  0,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, maxY, maxZ, 0, 1, -1,  0,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, maxY, minZ, 1, 1, -1,  0,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, minX, minY, minZ, 1, 0, -1,  0,  0, packedLight, packedOverlay);
      // East (+X)
      vertex(m, n, consumer, maxX, minY, minZ, 0, 0,  1,  0,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, maxY, minZ, 0, 1,  1,  0,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, maxY, maxZ, 1, 1,  1,  0,  0, packedLight, packedOverlay);
      vertex(m, n, consumer, maxX, minY, maxZ, 1, 0,  1,  0,  0, packedLight, packedOverlay);
    }

    private void vertex(Matrix4f m, Matrix3f n, VertexConsumer consumer,
                        float x, float y, float z, float u, float v,
                        float nx, float ny, float nz,
                        int packedLight, int packedOverlay) {
      consumer.vertex(m, x, y, z)
          .color(255, 255, 255, 255)
          .uv(u, v)
          .overlayCoords(OverlayTexture.NO_OVERLAY)
          .uv2(packedLight)
          .normal(n, nx, ny, nz)
          .endVertex();
    }
  }

  public enum RobotStationState {
    None,
    Available,
    Reserved,
    Linked
  }

  private RobotStationState renderState = RobotStationState.Available;
  private DockingStationPipe station;
  private boolean isValid = false;

  public RobotStationPluggable() {
    super(BCRebornTransport.ROBOT_STATION);
  }

  @Override
  public void writeToNBT(CompoundTag nbt) {
  }

  @Override
  public void readFromNBT(CompoundTag nbt) {
  }

  @Override
  public ItemStack[] getDropItems(IPipeTile pipe) {
    return new ItemStack[]{new ItemStack(RoboticsItems.ROBOT_STATION.get())};
  }

  @Override
  public DockingStation<?> getStation() {
    return station;
  }

  @Override
  public boolean isBlocking(IPipeTile pipe, Direction direction) {
    return true;
  }

  @Override
  public void invalidate() {
    if (station != null
        && station.getPipe() != null
        && !station.world.isClientSide) {
      if (RobotManager.registryProvider != null) {
        RobotManager.registryProvider.getRegistry(station.world).removeStation(station);
      }
      isValid = false;
    }
  }

  @Override
  public void validate(IPipeTile pipe, Direction direction) {
    if (!isValid && !pipe.getWorld().isClientSide) {
      if (RobotManager.registryProvider != null) {
        station = (DockingStationPipe)
            RobotManager.registryProvider.getRegistry(pipe.getWorld()).getStation(
                pipe.getPos(),
                direction);

        if (station == null) {
          station = new DockingStationPipe(pipe, direction);
          RobotManager.registryProvider.getRegistry(pipe.getWorld()).registerStation(station);
        }
      } else {
        station = new DockingStationPipe(pipe, direction);
      }

      isValid = true;
    }
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    // Approximate bounding box matching the original shape
    return switch (side) {
      case DOWN -> new AABB(0.25, 0.749, 0.25, 0.75, 1.0, 0.75);
      case UP -> new AABB(0.25, 0.0, 0.25, 0.75, 0.251, 0.75);
      case NORTH -> new AABB(0.25, 0.25, 0.749, 0.75, 0.75, 1.0);
      case SOUTH -> new AABB(0.25, 0.25, 0.0, 0.75, 0.75, 0.251);
      case WEST -> new AABB(0.749, 0.25, 0.25, 1.0, 0.75, 0.75);
      case EAST -> new AABB(0.0, 0.25, 0.25, 0.251, 0.75, 0.75);
    };
  }

  @Override
  public void update(IPipeTile pipe, Direction direction) {
    if (pipe.getWorld().isClientSide) return;
    RobotStationState oldState = renderState;
    refreshRenderState();
    if (oldState != renderState) {
      pipe.scheduleRenderUpdate();
    }
  }

  private void refreshRenderState() {
    if (station == null) {
      renderState = RobotStationState.None;
      return;
    }
    this.renderState = station.isTaken()
        ? (station.isMainStation() ? RobotStationState.Linked : RobotStationState.Reserved)
        : RobotStationState.Available;
  }

  public RobotStationState getRenderState() {
    if (renderState == null) {
      renderState = RobotStationState.None;
    }
    return renderState;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return new RobotStationPluggableRenderer();
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    refreshRenderState();
    stream.writeByte(getRenderState().ordinal());
  }

  @Override
  public boolean requiresRenderUpdate(PipePluggable old) {
    return getRenderState() != ((RobotStationPluggable) old).getRenderState();
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    try {
      this.renderState = RobotStationState.values()[stream.readUnsignedByte()];
    } catch (ArrayIndexOutOfBoundsException e) {
      this.renderState = RobotStationState.None;
    }
  }

  @Override
  public RobotStationPluggable createPipePluggable(IPipe pipe, Direction side, ItemStack stack) {
    return new RobotStationPluggable();
  }

  @Override
  public void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player) {
    if (station == null) {
      info.add("RobotStationPluggable: No station found!");
    } else {
      refreshRenderState();
      info.add("Docking Station (side " + side.name() + ", " + getRenderState().name() + ")");
      if (station.robotTaking() != null && station.robotTaking() instanceof IDebuggable debuggable) {
        debuggable.getDebugInfo(info, Direction.UP, debugger, player);
      }
    }
  }
}
