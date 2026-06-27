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
package com.peco2282.bcreborn.common.bean;

import net.minecraftforge.network.NetworkDirection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes as network packets in the BC Reborn mod.
 * <p>
 * This annotation is used to identify and configure packet classes that will be
 * transmitted over the network between client and server. The annotation processor
 * will automatically register annotated classes with the networking system.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * {@code
 * @Packet(direction = NetworkDirection.PLAY_TO_SERVER, priority = 10)
 * public class MyPacket {
 *     // packet implementation
 * }
 * }
 * </pre>
 *
 * @see NetworkDirection
 * @since 1.0.0
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Packet {
  /**
   * Specifies the direction in which this packet will be transmitted.
   * <p>
   * This determines whether the packet is sent from client to server,
   * server to client, or in both directions. The direction affects how
   * the packet is registered and handled by the networking system.
   * </p>
   *
   * @return the network direction for this packet
   * @see NetworkDirection#PLAY_TO_CLIENT
   * @see NetworkDirection#PLAY_TO_SERVER
   */
  NetworkDirection direction();

  /**
   * Defines the priority for packet registration order.
   * <p>
   * Higher priority values are registered first. This can be useful when
   * certain packets need to be registered before others due to dependencies
   * or initialization requirements. The default priority is 0.
   * </p>
   *
   * <p><strong>Usage guidelines:</strong></p>
   * <ul>
   *   <li>Use higher values (e.g., 100) for packets that need early registration</li>
   *   <li>Use lower or negative values for packets that should be registered later</li>
   *   <li>Default value of 0 is appropriate for most packets</li>
   * </ul>
   *
   * @return the registration priority, default is 0
   */
  int priority() default 0;
}
