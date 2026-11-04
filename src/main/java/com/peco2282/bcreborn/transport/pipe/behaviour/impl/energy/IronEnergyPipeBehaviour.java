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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy;

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.PowerMode;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.statements.ActionPowerLimiter;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 鉄のエネルギーパイプの振る舞い。
 * 転送量をステップで制限できる。レンチ右クリックでステップを変更できる。
 * 20RF/t ～ 1280RF/t まで対応。
 * 転送量の設定は PipeBlockEntity の ironPipeEnergyLimit フィールドで管理する。
 * エネルギー転送ロジックは EnergyTransportModule が一元管理する。
 */
public class IronEnergyPipeBehaviour extends StandardEnergyPipeBehaviour {
  public static final IronEnergyPipeBehaviour INSTANCE = new IronEnergyPipeBehaviour();

  /**
   * 設定可能な転送量ステップ (RF/t)
   */
  public static final int[] TRANSFER_STEPS = {20, 40, 80, 160, 320, 640, 1280};

  private IronEnergyPipeBehaviour() {
  }

  /**
   * 現在の設定値から次のステップへ循環する。
   * レンチ右クリック時に呼び出す。
   */
  public static int nextTransferStep(int current) {
    for (int i = 0; i < TRANSFER_STEPS.length; i++) {
      if (TRANSFER_STEPS[i] == current) {
        return TRANSFER_STEPS[(i + 1) % TRANSFER_STEPS.length];
      }
    }
    return TRANSFER_STEPS[0];
  }

  /** Original {@code PipePowerIron.getActions()}: one power limiter action per {@link PowerMode}. */
  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (PowerMode mode : PowerMode.VALUES) {
      actions.add(TransportStatements.ACTION_POWER_LIMITER.get(mode).get());
    }
  }

  /** Original {@code PipePowerIron.actionsActivated()}: the first active limiter sets the mode. */
  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionPowerLimiter limiter) {
        if (pipe.getIronPipeEnergyLimit() != limiter.limit.maxPower) {
          pipe.setIronPipeEnergyLimit(limiter.limit.maxPower);
        }
        break;
      }
    }
  }

  /**
   * Original {@code PipePowerIron.updateEntity()}: {@code transport.maxPower = getMode().maxPower} every tick, so
   * the limit also holds after a reload (the module is rebuilt with the material default).
   */
  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    var module = pipe.getEnergyTransportModule();
    if (module != null && module.getMaxPower() != pipe.getIronPipeEnergyLimit()) {
      module.setMaxPower(pipe.getIronPipeEnergyLimit());
    }
  }

  @Override
  protected int getMaxTransferRate(PipeMaterial material) {
    // 鉄パイプのデフォルト上限は 1280 RF/t
    return PipeMaterial.IRON.getEnergyTransferRate();
  }

  /**
   * レンチ右クリックで転送量ステップを変更する。
   */
  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide) return InteractionResult.SUCCESS;
    int current = pipe.getIronPipeEnergyLimit();
    int next = nextTransferStep(current);
    pipe.setIronPipeEnergyLimit(next);
    // EnergyTransportModule の maxPower も更新
    var module = pipe.getEnergyTransportModule();
    if (module != null) {
      module.setMaxPower(next);
    }
    player.displayClientMessage(
      Component.translatable("bcreborn.pipe.iron_energy.limit", next), true);
    return InteractionResult.SUCCESS;
  }
}
