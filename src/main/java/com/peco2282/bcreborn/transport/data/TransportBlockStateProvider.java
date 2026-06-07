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
package com.peco2282.bcreborn.transport.data;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class TransportBlockStateProvider extends BlockStateProvider {
  private static final Set<PipeMaterial> EXTRACTION_OVERLAY_MATERIALS = Set.of(
    PipeMaterial.WOOD, PipeMaterial.DAIZULI, PipeMaterial.EMERALD, PipeMaterial.EMZULI, PipeMaterial.IRON
  );

  public TransportBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornTransport.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    TransportBlocks.pipesForEach((type, material, block) -> generatePipeBlockState(material, type, block));
  }

  private void generatePipeBlockState(PipeMaterial material, PipeType type, RegistryObject<PipeBlock> block) {
    String baseName = material.getSerializedName();
    String typeName = type.getSerializedName();

    ResourceLocation centerTex = getPipeTexture(material, type, "center");

    // センターモデルの作成
    BlockModelBuilder centerModel = models().withExistingParent("block/pipe/" + baseName + "_" + typeName + "_center", "block/block")
      .texture("particle", centerTex)
      .element()
      .from(4, 4, 4)
      .to(12, 12, 12)
      .face(Direction.NORTH).texture("#texture").uvs(4, 4, 12, 12).end()
      .face(Direction.SOUTH).texture("#texture").uvs(4, 4, 12, 12).end()
      .face(Direction.EAST).texture("#texture").uvs(4, 4, 12, 12).end()
      .face(Direction.WEST).texture("#texture").uvs(4, 4, 12, 12).end()
      .face(Direction.UP).texture("#texture").uvs(4, 4, 12, 12).end()
      .face(Direction.DOWN).texture("#texture").uvs(4, 4, 12, 12).end()
      .end()
      .renderType("cutout")
      .texture("texture", centerTex);

    MultiPartBlockStateBuilder builder = getMultipartBuilder(block.get());

    // waterloggedプロパティの全バリエーションに対応するための空のパーツ
    // 色付きパイプのオーバーレイ（必要に応じて実装）
    // 現時点では基本色のみ対応
    builder.part().modelFile(centerModel).addModel();

    // 各方向の接続パーツ
    for (Direction side : Direction.values()) {
      float[] from = getSideFrom(side);
      float[] to = getSideTo(side);

      ResourceLocation sideTex = getPipeTexture(material, type, side.getName());
      BlockModelBuilder sideModel = models().withExistingParent("block/pipe/" + baseName + "_" + typeName + "_" + side.getName(), "block/block")
        .texture("particle", sideTex)
        .texture("texture", sideTex)
        .renderType("cutout");

      var element = sideModel.element()
        .from(from[0], from[1], from[2])
        .to(to[0], to[1], to[2]);

      for (Direction dir : Direction.values()) {
        float[] uv = getSideUV(side, dir);
        element.face(dir).texture("#texture").uvs(uv[0], uv[1], uv[2], uv[3]).end();
      }

      element.end();

      builder.part()
        .modelFile(sideModel)
        .addModel()
        .condition(PipeBlock.PROPERTY_MAP.get(side), true);
    }

    // 搬入口オーバーレイモデルの追加（対応マテリアルのみ）
    ResourceLocation overlayTex = getExtractionOverlayTexture(material, type);
    if (overlayTex != null) {
      for (Direction side : Direction.values()) {
        int sideValue = side.get3DDataValue();
        BlockModelBuilder overlayModel = models().withExistingParent(
            "block/pipe/" + baseName + "_" + typeName + "_extract_" + side.getName(), "block/block")
          .texture("particle", overlayTex)
          .texture("overlay", overlayTex)
          .renderType("cutout");

        // センター部分のオーバーレイ面
        overlayModel.element()
          .from(4, 4, 4).to(12, 12, 12)
          .face(side).texture("#overlay").uvs(4, 4, 12, 12).end()
          .end();

        // サイド部分のオーバーレイ面（先端面のみ）
        float[] from = getSideFrom(side);
        float[] to = getSideTo(side);
        overlayModel.element()
          .from(from[0], from[1], from[2]).to(to[0], to[1], to[2])
          .face(side).texture("#overlay").uvs(4, 4, 12, 12).end()
          .end();

        builder.part()
          .modelFile(overlayModel)
          .addModel()
          .condition(PipeBlock.EXTRACTION_SIDE, sideValue);
      }
    }

    // アイテムモデルの作成
    itemModels()
      .withExistingParent(block.getId().getPath(), centerModel.getLocation());
  }

  private ResourceLocation getExtractionOverlayTexture(PipeMaterial material, PipeType type) {
    if (!EXTRACTION_OVERLAY_MATERIALS.contains(material)) return null;
    return switch (material) {
      case WOOD -> modLoc("block/pipes/pipe_all_wood_solid");
      case DAIZULI -> modLoc("block/pipes/pipe_all_daizuli_solid");
      case EMERALD -> modLoc("block/pipes/pipe_all_emerald_solid");
      case EMZULI -> modLoc("block/pipes/pipe_all_emzuli_solid");
      case IRON -> modLoc("block/pipes/pipe_all_iron_solid");
      default -> null;
    };
  }

  private ResourceLocation getPipeTexture(PipeMaterial material, PipeType type, String side) {
    String materialName = material.getSerializedName();
    String resPath = resolveTexturePath(material, type, side, materialName);
    return modLoc(resPath);
  }

  private String resolveTexturePath(PipeMaterial material, PipeType type, String side, String materialName) {
    // ENERGYパイプのテクスチャ解決
    if (type == PipeType.ENERGY) {
      return resolveEnergyTexture(material, materialName);
    }

    // FLUIDパイプのテクスチャ解決
    if (type == PipeType.FLUID) {
      return resolveFluidTexture(material, materialName);
    }

    // ITEMパイプのテクスチャ解決
    return resolveItemTexture(material, side, materialName);
  }

  private String resolveEnergyTexture(PipeMaterial material, String materialName) {
    return switch (material) {
      case WOOD -> "block/pipes/pipe_power_wood_standard";
      case IRON -> "block/pipes/pipe_power_iron_m2";
      case EMERALD -> "block/pipes/pipe_power_emerald_standard";
      case QUARTZ -> "block/pipes/pipe_power_quartz";
      case SANDSTONE -> "block/pipes/pipe_power_sandstone";
      case GOLD -> "block/pipes/pipe_power_gold";
      case DIAMOND -> "block/pipes/pipe_power_diamond";
      case COBBLESTONE -> "block/pipes/pipe_power_cobblestone";
      default -> "block/pipes/pipe_power_stone";
    };
  }

  private String resolveFluidTexture(PipeMaterial material, String materialName) {
    return switch (material) {
      case WOOD -> "block/pipes/pipe_fluids_wood_standard";
      case IRON -> "block/pipes/pipe_fluids_iron_standard";
      case EMERALD -> "block/pipes/pipe_fluids_emerald_standard";
      case COBBLESTONE -> "block/pipes/pipe_fluids_cobblestone";
      case STONE -> "block/pipes/pipe_fluids_stone";
      case GOLD -> "block/pipes/pipe_fluids_gold";
      case CLAY -> "block/pipes/pipe_fluids_clay";
      case QUARTZ -> "block/pipes/pipe_fluids_quartz";
      case SANDSTONE -> "block/pipes/pipe_fluids_sandstone";
      case OBSIDIAN -> "block/pipes/pipe_fluids_void";
      case DIAMOND -> "block/pipes/pipe_fluids_diamond_center";
      // VOID, LAPIS, DAIZULI, STRIPES, EMZULI はfluidテクスチャなし → voidにフォールバック
      default -> "block/pipes/pipe_fluids_void";
    };
  }

  private String resolveItemTexture(PipeMaterial material, String side, String materialName) {
    return switch (material) {
      case WOOD -> "block/pipes/pipe_items_wood_standard";
      case IRON -> "block/pipes/pipe_items_iron_standard";
      case EMERALD -> "block/pipes/pipe_items_emerald_standard";
      case EMZULI -> "block/pipes/pipe_items_emzuli_standard";
      case COBBLESTONE -> "block/pipes/pipe_items_cobblestone";
      case STONE -> "block/pipes/pipe_items_stone";
      case GOLD -> "block/pipes/pipe_items_gold";
      case CLAY -> "block/pipes/pipe_items_clay";
      case QUARTZ -> "block/pipes/pipe_items_quartz";
      case SANDSTONE -> "block/pipes/pipe_items_sandstone";
      case OBSIDIAN -> "block/pipes/pipe_items_obsidian";
      case VOID -> "block/pipes/pipe_items_void";
      case STRIPES -> "block/pipes/pipe_stripes";
      // DIAMONDはサイド別テクスチャあり
      case DIAMOND -> "block/pipes/pipe_items_diamond_" + side;
      // LAPISはカラーバリアントのみ → blueをデフォルトとして使用
      case LAPIS -> "block/pipes/pipe_items_lapis_blue";
      // DAIZULIはカラーバリアントのみ → blueをデフォルトとして使用
      case DAIZULI -> "block/pipes/pipe_items_daizuli_blue";
    };
  }

  private float[] getSideFrom(Direction side) {
    return switch (side) {
      case NORTH -> new float[]{4, 4, 0};
      case SOUTH -> new float[]{4, 4, 12};
      case WEST -> new float[]{0, 4, 4};
      case EAST -> new float[]{12, 4, 4};
      case DOWN -> new float[]{4, 0, 4};
      case UP -> new float[]{4, 12, 4};
    };
  }

  private float[] getSideTo(Direction side) {
    return switch (side) {
      case NORTH -> new float[]{12, 12, 4};
      case SOUTH -> new float[]{12, 12, 16};
      case WEST -> new float[]{4, 12, 12};
      case EAST -> new float[]{16, 12, 12};
      case DOWN -> new float[]{12, 4, 12};
      case UP -> new float[]{12, 16, 12};
    };
  }

  /**
   * サイドモデルの各面のUV座標を返す。
   * パイプの断面は常に4〜12px、長さ方向は0〜4px（near側）または12〜16px（far側）。
   */
  private float[] getSideUV(Direction pipeDir, Direction face) {
    // 断面の範囲（常に4〜12）
    // 長さ方向の範囲（near=0〜4, far=12〜16）
    return switch (pipeDir) {
      case NORTH -> switch (face) {
        case NORTH -> new float[]{4, 4, 12, 12}; // 先端面
        case SOUTH -> new float[]{4, 4, 12, 12}; // 根元面（センターと接する）
        case WEST, EAST -> new float[]{0, 4, 4, 12};
        case UP, DOWN -> new float[]{4, 0, 12, 4};
      };
      case SOUTH -> switch (face) {
        case SOUTH, NORTH -> new float[]{4, 4, 12, 12};
        case WEST, EAST -> new float[]{12, 4, 16, 12};
        case UP, DOWN -> new float[]{4, 12, 12, 16};
      };
      case WEST -> switch (face) {
        case WEST, EAST -> new float[]{4, 4, 12, 12};
        case NORTH, DOWN, UP, SOUTH -> new float[]{0, 4, 4, 12};
      };
      case EAST -> switch (face) {
        case EAST, WEST -> new float[]{4, 4, 12, 12};
        case NORTH, DOWN, UP, SOUTH -> new float[]{12, 4, 16, 12};
      };
      case DOWN -> switch (face) {
        case DOWN, UP -> new float[]{4, 4, 12, 12};
        case NORTH, EAST, WEST, SOUTH -> new float[]{4, 0, 12, 4};
      };
      case UP -> switch (face) {
        case UP, DOWN -> new float[]{4, 4, 12, 12};
        case NORTH, EAST, WEST, SOUTH -> new float[]{4, 12, 12, 16};
      };
    };
  }

  private Direction getUp(Direction side) {
    return side.getAxis().isHorizontal() ? Direction.UP : Direction.NORTH;
  }

  private Direction getDown(Direction side) {
    return side.getAxis().isHorizontal() ? Direction.DOWN : Direction.SOUTH;
  }
}
