package peco2282.bcreborn.api.mj;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface MJGenerator extends MJCapability {
  long perTick(Level level, BlockState state);
}
