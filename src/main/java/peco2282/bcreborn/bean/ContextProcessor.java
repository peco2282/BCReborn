package peco2282.bcreborn.bean;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.BCReborn;


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

  /**
   * Initializes and registers classes annotated with {@code InitRegister}.
   * Scans the mod's context for annotated classes and logs their discovery.
   * If a class is missing, logs an error message.
   */
  public static void initRegister() {
    ModFileScanData data = getInstance().container.getModInfo().getOwningFile().getFile().getScanResult();
    data.getAnnotations().stream().filter(a -> {
          try {
            Class<?> cls = Class.forName(a.annotationType().getClassName());
            return cls.isAssignableFrom(InitRegister.class);
          } catch (ClassNotFoundException e) {
            return false;
          }
        })
        .forEach(ad -> {
          try {
            var cls = Class.forName(ad.clazz().getClassName());
            log.info("Found and Accessed class {}", cls.getName());
          } catch (ClassNotFoundException e) {
            log.error("{} was not found", ad.clazz().getClassName(), e);
          }
        });
  }
}
