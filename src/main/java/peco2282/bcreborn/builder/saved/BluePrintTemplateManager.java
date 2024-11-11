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
  private static final FileToIdConverter CONVERTER = new FileToIdConverter("blueprint", ".save");
  private static final LevelResource PATH = new LevelResource(BCReborn.MODID);
  @LateinitField
  private static BluePrintTemplateManager INSTANCE;
  private final Map<String, BluePrintTemplate> template = Maps.newConcurrentMap();
  private final ResourceManager manager;
  private final LevelStorageSource.LevelStorageAccess access;
  private final Path dir;
  private final HolderGetter<Block> getter;

  public BluePrintTemplateManager(
      ResourceManager manager, LevelStorageSource.LevelStorageAccess access, HolderGetter<Block> getter
  ) {
    this.manager = manager;
    this.access = access;
    this.dir = access.getLevelPath(PATH).normalize();
    this.getter = getter;

    INSTANCE = this;
  }

  public static BluePrintTemplateManager getInstance() {
    return INSTANCE;
  }

  public void load() {
  }

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

  public BluePrintTemplate get(String name) {
    return template.computeIfAbsent(name, k -> new BluePrintTemplate());
  }
  private Path create(String name) {
    return FileUtil.createPathToResource(dir, name, ".save");
  }
}
