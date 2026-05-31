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
package com.peco2282.bcreborn.common.utils;

import java.util.Date;

public class IterableAlgorithmRunner extends Thread {

  private final IIterableAlgorithm pathFinding;

  private boolean stop = false;
  private final int maxIterations;

  private boolean done = false;

  public IterableAlgorithmRunner(IIterableAlgorithm iPathFinding, int iMaxIterations) {
    super("Path Finding");
    pathFinding = iPathFinding;
    maxIterations = iMaxIterations;
  }

  public IterableAlgorithmRunner(IIterableAlgorithm iPathFinding) {
    this(iPathFinding, 1000);
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < maxIterations; ++i) {
        if (isTerminated() || pathFinding.isDone()) {
          break;
        }

        long startTime = new Date().getTime();

        pathFinding.iterate();

        long elapsedTime = new Date().getTime() - startTime;
        int timeToWait = MathUtils.clamp((int) Math.ceil(elapsedTime * 1.5), 1, 500);
        sleep(timeToWait);
      }
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      done = true;
    }
  }

  public synchronized void terminate() {
    stop = true;
  }

  public synchronized boolean isTerminated() {
    return stop;
  }

  public synchronized boolean isDone() {
    return done;
  }

}
