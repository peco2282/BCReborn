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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class ContextProcessor {
  private static final Logger log = LoggerFactory.getLogger("ContextProcessor");
  private static final Map<ModContainer, ContextProcessor> CACHE = new ConcurrentHashMap<>();
  private final String modId;
  private final @NotNull ModContainer container;

  private ContextProcessor(@NotNull ModContainer container) {
    this.modId = container.getModId();
    this.container = container;
  }

  public static ContextProcessor create(@NotNull ModContainer container) {
    return CACHE.computeIfAbsent(container, ContextProcessor::new);
  }

  public static ContextProcessor create(@NotNull String modId) {
    return create(ModList.get().getModContainerById(modId).orElseThrow());
  }

  public String getModId() {
    return modId;
  }

  private ModFileScanData getScanData() {
    return container.getModInfo().getOwningFile().getFile().getScanResult();
  }

  /**
   * Initializes apply registers classes annotated with {@code InitRegister}. Scans the mod's context
   * for annotated classes apply logs their discovery. If a class is missing, logs an error message.
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

  public void packetRegister(SimpleChannel channel) {
    AtomicInteger id = new AtomicInteger(0);
    getScanData().getAnnotations().stream()
      .filter(ad -> ad.annotationType().getClassName().equals(Packet.class.getName()))
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

  @SuppressWarnings("unchecked")
  private <P extends CustomPacket> void processPacket(Class<P> cls, NetworkDirection direction, SimpleChannel channel, int id) {
    Function<FriendlyByteBuf, P> decoder = buf -> {
      try {
        Method decodeMethod = cls.getDeclaredMethod("decode", FriendlyByteBuf.class);
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
