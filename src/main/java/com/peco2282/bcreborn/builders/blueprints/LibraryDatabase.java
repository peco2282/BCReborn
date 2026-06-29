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
package com.peco2282.bcreborn.builders.blueprints;


import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.api.library.LibraryAPI;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class LibraryDatabase {
  private static final Logger LOGGER = BCRebornBuilders.LOGGER;
  protected Set<LibraryId> blueprintIds;
  protected LibraryId[] pages = new LibraryId[0];

  private File outputDir;
  private File inputDir;

  public static CompoundTag load(File blueprintFile) {
    if (blueprintFile.exists()) {
      try {
        return NbtIo.readCompressed(blueprintFile);
      } catch (IOException e) {
        LOGGER.error("Failed to load blueprint file: {}", blueprintFile.getName(), e);
      }
    }

    return new CompoundTag();
  }

  /**
   * Initialize the blueprint database.
   *
   * @param inputPath directories to readTag the blueprints from.
   */
  public void init(Path inputPath, Path outputPath) {
    outputDir = outputPath.toFile();
    inputDir = inputPath.toFile();

    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    if (!inputDir.exists()) {
      inputDir.mkdirs();
    }

    refresh();
  }

  public void refresh() {
    blueprintIds = new TreeSet<>();
    for (File f : inputDir.listFiles(File::isFile)) {
      loadIndex(f);
    }
  }

  public void deleteBlueprint(LibraryId id) {
    File blueprintFile = getBlueprintFile(id);

    if (blueprintFile != null) blueprintFile.delete();
    blueprintIds.remove(id);
    pages = new LibraryId[blueprintIds.size()];
    pages = blueprintIds.toArray(pages);
  }

  @Nullable
  protected File getBlueprintFile(LibraryId id) {
    String name = String.format(Locale.ENGLISH, "%s." + id.extension, id);

    for (File dir : inputDir.listFiles(File::isFile)) {
      File f = new File(dir, name);

      if (f.exists()) {
        return f;
      }
    }

    return null;
  }

  protected File getBlueprintOutputFile(LibraryId id) {
    String name = String.format(Locale.ENGLISH, "%s." + id.extension, id);

    return new File(outputDir, name);
  }

  public void add(LibraryId base, CompoundTag compound) {
    save(base, compound);

    if (!blueprintIds.contains(base)) {
      blueprintIds.add(base);
      pages = blueprintIds.toArray(pages);
    }
  }

  private void save(LibraryId base, CompoundTag compound) {
    byte[] data;

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      NbtIo.writeCompressed(compound, baos);
      data = baos.toByteArray();
    } catch (IOException ex) {
      BCLog.logger.error(String.format("Failed to serialize library compound: %s", ex.getMessage()));
      data = new byte[0];
    }

    base.generateUniqueId(data);
  }

  private void loadIndex(File directory) {
    FilenameFilter filter = (dir, name) -> {
      int dotIndex = name.lastIndexOf('.') + 1;
      String extension = name.substring(dotIndex);
      return LibraryAPI.getHandlerFor(extension) != null;
    };

    if (directory.exists()) {
      File[] files = directory.listFiles(filter);
      if (files == null || files.length == 0) {
        return;
      }

      for (File blueprintFile : files) {
        String fileName = blueprintFile.getName();

        LibraryId id = new LibraryId();

        int sepIndex = fileName.lastIndexOf(LibraryId.BPT_SEP_CHARACTER);
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0) {
          String extension = fileName.substring(dotIndex + 1);

          if (sepIndex > 0) {
            String prefix = fileName.substring(0, sepIndex);
            String suffix = fileName.substring(sepIndex + 1);

            id.name = prefix;
            id.uniqueId = LibraryId.toBytes(suffix.substring(0, suffix.length() - (extension.length() + 1)));
          } else {
            id.name = fileName.substring(0, dotIndex);
            id.uniqueId = new byte[0];
          }
          id.extension = extension;

          blueprintIds.add(id);
        } else {
          BCLog.logger.warn("Found incorrectly named (no extension) blueprint file: %s'!", fileName);
        }
      }

      pages = blueprintIds.toArray(LibraryId[]::new);
    }
  }

  public boolean exists(LibraryId id) {
    return blueprintIds.contains(id);
  }

  public CompoundTag load(final LibraryId id) {
    return load(getBlueprintFile(id));
  }

  public List<LibraryId> getBlueprintIds() {
    return List.copyOf(blueprintIds);
  }
}
