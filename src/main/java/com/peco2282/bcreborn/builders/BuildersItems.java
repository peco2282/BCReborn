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
package com.peco2282.bcreborn.builders;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.item.BlueprintStandardItem;
import com.peco2282.bcreborn.builders.item.BlueprintTemplateItem;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornBuilders.MODID)
public class BuildersItems {
  private static final BCRegistry REGISTRY = BCRebornBuilders.getRegistry();

  public static final RegistryObject<BlueprintStandardItem> BLUEPRINT_STANDARD = REGISTRY.registerItem("blueprint_standard", BlueprintStandardItem::new);
  public static final RegistryObject<BlueprintTemplateItem> BLUEPRINT_TEMPLATE = REGISTRY.registerItem("blueprint_template", BlueprintTemplateItem::new);
}
