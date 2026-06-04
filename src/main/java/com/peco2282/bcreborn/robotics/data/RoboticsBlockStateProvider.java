package com.peco2282.bcreborn.robotics.data;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.common.data.BCBlockStateHelper;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RoboticsBlockStateProvider extends BCBlockStateHelper {
  public RoboticsBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornRobotics.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {

  }
}
