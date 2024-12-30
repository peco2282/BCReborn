package peco2282.bcreborn.builder.saved;

import com.google.common.collect.Maps;
import net.minecraft.FileUtil;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.annotation.LateinitField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class BluePrintTemplateManager {
  /**
   * Converter for mapping blueprint save files to their respective IDs.
   */
  private static final FileToIdConverter CONVERTER = new FileToIdConverter("blueprint", ".save");
  /**
   * Path resource pointing to the mod-specific save data location.
   */
  private static final LevelResource PATH = new LevelResource(BCReborn.MODID);
  /**
   * Singleton instance of the BluePrintTemplateManager.
   */
  @LateinitField
  private static BluePrintTemplateManager INSTANCE;
  /**
   * Thread-safe map storing blueprint templates by name.
   */
  private final Map<String, BluePrintTemplate> template = Maps.newConcurrentMap();
  /**
   * Resource manager for accessing game resources.
   */
  private final ResourceManager manager;
  /**
   * Provides access to the level's storage directory.
   */
  private final LevelStorageSource.LevelStorageAccess access;
  /**
   * Normalized path to the blueprint save files directory.
   */
  private final Path dir;
  /**
   * Accessor for retrieving registered blocks.
   */
  private final HolderGetter<Block> getter;

  /**
   * Constructs a new BluePrintTemplateManager.
   *
   * @param manager The resource manager for accessing resources.
   * @param access  Provides access to the level's storage directory.
   * @param getter  Accessor for registered blocks.
   */
  public BluePrintTemplateManager(
      ResourceManager manager, LevelStorageSource.LevelStorageAccess access, HolderGetter<Block> getter
  ) {
    this.manager = manager;
    this.access = access;
    this.dir = access.getLevelPath(PATH).normalize();
    this.getter = getter;

    INSTANCE = this;
  }

  /**
   * Retrieves the singleton instance of BluePrintTemplateManager.
   *
   * @return The singleton instance of this manager.
   */
  public static BluePrintTemplateManager getInstance() {
    return INSTANCE;
  }

  /**
   * Loads blueprint templates from the storage directory.
   * Currently, this method is a placeholder and doesn't perform any operation.
   */
  public void load() {
  }

  /**
   * Saves a blueprint template with the given name to the storage directory.
   *
   * @param name The name of the blueprint template to save.
   */
  public void save(String name) {
    Path path = create(name);
    Path parent = path.getParent();
    if (parent == null) return;
    try {
      Files.createDirectories(Files.exists(parent) ? parent.toRealPath() : parent);
    } catch (IOException ignore) {
    }

    CompoundTag compoundtag = get(name).save(null);

    try (OutputStream out = new FileOutputStream(path.toFile())) {
      NbtIo.writeCompressed(compoundtag, out);
    } catch (Throwable ignore) {
    }

  }

  /**
   * Retrieves or creates a blueprint template by its name.
   *
   * @param name The name of the blueprint template to retrieve.
   * @return The blueprint template associated with the name.
   */
  public BluePrintTemplate get(String name) {
    return template.computeIfAbsent(name, k -> new BluePrintTemplate());
  }
  /**
   * Creates the file path for a blueprint template with the given name.
   * This method ensures the file path is structured properly within the blueprint's directory.
   *
   * @param name The name of the blueprint template.
   * @return The path of the blueprint template save file.
   */
  private Path create(String name) {
    return FileUtil.createPathToResource(dir, name, ".save");
  }
}
