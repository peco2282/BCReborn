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

import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Processes mod context for annotation-based registration and packet handling.
 * <p>
 * This class scans mod files for annotations such as {@link InitRegister} and {@link Packet},
 * and processes them to initialize registers and register network packets. It uses a cache
 * to ensure only one instance exists per mod container.
 * </p>
 *
 * @see InitRegister
 * @see Packet
 */
public class ContextProcessor {
  private static final Logger log = LoggerFactory.getLogger("ContextProcessor");
  private static final Map<ModContainer, ContextProcessor> CACHE = new ConcurrentHashMap<>();
  private final String modId;
  private final @NotNull ModContainer container;

  private ContextProcessor(ModContainer container) {
    this.modId = container.getModId();
    this.container = container;
  }

  /**
   * Creates or retrieves a cached ContextProcessor for the given mod container.
   *
   * @param container the mod container to create a processor for
   * @return the ContextProcessor instance for the container
   */
  public static ContextProcessor create(ModContainer container) {
    return CACHE.computeIfAbsent(container, ContextProcessor::new);
  }

  /**
   * Creates or retrieves a cached ContextProcessor for the mod with the given ID.
   *
   * @param modId the mod ID to create a processor for
   * @return the ContextProcessor instance for the mod
   * @throws java.util.NoSuchElementException if no mod with the given ID exists
   */
  public static ContextProcessor create(String modId) {
    return create(ModList.get().getModContainerById(modId).orElseThrow());
  }

  /**
   * Gets the mod ID associated with this processor.
   *
   * @return the mod ID
   */
  public String getModId() {
    return modId;
  }

  /**
   * Retrieves the scan data for the mod container.
   * <p>
   * This data contains information about all classes and annotations found during mod loading.
   * </p>
   *
   * @return the mod file scan data
   */
  private ModFileScanData getScanData() {
    return container.getModInfo().getOwningFile().getFile().getScanResult();
  }

  /**
   * Initializes and registers classes annotated with {@link InitRegister}.
   * <p>
   * Scans the mod's context for classes annotated with {@code @InitRegister} and loads them.
   * Classes are only processed if their modId matches this processor's modId.
   * Successfully found classes are logged, and missing classes generate error logs.
   * </p>
   */
  public void initRegister() {
    ModFileScanData data = getScanData();
    for (ModFileScanData.AnnotationData ad : data.getAnnotations()) {

      if (ad.annotationType().getClassName().equals(InitRegister.class.getName())) {
        log.debug("Processing annotation data for class {}", ad.clazz().getClassName());
        String modId = (String) ad.annotationData().get("modId");
        if (modId == null) {
          continue;
        }
        if (modId.equals(this.modId)) {
          try {
            var cls = Class.forName(ad.clazz().getClassName());
            log.info("Found apply Accessed class {}", cls.getName());
          } catch (ClassNotFoundException e) {
            log.error("{} was not found", ad.clazz().getClassName(), e);
          }
        }
      }
    }
  }


  /**
   * Registers all classes annotated with {@link Packet} to the given network channel.
   * <p>
   * This method scans for {@code @Packet} annotations, sorts them by priority and class name,
   * and registers each packet with the provided SimpleChannel. Each packet is assigned a unique
   * sequential ID starting from 0.
   * </p>
   *
   * @param channel the SimpleChannel to register packets to
   */
  public void packetRegister(SimpleChannel channel) {
    AtomicInteger id = new AtomicInteger(0);
    getScanData().getAnnotations().stream()
      .filter(ad -> ad.annotationType().getClassName().equals(Packet.class.getName()))
      .sorted(Comparator
        .comparingInt(this::getPacketPriority)
        .thenComparing(ad -> ad.clazz().getClassName()))
      .forEach(ad -> {
        try {
          @SuppressWarnings("unchecked")
          Class<? extends CustomPacket> cls = (Class<? extends CustomPacket>) Class.forName(ad.clazz().getClassName());
          var holder = (ModAnnotation.EnumHolder) ad.annotationData().get("direction");
          NetworkDirection direction = NetworkDirection.valueOf(holder.getValue());
          processPacket(cls, direction, channel, id.getAndIncrement());
        } catch (ClassNotFoundException e) {
          log.error("{} was not found", ad.clazz().getClassName(), e);
        }
      });
  }

  /**
   * Extracts the priority value from packet annotation data.
   *
   * @param annotationData the annotation data to extract priority from
   * @return the priority value, or 0 if not specified or not an integer
   */
  private int getPacketPriority(ModFileScanData.AnnotationData annotationData) {
    Object priority = annotationData.annotationData().get("priority");
    return priority instanceof Integer value ? value : 0;
  }

  /**
   * Processes and registers a single packet class with the network channel.
   * <p>
   * This method verifies that the packet class has a static {@code decode(FriendlyByteBuf)} method,
   * creates a decoder function, and registers the packet with the specified ID and direction.
   * </p>
   *
   * @param <P>       the packet type extending CustomPacket
   * @param cls       the packet class to register
   * @param direction the network direction for this packet
   * @param channel   the SimpleChannel to register the packet to
   * @param id        the unique ID for this packet
   * @throws RuntimeException if the decode method is missing or cannot be invoked
   */
  @SuppressWarnings("unchecked")
  private <P extends CustomPacket> void processPacket(Class<P> cls, NetworkDirection direction, SimpleChannel channel, int id) {
    final Method decodeMethod;
    try {
      decodeMethod = cls.getDeclaredMethod("decode", FriendlyByteBuf.class);
    } catch (NoSuchMethodException e) {
      var msg = """
        @Packet annotated class must have a static decode(FriendlyByteBuf) method
        """;
      log.error("Packet class {} must have a static decode(FriendlyByteBuf) method", cls.getName(), e);

      throw new RuntimeException(msg, e);
    }

    Function<FriendlyByteBuf, P> decoder = buf -> {
      try {
        return (P) decodeMethod.invoke(null, buf);
      } catch (Exception e) {
        var msg = """
          @Packet annotated class must have a static decode(FriendlyByteBuf) method
          """;
        log.error("Packet class {} must have a static decode(FriendlyByteBuf) method", cls.getName(), e);

        throw new RuntimeException(msg, e);
      }
    };

    channel.registerMessage(
      id,
      cls,
      CustomPacket::encode,
      decoder,
      CustomPacket::handle,
      Optional.of(direction)
    );
    log.debug("Registered packet: {} with direction {}", cls.getSimpleName(), direction);
  }
}
