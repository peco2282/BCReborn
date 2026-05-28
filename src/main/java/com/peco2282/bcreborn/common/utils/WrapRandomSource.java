package com.peco2282.bcreborn.common.utils;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.Random;

public class WrapRandomSource extends Random implements RandomSource {
    private final RandomSource wrapped;

    public WrapRandomSource(RandomSource wrapped) {
        this.wrapped = wrapped;
    }

    @Override
	public RandomSource fork() {
		return wrapped.fork();
	}

	@Override
	public PositionalRandomFactory forkPositional() {
		return wrapped.forkPositional();
	}


    @Override
    public int nextInt(int origin, int bound) {
        return wrapped.nextInt(origin, bound);
    }
}
