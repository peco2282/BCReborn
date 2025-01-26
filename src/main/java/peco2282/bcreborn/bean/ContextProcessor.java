package peco2282.bcreborn.bean;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.InternalLogger;
import peco2282.bcreborn.api.event.BCEventAnnotation;
import peco2282.bcreborn.event.internal.EventBus;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The ContextProcessor is responsible for handling the processing and management
 * of mod-related context within the BCReborn Mod. It initializes and registers
 * annotated classes used by the mod.
 *
 * @author peco2282
 */
public class ContextProcessor {
  private static final Logger log = InternalLogger.create();
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
    ChainPredicate<InitRegister> predicate = new ChainPredicate<>(InitRegister.class);
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
    ChainPredicate<CapabilityAttacher> predicate = new ChainPredicate<>(CapabilityAttacher.class);
    for (ModFileScanData.AnnotationData ad : data.getAnnotations()) {
      if (predicate.test(ad)) {
        throw new NotImplementedException();
      }
    }
  }

  public static void gatherBcEvent() {
    ModFileScanData data = getScanData();
    ChainPredicate<BCEventAnnotation> predicate = new ChainPredicate<>(BCEventAnnotation.class);
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

  @VisibleForTesting
  static class ChainPredicate<A extends Annotation> implements Predicate<ModFileScanData.AnnotationData> {
    private final @NotNull Class<A> annotation;
    private final List<Predicate<A>> predicates = new ArrayList<>();

    ChainPredicate(@NotNull Class<A> anotation) {
      this.annotation = anotation;
    }

    void add(Predicate<@NotNull A> predicate) {
      this.predicates.add(predicate);
    }

    @Override
    public boolean test(ModFileScanData.AnnotationData data) {
      if (annotationFilter(annotation).test(data)) return true;
      MutableBoolean found = new MutableBoolean(false);
      try {
        Class<?> cls = Class.forName(data.clazz().getClassName());
        A a = cls.getAnnotation(annotation);
        if (a != null)
          for (Predicate<A> predicate : predicates) {
            found.setValue(predicate.test(a));
          }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      return found.getValue();
    }
  }
}
