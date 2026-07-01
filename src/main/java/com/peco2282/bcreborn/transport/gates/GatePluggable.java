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
import com.peco2282.bcreborn.common.nbt.NbtReader;
import com.peco2282.bcreborn.common.nbt.NbtWriter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Set;

public class GatePluggable extends PipePluggable<GatePluggable> {

  public GateDefinition.GateMaterial material;
  public GateDefinition.GateLogic logic;
  public IGateExpansion[] expansions;
  public boolean isLit, isPulsing;
  public Gate realGate, instantiatedGate;
  private float pulseStage;

  public GatePluggable() {
    super(BCRebornTransport.GATE);
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
    NbtWriter.of(nbt)
      .putEnum(ItemGate.NBT_TAG_MAT, material)
      .putEnum(ItemGate.NBT_TAG_LOGIC, logic)
      .putCollection(ItemGate.NBT_TAG_EX, List.of(expansions), expansion -> StringTag.valueOf(expansion.getUniqueIdentifier().toString()))
      .done();
  }

  @Override
  public void readTag(CompoundTag nbt) {
    NbtReader.of(nbt)
      .applyEnum(ItemGate.NBT_TAG_MAT, GateDefinition.GateMaterial.class, mat -> material = mat)
      .applyEnum(ItemGate.NBT_TAG_LOGIC, GateDefinition.GateLogic.class, logic -> this.logic = logic)
      .applyStrings(ItemGate.NBT_TAG_EX, list -> {
        expansions = list.stream()
          .map(GateExpansions::getExpansion)
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
    // TODO: Implement logic without BuildCraft 1.7.10 classes
  }

  @Override
  public void onDetachedPipe(IPipeBlockEntity pipe, Direction direction) {
    // TODO: Implement logic without BuildCraft 1.7.10 classes
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
      // PipeRendererTESR.renderGate(x, y, z, (GatePluggable) pipePluggable, side);
    }

    @Override
    public void renderPluggable(IPipe pipe, Direction side, PipePluggable<?> pipePluggable, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      // PipeRendererTESR.renderGate(x, y, z, (GatePluggable) pipePluggable, side);
    }
  }
}
