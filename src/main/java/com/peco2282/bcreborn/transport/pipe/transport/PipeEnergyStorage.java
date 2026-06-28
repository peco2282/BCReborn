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
package com.peco2282.bcreborn.transport.pipe.transport;

import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * エネルギーパイプ用の IEnergyStorage 実装。
 * EnergyTransportModule へ処理を委譲する。
 */
public class PipeEnergyStorage implements IEnergyStorage {
    private final EnergyTransportModule module;
    private final Direction side;

    public PipeEnergyStorage(EnergyTransportModule module, Direction side) {
        this.module = module;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        if (simulate) {
            double space = module.getMaxPower() - module.getInternalPower()[side.get3DDataValue()];
            return (int) Math.min(maxReceive, space);
        }
        return module.receiveEnergy(side, maxReceive);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        // パイプからの積極的な抽出はサポートしない（module.tick で配送されるため）
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return (int) module.getInternalPower()[side.get3DDataValue()];
    }

    @Override
    public int getMaxEnergyStored() {
        return module.getMaxPower();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
