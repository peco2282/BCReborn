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
package com.peco2282.bcreborn.api.tablet;

public abstract class TabletProgramFactory {
  /**
   * Create an instance of the tablet program specified by this Factory.
   * <p>
   * Both parameters are mutable and can be edited freely; however, the
   * NBTTagCompound will only be synchronized after you leave the program.
   * <p>
   * Please note that the program runs client-side SOLELY. For server-side
   * queries, you must implement a custom communications protocol.
   *
   * @param tablet
   * @return An instance of the TabletProgram.
   */
  public abstract TabletProgram create(ITablet tablet);

  public abstract String getName();

  public abstract TabletBitmap getIcon();
}
