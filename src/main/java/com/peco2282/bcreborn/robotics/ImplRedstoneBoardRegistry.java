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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRegistry;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.robotics.boards.RedstoneBoardRobotEmptyNBT;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

@Deprecated(forRemoval = true)
public class ImplRedstoneBoardRegistry extends RedstoneBoardRegistry {
  private final HashMap<ResourceLocation, BoardFactory> boards = new HashMap<>();
  private RedstoneBoardRobotNBT emptyRobotBoardNBT = RedstoneBoardRobotEmptyNBT.instance;

  @Override
  public void registerBoardType(RedstoneBoardNBT<?> redstoneBoardNBT, int energyCost) {
    if (ConfigRobotics.getBoardsBlacklist().contains(redstoneBoardNBT.getID().toString())) {
      return;
    }
    BoardFactory factory = new BoardFactory();
    factory.boardNBT = redstoneBoardNBT;
    factory.energyCost = energyCost;

    boards.put(redstoneBoardNBT.getID(), factory);
  }

  @Override
  public void registerBoardClass(RedstoneBoardNBT<?> redstoneBoardNBT, float probability) {
    this.registerBoardType(redstoneBoardNBT, Math.round(160000 / probability));
  }

  @Override
  public RedstoneBoardRobotNBT getEmptyRobotBoard() {
    return emptyRobotBoardNBT;
  }

  @Override
  public void setEmptyRobotBoard(RedstoneBoardRobotNBT redstoneBoardNBT) {
    emptyRobotBoardNBT = redstoneBoardNBT;
  }

  @Override
  public RedstoneBoardNBT<?> getRedstoneBoard(CompoundTag nbt) {
    return getRedstoneBoard(nbt.getString("id"));
  }

  @Override
  public RedstoneBoardNBT<?> getRedstoneBoard(String id) {
    BoardFactory factory = boards.get(id);

    if (factory != null) {
      return factory.boardNBT;
    } else {
      return emptyRobotBoardNBT;
    }
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    // NO-OP or implement if needed for 1.20.1
  }

  @Override
  public Collection<RedstoneBoardNBT<?>> getAllBoardNBTs() {
    ArrayList<RedstoneBoardNBT<?>> result = new ArrayList<>();

    for (BoardFactory f : boards.values()) {
      result.add(f.boardNBT);
    }

    return result;
  }

  @Override
  public int getEnergyCost(RedstoneBoardNBT<?> board) {
    BoardFactory factory = boards.get(board.getID());
    return factory != null ? factory.energyCost : 0;
  }

  private static class BoardFactory {
    public RedstoneBoardNBT<?> boardNBT;
    public int energyCost;
  }

}
