package peco2282.bcreborn.bean;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.event.BCEventAnnotation;
import peco2282.bcreborn.event.internal.EventBus;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * The ContextProcessor is responsible for handling the processing and management
 * of mod-related context within the BCReborn Mod. It initializes and registers
 * annotated classes used by the mod.
 *
 * @author peco2282
 */
public class ContextProcessor {
  private static final Logger log = LoggerFactory.getLogger(ContextProcessor.class);
  private static final ContextProcessor instance = new ContextProcessor(
      ModList.get().getModContainerById(BCReborn.MODID).orElseThrow()
  );
  private final @NotNull ModContainer container;

  private ContextProcessor(@NotNull ModContainer container) {
    this.container = container;
  }

  /**
   * Provides the singleton instance of the ContextProcessor.
   *
   * @return The singleton instance of ContextProcessor.
   */
  public static @NotNull ContextProcessor getInstance() {
    return instance;
  }

  private static ModFileScanData getScanData() {
    return getInstance().container.getModInfo().getOwningFile().getFile().getScanResult();
  }

  private static <T extends Annotation> Predicate<ModFileScanData.AnnotationData> annotationFilter(Class<T> clazz) {
    return (ad) -> {
      try {
        return Class.forName(ad.annotationType().getClassName()).isAssignableFrom(clazz);
      } catch (ClassNotFoundException e) {
        return false;
      }
    };
  }

  /**
   * Initializes and registers classes annotated with {@code InitRegister}.
   * Scans the mod's context for annotated classes and logs their discovery.
   * If a class is missing, logs an error message.
   */
  public static void initRegister() {
    ModFileScanData data = getScanData();
    Predicate<ModFileScanData.AnnotationData> predicate = annotationFilter(InitRegister.class);
    for (ModFileScanData.AnnotationData ad : data.getAnnotations()) {
      if (predicate.test(ad)) {
        try {
          var cls = Class.forName(ad.clazz().getClassName());
          log.info("Found and Accessed class {}", cls.getName());
        } catch (ClassNotFoundException e) {
          log.error("{} was not found", ad.clazz().getClassName(), e);
        }
      }
    }
  }

  public static void capailitySearch() {
    ModFileScanData data = getScanData();
    Predicate<ModFileScanData.AnnotationData> predicate = annotationFilter(CapabilityAttacher.class);
    for (ModFileScanData.AnnotationData ad : data.getAnnotations()) {
      if (predicate.test(ad)) {
        throw new NotImplementedException();
      }
    }
  }

  public static void gatherBcEvent() {
    ModFileScanData data = getScanData();
    Predicate<ModFileScanData.AnnotationData> predicate = annotationFilter(BCEventAnnotation.class);
    EventBus bus = BCReborn.EVENT_BUS;
    for (ModFileScanData.AnnotationData ad : data.getAnnotations()) {
      if (predicate.test(ad)) {
        try {
          Class<?> clazz = Class.forName(ad.clazz().getClassName());
          bus.registerAnnotated(clazz.getConstructor().newInstance());
          log.info("Gathered BCEvent {}", clazz.getName());
        } catch (ReflectiveOperationException e) {
          log.error("At {} class", ad.clazz().getClassName(), e);
        }
      }
    }
  }
}
