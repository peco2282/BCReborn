package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Obsidian Pipe: 周囲に落ちているアイテムエンティティを吸い込む。
 * <p>
 * originalの PipeItemsObsidian と同様に、エンジンからエネルギーを消費しながら
 * 距離1〜4の範囲でアイテムエンティティを吸引する。
 * エネルギーがない場合は吸引しない。
 * <p>
 * 接続数の制限はなく、どの接続状態でも吸引動作を行う。
 * 他のObsidian/StripesパイプへのCanConnect制限はoriginalと同様。
 */
public class ObsidianItemPipeBehaviour implements ItemPipeBehaviour {

  public static final ObsidianItemPipeBehaviour INSTANCE = new ObsidianItemPipeBehaviour();

  // 吸引を試みるtick間隔
  private static final int SUCK_INTERVAL = 4;
  // 距離1あたりのエネルギーコスト (originalと同様: distance * 10 RF)
  private static final int ENERGY_PER_DISTANCE = 10;
  // アイテム1個あたりのエネルギーコスト (originalと同様: 10 * stackSize * distance)
  private static final int ENERGY_PER_ITEM = 10;

  private ObsidianItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    Level level = pipe.getLevel();
    if (level == null) return true;
    BlockPos neighborPos = pipe.getBlockPos().relative(dir);
    BlockEntity be = level.getBlockEntity(neighborPos);
    if (be instanceof PipeBlockEntity neighborPipe) {
      PipeMaterial mat = neighborPipe.getPipeMaterial();
      // originalと同様: Obsidian/StripesパイプへはCanConnect不可
      return mat != PipeMaterial.OBSIDIAN && mat != PipeMaterial.STRIPES;
    }
    return true;
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) return;
    // SUCK_INTERVAL ごとに吸引処理
    if (pipe.getTicksSincePull() % SUCK_INTERVAL != 0) return;

    // エネルギーがなければ吸引しない（originalと同様）
    int energy = pipe.getExtractionEnergy();
    if (energy <= 0) return;

    // 開いている出力方向を取得（吸引方向の基準）
    Direction openDir = getOpenOrientation(pipe, state);

    // originalと同様に距離1〜4で吸引を試みる
    for (int distance = 1; distance <= 4; distance++) {
      if (suckItem(pipe, level, pos, state, openDir, distance, energy)) {
        return;
      }
      // 距離ごとに最低限のエネルギーを消費（originalの battery.useEnergy(0, 5, false) に相当）
      int minCost = Math.min(5, energy);
      pipe.consumeExtractionEnergy(minCost);
      energy -= minCost;
      if (energy <= 0) return;
    }
  }

  /**
   * 指定距離でアイテムを吸引する。
   * originalの suckItem(int distance) に相当。
   */
  private boolean suckItem(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state,
                            Direction openDir, int distance, int availableEnergy) {
    AABB box = getSuckingBox(pos, openDir, distance);
    if (box == null) return false;

    List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);
    for (ItemEntity itemEntity : items) {
      if (!canSuck(itemEntity, distance, availableEnergy)) continue;
      pullItemIntoPipe(pipe, level, pos, state, itemEntity, distance, availableEnergy);
      return true;
    }
    return false;
  }

  /**
   * originalの getSuckingBox() に相当。
   * 指定方向・距離に応じたAABBを返す。
   */
  private AABB getSuckingBox(BlockPos pos, Direction orientation, int distance) {
    if (orientation == null) return null;

    double x = pos.getX();
    double y = pos.getY();
    double z = pos.getZ();

    double x1 = x, y1 = y, z1 = z;
    double x2 = x + 1, y2 = y + 1, z2 = z + 1;

    switch (orientation) {
      case EAST -> {
        x1 = x + distance;
        x2 = x + 1 + distance;
        y1 = y - distance;
        y2 = y + 1 + distance;
        z1 = z - distance;
        z2 = z + 1 + distance;
      }
      case WEST -> {
        x1 = x - distance;
        x2 = x - distance + 1;
        y1 = y - distance;
        y2 = y + 1 + distance;
        z1 = z - distance;
        z2 = z + 1 + distance;
      }
      case UP -> {
        x1 = x - distance;
        x2 = x + 1 + distance;
        y1 = y + distance;
        y2 = y + 1 + distance;
        z1 = z - distance;
        z2 = z + 1 + distance;
      }
      case DOWN -> {
        x1 = x - distance;
        x2 = x + 1 + distance;
        y1 = y - distance;
        y2 = y - distance + 1;
        z1 = z - distance;
        z2 = z + 1 + distance;
      }
      case SOUTH -> {
        x1 = x - distance;
        x2 = x + 1 + distance;
        y1 = y - distance;
        y2 = y + 1 + distance;
        z1 = z + distance;
        z2 = z + 1 + distance;
      }
      case NORTH -> {
        x1 = x - distance;
        x2 = x + 1 + distance;
        y1 = y - distance;
        y2 = y + 1 + distance;
        z1 = z - distance;
        z2 = z - distance + 1;
      }
    }

    return new AABB(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
        Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
  }

  /**
   * originalの canSuck() に相当。
   * エンティティが吸引可能かどうかを判定する。
   */
  private boolean canSuck(ItemEntity entity, int distance, int availableEnergy) {
    if (!entity.isAlive()) return false;
    ItemStack stack = entity.getItem();
    if (stack.isEmpty()) return false;
    // originalと同様: distance * 10 RF 以上のエネルギーが必要
    return availableEnergy >= distance * ENERGY_PER_DISTANCE;
  }

  /**
   * originalの pullItemIntoPipe() に相当。
   * アイテムエンティティをパイプに吸引する。
   */
  private void pullItemIntoPipe(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state,
                                ItemEntity itemEntity, int distance, int availableEnergy) {
    if (level.isClientSide) return;

    Direction injectDir = getOpenOrientation(pipe, state);
    if (injectDir == null) injectDir = Direction.UP;
    Direction injectDirFinal = injectDir.getOpposite();

    ItemStack contained = itemEntity.getItem();
    if (contained.isEmpty()) return;

    // originalと同様: 10 * stackSize * distance のエネルギーを消費
    int energyCost = Math.min(ENERGY_PER_ITEM * contained.getCount() * distance, availableEnergy);
    int itemsCanSuck = (distance == 0) ? contained.getCount() : energyCost / distance / ENERGY_PER_ITEM;

    ItemStack toInject;
    if (itemsCanSuck >= contained.getCount()) {
      toInject = contained.copy();
      itemEntity.discard();
    } else {
      toInject = contained.split(itemsCanSuck);
      if (contained.isEmpty()) {
        itemEntity.discard();
      }
    }

    pipe.consumeExtractionEnergy(energyCost);

    // アイテムの速度を引き継ぐ（originalと同様）
    double motionX = itemEntity.getDeltaMovement().x;
    double motionY = itemEntity.getDeltaMovement().y;
    double motionZ = itemEntity.getDeltaMovement().z;
    float speed = (float) Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
    speed = speed / 2f - 0.05f;
    if (speed < 0.01f) speed = 0.01f;

    pipe.injectItemWithSpeed(toInject, injectDirFinal, speed);
  }

  /**
   * パイプの開いている出力方向を取得する。
   * originalの getOpenOrientation() に相当。
   */
  private Direction getOpenOrientation(PipeBlockEntity pipe, BlockState state) {
    for (Direction dir : Direction.values()) {
      var prop = PipeBlock.PROPERTY_MAP.get(dir);
      if (prop != null && state.getValue(prop)) {
        return dir;
      }
    }
    return null;
  }

  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, com.peco2282.bcreborn.transport.pipe.TravelingItem item) {
    return null;
  }
}
