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
package com.peco2282.bcreborn.api.transport.pluggable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.api.transport.IPipe;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

public interface IPipePluggableDynamicRenderer {
  void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay);
}
