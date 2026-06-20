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

/**
 * Factory for creating tablet programs.
 * <p>
 * This abstract class serves as a factory for instantiating tablet programs that run
 * on tablets in BuildCraft Reborn. Each factory provides metadata about the program
 * (name and icon) and creates instances of the program on demand.
 * <p>
 * Implementations must provide a way to create program instances, retrieve the program's
 * display name, and provide an icon for visual representation.
 */
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
   * @param tablet The tablet on which the program will run.
   * @return An instance of the TabletProgram.
   */
  public abstract TabletProgram create(ITablet tablet);

  /**
   * Gets the display name of this tablet program.
   * <p>
   * This name is used for identifying and displaying the program in the tablet's UI.
   *
   * @return The name of the tablet program.
   */
  public abstract String getName();

  /**
   * Gets the icon representing this tablet program.
   * <p>
   * The icon is displayed in the tablet's UI to visually represent the program.
   *
   * @return A TabletBitmap containing the program's icon.
   */
  public abstract TabletBitmap getIcon();
}
