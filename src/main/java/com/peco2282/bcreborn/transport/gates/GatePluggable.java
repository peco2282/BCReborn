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
package com.peco2282.bcreborn.transport.gates;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.gates.GateExpansions;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.IPipeBlockEntity;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableDynamicRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.api.serialization.NbtReader;
import com.peco2282.bcreborn.api.serialization.NbtWriter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Set;
import com.mojang.math.Axis;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.item.ItemDisplayContext;

public class GatePluggable extends PipePluggable<GatePluggable> {

  public GateDefinition.GateMaterial material;
  public GateDefinition.GateLogic logic;
  public IGateExpansion[] expansions;
  public boolean isLit, isPulsing;
  public Gate realGate, instantiatedGate;
  private float pulseStage;
  private CompoundTag savedGate;

  public GatePluggable() {
    super(BCRebornTransport.GATE);
    material = GateDefinition.GateMaterial.REDSTONE;
    logic = GateDefinition.GateLogic.AND;
    expansions = new IGateExpansion[0];
  }

  public GatePluggable(Gate gate) {
    super(BCRebornTransport.GATE);
    instantiatedGate = gate;
    initFromGate(gate);
  }

  private void initFromGate(Gate gate) {
    this.material = gate.material;
    this.logic = gate.logic;

    Set<IGateExpansion> gateExpansions = gate.expansions.keySet();
    this.expansions = gateExpansions.toArray(IGateExpansion[]::new);
  }

  @Override
  public void writeTag(CompoundTag nbt) {
    if (realGate != null) {
      CompoundTag gateTag = new CompoundTag();
      realGate.writeToNBT(gateTag);
      nbt.put("gate", gateTag);
      nbt.putBoolean("lit", realGate.isGateActive());
      nbt.putBoolean("pulsing", realGate.isGatePulsing());
    } else if (savedGate != null) {
      nbt.put("gate", savedGate.copy());
    }
    NbtWriter.of(nbt)
      .putEnum(ItemGate.NBT_TAG_MAT, material)
      .putEnum(ItemGate.NBT_TAG_LOGIC, logic)
      .putCollection(ItemGate.NBT_TAG_EX, List.of(expansions), expansion -> StringTag.valueOf(expansion.getUniqueIdentifier().toString()))
      .done();
  }

  @Override
  public void readTag(CompoundTag nbt) {
    savedGate = nbt.contains("gate") ? nbt.getCompound("gate").copy() : null;
    isLit = nbt.getBoolean("lit");
    isPulsing = nbt.getBoolean("pulsing");
    NbtReader.of(nbt)
      .applyEnum(ItemGate.NBT_TAG_MAT, GateDefinition.GateMaterial.class, mat -> material = mat)
      .applyEnum(ItemGate.NBT_TAG_LOGIC, GateDefinition.GateLogic.class, logic -> this.logic = logic)
      .applyStrings(ItemGate.NBT_TAG_EX, list -> {
        expansions = list.stream()
          .map(GateExpansions::getExpansion)
          .filter(java.util.Objects::nonNull)
          .toArray(IGateExpansion[]::new);
      })
      .done();
  }

  @Override
  public void writeData(FriendlyByteBuf buf) {
    buf.writeByte(material.ordinal());
    buf.writeByte(logic.ordinal());
    buf.writeBoolean(realGate != null && realGate.isGateActive());
    buf.writeBoolean(realGate != null && realGate.isGatePulsing());

    final int expansionsSize = expansions.length;
    buf.writeShort(expansionsSize);

    for (IGateExpansion expansion : expansions) {
      buf.writeResourceLocation(expansion.getUniqueIdentifier());
    }
  }

  @Override
  public void readData(FriendlyByteBuf buf) {
    material = GateDefinition.GateMaterial.fromOrdinal(buf.readByte());
    logic = GateDefinition.GateLogic.fromOrdinal(buf.readByte());
    isLit = buf.readBoolean();
    isPulsing = buf.readBoolean();

    final int expansionsSize = buf.readUnsignedShort();
    expansions = new IGateExpansion[expansionsSize];

    for (int i = 0; i < expansionsSize; i++) {
      expansions[i] = GateExpansions.getExpansion(buf.readResourceLocation());
    }
  }

  @Override
  public boolean requiresRenderUpdate(PipePluggable<?> o) {
    // rendered by TESR
    return false;
  }

