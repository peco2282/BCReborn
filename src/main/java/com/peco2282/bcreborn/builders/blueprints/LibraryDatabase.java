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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class LibraryDatabase {
  private static final Logger LOGGER = BCRebornBuilders.LOGGER;
  protected Set<LibraryId> blueprintIds = new TreeSet<>();
  protected LibraryId[] pages = new LibraryId[0];

  private File outputDir;
  /** Original {@code inputDirs}: every directory searched for blueprints (the output directory included). */
  private final List<File> inputDirs = new ArrayList<>();

  public static CompoundTag load(@Nullable File blueprintFile) {
    if (blueprintFile != null && blueprintFile.exists()) {
      try {
        return NbtIo.readCompressed(blueprintFile);
      } catch (IOException e) {
        LOGGER.error("Failed to load blueprint file: {}", blueprintFile.getName(), e);
      }
    }

    return new CompoundTag();
  }

  /**
   * Original {@code init(inputPaths, outputPath)}: blueprints are read from {@code inputPath} and
   * {@code outputPath}, and new ones are written to {@code outputPath}.
   */
  public void init(Path inputPath, Path outputPath) {
    outputDir = outputPath.toFile();
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    inputDirs.clear();
    for (File dir : new File[]{inputPath.toFile(), outputDir}) {
      if (dir.exists() && !inputDirs.contains(dir)) {
        inputDirs.add(dir);
      }
    }

    refresh();
  }

  public void refresh() {
    blueprintIds = new TreeSet<>();
    for (File dir : inputDirs) {
      loadIndex(dir);
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

    for (File dir : inputDirs) {
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

  /** Original {@code save()}: the id is derived from the data, and the file is written once. */
  private void save(LibraryId base, CompoundTag compound) {
    byte[] data;

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      NbtIo.writeCompressed(compound, baos);
      data = baos.toByteArray();
    } catch (IOException ex) {
      BCLog.logger.error(String.format("Failed to serialize library compound: %s", ex.getMessage()));
      return;
    }

    base.generateUniqueId(data);
    if (outputDir == null) {
      return;
    }
    File blueprintFile = getBlueprintOutputFile(base);
    if (!blueprintFile.exists()) {
      try {
        Files.write(blueprintFile.toPath(), data);
      } catch (IOException ex) {
        LOGGER.error("Failed to save library file: {}", blueprintFile.getName(), ex);
      }
    }
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

  public CompoundTag load(@Nullable final LibraryId id) {
    return id == null ? new CompoundTag() : load(getBlueprintFile(id));
  }

  public List<LibraryId> getBlueprintIds() {
    return List.copyOf(blueprintIds);
  }
}
