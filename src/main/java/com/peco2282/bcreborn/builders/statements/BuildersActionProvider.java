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
package com.peco2282.bcreborn.builders.statements;

import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IActionProvider;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.builder.patterns.FillerPattern;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class BuildersActionProvider implements IActionProvider {
  private final HashMap<ResourceLocation, ActionFiller> actionMap = new HashMap<>();

  @Override
  public Collection<IActionInternal> getInternalActions(IStatementContainer container) {
    return Collections.emptyList();
  }

  @Override
  public Collection<IActionExternal> getExternalActions(Direction side, BlockEntity tile) {
    LinkedList<IActionExternal> actions = new LinkedList<>();
    if (tile instanceof FillerBlockEntity) {
      for (IFillerPattern p : RegistryUtil.getFillerPatterns().stream().map(Map.Entry::getValue).toList()) {
        if (p instanceof FillerPattern) {
          if (!actionMap.containsKey(p.getUniqueTag())) {
            actionMap.put(p.getUniqueTag(), new ActionFiller((FillerPattern) p));
          }
          actions.add(actionMap.get(p.getUniqueTag()));
        }
      }
    }
    return actions;
  }
}