  @Override
  public ItemStack[] getDropItems(IPipeBlockEntity pipe) {
    ItemStack gate = ItemGate.makeGateItem(material, logic);
    if (gate.isEmpty()) return new ItemStack[0];
    for (IGateExpansion expansion : expansions) {
      ItemGate.addGateExpansion(gate, expansion);
    }
    return new ItemStack[]{gate};
  }

  @Override
  public void update(IPipeBlockEntity pipe, Direction direction) {
    if (realGate == null) onAttachedPipe(pipe, direction);
    if (!pipe.getWorld().isClientSide && realGate != null) {
      realGate.resolveActions();
      realGate.tick();
      isLit = realGate.isGateActive();
      isPulsing = realGate.isGatePulsing();
    }
    if (isPulsing || pulseStage > 0.11F) {
      // if it is moving, or is still in a moved state, then complete
      // the current movement
      pulseStage = (pulseStage + 0.01F) % 1F;
    } else {
      pulseStage = 0;
    }
  }

  @Override
  public void onAttachedPipe(IPipeBlockEntity pipe, Direction direction) {
    if (realGate != null) return;
    if (!(pipe instanceof PipeBlockEntity entity)) return;
    if (savedGate != null) {
      realGate = GateFactory.makeGate(entity.getPipe(), savedGate).orElse(null);
      savedGate = null;
    } else if (instantiatedGate != null) {
      realGate = instantiatedGate;
    } else {
      realGate = GateFactory.makeGate(entity.getPipe(), material, logic, direction);
      for (IGateExpansion expansion : expansions) if (expansion != null) realGate.addGateExpansion(expansion);
    }
    if (realGate != null) realGate.setDirection(direction);
  }

  @Override
  public void onDetachedPipe(IPipeBlockEntity pipe, Direction direction) {
    realGate = null;
    instantiatedGate = null;
  }

  @Override
  public boolean isBlocking(IPipeBlockEntity pipe, Direction direction) {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof GatePluggable o)) {
      return false;
    }
    if (o.material == null || material == null || o.material.ordinal() != material.ordinal()) {
      return false;
    }
    if (o.logic == null || logic == null || o.logic.ordinal() != logic.ordinal()) {
      return false;
    }
    if (o.expansions.length != expansions.length) {
      return false;
    }
    for (int i = 0; i < expansions.length; i++) {
      if (o.expansions[i] != expansions[i]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    float min = 0.25F + 0.05F;
    float max = 0.75F - 0.05F;
    return new AABB(min, 0, min, max, 0.1, max); // Placeholder
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return GatePluggableRenderer.INSTANCE;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableDynamicRenderer getDynamicRenderer() {
    return GatePluggableRenderer.INSTANCE;
  }

  public float getPulseStage() {
    return pulseStage;
  }

  public GateDefinition.GateMaterial getMaterial() {
    return material;
  }

  public GateDefinition.GateLogic getLogic() {
    return logic;
  }

  public IGateExpansion[] getExpansions() {
    return expansions;
  }

  @OnlyIn(Dist.CLIENT)
  private static final class GatePluggableRenderer implements IPipePluggableRenderer, IPipePluggableDynamicRenderer {
    public static final GatePluggableRenderer INSTANCE = new GatePluggableRenderer();

    private GatePluggableRenderer() {

    }

    @Override
    public void renderPluggable(IPipe pipe, Direction side, PipePluggable<?> pipePluggable, int renderPass, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      renderPluggable(pipe, side, pipePluggable, poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderPluggable(IPipe pipe, Direction side, PipePluggable<?> pipePluggable, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      GatePluggable gate = (GatePluggable) pipePluggable;
      poseStack.pushPose();
      double offset = 0.27 + (gate.isPulsing ? Math.sin(gate.pulseStage * Math.PI * 2) * 0.02 : 0);
      poseStack.translate(0.5 + side.getStepX() * offset, 0.5 + side.getStepY() * offset, 0.5 + side.getStepZ() * offset);
      switch (side) {
        case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
        case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
        case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
        default -> { }
      }
      poseStack.scale(0.4f, 0.4f, 0.4f);
      Minecraft.getInstance().getItemRenderer().renderStatic(
        ItemGate.makeGateItem(gate.material, gate.logic), ItemDisplayContext.FIXED,
        gate.isLit ? LightTexture.FULL_BRIGHT : packedLight,
        packedOverlay, poseStack, buffer, pipe.getBlockEntity().getWorld(), 0);
      poseStack.popPose();
    }
  }
}
